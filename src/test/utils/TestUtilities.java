package test.utils;

import nodes.LightNode;
import nodes.Node;
import nodes.NormalNode;
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

    public TestUtilities(){

    }

    public void testLocal(){
        TransactionManager manager = new TransactionManager(managerFilePath);
        manager.generateInterestFiles(interestFilePath,4,10,destPath);
        manager.generateMultipleInterestsFiles(destPath2,destPath,4,1);
        manager.generateMultipleInterestsFiles(destPath3,destPath,4,2);

        /*Percentages*/
        int[] normalPerc = new int[]{70,20,10};
        int[] lightPerc = new int[]{50,30,20};
        String[] filePaths = new String[]{destPath,destPath2,destPath3};

        TestInfo info = new TestInfo(100,50,normalPerc,lightPerc);
        Node[] nodes = info.generateUniformInt(filePaths,configFilePath,7001,5000,"localhost");

        ArrayList<HashMap<String,Object>> transactions;
        transactions = manager.createNormalTransactions(5);

        for(HashMap<String,Object> t : transactions){
            System.out.println(t);
        }
    }
}
