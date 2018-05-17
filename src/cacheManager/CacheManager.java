package cacheManager;

import nodes.Node;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;
import structures.SavedNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*Super class for all managers*/
public class CacheManager {

    /*available configurations*/
    private static final int NO_CACHE_LIMIT = 0;
    private static final int CACHE_LIMIT_SIMPLE = 1;
    private static final int SCORE_CACHE = 2;
    private static final int LABEL_BLOCK_SIZE = 3;
    private static final int SCORE_BLOCK_SIZE = 4;
    private static final int SCORE_RECENCY = 5;

    /*Which nodes we got the best interests from. This is
        sorted. Lowest index = highest score*/
    public ArrayList<SavedNode> bestNodes;

    private ArrayList<Block> blocksInCache;

    public boolean addBlock(Block block){
        blocksInCache.add(block);
        return true;
    }

    public void removeOldBlocks(){

    }

    public static CacheManager createManager(int type,long timeRestraint,long cacheSize,int scoreBound){
        switch (type){
            case  NO_CACHE_LIMIT:
                return new SimpleCacheManager(timeRestraint,cacheSize);
            case CACHE_LIMIT_SIMPLE:
                return new LabelCacheManager(timeRestraint,cacheSize);
            case SCORE_CACHE:
                return new ThreshHoldCacheManager(timeRestraint,cacheSize,scoreBound);
            case LABEL_BLOCK_SIZE:
                return new LabelBlockSizeCacheManager(timeRestraint,cacheSize);
            case SCORE_BLOCK_SIZE:
                return new ThreshHoldBlockSizeCacheManager(timeRestraint,cacheSize,scoreBound);
            case SCORE_RECENCY:
                return new ThreshHoldRecencyCacheManager(timeRestraint,cacheSize,scoreBound);

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
