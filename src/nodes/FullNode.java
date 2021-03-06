package nodes;

import org.json.JSONArray;
import org.json.JSONObject;
import storage.DiskStorageManager;
import storage.MemoryStorageManager;
import storage.StorageManager;
import structures.Block;
import structures.Interest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*Has all the block chain stored*/
public class FullNode extends Node{

    private static final String NETWORK_TOPOLOGY = "network_topology";
    private static final String MINER_INFO = "miner_node";
    private static final String STORAGE_OPTION = "storage";
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";

    /*storage manager*/
    public StorageManager storageManager;

    /*Initialize with genesis block*/
    public FullNode(String configFilePath,String transactionPath,Block genesisBlock,int port,int timeOut,String host){
        super(port,timeOut,host);

        /*Get configurations*/
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line, key, value;
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
                switch(key){
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
                    case STORAGE_OPTION:
                        if(Integer.parseInt(value) == 0){
                            storageManager = new MemoryStorageManager(transactionPath,genesisBlock);
                        }
                        else if(Integer.parseInt(value) == 1){
                            storageManager = new DiskStorageManager(transactionPath,genesisBlock,this);
                        }
                        break;
                }

            }
        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }

    }

    public void addBlock(Block block){
        storageManager.addBlock(block);
    }

    /*Some node wants separate blocks*/
    public ArrayList<Block> getSeparateBlocks(List<Integer> indexes){
        return storageManager.getSeparateBlocks(indexes);
    }

    /*Some node wants block in intervals*/
    public ArrayList<Block> getBlocksInIntervals(List<Integer> indexes){
        return storageManager.getBlocksInIntervals(indexes);
    }

    @Override
    public void processMessage(JSONObject jsonObject, Socket socket){
        if((Integer)jsonObject.get("type") == REQUEST_TO_FULL_NODE){
            ArrayList<Block> blocks = null;
            List<Integer> indexes = new ArrayList<>();

            /*Process the json array to array list*/
            JSONArray jsonArray = (JSONArray) jsonObject.get("index_array");

            for(int i =0; i < jsonArray.length(); i++){
                indexes.add(jsonArray.getInt(i));
            }

            /*Separate blocks*/
            if((Integer)jsonObject.get("request_type") == 0){
                blocks = getSeparateBlocks(indexes);
            }
            /*Blocks in intervals*/
            else if((Integer)jsonObject.get("request_type") == 1){
                blocks = getBlocksInIntervals(indexes);
            }

            JSONObject jsonReply = createMessageFromFullNode(blocks);

            try {
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                out.write(jsonReply.toString() + "\n");
                out.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        else if((Integer)jsonObject.get("type") == BLOCK_FROM_MINER){
            Block block = new Block((JSONObject) jsonObject.get("block"),this);
            addBlock(block);
            //System.out.println("GOT MESSAGE FROM MINER, my length is " + blockChain.size());
        }
        else if((Integer)jsonObject.get("type") == PROPAGATE_BLOCK){
            //System.out.println("FULL NODE PROPAGATE BLOCK");
            propagateBlock(jsonObject);
        }
    }

    public int getSize(){
        return storageManager.getSize();
    }

    public ArrayList<Block> getBlocksFromInterests(ArrayList<Interest> interest,int limit){
        return storageManager.getBlockFromInterests(interest,limit);
    }

}
