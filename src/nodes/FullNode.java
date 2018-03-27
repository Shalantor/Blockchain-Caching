package nodes;

import org.json.JSONArray;
import org.json.JSONObject;
import structures.Block;

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

    public List<Block> blockChain = new LinkedList<>();

    private static final String NETWORK_TOPOLOGY = "network_topology";
    private static final String MINER_INFO = "miner_node";

    /*Initialize with genesis block*/
    public FullNode(String configFilePath,Block genesisBlock,int port,int timeOut,String host){
        super(port,timeOut,host);
        blockChain.add(genesisBlock);

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
                }

            }
        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }
    }

    public void addBlock(Block block){
        blockChain.add(block);
    }

    /*Some node wants separate blocks*/
    public ArrayList<Block> getSeparateBlocks(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();
        for(Integer index: indexes){
            blocks.add(blockChain.get(index));
        }
        return blocks;
    }

    /*Some node wants block in intervals*/
    public ArrayList<Block> getBlocksInIntervals(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();
        for(int i=0; i < indexes.size(); i += 2){
            blocks.addAll(blockChain.subList(indexes.get(i),indexes.get(i+1)+1 ));
        }
        return blocks;
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
            System.out.println("GOT MESSAGE FROM MINER, my length is " + blockChain.size());
        }
        else if((Integer)jsonObject.get("type") == PROPAGATE_BLOCK){
            System.out.println("FULL NODE PROPAGATE BLOCK");
            propagateBlock(jsonObject);
        }
    }

    public int getSize(){
        return blockChain.size();
    }

}
