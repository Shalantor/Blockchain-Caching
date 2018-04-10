package structures.minerUtils.groupInfo;

import java.util.ArrayList;

public class InterestInfo {

    /*Possible names for type values*/
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";
    private static final String GREATER = "greater";
    private static final String LOWER = "lower";

    /*This info is for all interests*/
    private ArrayList<Integer> indices;
    private String type; /*Integer, Double, Long or String*/
    private Integer count = 0;

    /*Further info for numeric interests*/
    private String comparison; /*greater or lower*/
    private Object value;      /*A numeric value*/

    public InterestInfo(){
        indices = new ArrayList<>();
    }

    public InterestInfo(String comparison,Object value){
        this();
        this.comparison = comparison;
        this.value = value;
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public String getType() {
        return type;
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
        if(type.equals(DOUBLE)){
            return (Double) value;
        }
        else if(type.equals(INTEGER)){
            return (Integer) value;
        }
        else if(type.equals(LONG)){
            return (Long) value;
        }
        return value;
    }

    public void addIndex(int index){
        indices.add(index);
    }
}
