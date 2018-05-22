package cacheManager.CacheUtils;

import structures.Block;

public class HitRateCostBlock {

    private Block block;
    private int accessRate;

    public HitRateCostBlock(Block block, int accessRate){
        this.block = block;
        this.accessRate = accessRate;
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
}
