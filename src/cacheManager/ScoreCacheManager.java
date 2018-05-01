package cacheManager;

import structures.SavedNode;

import java.util.ArrayList;

public class ScoreCacheManager {

    private long timeLimit;
    private long cacheSize;
    private long sizeOfCachedBlocks;

    /*Which nodes we got the best interests from. This is
    sorted. Lowest index = highest score*/
    public ArrayList<SavedNode> bestNodes;

    public ScoreCacheManager(long timeLimit,long cacheSize){
        this.timeLimit = timeLimit;
        this.cacheSize = cacheSize;
    }

    
}
