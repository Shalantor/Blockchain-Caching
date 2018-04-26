package storage;

import com.mongodb.*;
import com.mongodb.util.JSON;
import nodes.Node;
import org.json.JSONObject;
import storage.storageUtils.BlockExplorer;
import structures.Block;
import structures.Interest;

import java.net.UnknownHostException;
import java.util.*;

public class DiskStorageManager extends StorageManager{

    /*Database name,address, collection name*/
    private final static String DATABASE_NAME = "Blockchain";
    private final static String DATABASE_ADDRESS = "localhost";
    private final static String COLLECTION_NAME = "blockchain";
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";

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

            BasicDBObject query = new BasicDBObject("index", new BasicDBObject("$gte",
                    indexes.get(i)).append("$lte", indexes.get(i+1)));

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

    @Override
    public ArrayList<Block> getBlockFromInterests(ArrayList<Interest> interests){

        ArrayList<Block> blocks = new ArrayList<>();

        for(Interest i : interests){
            if(i.type == Interest.STRING_TYPE){

                /*values that the node is interested in*/
                BasicDBList criteria = new BasicDBList();
                for(String value : i.interestValues){
                    criteria.add(new BasicDBObject("transactions." + i.interestName,value));
                }
                BasicDBObject query = new BasicDBObject("$or",criteria);
                DBCursor cursor = dbCollection.find(query);
                while (cursor.hasNext()){
                    JSONObject jsonObject = new JSONObject(cursor.next().toString().trim());
                    jsonObject.remove("_id");
                    blocks.add(new Block(jsonObject,node));
                }
            }
            else if(i.type == Interest.NUMERIC_TYPE){

                /*Check type*/
                Object value = null;
                String name = i.interestName;
                if(types.get(name).equals(DOUBLE)){
                    value = (Double) i.numericValue;
                }
                else if(types.get(name).equals(LONG)){
                    value = (Long) i.numericValue;
                }
                else if(types.get(name).equals(INTEGER)){
                    value = (Integer) i.numericValue;
                }

                String numericOperator = "";

                if(i.numericType == Interest.NUMERIC_GREATER){
                    numericOperator = "$gt";
                }
                else{
                    numericOperator = "$lt";
                }

                /*numeric query*/
                BasicDBObject query = new BasicDBObject(numericOperator,value);

                DBCursor cursor = dbCollection.find(new BasicDBObject("transactions." + i.interestName,query));

                /*Add to blocks to return*/
                while (cursor.hasNext()){
                    JSONObject jsonObject = new JSONObject(cursor.next().toString().trim());
                    jsonObject.remove("_id");
                    blocks.add(new Block(jsonObject,node));
                }

            }
        }

        /*Now sort*/
        blocks.sort(Block::compareTo);

        return blocks;
    }

}
