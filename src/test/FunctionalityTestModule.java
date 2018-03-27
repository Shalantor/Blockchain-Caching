package test;

import nodes.*;
import structures.Block;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FunctionalityTestModule {

    public static void main(String[] args) {

        /*create transactions*/
        TransactionManager manager = new TransactionManager("src/test/resources/example.txt");


        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
        for(int i =0; i < 3; i ++){
            transactions.add(manager.createRandomTransaction());
        }

        /*Genesis block*/
        Block genesisBlock = new Block(0,"genesis",transactions);


        /*Create full node, with port 7012*/
        FullNode fullNode = new FullNode("src/test/resources/full_node.txt",
                genesisBlock, 7012,1000,"localhost");

        /*create miner node with port 7011*/
        MinerNode minerNode = new MinerNode(genesisBlock,"src/test/resources/miner.txt",
                null,7011,1000,"localhost");

        /*Now create normal nodes with ports ranging from 7000 to 7010*/
        NormalNode[] normalNodes = new NormalNode[8];
        LightNode[] lightNodes = new LightNode[3];

        /*Create threads*/
        Thread[] threads = new Thread[normalNodes.length + lightNodes.length + 2];
        threads[0] = new Thread(fullNode);
        threads[1] = new Thread(minerNode);
        int counter = 2;

        for(int i=0; i < normalNodes.length; i++){
            normalNodes[i] = new NormalNode("src/test/resources/normal_node_config.txt",
                    "src/test/resources/normal_node_interests.txt",
                    7000+i,1000,"localhost");
            threads[counter] = new Thread(normalNodes[i]);
            counter ++;
        }

        /*also create light nodes*/
        for(int i = 0; i < lightNodes.length; i++){
            lightNodes[i] = new LightNode("src/test/resources/normal_node_config.txt",
                    "src/test/resources/normal_node_interests.txt",
                    7000+i + normalNodes.length,1000,"localhost");
            threads[counter] = new Thread(lightNodes[i]);
            counter ++;
        }

        /*Now start threads*/
        for(int i = 0; i < threads.length; i++){
            threads[i].start();
        }

        /*test block propagation*/
        minerNode.addTransaction(transactions.get(0));

        /*Wait for enter from user*/
        Scanner scanner = new Scanner(System.in);
        String enter = scanner.nextLine();

        /*Stop nodes from listening*/
        fullNode.stop();
        minerNode.stop();

        /*stop light and normal nodes*/
        for(int i=0; i < normalNodes.length; i++){
            normalNodes[i].stop();
        }

        for(int i=0; i < lightNodes.length; i++){
            lightNodes[i].stop();
        }

        /*Wait for threads to stop*/
        try {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
                System.out.println("Stop thread with id " + i);
            }
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

}
