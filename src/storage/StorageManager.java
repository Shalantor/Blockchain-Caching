package storage;

import structures.Block;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/*Super class for storage. Currently only used by the full node to index transactions and blocks.
* But could also be used by a normal node in a future udpate*/
public class StorageManager {

    public StorageManager(){

    }

    public void addBlock(Block block){

    }

    public ArrayList<Block> getSeparateBlocks(List<Integer> indexes){
        return null;
    }

    public ArrayList<Block> getBlocksInIntervals(List<Integer> indexes){
        return null;
    }

    public int getSize(){
        return 0;
    }
}
