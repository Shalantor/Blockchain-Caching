package storage;

import com.mongodb.*;
import com.mongodb.util.JSON;
import nodes.Node;
import org.json.JSONObject;
import structures.Block;

import java.net.UnknownHostException;

public class DiskStorageManager extends StorageManager{

    /*Database name,address, collection name*/
    private final static String DATABASE_NAME = "Blockchain";
    private final static String DATABASE_ADDRESS = "localhost";
    private final static String COLLECTION_NAME = "blockchain";

    /*Database connection*/
    private DBCollection dbCollection;

    /*Associated node*/
    private Node node;

    /*Size of blockchain*/
    private int size;

    public DiskStorageManager(String transactionPath,Block genesis, Node node){
        super(transactionPath);
        size = 0;

        this.node = node;
        try{
            MongoClient client = new MongoClient(DATABASE_ADDRESS,27017);
            DB db = client.getDB(DATABASE_NAME);
            dbCollection = db.getCollection(COLLECTION_NAME);
        }
        catch (UnknownHostException ex){
            ex.printStackTrace();
        }

        addBlock(genesis);
    }

    @Override
    public void addBlock(Block block){
        size += 1;
        JSONObject jsonBlock = node.blockToJSON(block);
        dbCollection.insert((DBObject) JSON.parse(jsonBlock.toString()));
    }

}
