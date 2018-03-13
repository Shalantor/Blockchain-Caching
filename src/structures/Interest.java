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

    /*Name of interest*/
    private ArrayList<String> interestValues;
    private String interestName;

    public Interest(int type, int numericType, String name,Object numericValue, ArrayList<String> values){
        this.type = type;
        this.numericType = numericType;
        interestName = name;
        this.numericValue = numericValue;
        interestValues = values;
    }

    public boolean checkTransaction(HashMap<String,Object> transaction){

        if(type == STRING_TYPE){
            String value = transaction.get(interestName).toString();
            if(interestValues.contains(value)){
                return true;
            }
        }
        else if(type == NUMERIC_TYPE){
            if(numericType == NUMERIC_GREATER){
                Object value = transaction.get(interestName);
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
            else if(numericType == NUMERIC_LOWER){
                Object value = transaction.get(interestName);
                if(value instanceof Double){
                    if( Double.parseDouble(numericValue.toString()) <=
                            Double.parseDouble(value.toString()) ){
                        return true;
                    }
                }
                else if(value instanceof Long){
                    if( Long.parseLong(numericValue.toString()) <=
                            Long.parseLong(value.toString()) ){
                        return true;
                    }

                }
                else if(value instanceof Integer){
                    if( Integer.parseInt(numericValue.toString()) <=
                            Integer.parseInt(value.toString()) ){
                        return true;
                    }

                }
            }
        }
        return false;
    }
}
