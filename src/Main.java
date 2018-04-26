import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import nodes.FullNode;
import structures.Block;
import structures.TransactionManager;
import test.BasicTestModule;
import test.LocalTestModule;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        //BasicTestModule module = new BasicTestModule(1);
        //module.startTest();

        /*Test mongo database*/
        String interestFilePath = "src/test/examples/marketplace_example.txt";
        String configFilePath = "src/test/resources/node_config.txt";
        TransactionManager manager = new TransactionManager(interestFilePath);
        Block genesis = new Block(0,"genesis",manager.createNormalTransactions(1));

        /*Full node initialize*/
        FullNode fullNode = new FullNode(configFilePath,interestFilePath,genesis,9898,5000,"localhost");

        for(int i = 1; i <= 20; i++){
            //fullNode.addBlock(new Block(i,"pilabi",manager.createNormalTransactions(1)));
        }

        /*Indices we want to get*/
        List<Integer> indices = new ArrayList<>();
        indices.add(1);
        indices.add(5);

        /*call to full node*/
        ArrayList<Block> receive = fullNode.getBlocksInIntervals(indices);

        for(Block b : receive){
            System.out.println(b.index);
        }
    }
}
