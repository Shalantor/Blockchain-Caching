package cacheManager;

import nodes.Node;
import org.json.JSONObject;
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
    private static final int SCORE_CACHE = 2;

    private ArrayList<Block> blocksInCache;

    public boolean addBlock(Block block){
        blocksInCache.add(block);
        return true;
    }

    public void removeOldBlocks(ArrayList<Block> blocksInCache){

    }

    public static CacheManager createManager(int type,long timeRestraint,long cacheSize,int scoreBound){
        switch (type){
            case  NO_CACHE_LIMIT:
                return new SimpleCacheManager(timeRestraint,cacheSize);
            case CACHE_LIMIT_SIMPLE:
                return new SimpleLimitedCacheManager(timeRestraint,cacheSize);
            case SCORE_CACHE:
                return new ScoreCacheManager(timeRestraint,cacheSize,scoreBound);

        }
        return null;
    }

    public void evaluateInterests(JSONObject jsonObject, HashMap<String,Interest> ownInterests,
                                  Node node){

    }

    public void addReceivedBlocks(ArrayList<Block> receivedBlocks,HashMap<String,Interest> interests){

    }

    public boolean checkBlock(Block block, Map<String,Interest> interests){
        return true;
    }

    public void removeSavedNodes(){

    }

    public ArrayList<Block> getBlocksInCache() {
        return blocksInCache;
    }
}
