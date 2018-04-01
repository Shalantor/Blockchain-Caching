package test;

import nodes.NormalNode;
import structures.Block;
import structures.Interest;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BasicTestModule {

    public static final int JUST_CHECK_FUNCTIONALITY = 0;

    private int testType;
    private String interestFilePath = "src/test/examples/marketplace_example.txt";
    private String configFilePath = "src/test/resources/node_config.txt";
    private String managerFilePath = "src/test/resources/example.txt";
    private String destPath = "src/test/examples/marketplace/";

    public BasicTestModule(int testType){
        this.testType = testType;
    }

    public void startTest(){
        if(testType == JUST_CHECK_FUNCTIONALITY){
            TransactionManager manager = new TransactionManager(managerFilePath);
            manager.generateInterestFiles(interestFilePath,4,3,destPath);
        }
    }
}