# dubbo & zookeeper

[zookeeper download](https://www.apache.org/dyn/closer.lua/zookeeper/zookeeper-3.6.1/apache-zookeeper-3.6.1-bin.tar.gz)

[doubbo admin download](https://github.com/apache/dubbo-admin)

## zookeeper

1. 解压zookeeper压缩包

2. 在confi文件夹中复制一份名为：`zoo_sample.cfg`的配置文件，保存在当前文件夹下，改名为`zoo.cfg`,修改里面的配置`dataDir`

   ```properties
   #在上一层目录记得创建该文件夹
   dataDir=../data
   
   #zookeeper 3.5.5版本中包含一个AdminServer默认的端口是8080，会和dubbo-admin-server的端口冲突，所以改成2333
   server.port=2333
   ```

   

3. 在`bin`文件下启动`zkServer.cmd`服务

4. 在`bin`文件下启动`zkCli.cmd`进行测试

   ```cmd
   #查看节点
   ls /
   
   #创建一个节点
   create -e /pengan 456789456
   
   #查看节点内容
   get /pengan
   ```

## doubbo admin

> 监控中心，可以让我们更方便的管理和维护众多的服务（当然，我们不安装它也不会影响到我们的正常使用）

1. 下载完后先在`dubbo-admin-server`目录下构建jar包(该命令表示打包时跳过测试阶段，不然会报错，无法构建成功)

   ```cmd
   mvn clean package -D maven.test.skip=true
   
   java -jar .\dubbo-admin-server-0.2.0-SNAPSHOT.jar
   ```

   

2. 在`dubbo-admin-ui`中运行（因为这是一个前后端分离的项目）

   ``````
   npm install
   
   npm run
   ``````

3. 在打开http://localhost:8081/，进入admin-ui的页面

## Dubbo

![dubbo architecture](https://dubbo.apache.org/img/architecture.png)

1. 让服务提供者注册到注册中心

   1. 由于我们使用的是zooKeeper作为注册中心，所以需要引入zooKeeper客户端。

      > dubbo 2.6版本以前的版本引入zkclient操作zooKeeper  
      >
      > dubbo 2.6及之后的版本引入curator操作zooKeeper 

      ```pom
         <!-- https://mvnrepository.com/artifact/com.alibaba/dubbo -->
              <dependency>
                  <groupId>com.alibaba</groupId>
                  <artifactId>dubbo</artifactId>
                  <version>2.6.8</version>
              </dependency>
      
      <!-- https://mvnrepository.com/artifact/org.apache.curator/curator-framework -->
      <dependency>
          <groupId>org.apache.curator</groupId>
          <artifactId>curator-framework</artifactId>
          <version>4.3.0</version>
      </dependency>
      ```

   2. 创建一个`provider.xml`配置文件

      ```xml
      <?xml version="1.0" encoding="UTF-8"?>
      <beans xmlns="http://www.springframework.org/schema/beans"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
             xsi:schemaLocation="http://www.springframework.org/schema/beans
             http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
             http://dubbo.apache.org/schema/dubbo
             http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
      
      
      <!--    当前服务的名称-->
          <dubbo:application name="dubbo-learn-provider"></dubbo:application>
      
      <!--    注册在zooKeeper中-->
          <dubbo:registry protocol="zookeeper" address="127.0.0.1:2181" />
      
          <!-- 通信协议及端口：用dubbo协议在20880端口暴露服务 -->
          <dubbo:protocol name="dubbo" port="20080"></dubbo:protocol>
      
      <!--    对外暴露的服务接口-->
          <dubbo:service interface="cn.pengan.service.IBookService" ref="booService"></dubbo:service>
      
      <!--    服务的实现bean-->
          <bean id="booService" class="cn.pengan.service.impl.BookServiceImpl"></bean>
      </beans>
      ```

   3. 编辑服务启动类

      ```java
      package cn.pengan;
      
      import org.springframework.context.support.ClassPathXmlApplicationContext;
      
      import java.io.IOException;
      
      public class MainApplication {
          public static void main(String[] args) throws IOException {
              ClassPathXmlApplicationContext ioc = new ClassPathXmlApplicationContext("provider.xml");
              ioc.start();
              //防止程序结束（按任意键结束）
              System.in.read();
          }
      }
      
      ```

   4. 在dubbo admin中查看服务，既可以看到我们注册的提供者服务

2. 让服务消费者去注册中心订阅服务提供者的服务地址

    1. 在消费者导入与提供者服务相同的两个jar包

    2. 配置消费者服务配置

       ```xml
       <?xml version="1.0" encoding="UTF-8"?>
       <beans xmlns="http://www.springframework.org/schema/beans"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
              xsi:schemaLocation="http://www.springframework.org/schema/beans
              http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
              http://dubbo.apache.org/schema/dubbo
              http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
       
           <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
           <dubbo:application name="dubbo-learn-consumer"></dubbo:application>
           <!--zookeeper注册中心-->
           <dubbo:registry protocol="zookeeper"  address="127.0.0.1:2181"></dubbo:registry>
           <!-- 生成远程服务代理，可以和本地bean一样使用demoService -->
           <dubbo:reference id="bookService" interface="cn.pengan.service.IBookService"></dubbo:reference>
       </beans>
       ```

      	3. 编写消费者服务启动类

       ```java
       package cn.pengan;
       
       import cn.pengan.pojo.Book;
       import cn.pengan.service.IBookService;
       import org.springframework.context.support.ClassPathXmlApplicationContext;
       
       import java.io.IOException;
       import java.util.List;
       
       public class MainConsumerApplication {
           public static void main(String[] args) throws IOException {
               ClassPathXmlApplicationContext ioc = new ClassPathXmlApplicationContext("consumer.xml");
               ioc.start();
               IBookService bookService = (IBookService)ioc.getBean("bookService");
               List<Book> allBook = bookService.findAllBook();
               allBook.forEach(System.out::println);
               System.in.read();
           }
       }
       
       ```

      	4. 即可在控制台中查看到获取的数据