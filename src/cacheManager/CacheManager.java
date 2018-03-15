package cacheManager;

import structures.Block;

import java.util.ArrayList;

/*Super class for all managers*/
public class CacheManager {

    /*available configurations*/
    private static final int NO_CACHE_LIMIT = 0;
    private static final int CACHE_LIMIT_SIMPLE = 1;


    public boolean addBlock(ArrayList<Block> blocksInCache, Block block){
        blocksInCache.add(block);
        return true;
    }

    public void removeOldBlocks(ArrayList<Block> blocksInCache){

    }

    public static CacheManager createManager(int type,long timeRestraint,long cacheSize){
        switch (type){
            case  NO_CACHE_LIMIT:
                return new SimpleCacheManager(timeRestraint,cacheSize);

        }
        return null;
    }
}
