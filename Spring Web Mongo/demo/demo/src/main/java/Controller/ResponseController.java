package Controller;


import Constants.Constants;
import Database.MongoBase;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
public class ResponseController {

    @RequestMapping(value = "/jsonresult", method = RequestMethod.GET)
    @ResponseBody
    public String jsonresult(@RequestParam(value = "id", required = true) String id){
        MongoBase dbConnection = new MongoBase();
        return dbConnection.documentToJSON(id, Constants.database,Constants.collection);

    }

    @RequestMapping(value = "/alljsonresult", method = RequestMethod.GET)
    @ResponseBody
    public String alljsonresult(){
        MongoBase dbConnection = new MongoBase();
        return dbConnection.allDocumentToJSON(Constants.database,Constants.collection);

    }

}
