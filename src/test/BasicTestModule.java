package test;

import nodes.NormalNode;
import structures.Block;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BasicTestModule {

    public static final int JUST_CHECK_FUNCTIONALITY = 0;

    private int testType;
    private String interestFilePath = "src/test/resources/normal_node_interests.txt";
    private String configFilePath = "src/test/resources/node_config.txt";
    private String managerFilePath = "src/test/resources/example.txt";

    public BasicTestModule(int testType){
        this.testType = testType;
    }

    public void startTest(){
        if(testType == JUST_CHECK_FUNCTIONALITY){
            NormalNode normalNode = new NormalNode(configFilePath,interestFilePath,
                    8001,5000,"localhost");

            TransactionManager manager = new TransactionManager(managerFilePath);

            ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();

            for(int i =0; i < 10; i++){
                /*Node is interested in this*/
                transactions.add(manager.createTransaction(new ArrayList<>(
                        Arrays.asList("lol","spiros","florian",
                        50.0,"pizza",20))));
            }

            /*Test add blocks method*/
            for(int i = 1; i<10; i+=2){
                Block block = new Block(i,"genesis",transactions);
                if(normalNode.cacheManager.checkBlock(block,normalNode.interests)){
                    normalNode.cacheManager.addBlock(normalNode.blocksInCache,block);
                }
            }

            ArrayList<Block> testBlocks = new ArrayList<>();
            for(int i=0; i < 10; i+=2){
                testBlocks.add(new Block(i,"genesis",transactions));
            }

            normalNode.cacheManager.addReceivedBlocks(testBlocks,normalNode.blocksInCache);

            for(Block b : normalNode.blocksInCache){
                System.out.println(b.index);
            }
        }
    }
}