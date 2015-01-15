@Grab("org.springframework.cloud:spring-cloud-starter-hystrix:1.0.0.RC1")
@Grab("org.springframework.cloud:spring-cloud-starter-eureka:1.0.0.BUILD-SNAPSHOT")

import org.springframework.web.client.RestTemplate
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand

@EnableDiscoveryClient
@EnableCircuitBreaker
@RestController
public class Application {

  @Autowired
  ProducerClient client

  @RequestMapping("/")
  String consume() {
    ProducerResponse response = client.getProducerResponse()

    "{\"value\": ${response.value}"
  }

}

@Component
public class ProducerClient {

  @Autowired
  RestTemplate restTemplate

  @HystrixCommand(fallbackMethod = "getProducerFallback")
  ProducerResponse getProducerResponse() {
    restTemplate.getForObject("http://producer", ProducerResponse.class)
  }

  ProducerResponse getProducerFallback() {
    new ProducerResponse(value: 42)
  }
}

public class ProducerResponse {
  Integer value
}
