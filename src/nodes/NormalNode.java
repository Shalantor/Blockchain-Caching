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
    private List<Interest> interests = new ArrayList<>();
    private List<String> interestNames;


    /*The constructor as of now*/
    /*TODO:Change default weight*/
    public NormalNode(List<String> interests){
        for(String interest: interests){
            this.interests.add(new Interest(interest,1));
        }
        this.interestNames = interests;
    }

    public NormalNode(List<String> interests,long maxCacheSize, long timeRestraint){
        this(interests);
        this.maxCacheSize = maxCacheSize;
        this.timeRestraint = timeRestraint;
    }

    /*TODO:This is the main point that needs testing to see efficiency of different algorithms*/
    /*Inspect block to add it to interests or not*/
    public void inspectBlock(Block block){
        for(HashMap<String,Object> transaction : block.transactions){
            for(Map.Entry entry : transaction.entrySet()){
                for(int i=0; i < interestNames.size(); i++){
                    if(entry.getKey().toString().equals(interestNames.get(i))){
                        interests.get(i).addBlock(block);
                        break;
                    }
                }
            }
        }
    }

}
