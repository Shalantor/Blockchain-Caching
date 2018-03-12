package structures;

import java.util.ArrayList;
import java.util.List;

public class Interest {

    private String name;
    private List<Block> blocks = new ArrayList<>();
    private int weight;
    private long size;

    public Interest(String name,int weight){
        this.name = name;
        this.weight = weight;
    }

    public Interest(String name,int weight, List<Block> initialBlocks){
        this(name,weight);
        blocks = initialBlocks;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void calculateBlocksSize(){
        long size = 0;
        for(Block block : blocks){
            size += block.blockSize;
        }
        this.size = size;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public long getSize(){
        return size;
    }

    public void addBlock(Block block){
        blocks.add(block);
    }

    public void removeBlock(int index){
        blocks.remove(index);
    }

    public String getName(){
        return name;
    }
}
