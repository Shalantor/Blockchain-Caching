package cacheManager;

import structures.Block;

import java.util.ArrayList;

public class SimpleLimitedCacheManager extends CacheManager{

    private long timeLimit;
    private long cacheSize;

    public SimpleLimitedCacheManager(long timeLimit,long cacheSize){
        this.timeLimit = timeLimit;
        this.cacheSize = cacheSize;
    }

    @Override
    public boolean addBlock(ArrayList<Block> blocksInCache, Block block){
        blocksInCache.add(block);
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
            blocksInCache.remove(0);
        }

    }
}
