package test.utils;

import nodes.LightNode;
import nodes.MinerNode;
import nodes.Node;
import nodes.NormalNode;
import org.apache.commons.io.FileUtils;
import structures.Block;
import structures.TransactionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TestUtilities {

    private static final String EXAMPLE_END = "_example.txt";
    private static final String EXAMPLE_START = "src/test/examples/";
    private String interestFilePath;
    private String configFilePath = "src/test/resources/node_config.txt";
    private String managerFilePath;
    private String destPath;
    private String destPath2;
    private String destPath3;
    private TransactionManager manager;
    public Node[] nodes;

    public TestUtilities(String path){
        interestFilePath = EXAMPLE_START + path + EXAMPLE_END;
        managerFilePath = interestFilePath;
        destPath = EXAMPLE_START + path + "/one_interest/";
        destPath2 = EXAMPLE_START + path + "/two_interests/";
        destPath3 = EXAMPLE_START + path + "/three_interests/";

        /*Delete directories*/
        try{
            FileUtils.deleteDirectory(new File(destPath));
            FileUtils.deleteDirectory(new File(destPath2));
            FileUtils.deleteDirectory(new File(destPath3));
        }
        catch (IOException ex){
            System.out.println("trololol");
        }

        /*recreate them*/
        new File(destPath).mkdirs();
        new File(destPath2).mkdirs();
        new File(destPath3).mkdirs();

    }

    public void initLocal(int numNormal,int numLight, int[] normalPerc,int[] lightPerc){
        manager = new TransactionManager(managerFilePath);
        manager.generateInterestFiles(interestFilePath,100,destPath);
        manager.generateMultipleInterestsFiles(destPath2,destPath,5,1);
        manager.generateMultipleInterestsFiles(destPath3,destPath,5,2);

        /*Percentages*/
        String[] filePaths = new String[]{destPath,destPath2,destPath3};

        TestInfo info = new TestInfo(numNormal,numLight,normalPerc,lightPerc);
        nodes = info.generateInterests(filePaths,configFilePath,7001,5000,"localhost");

    }

    public void initManager(){
        manager = new TransactionManager(managerFilePath);
        manager.generateInterestFiles(interestFilePath,100,destPath);
        manager.generateMultipleInterestsFiles(destPath2,destPath,4,1);
        manager.generateMultipleInterestsFiles(destPath3,destPath,4,2);
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

    public HashMap<String,Object> getTransactionPoisson(){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createPoissonTransactions(1);
        return transactions.get(0);
    }

    public HashMap<String,Object> getTransactionsPoisson(int count){
        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createPoissonTransactions(count);
        return transactions.get(0);
    }

    public MinerNode createMiner(){
        /*Genesis block*/
        Block genesisBlock = new Block(0,"genesis",getTransactionsUniform(1));
        MinerNode miner = new MinerNode(genesisBlock,configFilePath,interestFilePath,7000,5000,"localhost");
        return miner;
    }
}
