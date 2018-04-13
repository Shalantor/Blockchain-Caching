package structures.minerUtils.groupInfo;

import java.util.ArrayList;

public class InterestInfo implements Comparable<InterestInfo> {

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
    private boolean isRange;
    private String name;

    /*Further info for numeric interests*/
    private String comparison; /*greater or lower*/
    private Object value;      /*A numeric value*/
    private Object min;
    private Object max;
    private int breakpoints;

    public InterestInfo(boolean isRange,String type,String name){
        indices = new ArrayList<>();
        this.isRange = isRange;
        this.type = type;
        this.name = name;
    }

    public InterestInfo(String comparison,Object[] values,int breakpoints,String type,String name){
        this(false,type,name);
        this.comparison = comparison;
        this.value = values[0];
        min = values[1];
        max = values[2];
        this.breakpoints = breakpoints;
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

    public void setIndices(ArrayList<Integer> indices) {
        this.indices = indices;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(InterestInfo info){
        return count - info.count;
    }

    @Override
    public String toString(){
        return name + " : " + count;
    }
}
