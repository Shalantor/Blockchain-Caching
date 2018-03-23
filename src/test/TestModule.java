package test;

import nodes.*;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;
import structures.TransactionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestModule{

    public static final int JUST_CHECK_FUNCTIONALITY = 0;

    private int testType;
    public TestModule(int testType){
        this.testType = testType;
    }

    public void startTest(){
        if(testType == JUST_CHECK_FUNCTIONALITY){
            TransactionManager manager = new TransactionManager("src/test/resources/example.txt");


            ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
            for(int i =0; i < 3; i ++){
                transactions.add(manager.createRandomTransaction());
            }

            /*Start full node that listens*/
            Block block = new Block(0,"No Game No Life",transactions);
            List<String> values = new ArrayList<>();
            NormalNode normal = new NormalNode("src/test/resources/normal_node_config.txt",
                    "src/test/resources/normal_node_interests.txt",9090,3000,"localhost");
            Thread thread = new Thread(normal);
            thread.start();

            try {
                Node node = new Node(7331,5000,"localhost");
                System.out.println(transactions.get(0));
                JSONObject jsonObject = node.createBlockRequest("normal");
                Socket socket = new Socket("localhost", 9090);
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                out.write(jsonObject.toString() + "\n");
                out.flush();

                /*read message*/
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("TEST MODULE NOW WILL READ");
                JSONObject jsonReply = new JSONObject(br.readLine());

                System.out.println("Test module reply is " + jsonReply);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

            normal.stop();

        }
    }
}