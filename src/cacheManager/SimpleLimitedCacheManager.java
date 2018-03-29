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

public class SimpleLimitedCacheManager extends CacheManager{

    private long timeLimit;
    private long cacheSize;
    private long sizeOfCachedBlocks;

    /*Which nodes we got the best interests from. This is
    sorted. Lowest index = highest score*/
    public ArrayList<SavedNode> bestNodes;

    public SimpleLimitedCacheManager(long timeLimit,long cacheSize){
        this.timeLimit = timeLimit;
        this.cacheSize = cacheSize;
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
    public void addReceivedBlocks(ArrayList<Block> receivedBlocks, ArrayList<Block> blocksInCache) {
        /*Insert them based on the order of their indexes*/
        int start = 0;
        for(Block receivedBlock : receivedBlocks){
            /*block with greater index than the others in cache?*/
            if(receivedBlock.index > blocksInCache.get(blocksInCache.size()-1).index){
                blocksInCache.add(receivedBlock);
                sizeOfCachedBlocks += receivedBlock.blockSize;
                continue;
            }
            /*block not in cache*/
            for(int i = start; i < blocksInCache.size()-1; i++){
                if(receivedBlock.index == blocksInCache.get(start).index){
                    start = i;
                    break;
                }
                else if(receivedBlock.index > blocksInCache.get(start).index){
                    start = i + 1;
                    blocksInCache.add(i+1,receivedBlock);
                    sizeOfCachedBlocks += receivedBlock.blockSize;
                    break;
                }
            }
        }

        /*Check if there are too many blocks*/
        while(sizeOfCachedBlocks > cacheSize){
            sizeOfCachedBlocks -= blocksInCache.get(0).blockSize;
            blocksInCache.remove(0);
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
                    missMatches += Math.abs(interest.interestValues.size() - ownInterest.interestValues.size());
                }
                /*or numeric type?. Numeric are simpler*/
                else if(interest.type == Interest.NUMERIC_TYPE){
                    if(interest.numericType == Interest.NUMERIC_GREATER){
                        if(interest.numericValue == ownInterest.numericValue){
                            matches += 1;
                        }
                        else{
                            missMatches += 1;
                        }
                    }
                }
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
