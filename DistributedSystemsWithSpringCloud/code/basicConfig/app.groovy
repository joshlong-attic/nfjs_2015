@RestController
class BasicConfig {

  @Value('${greeting}')
  String greeting

  @RequestMapping("/")
  String home() {
    "${greeting} World!"
  }
}
