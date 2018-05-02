package test;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import nodes.*;
import structures.Block;
import structures.Interest;
import structures.TransactionManager;
import test.utils.TestInfo;
import test.utils.TestUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BasicTestModule {

    public static final int JUST_CHECK_FUNCTIONALITY = 0;
    public static final int CHECK_FULL_NODE = 1;
    public static final int CHECK_NORMAL_NODE = 2;

    private int testType;
    private String interestFilePath = "src/test/examples/marketplace_example.txt";
    private String configFilePath = "src/test/resources/node_config.txt";
    private String managerFilePath = "src/test/examples/marketplace_example.txt";
    private String destPath = "src/test/examples/marketplace/one_interest/";
    private String destPath2 = "src/test/examples/marketplace/two_interests/";
    private String destPath3 = "src/test/examples/marketplace/three_interests/";

    public BasicTestModule(int testType){
        this.testType = testType;
    }

    public void startTest(){
        if(testType == JUST_CHECK_FUNCTIONALITY){
            TransactionManager manager = new TransactionManager(managerFilePath);
            Block block = new Block(0,"genesis",manager.createNormalTransactions(1));
            MinerNode minerNode = new MinerNode(block,configFilePath,managerFilePath,7464,1000,"localhost");

            ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();

            for(int i = 0; i < 10;i++){
                HashMap<String,Object> tr = new HashMap<>();
                tr.put("sender","node1");
                tr.put("receiver","node2");
                tr.put("category","sports");
                tr.put("price",3500.0);
                tr.put("count",60);
                tr.put("origin","europe");
                tr.put("fee",12.0);
                transactions.add(tr);
            }

            for(int i = 0; i < 9;i++){
                HashMap<String,Object> tr = new HashMap<>();
                tr.put("sender","node65");
                tr.put("receiver","node42");
                tr.put("category","toys");
                tr.put("price",3500.0);
                tr.put("count",60);
                tr.put("origin","north_america");
                tr.put("fee",12.0);
                transactions.add(tr);
            }

            for(int i = 0; i < 15;i++){
                HashMap<String,Object> tr = new HashMap<>();
                tr.put("sender","node78");
                tr.put("receiver","node22");
                tr.put("category","gaming");
                tr.put("price",3500.0);
                tr.put("count",60);
                tr.put("origin","asia");
                tr.put("fee",12.0);
                transactions.add(tr);
            }

            for(HashMap<String,Object> t : transactions){
                block = minerNode.addTransactionLocal(t);
                if(block != null){
                    //System.out.println(block);
                }
            }

            //System.out.println("transactions all are size " + transactions.size());
            //System.out.println("Remaining transactions in miner are " + minerNode.pendingTransactions.size());
            //minerNode.groupManager.printInfo();
        }
        else if(testType == CHECK_FULL_NODE){
            /*Test full node*/
            TransactionManager manager = new TransactionManager(managerFilePath);
            Block genesis = new Block(0,"genesis",manager.createNormalTransactions(1));
            FullNode fullNode = new FullNode(configFilePath,interestFilePath,genesis,9090,5000,"localhost");

            ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();

            /*how many blocks*/
            for(int i =0; i < 5; i++){
                transactions = new ArrayList<>();
                HashMap<String,Object> tr = new HashMap<>();
                tr.put("sender","node78");
                tr.put("receiver","node22");
                tr.put("category","books");
                tr.put("price",1000.0);
                tr.put("count",60);
                tr.put("origin","arctic");
                tr.put("fee",12.0);
                transactions.add(tr);

                fullNode.addBlock(new Block(i+1,"pilabi",transactions));
            }

            for(int i =5; i < 10; i++){
                transactions = new ArrayList<>();
                HashMap<String,Object> tr = new HashMap<>();
                tr.put("sender","node78");
                tr.put("receiver","node22");
                tr.put("category","books");
                tr.put("price",4500.0);
                tr.put("count",60);
                tr.put("origin","arctic");
                tr.put("fee",12.0);
                transactions.add(tr);

                fullNode.addBlock(new Block(i+1,"pilabi",transactions));
            }

            /*Indices we want back*/
            NormalNode normalNode = new NormalNode(configFilePath,destPath2 + "2_S_1_S_1_14.txt",9898,1000,"localhost");

            System.out.println(fullNode.storageManager.blockChainIndex);
            System.out.println("-------------------------------------");
            HashMap<String,Interest> interests = normalNode.interests;
            ArrayList<Interest> interestsToSend = new ArrayList<>();
            for( String key : interests.keySet()){
                interestsToSend.add(interests.get(key));
            }

            System.out.println(fullNode.storageManager.getBlockFromInterests(interestsToSend));
        }
        else if(testType == CHECK_NORMAL_NODE){
            /*Test normal node*/
            TransactionManager manager = new TransactionManager(managerFilePath);

            /*Normal node to check*/
            NormalNode normalNode = new NormalNode(configFilePath,destPath + "1_S_1_2.txt",9898,1000,"localhost");

            ArrayList<HashMap<String,Object>> transactions;
            ArrayList<Block> blocks = new ArrayList<>();

            /*how many blocks*/
            for(int i =0; i <= 5; i++){
                transactions = new ArrayList<>();
                HashMap<String,Object> tr = new HashMap<>();
                tr.put("sender","node78");
                tr.put("receiver","node22");
                tr.put("category","electronics");
                tr.put("price",1000.0);
                tr.put("count",60);
                tr.put("origin","arctic");
                tr.put("fee",12.0);
                transactions.add(tr);

                blocks.add(new Block(i+1,"pilabi",transactions));
            }
            normalNode.cacheManager.addReceivedBlocks(blocks,normalNode.interests);

            transactions = new ArrayList<>();
            /*how many blocks*/
            for(int i =0; i <= 2; i++){
                HashMap<String,Object> tr = new HashMap<>();
                tr.put("sender","node78");
                tr.put("receiver","node22");
                tr.put("category","electronics");
                tr.put("price",1000.0);
                tr.put("count",60);
                tr.put("origin","arctic");
                tr.put("fee",12.0);
                transactions.add(tr);
            }
            normalNode.checkBlock(new Block(0,"pilabi",transactions));

            for(Block b: normalNode.cacheManager.getBlocksInCache()){
                System.out.println(b.index);
            }

            System.out.println("Blocks in cache are " + normalNode.cacheManager.getBlocksInCache().size());

        }
    }
}