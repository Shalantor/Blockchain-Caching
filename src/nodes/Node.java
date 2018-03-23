package nodes;

import org.json.JSONArray;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;
import structures.StrippedBlock;

import java.io.*;
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
    public static final int PROPAGATE_BLOCK = 9;

    private ServerSocket listener;
    private int timeOut;
    private boolean running;
    private String host;
    private int port;

    /*Read configuration from text file*/
    public Node(int port,int timeOut,String host){
        try {
            listener = new ServerSocket(port);
            listener.setSoTimeout(timeOut);
        }
        catch (IOException ex){
            System.out.println("Could not create server socket");
        }
        this.timeOut = timeOut;
        this.host = host;
        this.port = port;
    }

    /*run method*/
    @Override
    public void run(){
        running = true;
        while (running){
            try{
                Socket socket = listener.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                JSONObject jsonObject = new JSONObject(br.readLine());
                br.close();
                socket = new Socket(jsonObject.getString("host"),jsonObject.getInt("port"));
                processMessage(jsonObject,socket);
            }
            catch (SocketTimeoutException ex){
                continue;
            }
            catch (IOException ex){
                System.out.println("Could not accept connection");
            }
        }
    }

    public void processMessage(JSONObject jsonObject,Socket socket){

    }

    public void stop(){
        running = false;
        System.out.println("Thread killed with Death Note");
    }

    /*Create JSON object from transaction*/
    public JSONObject transactionToJSON(HashMap<String,Object> transaction){
        JSONObject jsonObject = new JSONObject();

        for(Map.Entry entry : transaction.entrySet()){
            jsonObject.put(entry.getKey().toString(),entry.getValue());
        }

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
    public JSONObject createMessageForMiner(HashMap<String,Object> transaction){
        JSONObject jsonTransaction = transactionToJSON(transaction);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("transactions",jsonTransaction);
        jsonObject.put("type",TRANSACTION_TO_MINER);
        jsonObject.put("host",host);
        jsonObject.put("port",port);
        return jsonObject;
    }

    /*Message for new block created from miner*/
    public JSONObject createNewBlockMessage(Block block){
        JSONObject jsonBlock = blockToJSON(block);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("block",jsonBlock);
        jsonObject.put("type",BLOCK_FROM_MINER);
        jsonObject.put("host",host);
        jsonObject.put("port",port);
        return jsonObject;
    }

    /*MESSAGES TO AND FROM FULL NODE*/
    /*Message from full node to a node that made a request for some blocks*/
    public JSONObject createMessageFromFullNode(ArrayList<Block> blocks){
        JSONArray jsonBlocks = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        for(Block block : blocks){
            jsonBlocks.put(blockToJSON(block));
        }

        jsonObject.put("blocks",jsonBlocks);
        jsonObject.put("number_blocks",jsonBlocks.length());
        jsonObject.put("type",REPLY_FROM_FULL_NODE);
        jsonObject.put("host",host);
        jsonObject.put("port",port);

        return jsonObject;
    }

    /*Message to full node for requesting some blocks*/
    /*Type is separate blocks or intervals*/
    public JSONObject createRequestToFullNode(int requestType, List<Integer> indexes){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        jsonObject.put("type",REQUEST_TO_FULL_NODE);
        jsonObject.put("request_type",requestType);
        jsonObject.put("host",host);
        jsonObject.put("port",port);

        for(Integer index : indexes){
            jsonArray.put(index);
        }

        jsonObject.put("index_array",indexes);

        return jsonObject;
    }


    /*MESSAGES FROM AND TO NORMAL NODES*/
    /*Normal node sends requests to nodes for their interests*/
    /*Source is the node type that send the message*/
    public JSONObject createInterestRequest(String source){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",INTEREST_REQUEST_TO_NORMAL);
        jsonObject.put("source",source);
        jsonObject.put("host",host);
        jsonObject.put("port",port);
        return jsonObject;
    }

    /*Normal node answers with its interests*/
    public JSONObject createInterestAnswer(String source, ArrayList<Interest> interests){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",INTEREST_REPLY_FROM_NORMAL);
        jsonObject.put("source",source);
        jsonObject.put("host",host);
        jsonObject.put("port",port);

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
        jsonObject.put("host",host);
        jsonObject.put("port",port);

        /*Interested values*/
        JSONArray jsonArray = new JSONArray();
        if(interest.interestValues != null) {
            for (String value : interest.interestValues) {
                jsonArray.put(value);
            }
        }
        jsonObject.put("interested_values",jsonArray);

        return jsonObject;
    }

    public Interest JSONToInterest(JSONObject jsonObject){

        ArrayList<String> interestedValues = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("interested_values");
        for(int i = 0; i < jsonArray.length(); i++){
            interestedValues.add(jsonArray.getString(i));
        }

        Interest interest = new Interest(jsonObject.getInt("interest_type"),
                jsonObject.getInt("interest_numeric_type"),
                jsonObject.getInt("interest_weight"),
                jsonObject.getString("interest_name"),
                jsonObject.get("interest_numeric_value"),
                interestedValues);

        return interest;
    }

    /*Block request from normal node to normal node*/
    public JSONObject createBlockRequest(String source){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",BLOCK_REQUEST_TO_NORMAL);
        jsonObject.put("source",source);
        jsonObject.put("host",host);
        jsonObject.put("port",port);

        return jsonObject;
    }

    /*Normal node answer with blocks after getting request from normal node*/
    public JSONObject createBlockReply(ArrayList<Block> blocks){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",BLOCK_REPLY_FROM_NORMAL);

        JSONArray jsonArray = new JSONArray();
        for(Block block : blocks){
            jsonArray.put(blockToJSON(block));
        }

        jsonObject.put("blocks",jsonArray);
        jsonObject.put("host",host);
        jsonObject.put("port",port);

        return jsonObject;
    }

    /*LIGHT NODE MESSAGES*/
    /*Light node answers with indices of blocks*/
    public JSONObject createIndicesReply(ArrayList<Block> blocks){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",INDICES_REPLY_FROM_LIGHT);

        JSONArray jsonArray = new JSONArray();

        for(Block block : blocks){
            jsonArray.put(block.index);
        }

        jsonObject.put("indexes",jsonArray);
        jsonObject.put("host",host);
        jsonObject.put("port",port);
        return jsonObject;
    }

    /*Send block to other nodes via some protocol*/
    public void propagateBlock(Block block){
        JSONObject jsonBlock = blockToJSON(block);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("block",jsonBlock);
        jsonObject.put("type",PROPAGATE_BLOCK);
        jsonObject.put("host",host);
        jsonObject.put("port",port);

        /*TODO:CHANGE WHERE THE BLOCK IS SENT HUHU*/
        try {
            Socket socket = new Socket("localhost", 9090);
            try {
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                out.write(jsonObject.toString());
                out.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        catch (IOException ex){
            System.out.println("El no socketo de la creato");
            System.exit(1);
        }
    }

}
