package cacheManager;

import structures.Block;
import structures.SavedNode;

import java.util.ArrayList;

public class ScoreCacheManager extends CacheManager{

    private long timeLimit;
    private long cacheSize;
    private long sizeOfCachedBlocks;

    /*Which nodes we got the best interests from. This is
    sorted. Lowest index = highest score*/
    public ArrayList<SavedNode> bestNodes;

    /*Keep the score of the blocks in a */

    public ScoreCacheManager(long timeLimit,long cacheSize){
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


}
