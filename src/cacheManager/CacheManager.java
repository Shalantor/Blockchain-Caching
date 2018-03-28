package cacheManager;

import structures.Block;
import structures.Interest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
            case CACHE_LIMIT_SIMPLE:
                return new SimpleLimitedCacheManager(timeRestraint,cacheSize);

        }
        return null;
    }

    public void evaluateInterests(ArrayList<Interest> interests){

    }

    public void addReceivedBlocks(ArrayList<Block> receivedBlocks,ArrayList<Block> blocksInCache){

    }

    public boolean checkBlock(Block block, Map<String,Interest> interests){
        return true;
    }
}
