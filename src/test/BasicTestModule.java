package test;

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
            for(int i =0; i < 10; i++){
                transactions = manager.createNormalTransactions(1);
                Block block = new Block(i+1,"pilabi",transactions);
                fullNode.addBlock(block);
            }

            /*Indices we want back*/
            NormalNode normalNode = new NormalNode(configFilePath,destPath + "1_S_1_7.txt",9898,1000,"localhost");

            System.out.println(fullNode.storageManager.blockChainIndex);
            System.out.println("-------------------------------------");
            HashMap<String,Interest> interests = normalNode.interests;
            ArrayList<Interest> interestsToSend = new ArrayList<>();
            for( String key : interests.keySet()){
                interestsToSend.add(interests.get(key));
            }

            System.out.println(fullNode.storageManager.getBlockFromInterests(interestsToSend));
        }
    }
}