@EnableDiscoveryClient
@RestController
@Log
public class Application {

  int counter = 0

  @RequestMapping(value = "/", produces = "application/json")
  String produce() {
    counter++
    log.info("Produced a value: ${counter}")

    "{\"value\": ${counter}}"
  }
}
