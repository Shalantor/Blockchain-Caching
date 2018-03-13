package structures;

import java.util.ArrayList;
import java.util.HashMap;

/*An interest can be either for numeric or string types of variables*/
public class Interest {

    /*Constants for interest modes*/
    public final static int NUMERIC_TYPE = 0;
    public final static int STRING_TYPE = 1;
    public final static int NUMERIC_LOWER = 0;
    public final static int NUMERIC_GREATER = 1;

    /*Variables for the actual modes*/
    private int type;
    private int numericType;
    private Object numericValue;
    private int weight ;

    /*Name of interest*/
    private ArrayList<String> interestValues;
    private String interestName;

    public Interest(int type, int numericType,int weight, String name,Object numericValue, ArrayList<String> values){
        this.type = type;
        this.numericType = numericType;
        interestName = name;
        this.numericValue = numericValue;
        interestValues = values;
        this.weight = weight;
    }

    public boolean checkBlock(Block block){
        /*TODO:Make loops more efficient*/

        if(type == STRING_TYPE){
            String value;
            for(HashMap<String,Object> transaction : block.transactions){
                value = transaction.get(interestName).toString();
                if(interestValues.contains(value)){
                    return true;
                }
            }
        }
        else if(type == NUMERIC_TYPE){
            if(numericType == NUMERIC_GREATER){
                Object value;
                for(HashMap<String,Object> transaction : block.transactions){
                    value = transaction.get(interestName);
                    if(value instanceof Double){
                        if( Double.parseDouble(numericValue.toString()) >=
                                Double.parseDouble(value.toString()) ){
                            return true;
                        }
                    }
                    else if(value instanceof Long){
                        if( Long.parseLong(numericValue.toString()) >=
                                Long.parseLong(value.toString()) ){
                            return true;
                        }

                    }
                    else if(value instanceof Integer){
                        if( Integer.parseInt(numericValue.toString()) >=
                                Integer.parseInt(value.toString()) ){
                            return true;
                        }

                    }
                }
            }
            else if(numericType == NUMERIC_LOWER){
                Object value;
                for(HashMap<String,Object> transaction : block.transactions) {
                    value = transaction.get(interestName);
                    if (value instanceof Double) {
                        if (Double.parseDouble(numericValue.toString()) <=
                                Double.parseDouble(value.toString())) {
                            return true;
                        }
                    } else if (value instanceof Long) {
                        if (Long.parseLong(numericValue.toString()) <=
                                Long.parseLong(value.toString())) {
                            return true;
                        }

                    } else if (value instanceof Integer) {
                        if (Integer.parseInt(numericValue.toString()) <=
                                Integer.parseInt(value.toString())) {
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }
}
