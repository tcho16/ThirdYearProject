package DAO;

public interface DAOInterface {

    //Retrieve document
    public String documentToJSON(String id, String databaseName, String collectionName);

    //Add to database
    public void addGPSEntry(String id, String longitude, String latitude, String status, String databaseName, String collectionName);

    //Close the connection
    public void closeConnection();

    //Retrieve all documents in the collection contained in the database
    public String allDocumentToJSON(String databaseName, String collectionName);

}
