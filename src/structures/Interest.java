package structures;

import java.util.ArrayList;
import java.util.List;

public class Interest {

    private String name;
    private List<Block> blocks = new ArrayList<>();
    private int weight;

    public Interest(String name,int weight){
        this.name = name;
        this.weight = weight;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

}
