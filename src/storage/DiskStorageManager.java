package storage;

import com.mongodb.*;

import java.net.UnknownHostException;

public class DiskStorageManager extends StorageManager{

    /*Database name,address, collection name*/
    private final static String DATABASE_NAME = "Blockchain";
    private final static String DATABASE_ADDRESS = "localhost";
    private final static String COLLECTION_NAME = "blockchain";

    /*Database connection*/
    private DBCollection dbCollection;

    /*Most recent blocks here*/
    private int size;

    public DiskStorageManager(String transactionPath){
        super(transactionPath);
        size = 0;
        /*Test mongo database*/

        try{
            MongoClient client = new MongoClient(DATABASE_ADDRESS,27017);
            DB db = client.getDB(DATABASE_NAME);
            dbCollection = db.getCollection(COLLECTION_NAME);
        }
        catch (UnknownHostException ex){
            ex.printStackTrace();
        }

    }


}
