package structures.managerUtils;

public class LongInterest {

    private String name;
    private Long minValue;
    private Long maxValue;
    private int breakpoints;

    public LongInterest(String name,Long minValue, Long maxValue,int breakpoints){
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.breakpoints = breakpoints;
    }

    public String getName() {
        return name;
    }

    public Long getMinValue() {
        return minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public int getBreakpoints(){
        return breakpoints;
    }
}
