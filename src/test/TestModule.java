package test;

import nodes.FullNode;
import nodes.LightNode;
import nodes.Node;
import nodes.NormalNode;
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
            FullNode fullNode = new FullNode(block,9090,3000);
            Thread thread = new Thread(fullNode);
            thread.start();
            block = new Block(1,"No Game No Life",transactions);

            long pilafi = block.blockSize;

            try {
                Node node = new Node(7331,5000);
                List<Integer> indexes = new ArrayList<>();
                indexes.add(0);
                JSONObject jsonObject = node.createNewBlockMessage(block,Node.BLOCK_FROM_MINER);
                Socket socket = new Socket("localhost", 9090);
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                out.write(jsonObject.toString() + "\n");
                out.flush();

                /*read message*/
                //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //JSONObject jsonReply = new JSONObject(br.readLine());

                //System.out.println("Test module reply is " + jsonReply);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

            fullNode.stop();

        }
    }
}