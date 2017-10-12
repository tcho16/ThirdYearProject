package Controller;

import Constants.Constants;
import Database.MongoBase;
import org.codehaus.groovy.control.messages.Message;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RestController
public class PostToDatabaseController {


    @RequestMapping(value = "/posttodb", method = RequestMethod.GET)
    @ResponseBody //@ResponseBody allows you to return a string rather than a thymeleaf template
    public String posttodb(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "longitude", required = true) String longitude,
            @RequestParam(value = "latitude", required = true) String latitude,
            @RequestParam(value = "status", required = true) String status
    ) {


        MongoBase dbConnection = new MongoBase();
        dbConnection.addGPSEntry(id, longitude, latitude, status, Constants.database, Constants.collection);

        return "Added " + id + ". Long: " + longitude + ". Latitude: " + latitude + ". Status: " + status + " successfully!";

    }

}

