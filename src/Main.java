import nodes.NormalNode;
import structures.Block;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TransactionManager manager = new TransactionManager();


        /*Test block*/
        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
        for(int i =0; i < 10; i ++){
            transactions.add(manager.createRandomTransaction());
        }
        Block block = new Block(1,"qwe",transactions);
        Block block2 = new Block(1,"qwe",transactions);

        /*Test normal node*/
        List<String> testList = new ArrayList<>();
        for(int i =0; i < 10; i ++){
            testList.add("AA");
        }
        NormalNode node = new NormalNode(testList,NormalNode.NO_LIMIT,NormalNode.NO_LIMIT);
    }
}
