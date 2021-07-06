
from bin.alipan import YxhpyAliPan
import time
from flask import Flask, render_template
from config.config import SYNC_DELAY, ALI_FOLDER_NAME, LOCAL_FOLDER_NAME
from threading import Thread

app = Flask(__name__)


def main():
    ali_pan = YxhpyAliPan("ef34fe0510394538bf3624211b7f738c")
    file_id = ali_pan.get_file_id(ALI_FOLDER_NAME)
    while True:
        print("同步开始，请勿在同步的时候删除文件")
        ali_pan.remove_sync()
        ali_pan.sync_path(LOCAL_FOLDER_NAME, file_id)
        print(f"同步完成，等待{SYNC_DELAY}秒")
        time.sleep(SYNC_DELAY)


@app.route("/")
def index():
    data = {"ALI_FOLDER_NAME": ALI_FOLDER_NAME,
            "LOCAL_FOLDER_NAME": LOCAL_FOLDER_NAME,
            "info": YxhpyAliPan.info[-3:]}
    return render_template("index.html", **data)


def start_web():
    app.run("0.0.0.0", 8081)


if __name__ == '__main__':
    t = Thread(target=start_web)
    t.start()
    main()