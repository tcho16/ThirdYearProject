package Controller;


import Constants.Constants;
import DAO.DAOInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class PostToDatabaseController {

    @Autowired
    DAOInterface db;

    @RequestMapping(value = "/posttodb", method = RequestMethod.GET)
    @ResponseBody //@ResponseBody allows you to return a string rather than a thymeleaf template
    public String posttodb(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "longitude", required = true) String longitude,
            @RequestParam(value = "latitude", required = true) String latitude,
            @RequestParam(value = "status", required = true) String status
    ) {
        db.addGPSEntry(id, longitude, latitude, status, Constants.database, Constants.collection);

        return "Added the following: " + id + ". Long: " + longitude + ". Latitude: " + latitude + ". Status: " + status;
    }

}

