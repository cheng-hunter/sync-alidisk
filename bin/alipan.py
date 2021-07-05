import base64
import json
import os
import re
import time

import requests

from config.config import UPLOAD_PART_SIZE, DEFAULT_HEADERS
from utils.file import get_file_info, compute_part_num, get_file_hash, parse_js_data
from utils.qrcodeUtils import create_qr


class AliPan:
    info = []

    def __init__(self, token=None):
        self.token = ""
        self.token_type = ""
        self.refresh_token = token
        self.drive_id = None
        self.folders = {}
        self.headers = DEFAULT_HEADERS
        self.part_size = UPLOAD_PART_SIZE
        self.login_qr_check = {}
        self.login_session = None
        if token:
            self.refresh()
        else:
            self.get_login_qr()

    def post(self, url, json=None):
        response = requests.post(url, json=json,
                                 headers=self.headers)
        return response

    def get(self, url, params=None):
        response = requests.get(url, params=params, headers=self.headers)
        return response

    '''
    过期刷新token
    '''

    def refresh(self):
        json = {"refresh_token": self.refresh_token}
        response = requests.post("https://api.aliyundrive.com/token/refresh", json=json)
        if response.status_code == 200:
            data = response.json()
            self.token = data.get("access_token")
            self.token_type = data.get("token_type")
            self.refresh_token = data.get("refresh_token")
            self.drive_id = data.get("default_drive_id")
            self.headers["authorization"] = f"{self.token_type} {self.token}"

    '''
    判断文件是否是已经存在了
    '''

    def file_exists(self, parent_file_id, file_info: dict):
        part_num = compute_part_num(file_info, self.part_size)
        data = {"drive_id": self.drive_id,
                "part_info_list": [{"part_number": i + 1} for i in range(part_num)],
                "parent_file_id": parent_file_id,
                "name": file_info.get("file_name"),
                "type": "file",
                "check_name_mode": "auto_rename",
                "size": file_info.get("file_size"),
                "pre_hash": file_info.get("pre_hash")}
        response = self.post("https://api.aliyundrive.com/adrive/v2/file/createWithFolders", data)
        if response.status_code == 409:  # 表示已经存在文件了
            AliPan.info.append("文件已经存在于服务器了")
            return False, response.json()
        else:
            AliPan.info.append("这是一个新文件")
            return True, response.json()

    def complete(self, file_id, upload_id):
        data = {"drive_id": self.drive_id,
                "upload_id": upload_id,
                "file_id": file_id
                }
        response = self.post("https://api.aliyundrive.com/v2/file/complete", json=data)
        AliPan.info.append(response.json())

    def create_with_folders(self, parent_file_id, file_info: dict, mode="file"):
        data = {}
        if mode == "file":
            part_num = compute_part_num(file_info, self.part_size)
            data = {"drive_id": self.drive_id,
                    "part_info_list": [{"part_number": i + 1} for i in range(part_num)],
                    "parent_file_id": parent_file_id,
                    "name": file_info.get("file_name"),
                    "type": "file",
                    "check_name_mode": "auto_rename",
                    "size": file_info.get("file_size"),
                    "content_hash": file_info.get("content_hash"),
                    "content_hash_name": "sha1",
                    "proof_code": file_info.get("proof_code"),
                    "proof_version": "v1"}
        if mode == "dir":
            data = {"drive_id": self.drive_id, "parent_file_id": parent_file_id, "name": file_info["name"],
                    "check_name_mode": "refuse", "type": "folder"}
        response = self.post("https://api.aliyundrive.com/adrive/v2/file/createWithFolders", data)
        return response.json()

    def file_list(self, parent_file_id="root"):
        json = {
            'all': False,
            'drive_id': self.drive_id,
            'fields': "*",
            'image_thumbnail_process': "image/resize,w_400/format,jpeg",
            'image_url_process': "image/resize,w_1920/format,jpeg",
            'limit': 100,
            'order_by': "updated_at",
            'order_direction': "DESC",
            'parent_file_id': parent_file_id,
            'url_expire_sec': 1600,
            'video_thumbnail_process': "video/snapshot,t_0,f_jpg,ar_auto,w_300",
        }
        if parent_file_id:
            json['parent_file_id'] = parent_file_id
        response = self.post("https://api.aliyundrive.com/v2/file/list", json)
        response_json = response.json()
        for item in response_json.get("items"):
            self.folders[item.get('name')] = item
        return response_json

    def get_file_id(self, file_name, parent_file_id="root"):
        self.file_list(parent_file_id)
        return self.folders[file_name]["file_id"]

    def upload_file(self, parent_file_id, file_path):
        start_time = time.time()
        file_info = get_file_info(file_path, self.token)
        status, data = self.file_exists(parent_file_id, file_info)
        if not status:
            # 如果文件已经存在于数据库之中，那么就开始秒传
            AliPan.info.append(self.create_with_folders(parent_file_id, file_info))
        else:
            part_info_list = data.get("part_info_list")
            file_id = data.get("file_id")
            upload_id = data.get("upload_id")
            try:
                with open(file_path, "rb") as f:
                    for part_index, part_info in enumerate(part_info_list):
                        upload_url = part_info.get("upload_url")
                        data = f.read(self.part_size)
                        requests.put(upload_url, data=data)
                    f.close()
            except Exception as e:
                print(e)
            # 确认发送完毕
            self.complete(file_id, upload_id)
        AliPan.info.append(time.time() - start_time)
        AliPan.info.append("完成")

    def get_login_qr(self):
        self.login_session = requests.session()
        self.login_session.get(
            "https://auth.aliyundrive.com/v2/oauth/authorize?client_id=25dzX3vbYqktVxyX&redirect_uri=https%3A%2F%2Fwww.aliyundrive.com%2Fsign%2Fcallback&response_type=code&login_type=custom&state=%7B%22origin%22%3A%22https%3A%2F%2Fwww.aliyundrive.com%22%7D")
        response = self.login_session.get(
            "https://passport.aliyundrive.com/newlogin/qrcode/generate.do?appName=aliyun_drive&fromSite=52&appName=aliyun_drive&appEntrance=web&_csrf_token=8iPG8rL8zndjoUQhrQnko5&umidToken=27f197668ac305a0a521e32152af7bafdb0ebc6c&isMobile=false&lang=zh_CN&returnUrl=&hsiz=1d3d27ee188453669e48ee140ea0d8e1&fromSite=52&bizParams=&_bx-v=2.0.31")
        data = response.json()['content']['data']
        self.login_qr_check["ck"] = data['ck']
        self.login_qr_check["t"] = data['t']
        AliPan.info.append("登录地址" + data['codeContent'])
        create_qr(data['codeContent'])
        self.get_qr_status()

    def get_qr_status(self):
        while True:
            response = self.login_session.post(
                "https://passport.aliyundrive.com/newlogin/qrcode/query.do?appName=aliyun_drive&fromSite=52&_bx-v=2.0.31",
                data=self.login_qr_check)
            data = response.json()["content"]['data']
            qr_code_status = data['qrCodeStatus']
            if qr_code_status == 'NEW':
                AliPan.info.append("等待扫码登录")
            if qr_code_status == 'SCANED':
                AliPan.info.append("请到手机端确认登录")
            if qr_code_status == 'CONFIRMED':
                AliPan.info.append("登录成功")
                pds_login_result = json.loads(base64.b64decode(data['bizExt']).decode("gbk"))['pds_login_result']
                access_token = pds_login_result['accessToken']
                code = self.get_token_login_code(access_token)
                self.get_refresh_token(code)
                self.refresh()
                break
            if qr_code_status == 'EXPIRED':
                AliPan.info.append("二维码已经过期")
                self.get_login_qr()
                break
            time.sleep(5)

    def get_token_login_code(self, token):
        json = {
            'token': token
        }
        headers = {
            "Referer": "https://auth.aliyundrive.com/v2/oauth/authorize?client_id=25dzX3vbYqktVxyX&redirect_uri=https%3A%2F%2Fwww.aliyundrive.com%2Fsign%2Fcallback&response_type=code&login_type=custom&state=%7B%22origin%22%3A%22https%3A%2F%2Fwww.aliyundrive.com%22%7D"
        }
        response = self.login_session.post("https://auth.aliyundrive.com/v2/oauth/token_login", json=json,
                                           headers=headers)
        code = re.findall(r"code=(.*?)\&", response.json()['goto'])[0]
        return code

    def get_refresh_token(self, code):
        json = {
            'code': code
        }
        response = self.login_session.post("https://api.aliyundrive.com/token/get", json=json)
        self.refresh_token = response.json()['refresh_token']

    def download_file(self, dir_path, file_name, file_id):
        data = {"drive_id": self.drive_id, "file_id": file_id}
        response = self.post("https://api.aliyundrive.com/v2/file/get_download_url", json=data)
        if response.status_code == 200:
            json = response.json()
            if json.get("streams_url"):
                download_url = json["streams_url"]["jpeg"]
            else:
                download_url = json["url"]
            if download_url:
                response = requests.get(download_url, headers={"Referer": "https://www.aliyundrive.com/"})
                if response.status_code == 200:
                    try:
                        with open(os.path.join(dir_path, file_name), "wb") as f:
                            f.write(response.content)
                    except Exception as e:
                        print(e)

    def trash_file(self, file_id):
        json = {"drive_id": self.drive_id, "file_id": file_id}
        self.post("https://api.aliyundrive.com/v2/recyclebin/trash", json=json)

    def sync_path_exists(self, items, path):
        ali_file_dict = {item["name"]: item for item in items}
        for full_path_item, path_item in [(os.path.join(path, path_item), path_item) for path_item in os.listdir(path)]:
            if os.path.isfile(full_path_item):
                file_hash = get_file_hash(full_path_item)
                ali_file_info = ali_file_dict.get(path_item, None)
                if ali_file_info:
                    if file_hash.upper() != ali_file_info["content_hash"].upper():
                        # 版本不一致需要更新版本，查看更新时间更新为最新的
                        local_update_at = os.path.getatime(full_path_item)
                        ali_updated_at = parse_js_data(ali_file_info["updated_at"]) / 1000
                        if local_update_at > ali_updated_at:
                            # 本地最新上传
                            print("1本地最新上传", full_path_item)
                            self.trash_file(ali_file_dict[path_item]["file_id"])
                            self.upload_file(ali_file_dict[path_item]["parent_file_id"], full_path_item)
                        else:
                            # 远程下载
                            print("2远程最新下载", full_path_item)
                            self.download_file(path, path_item, ali_file_dict[path_item]["file_id"])

    def sync_path(self, local_path, parent_file_id):
        items = self.file_list(parent_file_id).get("items", [])
        ali_pan_file_list = set(items["name"] for items in items if items['type'] == 'file')
        ali_pan_path_list = set(items["name"] for items in items if items['type'] == 'folder')
        local_file_list = set(
            path for path in os.listdir(local_path) if os.path.isfile(os.path.join(local_path, path)))
        local_path_list = set(
            path for path in os.listdir(local_path) if os.path.isdir(os.path.join(local_path, path)))

        upload_file_list = local_file_list - ali_pan_file_list
        download_file_list = ali_pan_file_list - local_file_list
        # 改变的进行同步
        self.sync_path_exists(items, local_path)
        # 同步到本地
        for download_path in ali_pan_path_list:
            full_download_path = os.path.join(local_path, download_path)
            if not os.path.exists(full_download_path):
                os.mkdir(full_download_path)
            full_ali_pan_path_id = self.get_file_id(download_path, parent_file_id)
            self.sync_path(full_download_path, full_ali_pan_path_id)
        for upload_path in local_path_list:
            full_upload_path = os.path.join(local_path, upload_path)
            new_path_id = self.create_with_folders(parent_file_id, {"name": upload_path}, mode="dir")["file_id"]
            self.sync_path(full_upload_path, new_path_id)
        for download_file in download_file_list:
            file_id = self.folders[download_file]["file_id"]
            self.download_file(local_path, download_file, file_id)
        # 同步到服务器
        for download_file in upload_file_list:
            self.upload_file(parent_file_id, os.path.join(local_path, download_file))
