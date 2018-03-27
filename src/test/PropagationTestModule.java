package test;

import nodes.FullNode;
import nodes.LightNode;
import nodes.MinerNode;
import nodes.NormalNode;

import java.util.ArrayList;
import java.util.HashMap;

public class PropagationTestModule {

    public static void testPropagation(MinerNode minerNode,
                                       ArrayList<HashMap<String,Object>> transactions,
                                       FullNode fullNode,
                                       LightNode[] lightNodes,
                                       NormalNode[] normalNodes){
        minerNode.addTransaction(transactions.get(0));
        minerNode.addTransaction(transactions.get(0));
        minerNode.addTransaction(transactions.get(0));
        minerNode.addTransaction(transactions.get(0));
        minerNode.addTransaction(transactions.get(0));
    }


    public static void printResults(FullNode fullNode,
                             LightNode[] lightNodes,
                             NormalNode[] normalNodes){
        /*Print stats*/
        System.out.println("Full node blockchain size: " + fullNode.blockChain.size());

        for(int i=0; i < normalNodes.length; i++){
            System.out.println("Normal node with port " +normalNodes[i].port+" blockchain size: " + normalNodes[i].blocksInCache.size());
        }

        for(int i=0; i < lightNodes.length; i++){
            System.out.println("light node blockchain size: " + lightNodes[i].blocksInCache.size());
        }
    }

}
