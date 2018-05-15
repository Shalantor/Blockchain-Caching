package nodes;

import org.json.JSONObject;
import structures.Block;
import structures.minerUtils.GroupManager;
import structures.minerUtils.PopularityGroupManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    private static final String NETWORK_TOPOLOGY = "network_topology";
    private static final String FULL_NODE_INFO = "full_node";

    public static final int NO_GROUP = -1;
    public static final int GROUP_POPULARITY = 0;

    /*Store those configurations*/
    private long minBlockSize;
    private long maxBlockSize;
    private int groupContent = NO_GROUP;

    /*Last block in blockchain*/
    private Block lastBlock;

    /*Current transactions size*/
    private long sizeInBytes;

    /*Manager for grouping*/
    public GroupManager groupManager;

    /*List of transactions to put into the new block. Based on the configuration, the
    * list will either be a general list with all blocks, or we will have a list for
    * each interest available in the blockchain implementation. So a transaction is a
    * hashmap with names and names and the values of some fields. We want many of them.
    * So we need a list of lists of those transactions*/
    /*For now no grouping*/
    public ArrayList<HashMap<String,Object>> pendingTransactions = new ArrayList<>();


    public MinerNode(Block block,String configFilePath,String interestPath,int port,int timeOut,String host) {

        super(port,timeOut,host);

        lastBlock = block;
        sizeInBytes = lastBlock.getHeaderSize();
        minerPort = port;
        minerAddress = host;

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
                        if(Boolean.parseBoolean(value)){
                            groupContent = Integer.parseInt(info[2]);
                            groupManager = new PopularityGroupManager(interestPath);
                        }
                        else{
                            groupContent = NO_GROUP;
                            groupManager = new GroupManager();
                        }
                        break;
                    case NETWORK_TOPOLOGY:
                        networkTopology = Integer.parseInt(value);
                        portStart = Integer.parseInt(info[2]);
                        portEnd = Integer.parseInt(info[3]);
                        recipients = Integer.parseInt(info[4]);
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


    }

    @Override
    public void processMessage(JSONObject jsonObject, Socket socket){
        if((Integer)jsonObject.get("type") == TRANSACTION_TO_MINER){
            HashMap<String,Object> transaction = JSONToTransaction((JSONObject) jsonObject.get("transactions"));
            addTransaction(transaction);
        }
        else if((Integer)jsonObject.get("type") == PROPAGATE_BLOCK){
            propagateBlock(jsonObject);
        }
    }

    /*Add transaction to pending ones*/
    public void addTransaction(HashMap<String,Object> transaction){
        sizeInBytes = groupManager.addTransaction(transaction,pendingTransactions, sizeInBytes);
        if(groupManager.canCreateBlock(sizeInBytes,minBlockSize,maxBlockSize)){
            lastBlock = generateNewBlock();
        }
    }


    public Block generateNewBlock(){
        /*Generate new block*/

        Block block = groupManager.generateNewBlock(pendingTransactions,lastBlock);
        sizeInBytes = groupManager.getNewSize(pendingTransactions);

        JSONObject jsonObject = createNewBlockMessage(block);

        /*Send to full node*/
        //System.out.println("SEND TO FULL NODE");
        try {
            Socket socket = new Socket(fullNodeAddress,fullNodePort);
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            out.write(jsonObject.toString() + "\n");
            out.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        propagateBlock(block);
        return block;
    }

    /*Local options for generating block and adding transaction*/
    public Block addTransactionLocal(HashMap<String,Object> transaction){
        sizeInBytes = groupManager.addTransaction(transaction,pendingTransactions, sizeInBytes);
        if(groupManager.canCreateBlock(sizeInBytes,minBlockSize,maxBlockSize)){
            lastBlock = groupManager.generateNewBlock(pendingTransactions,lastBlock);
            sizeInBytes = groupManager.getNewSize(pendingTransactions);
            //System.out.println("TRANSACTIONS IN MINER ARE SIZE " + pendingTransactions.size());
            return lastBlock;
        }
        return null;
    }
}
