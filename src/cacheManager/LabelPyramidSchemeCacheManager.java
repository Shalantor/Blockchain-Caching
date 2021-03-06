package cacheManager;

import cacheManager.CacheUtils.HitRateCostBlock;
import nodes.Node;
import org.json.JSONArray;
import org.json.JSONObject;
import structures.Block;
import structures.Interest;
import structures.SavedNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LabelPyramidSchemeCacheManager extends CacheManager{

    private long timeLimit;
    private long cacheSize;
    private long sizeOfCachedBlocks;
    private int numOfCachedBlocks;

    private float noise = 0.5f;
    private float size = 0.5f;

    //random number for accesses
    private int numAccesses;

    private Random random = new Random();

    //key is size, value is arraylist with blocks
    private HashMap<Integer,ArrayList<HitRateCostBlock>> blocksInCache;

    public LabelPyramidSchemeCacheManager(long timeLimit, long cacheSize) {
        this.timeLimit = timeLimit;
        this.cacheSize = cacheSize;
        blocksInCache = new HashMap<>();
        bestNodes = new ArrayList<>();

        //number of accesses initialization
        numAccesses = random.nextInt(100) + 100;
    }

    @Override
    public boolean addBlock(Block block){

        /*custom block. Simulate accesses with random number*/
        HitRateCostBlock hitRateCostBlock = new HitRateCostBlock(block,random.nextInt(100),numAccesses);

        /*Now check exponential size*/
        int exponentialSize = 2;

        while(exponentialSize < block.blockSize){
            exponentialSize *= 2;
        }

        /*Check if there is key with this value for size*/
        if(!blocksInCache.containsKey(exponentialSize)){
            blocksInCache.put(exponentialSize,new ArrayList<>());
        }

        /*check if cache empty*/
        if(blocksInCache.get(exponentialSize).size() == 0){
            blocksInCache.get(exponentialSize).add(hitRateCostBlock);
            sizeOfCachedBlocks += block.blockSize;
            numOfCachedBlocks++;
            checkIfSpace();
            return true;
        }

        ArrayList<HitRateCostBlock> insertList = blocksInCache.get(exponentialSize);

        /*Check last block in cache*/
        float average = sizeOfCachedBlocks/numOfCachedBlocks;
        HitRateCostBlock lastBlock = insertList.get(insertList.size()-1);
        if(lastBlock.getCalculatedScore(noise,size,average) <
                hitRateCostBlock.getCalculatedScore(noise,size,average)){
            insertList.add(hitRateCostBlock);
            sizeOfCachedBlocks += block.blockSize;
            numOfCachedBlocks++;
            checkIfSpace();
            return true;
        }

        /*Insert into sorted array list in cache*/
        for(int i = 0; i < insertList.size(); i++){
            if(insertList.get(i).getCalculatedScore(noise,size,average) >
                    hitRateCostBlock.getCalculatedScore(noise,size,average) ){
                insertList.add(i,hitRateCostBlock);
                break;
            }
        }
        sizeOfCachedBlocks += block.blockSize;
        numOfCachedBlocks++;

        /*Check if there are too many blocks*/
        checkIfSpace();

        return true;
    }

    public void checkIfSpace(){
        while (sizeOfCachedBlocks > cacheSize){
            ArrayList<HitRateCostBlock> blocksForRemove = new ArrayList<>();
            for(Map.Entry entry : blocksInCache.entrySet()){
                ArrayList<HitRateCostBlock> currentList = (ArrayList<HitRateCostBlock>) entry.getValue();
                if(currentList.size() == 0){
                    continue;
                }
                blocksForRemove.add(currentList.get(0));
            }

            float average = sizeOfCachedBlocks/numOfCachedBlocks;

            float max = -100000;
            HitRateCostBlock victim = null;
            for(HitRateCostBlock block : blocksForRemove){
                if(block.getCalculatedScore(noise,size,average) > max){
                    max = block.getCalculatedScore(noise,size,average);
                    victim = block;
                }
            }

            int sizeCategory = 2;
            while(sizeCategory < victim.getBlock().blockSize){
                sizeCategory *= 2;
            }

            sizeOfCachedBlocks -= victim.getBlock().blockSize;
            blocksInCache.get(sizeCategory).remove(victim);
            if(countTransactions){
                Block block = victim.getBlock();
                overallTransactions -= block.transactions.size();
                interestingTransactions -= transactionStats.get(block.index);
                transactionStats.remove(block.index);
            }
            numOfCachedBlocks--;
        }
    }

    @Override
    public void addReceivedBlocks(ArrayList<Block> receivedBlocks, HashMap<String,Interest> interests) {
        /*Insert them based on the order of their indexes*/
        for(Block receivedBlock : receivedBlocks){

            if(!checkBlock(receivedBlock,interests)){
                continue;
            }

            addBlock(receivedBlock);
        }
    }

    @Override
    public boolean checkBlock(Block block, Map<String,Interest> interests){

        if(countTransactions){
            int currentInteresting = 0;
            for(HashMap<String,Object> tr : block.transactions){
                overallTransactions += 1;
                for (Map.Entry entry : interests.entrySet()){
                    if(((Interest)entry.getValue()).checkTransaction(tr)){
                        interestingTransactions += 1;
                        currentInteresting += 1;
                        break;
                    }
                }
            }
            transactionStats.put(block.index,currentInteresting);
        }

        for (Map.Entry entry : interests.entrySet()){
            if(((Interest)entry.getValue()).checkBlock(block)){
                interestedBlocks += 1;
                return true;
            }
        }
        return  false;
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
        ArrayList<Block> blocks = new ArrayList<>();
        for(Map.Entry entry : blocksInCache.entrySet()){
            for(HitRateCostBlock b : (ArrayList<HitRateCostBlock>)entry.getValue()){
                blocks.add(b.getBlock());
            }
        }
        return blocks;
    }

    public long getSizeOfCachedBlocks() {
        return sizeOfCachedBlocks;
    }

    @Override
    public void clearAll(){
        sizeOfCachedBlocks = 0;
        blocksInCache.clear();
        interestedBlocks = 0;
        interestingTransactions = 0;
        overallTransactions = 0;
    }
}
