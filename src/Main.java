import structure.Block;
import structure.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;

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
    }
}
