package cacheManager.CacheUtils;

import structures.Block;

public class ScoreBlock {

    private Block block;
    private int score;

    public ScoreBlock(Block block, int score){
        this.block = block;
        this.score = score;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
