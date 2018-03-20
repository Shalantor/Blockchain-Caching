package nodes;

import org.json.JSONArray;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

public class Node implements Runnable{

    /*Static constants for message types*/
    public static final int TRANSACTION_TO_MINER = 0;
    public static final int BLOCK_FROM_MINER = 1;
    public static final int REPLY_FROM_FULL_NODE = 2;
    public static final int REQUEST_TO_FULL_NODE = 3;
    public static final int INTEREST_REQUEST_TO_NORMAL = 4;
    public static final int INTEREST_REPLY_FROM_NORMAL = 5;
    public static final int BLOCK_REQUEST_TO_NORMAL = 6;
    public static final int BLOCK_REPLY_FROM_NORMAL = 7;
    public static final int INDICES_REPLY_FROM_LIGHT = 8;

    private ServerSocket listener;
    private int timeOut;
    private boolean running;

    /*Read configuration from text file*/
    public Node(int port,int timeOut){
        try {
            listener = new ServerSocket(port);
            listener.setSoTimeout(timeOut);
        }
        catch (IOException ex){
            System.out.println("Could not create server socket");
        }
        this.timeOut = timeOut;
    }

    /*run method*/
    @Override
    public void run(){
        running = true;
        while (running){
            try{
                Socket socket = listener.accept();
                processMessage(socket);
            }
            catch (SocketTimeoutException ex){
                continue;
            }
            catch (IOException ex){
                System.out.println("Could not accept connection");
            }
        }
    }

    public void processMessage(Socket socket){

    }

    public void stop(){
        running = false;
    }

    /*Create JSON object from transaction*/
    public JSONObject transactionToJSON(HashMap<String,Object> transaction){
        JSONObject jsonObject = new JSONObject();

        for(Map.Entry entry : transaction.entrySet()){
            jsonObject.put(entry.getKey().toString(),entry.getValue());
        }

        System.out.println(jsonObject);
        return jsonObject;
    }

    /*Create transaction from JSON object*/
    public HashMap<String,Object> JSONToTransaction(JSONObject jsonObject){

        Iterator<?> keys = jsonObject.keys();
        HashMap<String,Object> transaction = new HashMap<>();

        while(keys.hasNext()){
            String key = (String) keys.next();
            transaction.put(key,jsonObject.get(key));
        }

        System.out.println(transaction);
        return transaction;
    }

    /*Create JSON object form block*/
    public JSONObject blockToJSON(Block block){
        JSONObject jsonObject = new JSONObject();

        /*headers of block*/
        jsonObject.put("index",block.index);
        jsonObject.put("timestamp",block.timestamp);
        jsonObject.put("transaction_hash",block.transactionHash);
        jsonObject.put("previous_block_hash",block.previousBlockHash);
        jsonObject.put("block_size",block.blockSize);
        jsonObject.put("number_transactions",block.numTransactions);

        /*transaction info*/
        JSONArray transactions = new JSONArray();

        for(HashMap<String,Object> transaction : block.transactions){
            transactions.put(transactionToJSON(transaction));
        }

        jsonObject.put("transactions",transactions);

        return jsonObject;
    }

    /*MESSAGES TO AND FROM MINER*/
    /*Message to send to miner*/
    public JSONObject createMessageForMiner(int type, HashMap<String,Object> transaction){
        JSONObject jsonObject = transactionToJSON(transaction);
        jsonObject.put("type",type);
        return jsonObject;
    }

    /*Message for new block created from miner*/
    public JSONObject createNewBlockMessage(Block block,int type){
        JSONObject jsonObject = blockToJSON(block);
        jsonObject.put("type",type);
        return jsonObject;
    }

    /*MESSAGES TO AND FROM FULL NODE*/
    /*Message from full node to a node that made a request for some blocks*/
    public JSONObject createMessageFromFullNode(int type,ArrayList<Block> blocks){
        JSONArray jsonBlocks = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        for(Block block : blocks){
            jsonBlocks.put(blockToJSON(block));
        }

        jsonObject.put("blocks",jsonBlocks);
        jsonObject.put("number_blocks",jsonBlocks.length());
        jsonObject.put("type",type);

        return jsonObject;
    }

    /*Message to full node for requesting some blocks*/
    /*Type is separate blocks or intervals*/
    public JSONObject createRequestToFullNode(int type, List<Integer> indexes){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        jsonObject.put("type",type);

        for(Integer index : indexes){
            jsonArray.put(index);
        }

        jsonObject.put("indexArray",indexes);

        return jsonObject;
    }


    /*MESSAGES FROM AND TO NORMAL NODES*/
    /*Normal node sends requests to nodes for their interests*/
    /*Source is the node type that send the message*/
    public JSONObject createInterestRequest(String source,int type){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",type);
        jsonObject.put("source",source);
        return jsonObject;
    }

    /*Normal node answers with its interests*/
    public JSONObject createInterestAnswer(int type,String source, ArrayList<Interest> interests){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",type);
        jsonObject.put("source",source);

        JSONArray jsonArray = new JSONArray();
        for(Interest interest : interests){
            jsonArray.put(interestToJSON(interest));
        }

        jsonObject.put("interests",jsonArray);

        return jsonObject;
    }

    /*Create json object from interest*/
    public JSONObject interestToJSON(Interest interest){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("interest_type",interest.type);
        jsonObject.put("interest_name",interest.interestName);
        jsonObject.put("interest_numeric_type",interest.numericType);
        jsonObject.put("interest_numeric_value",interest.numericValue);
        jsonObject.put("interest_weight",interest.weight);

        /*Interested values*/
        JSONArray jsonArray = new JSONArray();
        for(String value: interest.interestValues){
            jsonArray.put(value);
        }
        jsonObject.put("interested_values",jsonArray);

        return jsonObject;
    }

    /*Block request from normal node to normal node*/
    public JSONObject createBlockRequest(int type,String source){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",type);
        jsonObject.put("source",source);

        return jsonObject;
    }

    /*Normal node answer with blocks after getting request from normal node*/
    public JSONObject createBlockReply(int type,ArrayList<Block> blocks){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",type);

        JSONArray jsonArray = new JSONArray();
        for(Block block : blocks){
            jsonArray.put(blockToJSON(block));
        }

        jsonObject.put("blocks",jsonArray);

        return jsonObject;
    }

    /*LIGHT NODE MESSAGES*/
    /*Light node answers with indices of blocks*/
    public JSONObject createIndicesReply(int type,ArrayList<Integer> indexes){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",type);

        JSONArray jsonArray = new JSONArray();

        for(Integer index : indexes){
            jsonArray.put(index);
        }

        jsonObject.put("indexes",jsonArray);
        return jsonObject;
    }

}
