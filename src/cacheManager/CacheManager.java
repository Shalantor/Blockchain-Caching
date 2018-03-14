package cacheManager;

import structures.Block;

import java.util.ArrayList;

/*Super class for all managers*/
public class CacheManager {

    public boolean addBlock(ArrayList<Block> blocksInCache, Block block){
        blocksInCache.add(block);
        return true;
    }
}
