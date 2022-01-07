# 该版本将重新全部使用java编写，已经可以使用
# 功能简介
* 使用JAVA重写项目
* 基于阿里云盘实现
* 实现`秒传`
* 支持本地更新文件同步到网盘
* ~~支持多线程上传~~
* ~~支持扫码登陆，不支持记住登录状态（保护账户安全）~~
* 支持记住登录，扫描登录
* 支持文件加密和解密 安全性更高
#### 更新日志
* 2021/1/7 9:00 下载同步和上传同步不同时执行，防止出错，修复编码问题，本地文件夹不存在自动创建
* 2021/1/3 12:00 使用java重写项目，确保系统基本无bug
* 2021/7/5 16:52 已经支持文件更新，网盘或本地自动同步到最新版本
* 2021/7/5 18:27 已经支持本地文件删除，并同步到网盘上
* 2021/7/6 15:08 已经支持本地文件删除bug修复
* ~~2021/8/6 16:45 重大更新，加入多线程大大加快上传速度配置到`config.py`的`MAX_THREAD_NUM`更改线程数，线程数越多上传越快~~
* 2021/9/15 加入记住登录，自动记住，删除线程池，不知道为何线程池无法进入函数，等待测试完成后上传
* 2021/9/16 修复无static文件夹下无refresh_token文件导致的错误
* 2021/11/15 暂时将错误的异常捕获来处理json解析错误问题，比较忙我需要慢慢排查一下
* 注：1、由于删除是在用户任意时刻去触发的，程序无法在用户删除时去中断自己的执行，可能造成bug，建议用户尽量不要使用删除操作。后续有办法再解决。2、为安全起见就不将token到本地了，相当于可以记住登录，如果确实有需求再请大家提出来。
#### 配置说明
```
# UA默认即可
request.header.ua=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36
# 加密密码
file.password=520612lgh1
file.password.salt=123456789
# 是否开启加密
file.password.enable=true
# 本地路径
local.path=D:\\sync
# 远程路径
remote.path=abc\\test01
# 默认即可，分片上传的每片最大文件大小
upload.part.size=10485760
# 是否是文件的供应端，如果是那么本地文件更新后会直接上传到aliyun
# 如果不是那么只提供同步下载最新文件到本地
provider.enable=false
```
# 免责声明
1. 本软件为免费开源项目，无任何形式的盈利行为。
2. 本软件服务于阿里云盘，旨在让阿里云盘功能更强大。如有侵权，请与我联系，会及时处理。
3. 本软件皆调用官方接口实现，无任何“Hack”行为，无破坏官方接口行为。
5. 本软件仅做流量转发，不拦截、存储、篡改任何用户数据。
6. 严禁使用本软件进行盈利、损坏官方、散落任何违法信息等行为。
7. 本软件不作任何稳定性的承诺，如因使用本软件导致的文件丢失、文件破坏等意外情况，均与本软件无关。