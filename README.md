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

#### 登陆界面

![](https://gitee.com/markuszcl99/images/raw/master/20200710103925.png)

#### 管理界面

![](https://gitee.com/markuszcl99/images/raw/master/20200710103953.png)

![](https://gitee.com/markuszcl99/images/raw/master/20200710104015.png)

![](https://gitee.com/markuszcl99/images/raw/master/20200710104040.png)

![](https://gitee.com/markuszcl99/images/raw/master/20200710104101.png)



# 一、搜索推荐基础

## 距离计算：

1. 需要接入百度地图sdk通过api获取用户当前经纬度传给服务端
2. 利用球体平面计算公式，在sql语句层面计算出用户和门店之间的距离
3. 对应距离数值可集成到门店模型内用于展示或后续的排序

## 应用程序集成

1 通过spring mvc集成搜索和推荐的controller，service和dao

2 完成前端页面闭环

# 二、推荐1.0结构

## 原理：

通过数据库的线性计算公式给门店打分后排序输出给用户

## 不足：

1. 线性计算公式，人的理解和实际计算操作过程有误差，线性计算公式没有办法很好的满足实际的需求
2. 没有考虑人的特点（当前距离除外），没有办法做到个性化的千人千面推荐

# 三、搜索1.0结构

## 原理：

通过数据库的关键词模糊匹配的方式结合线性计算公式给门店打分后排序输出给用户

## 不足：

1. 关键词模糊匹配，最基础的文本匹配方式，没办法考虑分词，中文特性，用户深层次需求等，连最基本的分词模糊匹配都无法完成。
2. 和推荐一样的线性排序计算公式，人的理解和实际计算操作过程有误差，线性计算公式没有办法很好的满足实际的需求
3. 数据库做搜索性能很差
