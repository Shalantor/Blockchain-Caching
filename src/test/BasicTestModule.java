package test;

import nodes.NormalNode;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BasicTestModule {

    public static final int JUST_CHECK_FUNCTIONALITY = 0;

    private int testType;
    private String interestFilePath = "src/test/resources/normal_node_interests.txt";
    private String configFilePath = "src/test/resources/node_config.txt";
    private String managerFilePath = "src/test/resources/example.txt";
    private String otherInterestFilePath = "src/test/resources/normal_node_interests2.txt";

    public BasicTestModule(int testType){
        this.testType = testType;
    }

    public void startTest(){
        if(testType == JUST_CHECK_FUNCTIONALITY){
            NormalNode normalNode = new NormalNode(configFilePath,interestFilePath,
                    8001,5000,"localhost");

            TransactionManager manager = new TransactionManager(managerFilePath);

            ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();

            for(int i =0; i < 10; i++){
                /*Node is interested in this*/
                transactions.add(manager.createTransaction(new ArrayList<>(
                        Arrays.asList("lol","spiros","florian",
                        50.0,"pizza",20))));
            }
            /*Other node*/
            NormalNode normalNode2 = new NormalNode(configFilePath,otherInterestFilePath,
                    8002,5000,"localhost");

            /*Create interests*/
            ArrayList<Interest> interestsToSend = new ArrayList<>();
            for( String key : normalNode2.interests.keySet()){
                interestsToSend.add(normalNode2.interests.get(key));
            }
            JSONObject jsonObject = normalNode2.createInterestAnswer("normal",interestsToSend);

            /*Now evaluate them*/
            normalNode.cacheManager.evaluateInterests(jsonObject,normalNode.interests,normalNode);
        }
    }
}