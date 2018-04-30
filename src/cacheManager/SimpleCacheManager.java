package cacheManager;

import nodes.Node;
import org.json.JSONArray;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;
import structures.SavedNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*The simplest manager, it just adds the block to the cache
* if there is something interesting in it.
* This class is used if there is no limit in the cache*/
public class SimpleCacheManager extends CacheManager{

    /*Cache statistics*/
    private long timeLimit;
    private long sizeOfCachedBlocks;

    /*Which nodes we got the best interests from. This is
    sorted. Lowest index = highest score*/
    public ArrayList<SavedNode> bestNodes = new ArrayList<>();


    public SimpleCacheManager(long timeLimit,long cacheSize){
        this.timeLimit = timeLimit;
    }

    @Override
    public boolean addBlock(ArrayList<Block> blocksInCache, Block block){

        /*check if cache empty*/
        if(blocksInCache.size() == 0){
            blocksInCache.add(block);
            sizeOfCachedBlocks += block.blockSize;
            return true;
        }

        /*Check last block in cache*/
        if(blocksInCache.get(blocksInCache.size() - 1).index < block.index){
            blocksInCache.add(block);
            sizeOfCachedBlocks += block.blockSize;
            return true;
        }
        else if(blocksInCache.get(blocksInCache.size() - 1).index == block.index){
            return false;
        }

        /*Insert into sorted array list in cache*/
        for(int i = 0; i < blocksInCache.size(); i++){
            if(blocksInCache.get(i).index > block.index ){
                blocksInCache.add(i,block);
                break;
            }
            else if(blocksInCache.get(i).index == block.index ){
                return false;
            }
        }
        sizeOfCachedBlocks += block.blockSize;
        return true;
    }

    @Override
    public void removeOldBlocks(ArrayList<Block> blocksInCache){
        /*Binary search blocks and then remove. Blocks in cache are order with timestamps*/
        int size = blocksInCache.size();
        int start = 0,end = size-1;
        int pos = (start + end ) / 2;
        long currentTime;
        Block currentBlock;

        currentTime = System.currentTimeMillis();

        /*Check edge cases*/
        if(currentTime - blocksInCache.get(0).timestamp < timeLimit){
            return;
        }
        else if(currentTime - blocksInCache.get(end).timestamp > timeLimit){
            blocksInCache.clear();
            sizeOfCachedBlocks = 0;
            return;
        }

        while(start != end){
            currentBlock = blocksInCache.get(pos);

            if(currentTime - currentBlock.timestamp > timeLimit){
                start = pos;
                pos = (int) Math.ceil((start + end) * 1.0f / 2);
                if(start == end - 1){
                    break;
                }
            }
            else if(currentTime - currentBlock.timestamp < timeLimit){
                end = pos;
                pos = (int) Math.floor((start + end) * 1.0f / 2);
            }
        }

        /*Now remove blocks from list*/
        for(int i =0; i <= start; i++){
            sizeOfCachedBlocks -= blocksInCache.get(0).blockSize;
            blocksInCache.remove(0);
        }

    }

    @Override
    public void addReceivedBlocks(ArrayList<Block> receivedBlocks,ArrayList<Block> blocksInCache){

        /*Insert them based on the order of their indexes*/
        int start = 0;
        for(Block receivedBlock : receivedBlocks){
            /*Cache empty?*/
            if(blocksInCache.size() == 0){
                blocksInCache.add(receivedBlock);
                sizeOfCachedBlocks += receivedBlock.blockSize;
                continue;
            }
            /*block with greater index than the others in cache?*/
            if(receivedBlock.index > blocksInCache.get(blocksInCache.size()-1).index){
                blocksInCache.add(receivedBlock);
                sizeOfCachedBlocks += receivedBlock.blockSize;
                continue;
            }
            /*insert in sorted array list*/
            for(int i = start; i < blocksInCache.size(); i++){
                if(receivedBlock.index == blocksInCache.get(i).index){
                    start = i;
                    break;
                }
                else if(receivedBlock.index < blocksInCache.get(i).index){
                    start = i;
                    blocksInCache.add(i,receivedBlock);
                    sizeOfCachedBlocks += receivedBlock.blockSize;
                    break;
                }
            }
        }

    }

    @Override
    public boolean checkBlock(Block block, Map<String,Interest> interests){
        for (Map.Entry entry : interests.entrySet()){
            if(((Interest)entry.getValue()).checkBlock(block)){
                return true;
            }
        }
        return  false;
    }

    /*TODO: Make nodes use this*/
    @Override
    public void evaluateInterests(JSONObject receivedInterests,
                                  HashMap<String,Interest> ownInterests,
                                  Node node){

        /*Best case scenario is when the manager finds interests
        * that are exactly the same with its own interests. Else, find
        * interests with as little difference as possible*/
        JSONArray interests = receivedInterests.getJSONArray("interests");

        Interest interest,ownInterest;
        int matches=0,missMatches=0;

        for(int i = 0; i < interests.length(); i++){

            /*Extract interest*/
            interest = node.JSONToInterest(interests.getJSONObject(i));

            /*Is there an interest like this in my own interests?*/
            if(ownInterests.containsKey(interest.interestName)){
                ownInterest = ownInterests.get(interest.interestName);

                /*String type?*/
                if(interest.type == Interest.STRING_TYPE){
                    for(String value: interest.interestValues){
                        if(ownInterest.interestValues.contains(value)){
                            matches += 1;
                        }
                        else{
                            missMatches += 1;
                        }
                    }
                    /*Different size means that there are different interests*/
                    if(interest.interestValues.size() < ownInterest.interestValues.size()){
                        missMatches += ownInterest.interestValues.size() - interest.interestValues.size();
                    }
                }
                /*or numeric type?. Numeric are simpler*/
                else if(interest.type == Interest.NUMERIC_TYPE){
                    if(interest.numericType == ownInterest.numericType){
                        String interestString,ownInterestString;
                        interestString = interest.numericValue + "";
                        ownInterestString = ownInterest.numericValue + "";
                        if(interestString.equals(ownInterestString)){
                            matches += 1;
                        }
                        else{
                            missMatches += 1;
                        }
                    }
                }
            }
            else{
                missMatches += 2;
            }
        }

        /*Now check matches and miss matches and compare to others*/
        SavedNode savedNode = new SavedNode(receivedInterests.getString("host"),
                receivedInterests.getInt("port"), matches - missMatches);

        /*Is list empty? then just add, else insert in sorted list*/
        if(bestNodes.isEmpty()){
            bestNodes.add(savedNode);
        }
        else{
            /*Best node at start of list*/
            for(int i =0; i < bestNodes.size(); i++){
                if(bestNodes.get(i).score < savedNode.score){
                    bestNodes.add(i,savedNode);
                }
            }
        }

    }

    @Override
    public void removeSavedNodes(){
        bestNodes.clear();
    }

}
