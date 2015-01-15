footer: © 2015 Matt Stine
slidenumbers: true

# [fit] Building Distributed Systems with
# [fit] Netflix OSS
# [fit] and
# [fit] Spring Cloud
![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)

---

![left](../Common/images/mattmug.jpeg)
# Me

Matt Stine ([@mstine](http://twitter.com/mstine))
Senior Product Manager
Pivotal
[http://www.mattstine.com](http://www.mattstine.com)
[matt.stine@gmail.com](mailto:matt.stine@gmail.com)

---

# There Seems to Be Some Hype...
![](../Common/images/unicorn.jpg)

---

# Define: Microservice
> Loosely coupled service oriented architecture with bounded contexts...
-- Adrian Cockcroft

---

# [fit] Spring Boot
# [fit] A Microframework for Microservices
![](https://raw.githubusercontent.com/spring-projects/spring-boot/gh-pages/img/project-icon-large.png)

---

# It Can Get Pretty Small...

```java
@RestController
class ThisWillActuallyRun {
  @RequestMapping("/")
  String home() {
    "Hello World!"
  }
}
```

---

# [fit] DEMO

---

# With Spring Data REST!

```java
@Entity
@Table(name = "city")
public class City implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String county;

  //...

}
```

---

# With Spring Data REST!

```java
@RepositoryRestResource(collectionResourceRel = "cities", path = "cities")
public interface CityRepository extends PagingAndSortingRepository<City, Long> {}
```

---

# With Spring Data REST!

```java
@SpringBootApplication
@EnableJpaRepositories
@Import(RepositoryRestMvcConfiguration.class)
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
```

---

# With Spring Data REST!
```
{
  "_links" : {
    "next" : {
      "href" : "http://localhost:8080/cities?page=1&size=20"
    },
    "self" : {
      "href" : "http://localhost:8080/cities{?page,size,sort}",
      "templated" : true
    }
  },
  "_embedded" : {
    "cities" : [ {
      "name" : "HOLTSVILLE",
      "county" : "SUFFOLK",
      "stateCode" : "NY",
      "postalCode" : "00501",
      "latitude" : "+40.922326",
      "longitude" : "-072.637078",
```

---

# [fit] DEMO

---

# [fit] Writing a Single Service is
# [fit] Nice...

---

# [fit] But No Microservice
# [fit] is an Island
![](../Common/images/island-house.jpg)

---

# Challenges of Distributed Systems

* Configuration Management
* Service Registration & Discovery
* Routing & Load Balancing
* Fault Tolerance (Circuit Breakers!)
* Monitoring
* Concurrent API Aggregation & Transformation

---

![](../Common/images/netflix_oss.jpeg)

---

![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)
# [fit] Spring Cloud
# [fit] Distributed System Patterns FTW!

---

![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)
# [fit] Configuration
# [fit] Management

---

# Spring Environment

* Properties
* Profiles

---

# `app.groovy`

```java
@RestController
class BasicConfig {

  @Value('${greeting}')
  String greeting

  @RequestMapping("/")
  String home() {
    "${greeting} World!"
  }
}
```

---

# `application.yml`

```
greeting: Hello
```

---

# [fit] DEMO

---

# Boot Priority

1. Command Line Args
1. JNDI
1. Java System Properties
1. OS Environment Variables
1. Properties Files
1. `@PropertySource`
1. Defaults

---

# [fit] DEMO

^ Show GREETING=Ohai spring run app.groovy

---

# [fit] Profiles

---

# `application.yml`

```
greeting: Hello

---

spring:
  profiles: spanish
greeting: Hola
```

---

# [fit] DEMO

^ Show SPRING_PROFILES_ACTIVE=spanish spring run app.groovy

^ Also show GREETING=Ohai SPRING_PROFILES_ACTIVE=spanish spring run app.groovy

---

# [fit] Distributed?

---

![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)
# [fit] Config
# [fit] Server!

---

# Config Server `app.groovy`

```java
@Grab("org.springframework.cloud:spring-cloud-starter-bus-amqp:1.0.0.RC1")
@Configuration
@EnableAutoConfiguration
@EnableConfigServer
class ConfigServer {
}
```

---

# Config Server `application.yml`

```
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/mstine/config-repo.git
```

---

![](../Common/images/github.jpeg)

# `https://github.com/mstine/config-repo/blob/master/demo.yml`

```
greeting: Bonjour
```

---

# Config Client `app.groovy`

```java
@Grab("org.springframework.cloud:spring-cloud-starter-bus-amqp:1.0.0.RC1")
@RestController
class BasicConfig {

  @Autowired
  Greeter greeter

  @RequestMapping("/")
  String home() {
    "${greeter.greeting} World!"
  }
}

@Component
@RefreshScope
class Greeter {

  @Value('${greeting}')
  String greeting

}
```

---

# Config Client `bootstrap.yml`

```
spring:
  application:
    name: demo
```

---

# [fit] DEMO

---

![right fit](../Common/images/rabbitmq.png)
# [fit] Cloud
# [fit] Bus!

---

# [fit] `curl -X POST http://localhost:8888/bus/refresh`

---

# [fit] DEMO

^ Change greeting in demo.yml to Howdy

^ git commit/push

^ Show greeting

^ Trigger refresh

^ Show greeting again!

---

![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)
# [fit] Service
# [fit] Registration &
# [fit] Discovery

---

# [fit] Eureka
![](../Common/images/netflix_oss.jpeg)

---

# [fit] Producer
# [fit] Consumer

---

# Eureka Service Registry

```java
@GrabExclude("ch.qos.logback:logback-classic")
@EnableEurekaServer
class Eureka {
}
```

---

# Producer

```java
@EnableDiscoveryClient
@RestController
public class Application {

  int counter = 0

  @RequestMapping("/")
  String produce() {
    "{\"value\": ${counter++}}"
  }
}
```

---

# Consumer

```java
@EnableDiscoveryClient
@RestController
public class Application {

  @Autowired
  DiscoveryClient discoveryClient

  @RequestMapping("/")
  String consume() {
    InstanceInfo instance = discoveryClient.getNextServerFromEureka("PRODUCER", false)

    RestTemplate restTemplate = new RestTemplate()
    ProducerResponse response = restTemplate.getForObject(instance.homePageUrl, ProducerResponse.class)

    "{\"value\": ${response.value}"
  }
}

public class ProducerResponse {
  Integer value
}
```

---

# [fit] DEMO

---

![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)
# [fit] Routing &
# [fit] Load Balancing

---

# [fit] Ribbon
![](../Common/images/netflix_oss.jpeg)

---

# Consumer with Load Balancer

```java
@Autowired
LoadBalancerClient loadBalancer

@RequestMapping("/")
String consume() {
  ServiceInstance instance = loadBalancer.choose("producer")
  URI producerUri = URI.create("http://${instance.host}:${instance.port}");

  RestTemplate restTemplate = new RestTemplate()
  ProducerResponse response = restTemplate.getForObject(producerUri, ProducerResponse.class)

  "{\"value\": ${response.value}"
}
```

---

# [fit] DEMO

---

# Consumer with Ribbon-enabled `RestTemplate`

```java
@Autowired
RestTemplate restTemplate

@RequestMapping("/")
String consume() {
  ProducerResponse response = restTemplate.getForObject("http://producer", ProducerResponse.class)

  "{\"value\": ${response.value}"
}
```

---

# [fit] DEMO

---

# Feign Client

```java
@FeignClient("producer")
public interface ProducerClient {

  @RequestMapping(method = RequestMethod.GET, value = "/")
  ProducerResponse getValue();
}
```

---

# Consumer with Feign Client

```java
@SpringBootApplication
@FeignClientScan
@EnableDiscoveryClient
@RestController
public class Application {

  @Autowired
  ProducerClient client;

  @RequestMapping("/")
  String consume() {
    ProducerResponse response = client.getValue();

    return "{\"value\": " + response.getValue() + "}";
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
```

---

# [fit] Demo

---


![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)
# [fit] Fault
# [fit] Tolerance

---

# [fit] Hystrix
![](../Common/images/netflix_oss.jpeg)

---

![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)
# [fit] Monitoring

---

# [fit] Hystrix
# [fit] Dashboard
![](../Common/images/netflix_oss.jpeg)

---

![](https://raw.githubusercontent.com/spring-projects/spring-cloud/gh-pages/img/project-icon-large.png)
# [fit] Concurrent
# [fit] API
# [fit] Aggregation &
# [fit] Transformation

---

# [fit] RxJava
![](../Common/images/netflix_oss.jpeg)

---

# Image Credits

* http://i.imgur.com/atz81.jpg
* http://theroomermill.net/wp-content/uploads/2014/06/island-house.jpg
