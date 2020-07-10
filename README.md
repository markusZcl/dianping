# 模仿大众点评项目

## 项目结构

```
├── canal  -- canal相关配置和操作
│
├── Common -- 公共类
│
├── config -- 配置信息
│
├── controller -- 控制器
|    ├── admin -- 后台管理员控制器
│	
├── service -- 业务逻辑接口
|    ├── impl -- 业务逻辑接口实现类
│
├── dal -- 数据访问接口
│
├── model--  数据持久化实体类
│
├── recommend -- 推荐逻辑
│
│
├── request -- 响应实体类
|    
│
├── resources
|    ├── mapping -- SQL对应的XML文件
|    ├── static -- 项目静态资源文件
|    ├── templates -- 后台管理界面
```

## 技术选型

- 后端业务：jdk8，SpringBoot 2.1.6
- 后端存储：MySQL数据库(5.6) MyBatis 1.3.1接入 连接池采用Druid
- 搜索系统：Elasticsearch分布式搜索引擎(7.3.0) canal(1.1.4)
- 推荐系统：Spark Mllib机器学习组件(2.4.4)
- 前端页面：html,css,js,jquery

## 演示效果图

### 前端页面

![](https://gitee.com/markuszcl99/images/raw/master/20200710103814.png)

提供功能用户登陆注册、分类查询商品、门店搜索、猜你喜欢

### 后台管理界面

####登陆界面

![](https://gitee.com/markuszcl99/images/raw/master/20200710103925.png)

#### 管理界面

![](https://gitee.com/markuszcl99/images/raw/master/20200710103953.png)

![](https://gitee.com/markuszcl99/images/raw/master/20200710104015.png)

![](https://gitee.com/markuszcl99/images/raw/master/20200710104040.png)

![](https://gitee.com/markuszcl99/images/raw/master/20200710104101.png)







