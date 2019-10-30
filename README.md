[toc]
# 1.认识

* 拆分单体应用，解耦和。![2b483ef3a3e43cc73bf6f8db9126fadc.png](en-resource://database/2614:1)
![c57eb2252e50b2085fb67a52e49a945d.png](en-resource://database/2616:1)

* spring cloud架构
![773d96421c4212a8dfc6f73f6fe7501f.png](en-resource://database/2618:1)

    * Eureka Serverr:注册中心
    *  Ribbon/FeinClient完成负载均衡
    *  Zuul实现网关
    *  Hystrix 熔断器
*   核心组件：
![bc8976828fb0cf8699aec0f9afbe1dfa.png](en-resource://database/2620:1)

# 2. 搭建项目
## 2.1 服务治理
* 服务治理：服务提供者-服务消费者-注册中心
    * 功能：服务注册、服务发现
    * spring cloud 集成Eureka实现服务治理。![41845e42080bda4022c717bfd976d928.png](en-resource://database/2622:1)
    

* 服务状态：up 可用 ；down 不可用

### 2.1.1 搭建Eureka Server注册中心

* eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成），在默认情况下erureka server也是一个eureka client ,必须要指定一个 server

* * *

* 创建maven项目
* 引入springboot依赖、springcloud 依赖
```
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.7.RELEASE</version>
    </parent>

    <!--加入springboot依赖-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

<!--    加入spring cloud依赖-->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Finchley.SR2</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```
* 创建EurekaServer模块
* 引入Eureka依赖
```
<dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>
    </dependencies>
```
* 添加EurekaServer配置
```
server :
  port : 8761 # 当前eureka server的服务端口
eureka:
  client:
    register-with-eureka: true #不允许注册eureka自己，不把自己当做微服务注册
    fetch-registry: false # 不同步其他注册中心的数据
    service-url:
      defaultZone : http://localhost:8761/eureka # 默认注册中心的访问地址

从其他地方看来的配置写法-斜杠变成大写

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

通过eureka.client.registerWithEureka：false和fetchRegistry：false来表明自己是一个eureka server.
```
* 创建spring-boot启动类，启动
```
@SpringBootApplication //声明该类是springboot服务的入口
@EnableEurekaServer //声明该类是eurekaServer微服务，提供服务注册和服务发现功能，即注册中心。
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class,args );
    }
}
```
### 2.1.2 搭建EurekaClient客户端

* 引入客户端依赖
```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>
```

* 修改配置
```
server :
  port : 8010
spring : # 当前服务在注册中心的名字
  application:
    name : provider
eureka :
  client:
    service-url:
      defaultZone :  http://localhost:8761/eureka # 注册中心访问地址
  instance:
    prefer-ip-address: true #是否将当前服务的ip注册到Eureka中心

```

* 另一种方式创建客户端：注解@EnableEurekaClient 表明自己是一个eurekaclient.
```

@SpringBootApplication@EnableEurekaClient@RestControllerpublic class ServiceHiApplication {

        public static void main(String[] args) {
                SpringApplication.run(ServiceHiApplication.class, args);
        }

        @Value("${server.port}")
        String port;
        @RequestMapping("/hi")
        public String home(@RequestParam String name) {
                return "hi "+name+",i am from port:" +port;
        }

}


eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

### RestTemplate的使用

* RestTemplate是spring基于REST的服务组件，对http的请求及访问进行了封装，提供了很多访问rest服务的方法。
* 使用
    * 在启动类添加resttempalte
    
```
@SpringBootApplication
public class RestTemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestTemplateApplication.class,args   );
    }

    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
```

* 在controller通过rest风格的URL访问，转发请求，获取数据
    * RestController 声明是rest风格的
    * GetMapping：处理get请求查询，直接写要拿的内容。有参数写在{}里面；用PathVariable获取在路径中的数据。--restTemplate使用的方法是getForEntity或者getForObject
    * PostMapping:提交多个json数据，可以用于保存。用RequestBody将提交的json改成实体类--restTemplate使用的方法是postForEntity或者postForObject
 
    * PutMapping:修改操作。用RequestBody将提交的json改成实体类restTemplate使用的方法是put
    * DeleteMapping:删除操作，有参数写在{}里面。--restTemplate使用的方法是delete


```
@RestController
@RequestMapping("/rest")
public class RestController {
    @Autowired
    private RestTemplate restTemplate;
    @GetMapping("/findAll")
    public Collection<Student> findAll() {
        return restTemplate.getForObject("http://localhost:8010/student/findALL", Collection.class);
    }
    @GetMapping("/findAll2")
    public Collection<Student> findAll2() {
        return restTemplate.getForEntity("http://localhost:8010/student/findALL", Collection.class).getBody();
    }
    @GetMapping("/findById/{id}")
    public Student findById(@PathVariable("id") long id){
        return  restTemplate.getForObject("http://localhost:8010/student/findById/{id}",Student.class,id);
    }
    @GetMapping("/findById2/{id}")
    public Student findById2(@PathVariable("id") long id){
        return  restTemplate.getForEntity("http://localhost:8010/student/findById/{id}",Student.class,id).getBody();
    }
    @PostMapping("/save")
    public void save(@RequestBody Student student){
        restTemplate.postForEntity("http://localhost:8010/student/save",student,null);
    }
    @PostMapping("/save2")
    public void save2(@RequestBody Student student){
        restTemplate.postForObject("http://localhost:8010/student/save",student,null);
    }
    @PostMapping("/update")
    public void update(@RequestBody Student student){
        restTemplate.postForObject("http://localhost:8010/student/update",student,null);
    }
    @PostMapping("/update2")
    public void update2(@RequestBody Student student){
        restTemplate.postForEntity("http://localhost:8010/student/update",student,null);
    }
    @DeleteMapping("/deleteById/{id}")
    public void deleteById(@PathVariable("id") long id ){
        restTemplate.delete("http://localhost:8010/student/delete",id);
    }
}
```

###  服务消费者

* 新疆maven项目，设置为Eureka-client,使用restful方式调服务生产者的接口，获取或者修改数据。


## 2.2 服务网关-Zuul

* 一个业务需要调多个服务协同完成一个请求，存在许多文档
    * 记住多个地址，改了怎么办
    * 跨域请求
    * 多个服务进行登录验证。。。

* 添加一个api网关。消费者跟网关交互，网关跟各个服务交互。提供统一的入口。
 
![71970b9e41d7ec249be07213d19599be.png](en-resource://database/2626:1)

* 可以把公共的内容放到网关完成：
    * 乱码解决
    * 登录验证

* spring cloud集成了zuul实现服务网关。Zuul是netflix提供 的一个开源的网关服务器。
    * 是客户端和网站后端所有请求的中间层，对外开放一个api，将所有的请求导入统一入口，屏蔽了服务端的具体实现逻辑，zuul可以实现反向代理的功能，在网关内部实现动态路由。身份认证、IP过滤、数据监控等。

* Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。

### 2.2.1 使用

* 添加依赖
```
 <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>
```

* 修改配置
```
server:
  port: 8030
spring:
  application:
    name: geteway

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8671/eureka

zuul:
  routes:
    provider: /p/**  #provider是服务提供者的名字。/p是为他起的别名、做的映射，使用的时候通过网关请求这个就行了。
```

* 启动类
```

@EnableZuulProxy//包含了@enableZuulServer，设置该类是网关的启动类
@EnableAutoConfiguration//可以帮助springboot应用将所有符合条件的@Configuration配置加载到当前springboot创建并使用的IOC容器中
public class ZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class,args);
    }
}
```

* 使用网关的方法：
http://localhost:8030/p/student/findALL；只需要记接口和网关的地址、映射名称
### 2.2.2 Zuul自带负载均衡功能

* 不需要配置，将服务名称设置成一样的，在映射的时候会自己逐个转发。

* 在Spring Cloud微服务系统中，一种常见的负载均衡方式是，客户端的请求首先经过负载均衡（zuul、Ngnix），再到达服务网关（zuul集群），然后再到具体的服务。

### 2.2.3 服务过滤
```

@Componentpublic class MyFilter extends ZuulFilter{

    private static Logger log = LoggerFactory.getLogger(MyFilter.class);
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        Object accessToken = request.getParameter("token");
        if(accessToken == null) {
            log.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("token is empty");
            }catch (Exception e){}

            return null;
        }
        log.info("ok");
        return null;
    }}
```

* **继承自ZuulFilter**
* filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
    * pre：路由之前routing：路由之时
    * post： 路由之后
    * error：发送错误调用
*  filterOrder：过滤的顺序
*  shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
*  run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。

## 2.3用Ribbon实现负载均衡

* 是什么？
    * spring cloud的组件，负载均衡的解决方案。Netflix 发布的负载均衡器，是一个用于对http请求进行控制的负载均衡客户端。
    * 在注册中心对Ribbon注册之后，ribbon可以基于某种某种负载均衡算法，如轮询、随机、加权轮询、加权随机等自动帮助服务消费者调用接口，也可以自定义ribbon负载均衡算法。
    * ribbon需要结合eureka使用。eureka server提供可以调用的服务提供者列表，ribbon基于特定负载均衡算法从这些服务提供者中选择要调用的具体实例。

* 请求时用服务名替代ip地址和端口号：http://provider/student/findALL



![3072decc25dabfb8c3d7fcc29424a3e8.png](en-resource://database/2628:1)

* ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign默认集成了ribbon。ribbon 已经默认实现了这些配置bean：
    * IClientConfig ribbonClientConfig: DefaultClientConfigImp
    * lIRule ribbonRule: ZoneAvoidanceRule
    * IPing ribbonPing: NoOpPing
    * ServerList ribbon ServerList: ConfigurationBasedServerList
    * ServerListFilter ribbon ServerListFilter: ZonePreferenceServerListFilter
    * ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer

### 2.3.1 使用

* 新建项目，引入eureka-client依赖；
* 修改配置文件，到eureka服务器端注册服务
```
server:
  port: 8040
spring:
  application:
    name: ribbon

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

* 创建启动类，添加restTemplate，并为其添加loadbanlance注解，实现负载均衡。
```
@SpringBootApplication
public class RibbonApplication {
    public static void main(String[] args) {
        SpringApplication.run(RibbonApplication.class,args);
    }

    @Bean
    @LoadBalanced//实现负载均衡，声明一个基于ribbon的负载均衡
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

* 在工程的启动类中,可以通过@EnableDiscoveryClient向服务中心注册；并且向程序的ioc注入一个bean: restTemplate;并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。

### 2.3.2 在ribbon中使用断路器

* 首先在pox.xml文件中加入spring-cloud-starter-hystrix的起步依赖
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
    </dependency>
```

* 在程序的启动类ServiceRibbonApplication 加@EnableHystrix注解开启Hystrix；
* 改造调用restTemplate方法的类方法，方法上加上@HystrixCommand注解。该注解对该方法创建了熔断器的功能，并指定了fallbackMethod熔断方法，熔断方法直接返回了一个字符串，字符串为”hi,”+name+”,sorry,error!”
```

@Servicepublic class HelloService {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "hiError")
    public String hiService(String name) {
        return restTemplate.getForObject("http://SERVICE-HI/hi?name="+name,String.class);
    }

    public String hiError(String name) {
        return "hi,"+name+",sorry,error!";
    }}
```


##  2.4 Feign--取代ribbon

* 由Netflix提供的，Feign 是一个声明式、模板化的web service客户端，简化了编写Web服务客户端的操作，开发者可以通过简单的接口和注解来调用httpapi。
* springcloud Feign整合了ribbon和Hystrix，具有可插拔、基于注解。负载均衡、服务熔断等一系列便捷的功能
* 相比较于ribbon+restTemplate的方式，Feign简化了代码的开发，支持多种注解，包括Feign注解、JAX-RS 注解、SpringMVC注解等。
* spring cloud 对Feign进行了优化，整合了ribbon和eureka，让Feign使用更将方便。

* Ribbon 是一个通用的http客户端，feign是基于ribbon实现的。
* 声明式：只需要添加注解，不需要写实现类
* Feign特点：
    * Feign 是一个声明式的webservice客户端
    * 支持多种注解
    * 基于ribbon实现，使用更加简单
    * 集成了hystrix，具备服务熔断的功能。

* 补充资料：
    * 在微服务架构中，根据业务来拆分成一个个的服务，服务与服务之间可以相互调用（RPC），在Spring Cloud可以用RestTemplate+Ribbon和Feign来调用。为了保证其高可用，单个服务通常会集群部署。
    * 由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，这就是服务故障的“雪崩”效应。
    * 为了解决这个问题，业界提出了断路器模型。
    * 当对特定的服务的调用的不可用达到一个阀值（Hystric 是5秒20次） 断路器将会被打开。断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值。
![f66a5371f67972f23c283a2209ed9efa.png](en-resource://database/2632:1)


### 2.4.1 使用feign

* 创建项目，引入依赖
```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>
```

* 修改配置，在服务中心注册
```
server:
  port: 8050
spring:
  application:
    name: feign
eureka:
  client:
    service-url:
      defaultZone : http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
```

* 创建启动类，添加EnableFeignClients注解
```
@SpringBootApplication
@EnableFeignClients
public class FeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignApplication.class,args );
    }
}
```

* 创建接口，运用声明式直接调服务
```
@FeignClient(value = "provider")//声明式不需要写实现，直接用注解。这里写要调用的服务名
public interface FeignProviderClient {
    @GetMapping("/student/findALL")//这里写要调用服务的路径
    public Collection<Student> getAll();
    @GetMapping("/student/index")
    public String index();
}
```

* 创建controller，调用接口请求数据
```
@RestController
@RequestMapping("/feign")
public class FeignHandler {

    @Autowired
    private FeignProviderClient client;

    @GetMapping("/findAll")
    public Collection<Student> getAll(){
        return  client.getAll();
    }
    @GetMapping("/index")
    public String index(){
        return client.index();
    }
}
```
### 2.4.2 自带熔断机制

* 通过降级或其他手段保证提供服务。
**步骤如下：**
* 开启feign的熔断机制--feign.hystrix.enabled.true
* 创建FeignProviderClient接口的实现类Feignerror,定义容错处理逻辑，通过@Component·注解将FerginError实例注入IOC容器中。
```
@Component
public class FeignError implements FeignProviderClient {
    @Override
    public Collection<Student> getAll() {
        return null;
    }
    @Override
    public String index() {
        return "服务器维护中";
    }
}
```

* 在FeignProviderClient 通过FeignClient的fallback属性设置映射。
```
@FeignClient(value = "provider",fallback = FeignError.class)/

```

## 2.5 Hystrix 容错机制

* 在不改变各个微服务调用关系的前提下，针对错误情况进行预先处理。
* 设计原则：
    * 服务隔离机制--防止一个服务有问题影响其他
    * 服务降级机制--服务有问题返回fallback
    * 熔断机制--请求失败多少次启用熔断机制对错误进行修复
    * 实时监控报警 --监控需要结合springcloud actuator来使用。
    * 实时配置修改

Hystrix数据监控需要结合spring boot Actuator来使用，Actuator提供了对服务的健康监控、数据统计，可以通过hystrix-stream节点获取监控的请求数据，提供了可视化的监控画面 


* 在微服务架构中为例保证程序的可用性，防止程序出错导致网络阻塞，出现了断路器模型。断路器的状况反应了一个程序的可用性和健壮性，它是一个重要指标。Hystrix Dashboard是作为断路器状态的一个组件，提供了数据监控和友好的图形化界面

### 2.5.1 使用

* 引入依赖
```
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>2.0.7.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>
        <!--可视化组件-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
            <version>2.0.2.RELEASE</version>
        </dependency>
    </dependencies>
```

* 配置yml文件
```
server:
  port: 8060
spring:
  application:
    name: hystrix
eureka:
  client:
    service-url:
      defaultZone : http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
feign:
  hystrix:
    enabled: true
#配置监控
management:
  endpoints:
    web:
      exposure:
        include: "hystrix.stream"#配置监控的节点
```

* 创建启动项
```
@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker//声明启用数据监控
@EnableHystrixDashboard//声明启用可视化的数据监控
public class HystrixApplication {
    public static void main(String[] args) {
        SpringApplication.run(HystrixApplication.class,args);
    }
}

```

* 打开地址 http://localhost:8060/actuator/hystrix.stream，可以不断刷新的数据监控；
* 打开 http://localhost:8060/hystrix，输入要监控的地址，可以看见可视化的数据。
* ![1e44302ec74af862de1b7f3c8342983c.png](en-resource://database/2630:1)
## 2.6. spring cloud config  -- 配置中心

* 通过服务端为多个客户端提供配置服务，将配置文件存储在本地 或者远程git仓库。
* 通过config server服务端管理所有的配置文件。本地修改、推送到远程。

### 2.6.1 本地配置

* 需要创建2个模块，一个本地配置中心，一个功能模块，从配置中心读取配置数据进行配置。

* 创建配置服务中心模块：依赖、配置、启动项
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
    <version>2.0.2.RELEASE</version>
</dependency>


server:
  port: 8762
spring:
  application:
    name: nativeconfigserver
  profiles:
    active: native #从本地读取配置文件-配置文件获取方式
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/shared # 本地配置文件保存的路径

@SpringBootApplication
@EnableConfigServer//声明配置中心
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class,args);
    }
}

```

* 在resources目录下创建shared目录，新建yml配置文件，对应上面的search-locations。给需要的项目调用配置
```
server:  
    port: 8070
foo: foo version 1
```

* 创建读取配置的客户端：
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
    <version>2.0.2.RELEASE</version>
</dependency>

@SpringBootApplication
public class NativeConfigClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(NativeConfigClientApplication.class,args);
    }

}
```

* 添加客户端的配置文件 bootstrap.yml，要求必须是这个名字
* 使用sping.application.name - spring.profiles.active拼接要调用yml文件的名字
```
spring:
  application:
    name: configclient
  profiles:
    active: dev # name-active 拼接目标配置文件名。在config-server中查找
  cloud:
    config:
      uri: http://localhost:8762 #本地configserver的访问路径
      fail-fast: true #设置客户端优先判断config-server是否正常，
```

* 创建controller填写测试
```
@RestController
@RequestMapping("/native")
public class NativeConfigHandler {
    @Value("${server.port}")
    private String port;
    @Value("{foo}")
    private String foo;

    @GetMapping("/index")
    public  String index(){
        return this.port+"--"+this.foo;
    }
}
```

### 2.6.2 远程配置

* config server推到远程仓库，仓库读取文件。
