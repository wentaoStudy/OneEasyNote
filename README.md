#OneEasyNote
非常简单的一款笔记记录软件，方便记录文字以及语音笔记，同时可以再网络端同步文字笔记，可以再笔记中添加图片内容，可以将文字内容分享给好友的简单的笔记软件，我们开发的初衷是通过自己的能力，开发出一款我们能够使用的，不受限制的简介，轻便的笔记软件，同时实现平常内容记录功能；

##
三、项目的主要功能
3、1首先打开app，打开app后会看到登陆界面，使用Bmob数据库存放已经注册的用户名，如图3.1
（因为这里是我们第一次使用，会自动跳转到登录界面，使用权限已经申请）
 
图3.1	 
图3.2	 
图3.3
3、2注册页面，连接Bmob，实现用户注册功能
3、3主界面主要功能实现
 
图3.4	 
图3.5	 
图3.6
3,4添加笔记
3.5添加标题，默认读取创建时间，插入图片内容
3.6存储界面（长按删除）
 
图3.7	 
图3.8	 图3.9
3.7 用户界面，主要功能
3.8 更换皮肤主题
3.9 语音笔记界面
  图3.10	 图3.11 	 
图3.12
3.10获取权限
3.11及时更新数据（服务器）
3.12桌面appWeiget
 图3.13	 图3.14	 
3.13用户设置
3.14 Matisse图片选择功能
头像功能部分，还有文档需要图片文件的部分，都是可以调用本地相册进行使用的，同时对使用的图片进行记录，更换主题部分，考虑到使用者对图片像素的选择可能会出现图片效果不完美，因此没有开放自定义从文件选择图片数量，而是使用选择方式是主题完美
四、项目的主要特色介绍
    1.可以多终端登录，实现数据库云端存储，数据收集和共享
2.具有好看的皮肤和头像供君选择
3.支持文档笔记和语音笔记存储
4.可读取本地数据，获取内容实现功能
五、项目的关键技术
Sqlite 3 数据库
表	表名
Goup表	db_group
User表	db_user
Note表	db_note
Radio表	db_radio


Sqlite3 db_user表
列名	含义
u_name	用户姓名
u_id	用户id
u_group	用户所属组
u_number	用户电话号码
u_password	用户密码
Sqlite3 db_note表
列名	含义
n_id	笔记id
n_userid	笔记所属者id
n_title	笔记标题
n_ content	笔记内容
n_crate_time	笔记创建时间
n_update_time	笔记更新时间

Bmob(包含http ， json)云数据库
表	表名
User表	User
Note表	Note
Glide框架工具
用来实现导入头像图片，NoteListFragment里面笔记展示列表中带图片的子项图片压缩展示的实现(如果直接使用图片源资源，不压缩，会出现)
Fragment使用
用来实现MainActivity里面各种功能布局的切换
CoordLayout，ConstantLayout ， LinerLayout等布局的使用
用来实现对各种布局需要的实现，实现一些比较美观的界面
MdiaPlayer工具
开源项目cuteRecorder的使用，开源项目地址
https://github.com/GodisGod/cuteRecorder
使用该项目实现本项目的录音功能
开源项目WaveLineView的使用，开源项目地址
https://github.com/Jay-Goo/WaveLineView
使用该项目实现本项目录音时动态的波浪效果
开源项目XRichText的使用，开源项目地址
https://github.com/sendtion/XRichText
使用该项目实现本项目的富文本编辑功能
开源项目YingBeautyNote的使用，开源项目地址
https://github.com/HuTianQi/YingBeautyNote
使用该项目借鉴其更改项目主题功能，以及控件Circle Image View
开源项目Matisse的使用，开源项目地址
https://github.com/zhihu/Matisse
该项目具有完整的图片选择功能
Bmob开发文档使用，地址
http://doc.bmob.cn/data/android/develop_doc/#2
Bmob开发的免费云端
因为Bmob上游服务商又拍云对文件域名的限制，短时间内没有通过Bmob是实现文件存储，即图片和录音存储问题，Bmob仅用来实现用户存储和笔记内容存储
AppWidget开发参考文章
《Android之AppWidget开发浅析》，地址：https://blog.csdn.net/feng020a/article/details/54917798
《Android列表小部件（Widget）开发详解》，地址：https://blog.csdn.net/qq_20521573/article/details/79174481

