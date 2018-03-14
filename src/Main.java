import nodes.FullNode;
import nodes.MinerNode;
import nodes.NormalNode;
import structures.Block;
import structures.TransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TransactionManager manager = new TransactionManager("src/test/resources/example.txt");


        /*Test block*/
        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
        /*for(int i =0; i < 10; i ++){
            transactions.add(manager.createRandomTransaction());
        }*/

        /*Test full node*/
        /*FullNode fullNode = new FullNode(new Block(0,"qwe",transactions));

        for(int i =0; i < 10; i ++){
            fullNode.addBlock(new Block(i+1,"",transactions));
        }

        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(0);
        indices.add(4);
        indices.add(8);
        indices.add(8);

        List<Block> blocks =  fullNode.getBlocksInIntervals(indices);

        for(Block block: blocks){
            System.out.println(block.index);
        }
        MinerNode miner = new MinerNode(new Block(0,"genesis",transactions),
                "src/config/miner.txt",manager.getKeys());

        for(int i =0; i < 50; i ++){
            miner.addTransaction(transactions.get(0));
        }*/
        NormalNode normal = new NormalNode("src/config/normal_node_config.txt",
                "src/config/normal_node_interests.txt");


        transactions.add(manager.createTransaction(new ArrayList<>(
                Arrays.asList("a","noob","pilafi",20.0,"batman",50)
        )));

        //normal.printInterests();

        Block block = new Block(0,"genesis",transactions);
        normal.checkBlock(block);

    }
}
