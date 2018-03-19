package test;

import nodes.LightNode;
import nodes.Node;
import nodes.NormalNode;
import structures.Block;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TestModule{

    public static final int JUST_CHECK_FUNCTIONALITY = 0;

    private int testType;
    public TestModule(int testType){
        this.testType = testType;
    }

    public void startTest(){
        if(testType == JUST_CHECK_FUNCTIONALITY){
            TransactionManager manager = new TransactionManager("src/test/resources/example.txt");


            /*Test block*/

            /*Test full node*/
        /*FullNode fullNode = new FullNode(new Block(0,"qwe",transactions));

        for(int i =0; i < 10; i ++){
            fullNode.addBlock(new Block(i+1,"",transactions));
        }

        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(0);
        indices.add(4);
        indices.add(8);
        indices.add(8);

        List<Block> blocks =  fullNode.getBlocksInIntervals(indices);

        for(Block block: blocks){
            System.out.println(block.index);
        }
        MinerNode miner = new MinerNode(new Block(0,"genesis",transactions),
                "src/test/resources/miner.txt",manager.getKeys());

        for(int i =0; i < 50; i ++){
            miner.addTransaction(transactions.get(0));
        }*/

            //System.out.println(transactions);

            //normal.printInterests();

            //Block block = new Block(0,"genesis",transactions);
            //System.out.println(block);

            //normal.checkBlock(block);
            //LightNode lightNode = new LightNode("src/test/resources/normal_node_config.txt",
                    //"src/test/resources/normal_node_interests.txt");

            //lightNode.printInterests();
            //lightNode.checkBlock(block);
            //normal.checkBlock(block);
            //lightNode.printBlocks();
            //normal.printBlocks();

            //ArrayList<Block> blocks = new ArrayList<>();
            /*Create blocks*/
            /*for(int i =0;i < 2; i++){
                blocks.add(new Block(0,"genesis",transactions));
            }

            for(int i =0;i < 2; i++){
                blocks.get(i).timestamp = 0;
            }

            normal.blocksInCache = blocks;
            System.out.println(blocks.size());
            normal.cacheManager.removeOldBlocks(blocks);
            System.out.println(blocks.size());*/

            ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
            for(int i =0; i < 1; i ++){
                transactions.add(manager.createRandomTransaction());
            }
            System.out.println(transactions.get(0));

            Node node = new Node("");
            node.transactionToJSON(transactions.get(0));
        }
    }
}