# 功能简介
* 基于阿里云盘实现
* 实现`秒传`
* 支持本地更新文件同步到网盘
* 支持多线程上传
* 支持扫码登陆，不支持记住登录状态（保护账户安全）
#### 更新日志 
* 2021/7/5 16:52 已经支持文件更新，网盘或本地自动同步到最新版本
* 2021/7/5 18:27 已经支持本地文件删除，并同步到网盘上
* 2021/7/6 15:08 已经支持本地文件删除bug修复
* 2021/8/6 16:45 重大更新，加入多线程大大加快上传速度配置到`config.py`的`MAX_THREAD_NUM`更改线程数，线程数越多上传越快
* 注：1、由于删除是在用户任意时刻去触发的，程序无法在用户删除时去中断自己的执行，可能造成bug，建议用户尽量不要使用删除操作。后续有办法再解决。2、为安全起见就不将token到本地了，相当于可以记住登录，如果确实有需求再请大家提出来。
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

# 免责声明
1. 本软件为免费开源项目，无任何形式的盈利行为。
2. 本软件服务于阿里云盘，旨在让阿里云盘功能更强大。如有侵权，请与我联系，会及时处理。
3. 本软件皆调用官方接口实现，无任何“Hack”行为，无破坏官方接口行为。
5. 本软件仅做流量转发，不拦截、存储、篡改任何用户数据。
6. 严禁使用本软件进行盈利、损坏官方、散落任何违法信息等行为。
7. 本软件不作任何稳定性的承诺，如因使用本软件导致的文件丢失、文件破坏等意外情况，均与本软件无关。