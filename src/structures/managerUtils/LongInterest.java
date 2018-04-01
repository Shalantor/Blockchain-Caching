package structures.managerUtils;

public class LongInterest {

    private String name;
    private Long minValue;
    private Long maxValue;

    public LongInterest(String name,Long minValue, Long maxValue){
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
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
}
