package nodes;

import structures.Block;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*implementation of the miner node*/
public class MinerNode {

    /*Available configurations*/
    public static final String MIN_BLOCK_SIZE = "min_block_size";
    public static final String MAX_BLOCK_SIZE = "max_block_size";
    public static final String GROUP_CONTENT = "group_content";

    public static final int NO_GROUP = -1;

    /*Store those configurations*/
    private long minBlockSize;
    private long maxBlockSize;
    private int groupContent = NO_GROUP;

    /*Last block in blockchain*/
    private Block lastBlock;

    /*List of transactions to put into the new block*/
    private ArrayList<Block> transactions;

    public MinerNode(Block block,String configFilePath) {
        lastBlock = block;

        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line, key, value;
            String[] info;

            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }

                /*get key and value*/
                info = line.split("\\s+");
                key = info[0];
                value = info[1];
                switch(key){
                    case MIN_BLOCK_SIZE:
                        minBlockSize = Long.parseLong(value);
                        break;
                    case MAX_BLOCK_SIZE:
                        maxBlockSize = Long.parseLong(value);
                        break;
                    case GROUP_CONTENT:
                        groupContent = Boolean.parseBoolean(value) ? 0 : NO_GROUP;
                        break;
                }

            }
        }
        catch(IOException ex){
                System.out.println("Io exception occurred");
                ex.printStackTrace();
        }

        System.out.println(minBlockSize);
        System.out.println(maxBlockSize);
        System.out.println(groupContent);

    }

}
