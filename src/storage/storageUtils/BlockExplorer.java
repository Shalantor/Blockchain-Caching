package storage.storageUtils;

/*Class to index transactions in blockchain*/
public class BlockExplorer implements Comparable<BlockExplorer>{

    /*Index in blockchain*/
    public int blockIndex;

    /*Value, only used for numeric values*/
    public Object value;

    public BlockExplorer(int blockIndex){
        this.blockIndex = blockIndex;
    }

    public BlockExplorer(int blockIndex, Object value){
        this(blockIndex);
        this.value = value;
    }

    @Override
    public int compareTo(BlockExplorer explorer){
        if(value instanceof Double){
            Double diff = (Double) value - (Double) explorer.value;
            if(diff > 0){
                return 1;
            }
            else if(diff == 0){
                return 0;
            }
            else{
                return -1;
            }
        }
        else if(value instanceof Long){
            Long diff = (Long) value - (Long) explorer.value;
            if(diff > 0){
                return 1;
            }
            else if(diff == 0){
                return 0;
            }
            else{
                return -1;
            }
        }
        else if(value instanceof Integer){
            return (Integer) value - (Integer) explorer.value;
        }
        return 0;
    }

    @Override
    public String toString(){
        return this.blockIndex + "";
    }

}
