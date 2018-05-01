package cacheManager.CacheUtils;

import structures.Block;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreBlock that = (ScoreBlock) o;
        return block.index ==  that.block.index;
    }

    @Override
    public int hashCode() {

        return Objects.hash(block.index, score);
    }
}
