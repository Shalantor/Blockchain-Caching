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

    /*Network configuration types*/
    public static final int NETWORK_LOCAL = 0;

    private ServerSocket listener;
    private int timeOut;
    private boolean running;

    /*variables for network*/
    private String host;
    public int port;
    public int networkTopology;
    public int portStart,portEnd;
    public int recipients;
    public String fullNodeAddress;
    public int fullNodePort;
    public String minerAddress;
    public int minerPort;
    private int tries;

    /*Socket we got a reply from*/
    Socket readSocket;
    private static final int WAIT = 20;
    private static final int TRIES = 5;
    private int waitTime = WAIT;

    /*Read configuration from text file*/
    public Node(int port,int timeOut,String host){
        running = true;
        try {
            //System.out.println("Listening on port " + port);
            listener = new ServerSocket(port,100);
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
        while (running){
            /*TODO: CHECK IF WE WAIT FOR ANSWERS TO INTEREST REQUEST IN LOOP*/
            try{
                listener.setSoTimeout(timeOut);
                readSocket = listener.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));

                JSONObject jsonObject = null;
                while(true) {
                    try {
                        if (br.ready()) {
                            jsonObject = new JSONObject(br.readLine());
                            break;
                        }
                        Thread.sleep(waitTime);
                        tries++;
                        waitTime += WAIT;
                        if(tries >= TRIES){
                            tries = 0;
                            waitTime = WAIT;
                            break;
                        }
                    }
                    catch (InterruptedException ex){
                        System.out.println("Interrupted lol");
                        break;
                    }
                }

                br.close();
                if(jsonObject != null){
                    readSocket = new Socket(jsonObject.getString("host"),jsonObject.getInt("port"));
                    processMessage(jsonObject,readSocket);
                    readSocket.close();
                }
            }
            catch (SocketTimeoutException ex){
                continue;
            }
            catch (IOException ex){
                System.out.println("Could not accept connection lol");
            }
        }
    }

    public void processMessage(JSONObject jsonObject,Socket socket){

    }

    public void stop(){
        running = false;
        try{
            readSocket.close();
        }
        catch (IOException ex){
            System.out.println("IOexception in stop");
        }
        //System.out.println("Thread killed with Death Note");
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
        jsonObject.put("orig_host",host);
        jsonObject.put("orig_port",port);
        jsonObject.put("host",host);
        jsonObject.put("port",port);
        jsonObject.put("timeout",10);   /*TODO:Change hard coded number of hops*/
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

    /*Send block to other nodes via some protocol. This is only used from the miner*/
    public void propagateBlock(Block block){
        JSONObject jsonBlock = blockToJSON(block);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("block",jsonBlock);
        jsonObject.put("type",PROPAGATE_BLOCK);
        jsonObject.put("host",host);
        jsonObject.put("port",port);
        jsonObject.put("is_miner",true);

        int recipientPort = port;
        if(networkTopology == NETWORK_LOCAL){
            for(int i =1; i <= recipients; i++){
                try {
                    recipientPort = port + i;
                    if(recipientPort > portEnd){
                        recipientPort = portStart;
                    }
                    //System.out.println("MINER NODE: propagate to port " + recipientPort);
                    if( i == recipients){
                        jsonObject.put("next_port",recipientPort);
                    }
                    else{
                        jsonObject.put("next_port",-1);
                    }
                    Socket socket = new Socket("localhost", recipientPort);
                    try {
                        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                        out.write(jsonObject.toString() + "\n");
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
    }

    /*Method to relay node. This is used from all nodes*/
    public void propagateBlock(JSONObject jsonObject){
        if(networkTopology == NETWORK_LOCAL){

            /*check if message gets propagated*/
            int estimatePort = jsonObject.getInt("next_port");
            /*Only every last node in the interval propagates the block,
            so we have no duplicates. Only in the local configuration!!*/
            if(estimatePort == port){
                jsonObject.put("port",port);
                int recipientPort = port;
                if(networkTopology == NETWORK_LOCAL){
                    for(int i =1; i <= recipients; i++){
                        try {
                            recipientPort = port + i;
                            if(recipientPort > portEnd){
                                recipientPort = portStart;
                            }
                            //System.out.println("Recipient port is " + recipientPort + " and i am " + port);
                            if(recipientPort == minerPort || recipientPort == fullNodePort ){
                                //System.out.println("Got to stop now");
                                return;
                            }
                            if( i == recipients){
                                jsonObject.put("next_port",recipientPort);
                            }
                            else{
                                jsonObject.put("next_port",-1);
                            }
                            Socket socket = new Socket("localhost", recipientPort);
                            try {
                                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                                out.write(jsonObject.toString() + "\n");
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
            }
        }
    }

    /*TODO: Is same as propagateBlock but better test it. Only difference is timeout*/
    /*Propagate interests in network*/
    public void propagateInterestRequest(JSONObject jsonObject){
        if(networkTopology == NETWORK_LOCAL){

            /*check if message get propagated based on timeout*/
            int timeOut = jsonObject.getInt("timeout");

            if(timeOut == 0){
                return;
            }

            jsonObject.put("timeout",timeOut-1);

            /*check if message gets propagated by this node*/
            int estimatePort = jsonObject.getInt("next_port");

            /*Only every last node in the interval propagates the block,
            so we have no duplicates. Only in the local configuration!!*/
            if(estimatePort == port){
                jsonObject.put("port",port);
                int recipientPort = port;
                if(networkTopology == NETWORK_LOCAL){
                    for(int i =1; i <= recipients; i++){
                        try {
                            recipientPort = port + i;
                            if(recipientPort > portEnd){
                                recipientPort = portStart;
                            }

                            if( i == recipients){
                                jsonObject.put("next_port",recipientPort);
                            }
                            else{
                                jsonObject.put("next_port",-1);
                            }

                            Socket socket = new Socket("localhost", recipientPort);
                            try {
                                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                                out.write(jsonObject.toString() + "\n");
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
            }
        }
    }
}
