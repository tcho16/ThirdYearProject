package Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
                           @RequestParam(value = "name2", required = false, defaultValue = "World2") String name2,
                           Model model) {

        model.addAttribute("name", name2);
        return "greeting";
    }

}