package Controller;

import Database.MongoBase;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        MongoBase dbConnection = new MongoBase();
        dbConnection.addEntry("SpringWeb", "TestingSpring", name);
        model.addAttribute("name", name);
        return "greeting";
    }

}