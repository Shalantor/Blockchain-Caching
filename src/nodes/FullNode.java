package nodes;

import structures.Block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*Has all the block chain stored*/
public class FullNode extends Node{

    private List<Block> blockChain = new LinkedList<>();

    /*Initialize with genesis block*/
    public FullNode(Block genesisBlock,int port,int timeOut){
        super(port,timeOut);
        blockChain.add(genesisBlock);
    }

    public void addBlock(Block block){
        blockChain.add(block);
    }

    /*Some node wants separate blocks*/
    public ArrayList<Block> getSeparateBlocks(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();
        for(Integer index: indexes){
            blocks.add(blockChain.get(index));
        }
        return blocks;
    }

    /*Some node wants block in intervals*/
    public ArrayList<Block> getBlocksInIntervals(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();
        for(int i=0; i < indexes.size(); i += 2){
            blocks.addAll(blockChain.subList(indexes.get(i),indexes.get(i+1)+1 ));
        }
        return blocks;
    }

    public int getSize(){
        return blockChain.size();
    }

}
