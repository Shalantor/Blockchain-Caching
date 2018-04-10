package structures.minerUtils;

import structures.Block;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupManager {

    public GroupManager(){

    }

    public long addTransaction(HashMap<String,Object> transaction,
                               ArrayList<HashMap<String,Object>> transactions,
                               long size){
        transactions.add(transaction);
        size += Block.calculateSingleTransactionSize(transaction);
        return size;
    }

    public boolean canCreateBlock(long size,long minSize){
        return size >= minSize;
    }

    public Block generateNewBlock(ArrayList<HashMap<String,Object>> transactions,Block lastBlock){
        Block block = new Block(lastBlock.index + 1,
                lastBlock.getHeaderAsString(),new ArrayList<>(transactions));
        transactions.clear();
        return block;
    }

    public void printInfo(){

    }

}
