package structures.managerUtils;

import java.util.ArrayList;
import java.util.List;

public class StringInterest {

    private String name;
    private List<String> possibleValues;
    private String rangeName;
    private int rangeStart;
    private int rangeEnd;

    public StringInterest(String name,ArrayList<String> possibleValues){
        this.name = name;
        this.possibleValues = possibleValues.subList(0,possibleValues.size());
    }

    public StringInterest(String name,String rangeName,int rangeStart,int rangeEnd){
        this.name = name;
        this.rangeName = rangeName;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        possibleValues = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public String getRangeName() {
        return rangeName;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }
}
