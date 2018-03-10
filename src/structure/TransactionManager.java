package structure;

import java.io.BufferedReader;;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*Class that represents the transaction structure*/
public class TransactionManager {

    /*Configurations*/
    /*TODO:Make programm read these names and paths from files as well*/
    private static final String configFilename = "example.txt";
    private static final String filePath = "src/test/resources/" + configFilename;

    /*Possible names for type values*/
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";

    /*Hash map of values in transaction*/
    private HashMap<String,Object> transactionFields = new HashMap<>();

    public TransactionManager(){

        /*Open and read from example file*/
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line,key,value;
            String[] info;

            while (true) {
                line = br.readLine();
                if(line == null){
                    break;
                }

                /*get key and value*/
                info = line.split("\\s+");
                key = info[0];
                value = info[1];

                switch (value){
                    case STRING:
                        transactionFields.put(key,"");
                        break;
                    case DOUBLE:
                        transactionFields.put(key,new Double(0));
                        break;
                    case INTEGER:
                        transactionFields.put(key,new Integer(0));
                        break;
                    case LONG:
                        transactionFields.put(key,new Long(0));
                        break;
                }
            }

        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }


    }

}
