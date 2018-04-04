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
    public int type;
    public int numericType;
    public Object numericValue;
    public int weight ;

    /*Name of interest*/
    public ArrayList<String> interestValues;
    public String interestName;

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
                    //System.out.println("HERE AT STRING with value " + value);
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
                        if( Double.parseDouble(numericValue.toString()) <=
                                Double.parseDouble(value.toString()) ){
                            //System.out.println("HERE AT DOUBLE with value " + value);
                            return true;
                        }
                    }
                    else if(value instanceof Long){
                        if( Long.parseLong(numericValue.toString()) <=
                                Long.parseLong(value.toString()) ){
                            //System.out.println("HERE AT LONG with value " + value);
                            return true;
                        }

                    }
                    else if(value instanceof Integer){
                        if( Integer.parseInt(numericValue.toString()) <=
                                Integer.parseInt(value.toString()) ){
                            //System.out.println("HERE AT INT with value " + value);
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
                        if (Double.parseDouble(numericValue.toString()) >=
                                Double.parseDouble(value.toString())) {
                            //System.out.println("HERE AT DOUBLE LOWER with value " + value);
                            return true;
                        }
                    } else if (value instanceof Long) {
                        if (Long.parseLong(numericValue.toString()) >=
                                Long.parseLong(value.toString())) {
                            //System.out.println("HERE AT LONG LOWER with value " + value);
                            return true;
                        }

                    } else if (value instanceof Integer) {
                        if (Integer.parseInt(numericValue.toString()) >=
                                Integer.parseInt(value.toString())) {
                            //System.out.println("HERE AT INT LOWER with value " + value);
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    /*Light node uses this method*/
    public boolean checkTransaction(HashMap<String,Object> transaction){
        if(type == STRING_TYPE){
            String value;
            value = transaction.get(interestName).toString();
            if(interestValues.contains(value)){
                //System.out.println("HERE AT STRING with value " + value);
                return true;
            }
        }
        else if(type == NUMERIC_TYPE){
            if(numericType == NUMERIC_GREATER){
                Object value;
                value = transaction.get(interestName);
                if(value instanceof Double){
                    if( Double.parseDouble(numericValue.toString()) <=
                            Double.parseDouble(value.toString()) ){
                        //System.out.println("HERE AT DOUBLE with value " + value);
                        return true;
                    }
                }
                else if(value instanceof Long){
                    if( Long.parseLong(numericValue.toString()) <=
                            Long.parseLong(value.toString()) ){
                        //System.out.println("HERE AT LONG with value " + value);
                        return true;
                    }

                }
                else if(value instanceof Integer){
                    if( Integer.parseInt(numericValue.toString()) <=
                            Integer.parseInt(value.toString()) ){
                        //System.out.println("HERE AT INT with value " + value);
                        return true;
                    }

                }
            }
            else if(numericType == NUMERIC_LOWER){
                Object value;
                value = transaction.get(interestName);
                if (value instanceof Double) {
                    if (Double.parseDouble(numericValue.toString()) >=
                            Double.parseDouble(value.toString())) {
                        //System.out.println("HERE AT DOUBLE LOWER with value " + value);
                        return true;
                    }
                } else if (value instanceof Long) {
                    if (Long.parseLong(numericValue.toString()) >=
                            Long.parseLong(value.toString())) {
                        //System.out.println("HERE AT LONG LOWER with value " + value);
                        return true;
                    }

                } else if (value instanceof Integer) {
                    if (Integer.parseInt(numericValue.toString()) >=
                            Integer.parseInt(value.toString())) {
                        //System.out.println("HERE AT INT LOWER with value " + value);
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /*Below stuff for testing*/
    public void printInfo(){
        if(type == STRING_TYPE){
            for(String value : interestValues){
                System.out.print(value + " ");
            }
            System.out.print("\n");
        }
        else if(type == NUMERIC_TYPE){
            if(numericType == NUMERIC_GREATER){
                System.out.print("Number greater than: ");
            }
            else if(numericType == NUMERIC_LOWER){
                System.out.print("Number lower than: ");
            }
            System.out.println(numericValue);
        }
    }


}
