package cacheManager;

import structures.Block;

import java.util.ArrayList;

/*The simplest manager, it just adds the block to the cache
* if there is something interesting in it.
* This class is used if there is no limit in the cache*/
public class SimpleCacheManager extends CacheManager{

    public SimpleCacheManager(){

    }

    @Override
    public boolean addBlock(ArrayList<Block> blocksInCache, Block block){
        blocksInCache.add(block);
        return true;
    }


}
