package structures.managerUtils;

import java.util.ArrayList;
import java.util.List;

public class StringInterest {

    private String name;
    private List<String> possibleValues;

    public StringInterest(String name,ArrayList<String> possibleValues){
        this.name = name;
        this.possibleValues = possibleValues.subList(0,possibleValues.size());
    }

    public String getName() {
        return name;
    }

    public List<String> getPossibleValues() {
        return possibleValues;
    }
}
