package cacheManager;

import cacheManager.CacheUtils.ScoreBlock;
import nodes.Node;
import org.json.JSONArray;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;
import structures.SavedNode;

import java.util.*;
import java.util.function.Predicate;

public class ThreshHoldCacheManager extends CacheManager{

    private long timeLimit;
    private long cacheSize;
    private long sizeOfCachedBlocks;

    /*Block must have at least this score*/
    private int scoreBound;

    /*Keep the score of the blocks in an array list*/
    private ArrayList<ScoreBlock> blocksInCache;

    /*Latest score*/
    private int latestScore;

    public ThreshHoldCacheManager(long timeLimit, long cacheSize, int scoreBound){
        this.timeLimit = timeLimit;
        this.cacheSize = cacheSize;
        this.scoreBound = scoreBound;
        blocksInCache = new ArrayList<>();
        bestNodes = new ArrayList<>();
    }

    @Override
    public boolean addBlock(Block block){

        ScoreBlock scoreBlock = new ScoreBlock(block,latestScore);

        /*check if cache empty*/
        if(blocksInCache.size() == 0){
            blocksInCache.add(scoreBlock);
            sizeOfCachedBlocks += block.blockSize;
            return true;
        }

        if(blocksInCache.contains(scoreBlock)){
            return false;
        }

        /*last block*/
        if(blocksInCache.get(blocksInCache.size()-1).getScore() <= scoreBlock.getScore()){
            blocksInCache.add(scoreBlock);
            return true;
        }

        /*Insert into sorted array list in cache*/
        for(int i = 0; i < blocksInCache.size(); i++){
            if(blocksInCache.get(i).getScore() >= scoreBlock.getScore() ){
                blocksInCache.add(i,scoreBlock);
                break;
            }
        }

        sizeOfCachedBlocks += block.blockSize;

        /*Check if there are too many blocks*/
        if(sizeOfCachedBlocks > cacheSize){
            sizeOfCachedBlocks -= blocksInCache.get(0).getBlock().blockSize;
            blocksInCache.remove(0);
        }

        return true;
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

        /*Check if there are too many blocks*/
        while(sizeOfCachedBlocks > cacheSize){
            sizeOfCachedBlocks -= blocksInCache.get(0).getBlock().blockSize;
            blocksInCache.remove(0);
        }
    }

    @Override
    public void removeOldBlocks(){
        long currentTime;

        currentTime = System.currentTimeMillis();

        /*Remove old blocks. Wow syntax*/
        Predicate<ScoreBlock> predicate = p -> currentTime - p.getBlock().timestamp > timeLimit;
        blocksInCache.removeIf(predicate);

    }

    @Override
    public boolean checkBlock(Block block, Map<String,Interest> interests){

        latestScore = calculateScore(block,interests);

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

        ArrayList<Block> returnBlocks = new ArrayList<>();

        for(ScoreBlock scoreBlock : blocksInCache){
            returnBlocks.add(scoreBlock.getBlock());
        }

        return returnBlocks;
    }
}
