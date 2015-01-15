package consumerFeign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.FeignClientScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
