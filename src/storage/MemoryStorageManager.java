package storage;

import storage.storageUtils.BlockExplorer;
import structures.Block;
import structures.Interest;

import javax.print.DocFlavor;
import java.util.*;

public class MemoryStorageManager extends StorageManager{

    /*List representing the blockchain*/
    private List<Block> blockChain;
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";

    /*Hashmap that acts as an index. Strings are just key values. Key is the
    * value for the attribute and key is a list of indices.
    * For numeric values it is a little more complex. The key is again the attribute value
    * but the value leads to an array sorted by the values.*/
    private HashMap<String,ArrayList<BlockExplorer>> blockChainIndex;

    public MemoryStorageManager(String transactionPath){
        super(transactionPath);
        blockChain = new ArrayList<>();
        blockChainIndex = new HashMap<>();
    }

    @Override
    public void addBlock(Block block){
        blockChain.add(block);
    }

    @Override
    public ArrayList<Block> getSeparateBlocks(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();
        for(Integer index: indexes){
            blocks.add(blockChain.get(index));
        }
        return blocks;
    }

    @Override
    public ArrayList<Block> getBlocksInIntervals(List<Integer> indexes){
        ArrayList<Block> blocks = new ArrayList<>();
        for(int i=0; i < indexes.size(); i += 2){
            blocks.addAll(blockChain.subList(indexes.get(i),indexes.get(i+1)+1 ));
        }
        return blocks;
    }

    @Override
    public int getSize(){
        return blockChain.size();
    }

    @Override
    public void indexBlock(Block block){
        /*Loop over transactions and index them*/
        for(HashMap<String,Object> tr : block.transactions){
            for(String key : tr.keySet()){

                /*Case of string variable*/
                if(types.get(key).equals(STRING)){
                    String value = (String) tr.get(key);
                    if(blockChainIndex.get(value) == null){
                        ArrayList<BlockExplorer> list = new ArrayList<>();
                        list.add(new BlockExplorer(block.index));
                        blockChainIndex.put(value,list);
                    }
                    else{
                        blockChainIndex.get(value).add(new BlockExplorer(block.index));
                    }
                }
                /*Case of numeric variable*/
                else{

                    /*check which type*/
                    Object value = null;
                    if(types.get(key).equals(DOUBLE)){
                        value = (Double) tr.get(key);
                    }
                    else if(types.get(key).equals(LONG)){
                        value = (Long) tr.get(key);
                    }
                    else if(types.get(key).equals(INTEGER)){
                        value = (Integer) tr.get(key);
                    }

                    if(blockChainIndex.get(key) == null){
                        ArrayList<BlockExplorer> list = new ArrayList<>();
                        list.add(new BlockExplorer(block.index,value));
                        blockChainIndex.put(key,list);
                    }
                    else{
                        ArrayList<BlockExplorer> list = blockChainIndex.get(key);
                        BlockExplorer explorer = new BlockExplorer(block.index,value);

                        /*binary search collection*/
                        int pos = Collections.binarySearch(list,explorer,BlockExplorer::compareTo);

                        /*check result*/
                        if(pos >= 0){
                            list.add(pos,explorer);
                        }
                        else{
                            list.add(Math.abs(pos+1),explorer);
                        }
                    }
                }

            }
        }
    }

    @Override
    public ArrayList<Block> getBlockFromInterests(ArrayList<Interest> interests){
        ArrayList<Block> blocks = new ArrayList<>();

        for(Interest i : interests){
            if(i.type == Interest.STRING_TYPE){

                /*values that the node is interested in*/
                for(String value : i.interestValues){
                    ArrayList<BlockExplorer> indices = blockChainIndex.get(value);
                    for(BlockExplorer b : indices){
                        blocks.add(blockChain.get(b.blockIndex));
                    }
                }

            }
            else if(i.type == Interest.NUMERIC_TYPE){

                /*Check type*/
                Object value = null;
                String name = i.interestName;
                if(types.get(name).equals(DOUBLE)){
                    value = (Double) i.numericValue;
                }
                else if(types.get(name).equals(LONG)){
                    value = (Long) i.numericValue;
                }
                else if(types.get(name).equals(INTEGER)){
                    value = (Integer) i.numericValue;
                }

                /*Get list of block explorers and create new one to search*/
                ArrayList<BlockExplorer> list = blockChainIndex.get(name);
                BlockExplorer explorer = new BlockExplorer(0,value);

                /*Binary search*/
                int pos = Collections.binarySearch(list,explorer,BlockExplorer::compareTo);

                /*Not found adjust position*/
                if(pos < 0){
                    pos = Math.abs(pos + 1);
                }

                for(int j = 0; j < pos; j++){
                    blocks.add(blockChain.get(list.get(0).blockIndex));
                }

            }
        }

        /*Now remove duplicates*/
        

        return blocks;
    }
}
