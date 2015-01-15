import org.springframework.web.client.RestTemplate
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.cloud.client.ServiceInstance

@EnableDiscoveryClient
@RestController
public class Application {

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
}

public class ProducerResponse {
  Integer value
}
