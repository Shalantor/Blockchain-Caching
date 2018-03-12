package nodes;

import structures.Block;

import java.util.ArrayList;

/*implementation of the miner node*/
public class MinerNode {

    /*Last block in blockchain*/
    private Block lastBlock;

    /*List of transactions to put into the new block*/
    private ArrayList<Block> transactions;

    public MinerNode(Block block,String configFilePath){
        lastBlock = block;
    }


}
