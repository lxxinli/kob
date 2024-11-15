## 云服务器笔记

[阿里云地址](https://www.aliyun.com/)

---

#### 创建工作用户并赋予`sudo`权限
登录到新服务器。打开Terminal，然后：

```shell
ssh user@hostname
```

* `user`: 用户名(如root)
* `hostname`: IP地址或域名(如xxx.xxx.xxx.xxx)

> 第一次登录时会提示：

```shell
The authenticity of host '66.188.185.66 (66.188.185.66)' can't be established.
ED25519 key fingerprint is SHA256:SZfhynS8auEJWvEIyecTGm8m8VeflY1g+s8gzJ88PRI.
This key is not known by any other names
Are you sure you want to continue connecting (yes/no/[fingerprint])?
```

输入`yes`，然后回车即可。

这样会将该服务器的信息记录在`~/.ssh/known_hosts`文件中。

然后输入密码即可登录到远程服务器中。

默认登录端口号为22。如果想登录某一特定端口：

```shell
ssh user@hostname -p 22
```

登录成功后，创建`lx`用户：

```shell
adduser lx  # 创建用户lx
usermod -aG sudo lx  # 给用户lx分配sudo权限
```

切换到`lx`用户：

```shell
su lx #切换到lx用户
```

下次就可以直接ssh到新用户 `lx` 来登录到您的云服务器（实现多用户使用同一云服务器）

---

#### 配置别名和免密登录方式
按`Ctrl+d`或者输入`exit`退回本地的Terminal，在**自己电脑端**配置lx用户的别名和免密登录

##### 别名

创建文件` ~/.ssh/config`。

然后在文件中输入：

```shell
Host myserver1
    HostName xxx.xxx.xxx.xxx # IP地址或域名
    User lx # 用户名
	# Port 20000 这里我们没有修改端口号，所以不用加，但是后面的docker连接就需要加了
Host myserver2
    HostName xxx.xxx.xxx.xxx # IP地址或域名
    User root # 用户名
```

之后再使用服务器时，可以直接使用别名`myserver1`、`myserver2`。

##### 免密登录

本地主机创建密钥：

```shell
ssh-keygen
```

然后一直回车即可。

执行结束后，`~/.ssh/`目录下会多两个文件：

* `id_rsa`：私钥
* `id_rsa.pub`：公钥

之后想免密码登录哪个服务器，就将公钥传给哪个服务器即可。

例如，想免密登录`myserver1`服务器。则将公钥中的内容，复制到`myserver1`中的`~/.ssh/authorized_keys`文件里即可。

也可以使用如下命令一键添加公钥到服务器端：

```shell
ssh-copy-id myserver
```

---

#### 简易安全配置

##### 查看登录日志文件

```shell
sudo vim /var/log/auth.log
```

> 不出意外会看到很多类似如下的日志

```shell
Failed password for root from 183.146.30.163 port 22537 ssh2
Failed password for invalid user admin from 183.146.30.163 port 22545 ssh2
Invalid user tester from 101.254.217.219 port 56540
pam_unix(sshd:auth): check pass; user unknown
pam_unix(sshd:auth): authentication failure; logname= uid=0 euid=0 tty=ssh ruser= rhost=103.61.8.34
```

然后可以统计有多少人在暴力破解root密码错误登录，展示错误次数和ip

```shell
sudo grep "Failed password for root" /var/log/auth.log | awk '{print $11}' | sort | uniq -c | sort -nr | more
```

统计有多少暴力猜用户名的

```shell
sudo grep "Failed password for invalid user" /var/log/auth.log | awk '{print $13}' | sort | uniq -c | sort -nr | more
```

##### 禁止SSH的root用户登录
修改 `/etc/ssh/sshd_config`文件

首先创建一下文件的备份

```shell
sudo cp /etc/ssh/sshd_config /etc/ssh/sshd_config.bak
```

禁止以root用户身份通过 SSH 登录

```shell
PermitRootLogin no
```

##### 设置SSH单次登录限制
```shell
LogLevel INFO       #将LogLevel设置为INFO,记录登录和注销活动
MaxAuthTries 3      #限制单次登录会话的最大身份验证尝试次数
LoginGraceTime 20   #缩短单次的登录宽限期，即ssh登录必须完成身份验证的时间 单位是秒
```

重启ssh服务 `sudo service ssh restart`

##### 禁用密码登陆，使用RSA私钥登录
```shell
ssh-keygen #在客户端生成密钥
ssh-copy-id myserver1 #将公钥添加至服务端
```

还需要配置服务端

```shell
sudo vim /etc/ssh/sshd_config
PasswordAuthentication no #禁止密码认证
PermitEmptyPasswords no #禁止空密码用户登录
```

重启ssh服务` sudo service ssh restart`

---

#### docker配置和语法教程

##### 安装`tmux`和`docker`

登录自己的服务器，然后安装`tmux`：

```shell
sudo apt-get update
sudo apt-get install tmux
```

将本地配置通过`scp`传到新服务器上：

```shell
scp .vimrc .tmux.conf .bashrc server_name:   # server_name需要换成自己配置的别名（！！！注意目的地址后面要有冒号：） 
```

打开tmux。(养成好习惯，所有工作都在tmux里进行，防止意外关闭终端后，工作进度丢失)

> tmux操作小tips：按住shift就可以选择文本，然后ctrl-ins进行复制，shift-ins进行粘贴

然后在tmux中根据[docker安装教程](https://docs.docker.com/engine/install/ubuntu/)安装docker即可。

##### 将当前用户添加到`docker`用户组

为了避免每次使用`docker`命令都需要加上`sudo`权限，可以将当前用户加入安装中自动创建的`docker`用户组(可以参考[官方文档]([Post-installation steps | Docker Docs](https://docs.docker.com/engine/install/linux-postinstall/)))：

```shell
sudo usermod -aG docker $USER         # 这里USER不用改成lx，因为$USER会自动修改成当前的用户名
```


执行完此操作后，需要退出服务器，再重新登录回来，才可以省去`sudo`权限。

##### 镜像（images）
1. `docker pull ubuntu:20.04`：拉取一个镜像
2. `docker images`：列出本地所有镜像
3. `docker image rm ubuntu:20.04` 或 `docker rmi ubuntu:20.04`：删除镜像ubuntu:20.04
4. `docker [container] commit CONTAINER IMAGE_NAME:TAG`：创建某个container的镜像
5. `docker save -o ubuntu_20_04.tar ubuntu:20.04`：将镜像ubuntu:20.04导出到本地文件ubuntu_20_04.tar中
6. `docker load -i ubuntu_20_04.tar`：将镜像ubuntu:20.04从本地文件ubuntu_20_04.tar中加载出来

##### 容器(container)
1. `docker [container] create -it ubuntu:20.04`：利用镜像ubuntu:20.04创建一个容器。
2. `docker ps -a`：查看本地的所有容器
3. `docker [container] start CONTAINER`：启动容器
4. `docker [container] stop CONTAINER`：停止容器
5. `docker [container] restart CONTAINER`：重启容器
6. `docker [contaienr] run -itd ubuntu:20.04`：创建并启动一个容器
7. `docker [container] attach CONTAINER`：进入容器
   * 先按`Ctrl-p`，再按`Ctrl-q`可以挂起容器
8. `docker [container] exec CONTAINER COMMAND`：在容器中执行命令
9. `docker [container] rm CONTAINER`：删除容器
10. `docker container prune`：删除所有已停止的容器
11. `docker export -o xxx.tar CONTAINER`：将容器CONTAINER导出到本地文件xxx.tar中
12. `docker import xxx.tar image_name:tag`：将本地文件xxx.tar导入成镜像，并将镜像命名为image_name:tag
13. `docker export/import`与`docker save/load`的区别：
    * export/import会丢弃历史记录和元数据信息，仅保存容器当时的快照状态
    * save/load会保存完整记录，体积更大
14. `docker top CONTAINER`：查看某个容器内的所有进程
15. `docker stats`：查看所有容器的统计信息，包括CPU、内存、存储、网络等信息
16. `docker cp xxx CONTAINER:xxx` 或 `docker cp CONTAINER:xxx xxx`：在本地和容器间复制文件
17. `docker rename CONTAINER1 CONTAINER2`：重命名容器
18. `docker update CONTAINER --memory 500MB`：修改容器限制

##### 实战

进入Terminal，然后：

```shell
scp /var/lib/acwing/docker/images/docker_lesson_1_0.tar server_name:  # 将镜像上传到自己租的云端服务器
ssh server_name  # 登录自己的云端服务器

docker load -i docker_lesson_1_0.tar  # 将镜像加载到本地
docker run -p 20000:22 --name my_docker_server -itd docker_lesson:1.0  # 创建并运行镜像，将docker内的22(ssh)端口映射到外部的20000端口，docker_lesson:1.0是REPOSITORY:TAG

docker attach my_docker_server  # 进入创建的docker容器
passwd  # 设置root密码
```

去云平台控制台中修改安全组配置，放行端口`20000`。

返回Terminal，即可通过`ssh`登录自己的`docker`容器：

```shell
ssh root@xxx.xxx.xxx.xxx -p 20000  # 将xxx.xxx.xxx.xxx替换成自己租的服务器的IP地址
```

然后，可以仿照上面的*创建工作用户并赋予权限*，创建工作账户`lx`。

最后，可以参考上面的*配置别名和免密登录方式*配置`docker`容器的别名和免密登录。

---

#### SpringBoot项目部署上云

##### 安装、配置mysql
安装：

```shell
sudo apt-get install mysql-server 
```


启动：

```shell
sudo service mysql start
```

登录mysql:

```mysql
sudo mysql -u root
```

设置`root`用户的密码：

```mysql
ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'yourpasswd';         #yourpasswd需要改成你的密码
```

之后就得输入密码登录了：

```
sudo mysql -u root -p
```

##### 安装Java 17

```shell
sudo apt install openjdk-17-jdk
```

##### 打包后端
pom.xml中添加配置：

```xml
<packaging>jar</packaging>



<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <!--这里写上main方法所在类的路径-->
            <configuration>
                <mainClass>com.kob.backend.BackendApplication</mainClass>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

然后点击Maven中的`clean`和`build`，将代码打包成jar包，通过`scp`传输到服务器，再在服务器端使用命令行进行运行：

```shell
java -jar xxxxxx.jar 
```

##### 打包前端

前端所有的原来的`url: "http://127.0.0.1:3000/api/xxxx"`,都得换成你的实际ip：`http://47.116.187.43/api/xxxxx`

然后直接在vue界面里`build`界面点运行，就会生成一个`dist`文件夹，将文件夹里面内容发送到服务器即可

##### 配置Nginx

> 避雷：y总的那个docker镜像有毒，里面搭好的nginx有问题，需要卸载重新安装就好了，我三天一直在找这个问题，一直报错502，结果偶然看到acwing里一个交流贴提一嘴这个nginx有问题，总算大功告成

修改`/etc/nginx/nginx.conf`内容

```shell
user www-data;
worker_processes auto;
pid /run/nginx.pid;
include /etc/nginx/modules-enabled/*.conf;

events {
    worker_connections 1024;
}

http {
	sendfile on;
	tcp_nopush on;
	tcp_nodelay on;
	keepalive_timeout 65;
	types_hash_max_size 2048;
	
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    access_log /var/log/nginx/access.log;
    error_log /var/log/nginx/error.log;
	
	gzip on;
	
	include /etc/nginx/conf.d/*.conf;
	include /etc/nginx/sites-enabled/*;
    
    
    # 将 server 配置放在 http 块内
    server {
        listen 80;
        server_name 47.116.187.43;
        charset utf-8;
        access_log /var/log/nginx/access.1og;
        error_log /var/log/nginx/error.1og;
        client_max_body_size 10M;

        # 前端 Vue 项目的配置
        location / {
            root /home/lx/kob/web;  # 指向你的前端文件根目录
            index  index.html;
            try_files $uri $uri/ /index.html;  # 处理 Vue 路由
        }
        location /api {
            proxy_pass http://127.0.0.1:3000;
            proxy_set_header Host $http_host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
        location /websocket {
            proxy_pass http://127.0.0.1:3000;
            proxy_set_header Host $http_host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_read_timeout 36000s;
        }
	}
}
```

启动nginx:`sudo /etc/init.d/nginx start`

查看错误信息:`sudo cat /var/log/nginx/error.log`

更新nginx:`sudo /etc/init.d/nginx reload`

##### 获取域名及Https证书

分别配置`/etc/nginx/cert/acapp.key`和`/etc/nginx/cert/acapp.pem`

