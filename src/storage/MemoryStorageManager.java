package storage;

import structures.Block;

import java.util.ArrayList;

public class MemoryStorageManager extends StorageManager{

    private ArrayList<Block> blockChain;

    public MemoryStorageManager(){
        blockChain = new ArrayList<>();
    }

    @Override
    public void addBlock(Block block){
        blockChain.add(block);
    }

    
}
