package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*Light nodes collect blocks and remove any unecessary information*/
public class StrippedBlock {

    /*Dont keep hashes and timestamp*/
    public long index;
    public long blockSize;
    public ArrayList<HashMap<String,Object>> keptTransactions;

    public StrippedBlock(Block block, HashMap<String,Interest> interests){
        index = block.index;
        removeTransactions(block.transactions,interests);
        blockSize = Block.calculateTransactionSize(keptTransactions,16);/*16 is for two header fields*/
    }

    /*Store only interesting transactions*/
    private void removeTransactions(ArrayList<HashMap<String,Object>> transactions,
                                    HashMap<String,Interest> interests){
        keptTransactions = new ArrayList<>();
        for(HashMap<String,Object> transaction: transactions){
            boolean remove = true;
            for(Map.Entry entry: interests.entrySet()){
                if(((Interest)entry.getValue()).checkTransaction(transaction)){
                    remove = false;
                }
            }
            if(!remove){
                keptTransactions.add(transaction);
            }
        }
    }
}