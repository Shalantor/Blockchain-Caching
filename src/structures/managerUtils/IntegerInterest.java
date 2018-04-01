package structures.managerUtils;

public class IntegerInterest {

    private String name;
    private Integer minValue;
    private Integer maxValue;

    public IntegerInterest(String name,Integer minValue, Integer maxValue){
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }
}
