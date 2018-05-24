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

public class ThreshHoldRecencyCacheManager extends CacheManager{

    private long timeLimit;
    private long cacheSize;
    private long sizeOfCachedBlocks;

    /*Block must have at least this score*/
    private int scoreBound;

    /*Keep the score of the blocks in an array list*/
    private ArrayList<Block> blocksInCache;

    /*Latest score*/
    private int latestScore;

    public ThreshHoldRecencyCacheManager(long timeLimit, long cacheSize, int scoreBound){
        this.timeLimit = timeLimit;
        this.cacheSize = cacheSize;
        this.scoreBound = scoreBound;
        blocksInCache = new ArrayList<>();
        bestNodes = new ArrayList<>();
    }

    @Override
    public boolean addBlock(Block block){


        /*check if cache empty*/
        if(blocksInCache.size() == 0){
            blocksInCache.add(block);
            sizeOfCachedBlocks += block.blockSize;
            return true;
        }

        if(blocksInCache.contains(block)){
            return false;
        }

        /*last block*/
        if(blocksInCache.get(blocksInCache.size()-1).timestamp <= block.timestamp){
            blocksInCache.add(block);
            sizeOfCachedBlocks += block.blockSize;
            checkIfSpace();
            return true;
        }

        /*Insert into sorted array list in cache*/
        for(int i = 0; i < blocksInCache.size(); i++){
            if(blocksInCache.get(i).timestamp >= block.timestamp ){
                blocksInCache.add(i,block);
                break;
            }
        }

        sizeOfCachedBlocks += block.blockSize;

        checkIfSpace();

        return true;
    }

    public void checkIfSpace(){
        /*Check if there are too many blocks*/
        while (sizeOfCachedBlocks > cacheSize){
            sizeOfCachedBlocks -= blocksInCache.get(0).blockSize;
            blocksInCache.remove(0);
        }
    }

    @Override
    public void addReceivedBlocks(ArrayList<Block> receivedBlocks, HashMap<String,Interest> interests) {
        /*Insert them based on the order of their indexes*/
        for(Block receivedBlock : receivedBlocks) {

            if (!checkBlock(receivedBlock, interests)) {
                continue;
            }

            addBlock(receivedBlock);
        }
    }

    @Override
    public boolean checkBlock(Block block, Map<String,Interest> interests){

        latestScore = calculateScore(block,interests);

        if(latestScore >= scoreBound){
            interestedBlocks += 1;
        }

        return latestScore >= scoreBound;
    }

    private int calculateScore(Block block, Map<String,Interest> interests){
        int score = 0;
        for (Map.Entry entry : interests.entrySet()){
            Interest interest = (Interest)entry.getValue();
            score += interest.weight * interest.checkBlockScore(block);
        }
        return score;
    }

    /*TODO: make nodes use this*/
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
            for(int i =0; i < bestNodes.size(); i++){
                if(bestNodes.get(i).score <= savedNode.score){
                    bestNodes.add(i,savedNode);
                    break;
                }
            }
        }

    }

    @Override
    public void removeSavedNodes(){
        bestNodes.clear();
    }

    public ArrayList<Block> getBlocksInCache() {

        return blocksInCache;
    }

    public long getSizeOfCachedBlocks() {
        return sizeOfCachedBlocks;
    }

    @Override
    public void clearAll(){
        sizeOfCachedBlocks = 0;
        blocksInCache.clear();
        latestScore = 0;
        interestedBlocks = 0;
    }
}
