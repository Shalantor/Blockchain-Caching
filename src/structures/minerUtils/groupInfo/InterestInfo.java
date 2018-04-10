package structures.minerUtils.groupInfo;

import java.util.ArrayList;

public class InterestInfo {

    /*This info is for all interests*/
    private ArrayList<Integer> indices;
    private String type; /*Integer, Double, Long or String*/
    private Integer count;

    /*Further info for numeric interests*/
    private String comparison; /*greater or lower*/
    private Object value;      /*A numeric value*/

    public InterestInfo(String type){
        this.type = type;
        indices = new ArrayList<>();
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public void setIndices(ArrayList<Integer> indices) {
        this.indices = indices;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
