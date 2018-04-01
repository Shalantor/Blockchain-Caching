package structures.managerUtils;

public class DoubleInterest extends GeneratedInterest{

    private String name;
    private Double minValue;
    private Double maxValue;

    public DoubleInterest(String name,Double minValue, Double maxValue){
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
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
}
