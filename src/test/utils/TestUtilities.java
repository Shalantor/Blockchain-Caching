package test.utils;

import nodes.LightNode;
import nodes.MinerNode;
import nodes.Node;
import nodes.NormalNode;
import structures.Block;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class TestUtilities {

    private String interestFilePath = "src/test/examples/marketplace_example.txt";
    private String configFilePath = "src/test/resources/node_config.txt";
    private String managerFilePath = "src/test/examples/marketplace_example.txt";
    private String destPath = "src/test/examples/marketplace/one_interest/";
    private String destPath2 = "src/test/examples/marketplace/two_interests/";
    private String destPath3 = "src/test/examples/marketplace/three_interests/";
    private TransactionManager manager;
    public Node[] nodes;

    public TestUtilities(){

    }

    public void initLocal(int numNormal,int numLight){
        manager = new TransactionManager(managerFilePath);
        manager.generateInterestFiles(interestFilePath,4,10,destPath);
        manager.generateMultipleInterestsFiles(destPath2,destPath,4,1);
        manager.generateMultipleInterestsFiles(destPath3,destPath,4,2);

        /*Percentages*/
        int[] normalPerc = new int[]{70,20,10};
        int[] lightPerc = new int[]{50,30,20};
        String[] filePaths = new String[]{destPath,destPath2,destPath3};

        TestInfo info = new TestInfo(numNormal,numLight,normalPerc,lightPerc);
        nodes = info.generateUniformInt(filePaths,configFilePath,7001,5000,"localhost");

    }

    public ArrayList<HashMap<String,Object>> getTransactionsUniform(int count){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createRandomTransactions(count);
        return transactions;
    }

    public ArrayList<HashMap<String,Object>> getTransactionsNormal(int count){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createNormalTransactions(count);
        return transactions;
    }

    public HashMap<String,Object> getTransactionUniform(){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createRandomTransactions(1);
        return transactions.get(0);
    }

    public HashMap<String,Object> getTransactionNormal(){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createNormalTransactions(1);
        return transactions.get(0);
    }

    public HashMap<String,Object> getTransactionZipfian(){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createZipfianTransactions(1);
        return transactions.get(0);
    }

    public HashMap<String,Object> getTransactionsZipfian(int count){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createZipfianTransactions(count);
        return transactions.get(0);
    }

    public HashMap<String,Object> getTransactionExponential(){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createExponentialTransactions(1);
        return transactions.get(0);
    }

    public HashMap<String,Object> getTransactionsExponential(int count){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createExponentialTransactions(count);
        return transactions.get(0);
    }

    public MinerNode createMiner(){
        /*Genesis block*/
        Block genesisBlock = new Block(0,"genesis",getTransactionsUniform(1));
        MinerNode miner = new MinerNode(genesisBlock,configFilePath,interestFilePath,7000,5000,"localhost");
        return miner;
    }
}
