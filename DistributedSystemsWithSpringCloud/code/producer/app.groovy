@EnableDiscoveryClient
@RestController
@Log
public class Application {

  int counter = 0

  @RequestMapping("/")
  String produce() {
    counter++
    log.info("Produced a value: ${counter}")

    "{\"value\": ${counter}}"
  }
}
