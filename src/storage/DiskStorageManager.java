package storage;

import structures.Block;
import java.util.ArrayList;

public class DiskStorageManager {

    /*Most recent blocks here*/
    private ArrayList<Block> prunedBlockChain;
    private int size;

    public DiskStorageManager(){
        size = 0;
        prunedBlockChain = new ArrayList<>();
    }


}
