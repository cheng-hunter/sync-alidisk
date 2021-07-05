#### 更新日志 
* 2021/7/5 16:52 已经支持文件更新，网盘或本地自动同步到最新版本
* 2021/7/5 18:27 已经支持本地文件删除，并同步到网盘上
#### 使用
* 设置配置中的 `ALI_FOLDER_NAME` 和 `LOCAL_FOLDER_NAME`
```shell
ALI_FOLDER_NAME = "sync_folder"
LOCAL_FOLDER_NAME = "C:\\同步盘"
```
* 同步环境
```shell
pip install -r requirement.txt
```
* 运行项目
```shell
python run.py
```
* 打开url扫码登录
```shell
http://192.168.1.100:8081/
```
* 开始自动同步，可以上传文件到阿里网盘测试，由于是双向同步，所以一端删除文件又回被同步回来，可多客户端登录实现多端同步，由于阿里网盘的快传传一些知名大文件会非常的快速。
