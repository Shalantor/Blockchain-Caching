package storage;

import com.mongodb.*;
import com.mongodb.util.JSON;
import nodes.Node;
import org.json.JSONObject;
import structures.Block;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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

        //addBlock(genesis);
    }

    @Override
    public void addBlock(Block block){
        size += 1;
        JSONObject jsonBlock = node.blockToJSON(block);
        dbCollection.insert((DBObject) JSON.parse(jsonBlock.toString()));
    }

    @Override
    public ArrayList<Block> getSeparateBlocks(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();

        for(Integer index: indexes){
            DBCursor cursor = dbCollection.find(new BasicDBObject("index",index));
            while (cursor.hasNext()){
               JSONObject jsonObject = new JSONObject(cursor.next().toString().trim());
               jsonObject.remove("_id");
               blocks.add(new Block(jsonObject,node));
            }
        }

        return blocks;
    }

    @Override
    public ArrayList<Block> getBlocksInIntervals(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();

        for(int i=0; i < indexes.size(); i += 2){
            BasicDBObject options = new BasicDBObject("$gte",i);
            options.append("$lte", i+1);
            BasicDBObject query = new BasicDBObject("index",options);
            DBCursor cursor = dbCollection.find(query);
            while (cursor.hasNext()){
                JSONObject jsonObject = new JSONObject(cursor.next().toString().trim());
                jsonObject.remove("_id");
                blocks.add(new Block(jsonObject,node));
            }
        }

        return blocks;
    }

    @Override
    public int getSize(){
        return size;
    }

}
