#### **说明：**

##### 目录说明：

- rpc-common：包含model、Service服务接口
- rpc-consumer：rpc框架消费者服务
- rpc-core：rpc框架核心内容，容错机制、重试、注册中心、配置、负载均衡、TCP服务器等
- rpc-easy：简易rpc框架，仅包含客户端、服务器的调用，不含注册中心等core模块扩展内容
- rpc-provider：rpc框架服务提供者，在Spring Boot 项目中使用
- example-springboot-starter：注解驱动的 rpc框架，可在 Spring Boot 项目中快速使用
- example-springboot-consumer：封装为starter后Spring Boot 框架消费者调用流程demo
- example-springboot-provider：封装为starter后Spring Boot 框架服务提供注册服务流程demo



##### 相关环境

**环境：JDK 17**

**安装相关软件：Etcd注册中心、Vertx服务器、zookeeper注册中心等**



****



### 系统架构

TODO：

<img src="https://github.com/Song246/RPC/blob/master/架构图.png">



