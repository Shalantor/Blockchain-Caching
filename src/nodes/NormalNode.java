package nodes;

import structures.Block;
import structures.TransactionManager;

import java.util.*;

/*Implementation of the normal node*/
public class NormalNode {

    /*value indicating no cache size limit*/
    private static final int NO_CACHE_LIMIT = -1;

    /*Does the node have a maximum cache size?*/
    private long maxCacheSize = NO_CACHE_LIMIT;

    /*List of this nodes interests*/
    private Map<String,List<Block>> interestingBlocks = new HashMap<>();


    /*The constructor as of now*/
    public NormalNode(List<String> interests){
        for(String interest: interests){
            interestingBlocks.put(interest,new ArrayList<>());
        }
    }

    public NormalNode(List<String> interests,long maxCacheSize){
        this(interests);
        this.maxCacheSize = maxCacheSize;
    }

}
