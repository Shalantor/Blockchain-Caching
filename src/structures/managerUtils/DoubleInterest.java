package structures.managerUtils;

public class DoubleInterest {

    private String name;
    private Double minValue;
    private Double maxValue;
    private int breakpoints;

    public DoubleInterest(String name,Double minValue, Double maxValue, int breakpoints){
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.breakpoints = breakpoints;
    }

    public String getName() {
        return name;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public int getBreakpoints(){
        return breakpoints;
    }
}
