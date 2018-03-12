package nodes;

import structures.Block;
import structures.Interest;
import structures.TransactionManager;

import java.util.*;

/*Implementation of the normal node*/
public class NormalNode {

    /*value indicating no cache size limit*/
    public static final int NO_LIMIT = -1;

    /*Does the node have a maximum cache size?*/
    private long maxCacheSize = NO_LIMIT;
    private long timeRestraint = NO_LIMIT;

    /*List of this nodes interests*/
    private Map<String,Interest> interests = new HashMap<>();

    /*Size in bytes of interests*/
    private long size;

    /*The constructor as of now*/
    /*TODO:Change default weight*/
    public NormalNode(List<String> interests){
        for(String interest: interests){
            this.interests.put(interest,new Interest(interest,1));
        }
    }

    public NormalNode(List<String> interests,long maxCacheSize, long timeRestraint){
        this(interests);
        this.maxCacheSize = maxCacheSize;
        this.timeRestraint = timeRestraint;
    }

    /*TODO: This is the main point that needs testing to see efficiency of different algorithms*/
    /*TODO: Check dynamic method changing depending on configuration*/
    /*Inspect block to add it to interests or not. Only in case of no max cache size and no time limit*/
    public void inspectBlock(Block block){
        for(HashMap<String,Object> transaction : block.transactions){
            for(Map.Entry entry : transaction.entrySet()){
                Interest interest = interests.get(entry.getKey().toString());
                if(interest != null){    /*key exists,add block*/
                    interest.addBlock(block);
                    break;
                }
            }
        }
    }




}
