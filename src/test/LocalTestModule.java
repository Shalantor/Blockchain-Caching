package test;

import nodes.LightNode;
import nodes.MinerNode;
import nodes.Node;
import nodes.NormalNode;
import structures.Block;
import test.utils.TestUtilities;

import java.util.HashMap;

public class LocalTestModule {

    public static void main(String[] args) {
        TestUtilities testUtilities = new TestUtilities();

        /*create normal and light nodes. The nodes are now setup*/
        testUtilities.initLocal(100,50);

        /*create miner node*/
        MinerNode minerNode = testUtilities.createMiner();

        /*How many blocks to create?*/
        Block block;
        HashMap<String,Object> transaction;
        Node[] nodes = testUtilities.nodes;
        for(int i =0; i < 500; i++){
            while(true) {
                /*Add transactions until enough for block*/
                transaction = testUtilities.getTransactionPoisson();
                block = minerNode.addTransactionLocal(transaction);
                if (block != null) {
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
            }
            else if(n instanceof LightNode){
                System.out.println(((LightNode) n).cacheManager.getBlocksInCache().size());
            }
        }

    }

}
