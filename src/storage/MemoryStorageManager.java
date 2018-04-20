package storage;

import storage.storageUtils.BlockExplorer;
import structures.Block;

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
}
