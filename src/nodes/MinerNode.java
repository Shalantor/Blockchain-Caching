package nodes;

import org.json.JSONObject;
import structures.Block;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*implementation of the miner node*/
public class MinerNode extends Node{

    /*Available configurations*/
    public static final String MIN_BLOCK_SIZE = "min_block_size";
    public static final String MAX_BLOCK_SIZE = "max_block_size";
    public static final String GROUP_CONTENT = "group_content";

    public static final int NO_GROUP = -1;

    /*Store those configurations*/
    private long minBlockSize;
    private long maxBlockSize;
    private int groupContent = NO_GROUP;

    /*Last block in blockchain*/
    private Block lastBlock;

    /*Current transactions size*/
    private long sizeInBytes;

    /*List of transactions to put into the new block. Based on the configuration, the
    * list will either be a general list with all blocks, or we will have a list for
    * each interest available in the blockchain implementation. So a transaction is a
    * hashmap with names and names and the values of some fields. We want many of them.
    * So we need a list of lists of those transactions*/
    /*For now no grouping*/
    ArrayList<HashMap<String,Object>> pendingTransactions = new ArrayList<>();


    public MinerNode(Block block,String configFilePath,List<String> interests,int port,int timeOut,String host) {

        super(port,timeOut,host);

        lastBlock = block;
        sizeInBytes = lastBlock.getHeaderSize();

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
                    case MIN_BLOCK_SIZE:
                        minBlockSize = Long.parseLong(value);
                        break;
                    case MAX_BLOCK_SIZE:
                        maxBlockSize = Long.parseLong(value);
                        break;
                    case GROUP_CONTENT:
                        groupContent = Boolean.parseBoolean(value) ? 0 : NO_GROUP;
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
        if((Integer)jsonObject.get("type") == TRANSACTION_TO_MINER){
            HashMap<String,Object> transaction = JSONToTransaction((JSONObject) jsonObject.get("transactions"));
            addTransaction(transaction);
        }
        else if((Integer)jsonObject.get("type") == PROPAGATE_BLOCK){
            propagateBlock(new Block((JSONObject) jsonObject.get("block"),this));
        }
    }

    /*Add transaction to pending ones*/
    public void addTransaction(HashMap<String,Object> transaction){
        pendingTransactions.add(transaction);
        sizeInBytes += Block.calculateSingleTransactionSize(transaction);
        if(groupContent == NO_GROUP && sizeInBytes >= minBlockSize){
            //System.out.println("SIZE IS " + sizeInBytes);
            lastBlock = generateNewBlock();
        }
    }


    public Block generateNewBlock(){
        /*Check configuration*/
        if(groupContent == NO_GROUP && sizeInBytes >= minBlockSize){
            /*Generate new block*/

            Block block = new Block(lastBlock.index + 1,
                    lastBlock.getHeaderAsString(),pendingTransactions);

            /*clear list of previous transactions*/
            pendingTransactions.clear();
            sizeInBytes = lastBlock.getHeaderSize();
            //System.out.println("Generated new block with size " + block.blockSize);

            JSONObject jsonObject = createNewBlockMessage(block);
            propagateBlock(block);
            return block;
        }
        return null;
    }
}
