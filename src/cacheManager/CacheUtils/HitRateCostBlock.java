package cacheManager.CacheUtils;

import structures.Block;

public class HitRateCostBlock implements Comparable<HitRateCostBlock>{

    private Block block;
    private int accessRate;
    private long score;

    public HitRateCostBlock(Block block, int accessRate, int numAccesses){
        this.block = block;
        this.accessRate = accessRate;
        this.score = block.blockSize * (numAccesses - accessRate);
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getAccessRate() {
        return accessRate;
    }

    public void setAccessRate(int accessRate) {
        this.accessRate = accessRate;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public int compareTo(HitRateCostBlock block){
        return (int)(score - block.getScore());
    }

}
