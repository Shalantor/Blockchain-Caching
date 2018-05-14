package structures.managerUtils;

public class IntegerInterest {

    private String name;
    private Integer minValue;
    private Integer maxValue;
    private int breakpoints;

    public IntegerInterest(String name,Integer minValue, Integer maxValue, int breakpoints){
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.breakpoints = breakpoints;
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

    public int getBreakpoints(){
        return breakpoints;
    }
}
