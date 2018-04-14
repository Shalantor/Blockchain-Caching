package test;

import nodes.MinerNode;
import nodes.Node;
import nodes.NormalNode;
import structures.Block;
import structures.Interest;
import structures.TransactionManager;
import test.utils.TestInfo;
import test.utils.TestUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BasicTestModule {

    public static final int JUST_CHECK_FUNCTIONALITY = 0;

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

            for(int i = 0; i < 9;i++){
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
                    System.out.println(block);
                }
            }

            //minerNode.groupManager.printInfo();
        }
    }
}