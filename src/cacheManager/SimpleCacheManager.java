package cacheManager;

import structures.Block;

import java.util.ArrayList;

/*The simplest manager, it just adds the block to the cache
* if there is something interesting in it.
* This class is used if there is no limit in the cache*/
public class SimpleCacheManager extends CacheManager{

    private long timeLimit;

    public SimpleCacheManager(long timeLimit,long cacheSize){
        this.timeLimit = timeLimit;
    }

    @Override
    public boolean addBlock(ArrayList<Block> blocksInCache, Block block){
        blocksInCache.add(block);
        return true;
    }

    @Override
    public void removeOldBlocks(ArrayList<Block> blocksInCache){
        /*Binary search blocks and then remove. Blocks in cache are order with timestamps*/
        int size = blocksInCache.size();
        int start = 0,end = size;
        int pos;
        long currentTime;
        Block currentBlock;

        while(start != end){
            pos = (start + end) / 2;
            currentTime = System.currentTimeMillis();
            currentBlock = blocksInCache.get(pos);

            if(currentTime - currentBlock.timestamp > timeLimit){
                start = pos;
            }
            else if(currentTime - currentBlock.timestamp < timeLimit){
                end = pos;
            }
        }

        /*Now remove blocks from list*/
        for(int i =0; i < start; i++){
            blocksInCache.remove(0);
        }

    }


}
