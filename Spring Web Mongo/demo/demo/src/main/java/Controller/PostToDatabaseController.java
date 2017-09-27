package Controller;

import Database.MongoBase;
import org.codehaus.groovy.control.messages.Message;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RestController
public class PostToDatabaseController {


        @RequestMapping(value = "/postdb", method = RequestMethod.POST)
        @ResponseBody //@ResponseBody allows you to return a string rather than a thymeleaf template
        public String postingToDB(HttpEntity<String> httpEntit) {
            return httpEntit.getBody();
        }

    }

