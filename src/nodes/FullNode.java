package nodes;

import structures.Block;
import java.util.LinkedList;
import java.util.List;

/*Has all the block chain stored*/
public class FullNode {

    private List<Block> blockChain = new LinkedList<>();

    /*Initialize with genesis block*/
    public FullNode(Block genesisBlock){
        blockChain.add(genesisBlock);
    }

    public void addBlock(Block block){
        blockChain.add(block);
    }

    /*Some node wants separate blocks*/
    public List<Block> getSeparateBlocks(List<Integer> indexes){
        List<Block> blocks = new LinkedList<>();
        for(Integer index: indexes){
            blocks.add(blockChain.get(index));
        }
        return blocks;
    }

    /*Some node wants block in intervals*/
    public List<Block> getBlocksInIntervals(List<Integer> indexes){
        List<Block> blocks = new LinkedList<>();
        for(int i=0; i < indexes.size(); i += 2){
            blocks.addAll(blockChain.subList(indexes.get(i),indexes.get(i+2)));
        }
        return blocks;
    }

}
