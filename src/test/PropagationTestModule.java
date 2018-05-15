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
        for(int i =0; i < 20; i++){
            minerNode.addTransaction(transactions.get(0));
        }

        try{
            Thread.sleep(8000);
        }
        catch (InterruptedException ex){
            System.out.println("test propagation interrupt");
        }

        for(int i =0; i < 20; i++){
            minerNode.addTransaction(transactions.get(0));
        }

        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException ex){
            System.out.println("test propagation interrupt");
        }
    }


    public static void printResults(FullNode fullNode,
                             LightNode[] lightNodes,
                             NormalNode[] normalNodes){
        /*Print stats*/
        System.out.println("Full node blockchain size: " + fullNode.getSize());

        for(int i=0; i < normalNodes.length; i++){
            System.out.println("Normal node with port " +normalNodes[i].port+" blockchain size: " + normalNodes[i].cacheManager.getBlocksInCache().size());
        }

        for(int i=0; i < lightNodes.length; i++){
            System.out.println("light node blockchain size: " + lightNodes[i].cacheManager.getBlocksInCache().size());
        }
    }

}
