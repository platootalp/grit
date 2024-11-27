package github.grit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/demo")
@RestController
public class DemoController {

	@GetMapping("/filter")
	public String filter(@RequestParam String name){
		return name;
	}

	@GetMapping("/interceptor")
	public String interceptor(@RequestParam String name){
		System.out.println(name);
		return name;
	}
}
