import org.springframework.web.client.RestTemplate

@EnableDiscoveryClient
@RestController
public class Application {

  @Autowired
  RestTemplate restTemplate

  @RequestMapping("/")
  String consume() {
    ProducerResponse response = restTemplate.getForObject("http://producer", ProducerResponse.class)

    "{\"value\": ${response.value}"
  }
}

public class ProducerResponse {
  Integer value
}
