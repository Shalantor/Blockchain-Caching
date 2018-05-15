package test;

import nodes.*;
import structures.Block;
import structures.TransactionManager;
import test.utils.TestUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FunctionalityTestModule {

    private static int START = 7000;
    private static int STOP = 7021;
    private static String PREFIX = "src/test/examples/";
    private static String SUFFIX = "_example.txt";
    private static final String VOTING = "voting";
    private static final String MARKETPLACE = "marketplace";
    private static final String PAYMENT = "payment";
    private static final String CONFIG_FILE = "src/test/resources/node_config.txt";

    public static void main(String[] args) {

        /*create transactions*/
        TransactionManager manager = new TransactionManager(PREFIX + MARKETPLACE + SUFFIX);

        TestUtilities testUtilities = new TestUtilities(MARKETPLACE);

        /*create normal and light nodes. The nodes are now setup*/
        testUtilities.initLocal(10,10,new int[]{80,10,10},new int[]{80,10,10});

        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
        for(int i =0; i < 3; i ++){
            transactions.add(manager.createRandomTransaction());
        }

        /*Genesis block*/
        Block genesisBlock = new Block(0,"genesis",transactions);


        /*Create full node, with port STOP*/
        FullNode fullNode = new FullNode(CONFIG_FILE,PREFIX + MARKETPLACE + SUFFIX,
                genesisBlock, STOP,1000,"localhost");

        /*create miner node with port STOP - 1*/
        MinerNode minerNode = new MinerNode(genesisBlock,CONFIG_FILE,
                PREFIX + MARKETPLACE + SUFFIX,STOP - 1,1000,"localhost");

        /*Now create normal nodes with ports ranging from 7000 to 7010*/
        NormalNode[] normalNodes = testUtilities.normalNodes;
        LightNode[] lightNodes = testUtilities.lightNodes;

        /*Create threads*/
        Thread[] threads = new Thread[normalNodes.length + lightNodes.length + 2];
        threads[0] = new Thread(fullNode);
        threads[1] = new Thread(minerNode);
        int counter = 2;

        for(int i=0; i < normalNodes.length; i++){
            //Below is if we only want same interest in all nodes
            /*normalNodes[i] = new NormalNode("src/test/resources/node_config.txt",
                    "src/test/resources/normal_node_interests.txt",
                    START+i,1000,"localhost");*/
            threads[counter] = new Thread(normalNodes[i]);
            counter ++;
        }

        /*also create light nodes*/
        for(int i = 0; i < lightNodes.length; i++){
            //Below is if we only want the same interest in all nodes
            /*lightNodes[i] = new LightNode("src/test/resources/node_config.txt",
                    "src/test/resources/normal_node_interests.txt",
                    START+i + normalNodes.length,1000,"localhost");*/
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
        normalNodes[0].sendInterestRequest();
        Scanner scanner = new Scanner(System.in);
        String enter = scanner.nextLine();

        System.out.println("BEST NODE PORT: " + normalNodes[0].cacheManager.bestNodes.get(0).port);
        System.out.println("SENDING request for blocks to saved node");
        normalNodes[0].sendBlockRequestToNormal();

        System.out.println("write something to continue");
        scanner = new Scanner(System.in);
        enter = scanner.nextLine();

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
                System.out.println("Stop thread " + i);
            }
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

        PropagationTestModule.printResults(fullNode,lightNodes,normalNodes);
    }

}
