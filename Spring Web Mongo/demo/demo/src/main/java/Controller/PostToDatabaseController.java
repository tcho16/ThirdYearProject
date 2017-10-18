package Controller;

import Constants.Constants;
import DAO.DAOImplementation;
import DAO.DAOInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
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

        return "Received " + id + ". Long: " + longitude + ". Latitude: " + latitude + ". Status: " + status + " successfully!";
    }

}

