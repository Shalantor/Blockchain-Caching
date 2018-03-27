package nodes;

import cacheManager.CacheManager;
import cacheManager.SimpleCacheManager;
import org.json.JSONArray;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;
import structures.StrippedBlock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LightNode extends Node{

    /*value indicating no cache size limit*/
    public static final int NO_LIMIT = 0;

    /*Possible names for type values*/
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";
    private static final String GREATER = "greater";
    private static final String LOWER = "lower";

    /*Available configurations*/
    private static final String MAX_CACHE_SIZE = "max_cache_size";
    private static final String TIME_RESTRAINT = "time_restraint";
    private static final String CACHE_CONFIG = "cache_configuration";
    private static final String NETWORK_TOPOLOGY = "network_topology";
    private static final String FULL_NODE_INFO = "full_node";
    private static final String MINER_INFO = "miner_node";

    /*Does the node have a maximum cache size?*/
    private long maxCacheSize;
    private long timeRestraint;

    /*Interest name with the corresponding interest object*/
    private HashMap<String, Interest> interests = new HashMap<>();

    /*ArrayList of interested blocks*/
    private ArrayList<Block> blocksInCache = new ArrayList<>();

    /*Cache manager*/
    /*TODO:Change hardcode*/
    private CacheManager cacheManager = new SimpleCacheManager(timeRestraint,maxCacheSize);

    /*The constructor as of now*/
    public LightNode(String configFilePath,String interestFilePath,int port,int timeOut,String host){

        super(port,timeOut,host);
        /*Open and read from config file*/
        try(BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line,key,value;
            String[] info;

            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }

                /*get key and value*/
                info = line.split("\\s+");
                key = info[0];
                value = info[1];

                switch (key){
                    case MAX_CACHE_SIZE:
                        maxCacheSize = Long.parseLong(value);
                        break;
                    case TIME_RESTRAINT:
                        timeRestraint = Long.parseLong(value);
                    case CACHE_CONFIG:
                        cacheManager = CacheManager.createManager(
                                Integer.parseInt(value),
                                timeRestraint,maxCacheSize);
                        break;
                    case NETWORK_TOPOLOGY:
                        networkTopology = Integer.parseInt(value);
                        portStart = Integer.parseInt(info[2]);
                        portEnd = Integer.parseInt(info[3]);
                        recipients = Integer.parseInt(info[4]);
                        break;
                    case MINER_INFO:
                        minerAddress = value;
                        minerPort = Integer.parseInt(info[2]);
                        break;
                    case FULL_NODE_INFO:
                        fullNodeAddress = info[1];
                        fullNodePort = Integer.parseInt(info[2]);
                        break;
                }
            }

        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }

        /*Now open config file for interests*/
        try(BufferedReader br = new BufferedReader(new FileReader(interestFilePath))) {
            String line,key,value;
            String[] info;

            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }

                /*get key and value*/
                info = line.split("\\s+");
                value = info[1];

                Interest temp;
                switch (value){
                    case STRING:
                        String[] subArray = Arrays.copyOfRange(info,3,info.length);
                        ArrayList<String> subList = new ArrayList<>(Arrays.asList(subArray));
                        temp = new Interest(Interest.STRING_TYPE,
                                0,Integer.parseInt(info[2]),info[0],null,subList);
                        interests.put(info[0],temp);
                        break;
                    case DOUBLE:
                        int operationType = 0;
                        if(info[3].equals(GREATER)){
                            operationType = Interest.NUMERIC_GREATER;
                        }
                        else if(info[3].equals(LOWER)){
                            operationType = Interest.NUMERIC_LOWER;
                        }
                        temp = new Interest(Interest.NUMERIC_TYPE,
                                operationType,Integer.parseInt(info[2]),info[0],
                                Double.parseDouble(info[4]),null);
                        interests.put(info[0],temp);
                        break;
                    case INTEGER:
                        operationType = 0;
                        if(info[3].equals(GREATER)){
                            operationType = Interest.NUMERIC_GREATER;
                        }
                        else if(info[3].equals(LOWER)){
                            operationType = Interest.NUMERIC_LOWER;
                        }
                        temp = new Interest(Interest.NUMERIC_TYPE,
                                operationType,Integer.parseInt(info[2]),info[0],
                                Integer.parseInt(info[4]),null);
                        interests.put(info[0],temp);
                        break;
                    case LONG:
                        operationType = 0;
                        if(info[3].equals(GREATER)){
                            operationType = Interest.NUMERIC_GREATER;
                        }
                        else if(info[3].equals(LOWER)){
                            operationType = Interest.NUMERIC_LOWER;
                        }
                        temp = new Interest(Interest.NUMERIC_TYPE,
                                operationType,Integer.parseInt(info[2]),info[0],
                                Long.parseLong(info[4]),null);
                        interests.put(info[0],temp);
                        break;
                }
            }

        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }
    }

    @Override
    public void processMessage(JSONObject jsonObject, Socket socket){
        if((Integer)jsonObject.get("type") == INTEREST_REQUEST_TO_NORMAL){
            ArrayList<Interest> interestsToSend = new ArrayList<>();
            for( String key : interests.keySet()){
                interestsToSend.add(interests.get(key));
            }
            JSONObject jsonReply = createInterestAnswer("light",interestsToSend);

            /*Now send answer*/
            try {
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                out.write(jsonReply.toString());
                out.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        else if((Integer)jsonObject.get("type") == BLOCK_REQUEST_TO_NORMAL){
            JSONObject jsonReply = createIndicesReply(blocksInCache);
            /*Now send answer*/
            try {
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                out.write(jsonReply.toString());
                out.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        else if((Integer)jsonObject.get("type") == PROPAGATE_BLOCK){
            Block block = new Block((JSONObject) jsonObject.get("block"),this);
            blocksInCache.add(block);
            propagateBlock(jsonObject);
        }
        else if((Integer)jsonObject.get("type") == INTEREST_REPLY_FROM_NORMAL) {
            JSONArray jsonArray = jsonObject.getJSONArray("interests");
            ArrayList<Interest> interests = new ArrayList<>();

            for(int i =0; i < jsonArray.length(); i++){
                interests.add(JSONToInterest(jsonArray.getJSONObject(0)));
            }

            /*TODO:Here can evaluate interests*/
        }
        else if((Integer)jsonObject.get("type") == BLOCK_REPLY_FROM_NORMAL) {
            JSONArray jsonArray = jsonObject.getJSONArray("blocks");
            ArrayList<Block> blocks = new ArrayList<>();

            for(int i =0; i < jsonArray.length(); i++){
                blocks.add(new Block((JSONObject) jsonArray.get(i),this));
            }
            /*TODO:Here can evaluate blocks*/
        }
    }

    /*Send new transaction*/
    public void sendNewTransaction(HashMap<String,Object> transaction,Socket socket){
        JSONObject jsonObject = createMessageForMiner(transaction);

        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            out.write(jsonObject.toString());
            out.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /*Send request for interests*/
    public void sendInterestRequest(Socket socket){
        JSONObject jsonObject = createInterestRequest("node");

        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            out.write(jsonObject.toString());
            out.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /*Send block request*/
    public void sendBlockRequestToNormal(Socket socket){
        JSONObject jsonObject = createBlockRequest("normal");

        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            out.write(jsonObject.toString());
            out.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /*Send request to full node*/
    public void sendBlockRequestToFull(Socket socket,int type, ArrayList<Integer> values){
        JSONObject jsonObject = createRequestToFullNode(type,values);

        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            out.write(jsonObject.toString());
            out.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }


    /*Below stuff is just for printing*/
    public void printInterests(){
        for(Map.Entry entry : interests.entrySet()){
            System.out.println("Interest name: " + entry.getKey());
            ((Interest)entry.getValue()).printInfo();
        }
    }

    public boolean checkBlock(Block block){
        for (Map.Entry entry : interests.entrySet()){
            if(((Interest)entry.getValue()).checkBlock(block)){
                System.out.println("YES MOTHERFUCKERS INTERESTED");
                StrippedBlock strippedBlock = new StrippedBlock(block,interests);
                cacheManager.addBlock(blocksInCache,strippedBlock);
                return true;
            }
        }
        return false;
    }

    public void printBlocks(){
        System.out.println("LIGHT NODE BLOCKS");
        for(Block block : blocksInCache){
            System.out.println(block);
        }
    }
}
