package test;

import nodes.NormalNode;

public class BasicTestModule {

    public static final int JUST_CHECK_FUNCTIONALITY = 0;

    private int testType;
    private String interestFilePath = "src/test/resources/normal_node_interests.txt";
    private String configFilePath = "src/test/resources/node_config.txt";
    public BasicTestModule(int testType){
        this.testType = testType;
    }

    public void startTest(){
        if(testType == JUST_CHECK_FUNCTIONALITY){
            NormalNode normalNode = new NormalNode(configFilePath,interestFilePath,
                    8001,5000,"localhost");

        }
    }
}