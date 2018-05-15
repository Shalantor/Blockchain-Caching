package test;

import nodes.*;
import structures.Block;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FunctionalityTestModule {

    private static int START = 7000;
    private static int STOP = 7050;


    public static void main(String[] args) {

        /*create transactions*/
        TransactionManager manager = new TransactionManager("src/test/examples/marketplace_example.txt");


        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
        for(int i =0; i < 3; i ++){
            transactions.add(manager.createRandomTransaction());
        }

        /*Genesis block*/
        Block genesisBlock = new Block(0,"genesis",transactions);


        /*Create full node, with port STOP*/
        FullNode fullNode = new FullNode("src/test/resources/node_config.txt","src/test/examples/marketplace_example.txt",
                genesisBlock, STOP,1000,"localhost");

        /*create miner node with port STOP - 1*/
        MinerNode minerNode = new MinerNode(genesisBlock,"src/test/resources/node_config.txt",
                null,STOP - 1,1000,"localhost");

        /*Now create normal nodes with ports ranging from 7000 to 7010*/
        NormalNode[] normalNodes = new NormalNode[STOP - START - 1];
        LightNode[] lightNodes = new LightNode[0];

        /*Create threads*/
        Thread[] threads = new Thread[normalNodes.length + lightNodes.length + 2];
        threads[0] = new Thread(fullNode);
        threads[1] = new Thread(minerNode);
        int counter = 2;

        for(int i=0; i < normalNodes.length; i++){
            normalNodes[i] = new NormalNode("src/test/resources/node_config.txt",
                    "src/test/resources/normal_node_interests.txt",
                    START+i,1000,"localhost");
            threads[counter] = new Thread(normalNodes[i]);
            counter ++;
        }

        /*also create light nodes*/
        for(int i = 0; i < lightNodes.length; i++){
            lightNodes[i] = new LightNode("src/test/resources/node_config.txt",
                    "src/test/resources/normal_node_interests.txt",
                    START+i + normalNodes.length,1000,"localhost");
            threads[counter] = new Thread(lightNodes[i]);
            counter ++;
        }

        /*Now start threads*/
        for(int i = 0; i < threads.length; i++){
            threads[i].start();
            //System.out.println("Thread " + i + " started");
        }
        System.out.println("Threads started");

        /*test block propagation*/
        PropagationTestModule.testPropagation(minerNode,transactions,fullNode,lightNodes,normalNodes);

        /*Wait for enter from user*/
        System.out.println("write something to continue");
        //normalNodes[0].sendInterestRequest();
        Scanner scanner = new Scanner(System.in);
        String enter = scanner.nextLine();

        /*stop light and normal nodes*/
        for(int i=0; i < normalNodes.length; i++){
            normalNodes[i].stop();
        }

        for(int i=0; i < lightNodes.length; i++){
            lightNodes[i].stop();
        }

        /*Stop nodes from listening*/
        fullNode.stop();
        minerNode.stop();

        /*Wait for threads to stop*/
        try {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
                System.out.println("Stop");
            }
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

        PropagationTestModule.printResults(fullNode,lightNodes,normalNodes);
    }

}
