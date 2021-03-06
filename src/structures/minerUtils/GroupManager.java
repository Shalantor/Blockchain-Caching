package structures.minerUtils;

import structures.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GroupManager {

    private Block lastBlock = null;
    protected boolean randomMode = false;
    private long minSize;
    private long maxSize;
    protected Random random = new Random();
    private long lowerBound;

    public GroupManager(){
    }

    public void enableRandomMode(long minSize, long maxSize){
        this.minSize = minSize;
        this.maxSize = maxSize;
        randomMode = true;
        lowerBound = random.nextInt((int) (maxSize-minSize)) + minSize;
    }

    public long addTransaction(HashMap<String,Object> transaction,
                               ArrayList<HashMap<String,Object>> transactions,
                               long size){
        transactions.add(transaction);
        size += Block.calculateSingleTransactionSize(transaction);
        return size;
    }

    public boolean canCreateBlock(long size,long minSize,long maxSize){
        if(!randomMode){
            return size >= minSize;
        }
        else{
            return size >= lowerBound;
        }
    }

    public Block generateNewBlock(ArrayList<HashMap<String,Object>> transactions,Block lastBlock){
        Block block = new Block(lastBlock.index + 1,
                lastBlock.getHeaderAsString(),new ArrayList<>(transactions));
        this.lastBlock = block;
        transactions.clear();
        if(randomMode){
            lowerBound = random.nextInt((int) (maxSize-minSize)) + minSize;
        }
        return block;
    }

    public void printInfo(){

    }

    public void resetIndices(ArrayList<HashMap<String,Object>> transactions){

    }

    public long getNewSize(ArrayList<HashMap<String,Object>> transactions){
        return lastBlock.getHeaderSize();
    }

}
