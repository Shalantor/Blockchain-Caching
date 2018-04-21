package storage;

import storage.storageUtils.BlockExplorer;
import structures.Block;
import structures.Interest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*Super class for storage. Currently only used by the full node to index transactions and blocks.
* But could also be used by a normal node in a future udpate*/
public class StorageManager {
    public HashMap<String,ArrayList<BlockExplorer>> blockChainIndex;

    /*hash map with type of transaction attributes*/
    public HashMap<String,String> types;

    public StorageManager(String transactionPath){
        types = new HashMap<>();
        /*Get configurations*/
        try (BufferedReader br = new BufferedReader(new FileReader(transactionPath))) {
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

                types.put(key,value);

            }
        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }
    }

    public void addBlock(Block block){

    }

    public ArrayList<Block> getSeparateBlocks(List<Integer> indexes){
        return null;
    }

    public ArrayList<Block> getBlocksInIntervals(List<Integer> indexes){
        return null;
    }

    public int getSize(){
        return 0;
    }

    public void indexBlock(Block block){

    }

    public ArrayList<Block> getBlockFromInterests(ArrayList<Interest> interests){
        return null;
    }
}
