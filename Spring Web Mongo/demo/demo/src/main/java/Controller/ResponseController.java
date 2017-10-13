package Controller;


import Constants.Constants;
import DAO.DAOImplementation;
import DAO.DAOInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ResponseController {

    @Autowired
    DAOInterface dbConnection;

    @RequestMapping(value = "/jsonresult", method = RequestMethod.GET)
    @ResponseBody
    public String jsonresult(@RequestParam(value = "id", required = true) String id){

        return dbConnection.documentToJSON(id, Constants.database,Constants.collection);

    }

    @RequestMapping(value = "/alljsonresult", method = RequestMethod.GET)
    @ResponseBody
    public String alljsonresult(){

        return dbConnection.allDocumentToJSON(Constants.database,Constants.collection);
    }

}
