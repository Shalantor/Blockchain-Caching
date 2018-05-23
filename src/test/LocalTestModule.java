package test;

import nodes.LightNode;
import nodes.MinerNode;
import nodes.Node;
import nodes.NormalNode;
import structures.Block;
import structures.StrippedBlock;
import test.utils.TestUtilities;

import java.util.HashMap;

public class LocalTestModule {

    private static final String VOTING = "voting";
    private static final String MARKETPLACE = "marketplace";
    private static final String PAYMENT = "payment";

    public static void main(String[] args) {
        TestUtilities testUtilities = new TestUtilities(MARKETPLACE);

        /*create normal and light nodes. The nodes are now setup*/
        testUtilities.initLocal(10,10,new int[]{80,10,10},new int[]{80,10,10});

        /*create miner node*/
        MinerNode minerNode = testUtilities.createMiner();
        //minerNode.enableRandomMode();

        /*How many blocks to create?*/
        Block block;
        HashMap<String,Object> transaction;
        Node[] nodes = testUtilities.nodes;
        for(int i =0; i < 1000; i++){
            while(true) {
                /*Add transactions until enough for block*/
                transaction = testUtilities.getTransactionExponential();
                block = minerNode.addTransactionLocal(transaction);
                if (block != null) {
                    System.out.println("block size " + block.blockSize);
                    break;
                }
            }

            /*Now add block to each node*/
            for(Node n : nodes){
                if(n instanceof NormalNode){
                    ((NormalNode) n).checkBlock(block);
                }
                else if(n instanceof LightNode){
                    ((LightNode) n).checkBlock(block);
                }
            }

        }

        for(Node n : nodes){
            if(n instanceof NormalNode){
                System.out.println(((NormalNode) n).cacheManager.getBlocksInCache().size());
                //System.out.println(((NormalNode) n).cacheManager.getSizeOfCachedBlocks());
            }
            else if(n instanceof LightNode){
                System.out.println(((LightNode) n).cacheManager.getBlocksInCache().size());
                //System.out.println(((LightNode) n).cacheManager.getSizeOfCachedBlocks());
            }
        }

    }

}
