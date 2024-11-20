package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {



	@GetMapping("/")
	public String root() {
		return "redirect:/devicecheck";
	}

	@GetMapping("/devicecheck")
	public  String branch(){return "redirect:/board/question/list";}
}
