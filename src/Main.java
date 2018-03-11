import structure.Block;
import structure.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TransactionManager transaction = new TransactionManager();


        /*Test block*/
        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
        for(int i =0; i < 10; i ++){
            transactions.add(transaction.createRandomTransaction());
        }
        Block block = new Block(1,"qwe",transactions);

    }
}
