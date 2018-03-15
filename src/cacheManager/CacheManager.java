package cacheManager;

import structures.Block;

import java.util.ArrayList;

/*Super class for all managers*/
public class CacheManager {

    /*available configurations*/
    private static final int NO_LIMIT_NO_TIME_RESTRAINT = 0;
    private static final int NO_LIMIT_WITH_TIME_RESTRAINT = 1;
    private static final int LIMIT_NO_TIME_RESTRAINT = 2;
    private static final int LIMIT_WITH_TIME_RESTRAINT = 3;


    public boolean addBlock(ArrayList<Block> blocksInCache, Block block){
        blocksInCache.add(block);
        return true;
    }

    public void removeOldBlocks(ArrayList<Block> blocksInCache){

    }

    public static CacheManager createManager(int type,long timeRestraint,long cacheSize){
        switch (type){
            case  NO_LIMIT_NO_TIME_RESTRAINT:
                return new SimpleCacheManager(timeRestraint,cacheSize);
        }
        return null;
    }
}
