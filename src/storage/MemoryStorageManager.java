package storage;

import structures.Block;

import java.util.ArrayList;
import java.util.List;

public class MemoryStorageManager extends StorageManager{

    /*List representing the blockchain*/
    private ArrayList<Block> blockChain;

    /**/

    public MemoryStorageManager(String transactionPath){
        super(transactionPath);
        blockChain = new ArrayList<>();
    }

    @Override
    public void addBlock(Block block){
        blockChain.add(block);
    }

    @Override
    public ArrayList<Block> getSeparateBlocks(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();
        for(Integer index: indexes){
            blocks.add(blockChain.get(index));
        }
        return blocks;
    }

    @Override
    public ArrayList<Block> getBlocksInIntervals(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();
        for(int i=0; i < indexes.size(); i += 2){
            blocks.addAll(blockChain.subList(indexes.get(i),indexes.get(i+1)+1 ));
        }
        return blocks;
    }

    @Override
    public int getSize(){
        return blockChain.size();
    }

    @Override
    public void indexBlock(Block block){

    }
}
