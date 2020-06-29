## 简单说明

为了能在多个机器上的环境上方便的导入项目，对项目结构进行了优化

根目录分为三个 app, library, modules , modules分为 eclipse, studio, support
### 目录说明  
依赖关系是 app <-- library <-- modules

1. app 主要是放业务代码
2. library 主要是放一些jar包
3. modules 主要是放一些第三方的开源项目
   3.1 eclipse存放eclipse工程
   3.2 studio存放studio工程
   3.3 support存放一些google的support包

gradle采用了继承的写法，eclipse和studio 目录下放置第三方工程不需要明确指定gradle文件，如果有一些特殊的依赖，在自己的目录下的build.gradle添加自己的东西。

一些demo的代码，放在app目录下，开发过程先参考代码，之后可以删除，写自己的业务逻辑。

### 简单的使用第三方工程
1. 首先，把代码放入modules/eclipse或者modules/studio目录下
2. 在setting.gradle目录中声明工程 include ':modules:studio:custom'(名字需要自定义)
3. compile project(':modules:studio:custom')
4. 在app中就可以使用第三方工程的一些东西了

### 多环境打包   
版本号的管理还是在gradle.propertles文件中，在这里配置

后台环境现在分为dev，test，prod ，也是在这个配置文件里配置

使用的时候 ，获取BuildConfig.SERVER_HOST的值就可以，
    ApiManager apiManager = new ApiManager(BuildConfig.SERVER_HOST);
    
本地切换版本在 build variants里的app下选择自己要编译的指定后台的版本

发布是用gradle clean build，会生成三个后台环境的版本，选择prod的release版本即可

### 更新说明
jdk 使用1.8
gradle 使用版本4.1进行编译，使用时请升级android studio到3.0的正式版本来配置工程
android编译版本 26.0.2
kotlin使用 1.1.51
databind 使用最新的gradle写法
constraint-layout使用1.0.2版本

