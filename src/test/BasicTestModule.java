package test;

import nodes.Node;
import nodes.NormalNode;
import structures.Block;
import structures.Interest;
import structures.TransactionManager;
import test.utils.TestInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BasicTestModule {

    public static final int JUST_CHECK_FUNCTIONALITY = 0;

    private int testType;
    private String interestFilePath = "src/test/examples/marketplace_example.txt";
    private String configFilePath = "src/test/resources/node_config.txt";
    private String managerFilePath = "src/test/resources/example.txt";
    private String destPath = "src/test/examples/marketplace/one_interest/";
    private String destPath2 = "src/test/examples/marketplace/two_interests/";
    private String destPath3 = "src/test/examples/marketplace/three_interests/";

    public BasicTestModule(int testType){
        this.testType = testType;
    }

    public void startTest(){
        if(testType == JUST_CHECK_FUNCTIONALITY){
            TransactionManager manager = new TransactionManager(managerFilePath);
            manager.generateInterestFiles(interestFilePath,4,10,destPath);
            manager.generateMultipleInterestsFiles(destPath3,destPath,4,2);

            /*Percentages*/
            int[] normalPerc = new int[]{50,30,20};
            int[] lightPerc = new int[]{50,30,20};
            String[] filePaths = new String[]{destPath,destPath2,destPath3};

            TestInfo info = new TestInfo(100,50,normalPerc,lightPerc);
            Node[] nodes = info.generateUniformInt(filePaths,configFilePath,7001,5000,"localhost");
        }
    }
}