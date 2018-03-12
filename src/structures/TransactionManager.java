package structures;

import java.io.BufferedReader;;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*Class that represents the transaction structures*/
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

    /*Return all the keys from the hashmap*/
    public List<String>  getKeys(){
        return new ArrayList<String>(transactionFields.keySet());
    }

    public HashMap<String, Object> getTransactionFields() {
        return transactionFields;
    }

    /*This is for testing purposes*/
    public HashMap<String,Object> createRandomTransaction() {

        HashMap<String,Object> transaction = new HashMap<>();
        Object value;
        Random generator = new Random();

        for(Map.Entry entry : transactionFields.entrySet()){
            value = entry.getValue();
            if(value instanceof Double){
                transaction.put(entry.getKey().toString(),generator.nextDouble());
            }
            else if(value instanceof Long){
                transaction.put(entry.getKey().toString(),generator.nextLong());
            }
            else if(value instanceof Integer){
                transaction.put(entry.getKey().toString(),generator.nextInt());
            }
            else if(value instanceof String){
                transaction.put(entry.getKey().toString(),"AAABBBAAA");
            }

        }

        return transaction;

    }


}
