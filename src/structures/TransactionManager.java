package structures;

import structures.managerUtils.DoubleInterest;
import structures.managerUtils.IntegerInterest;
import structures.managerUtils.LongInterest;
import structures.managerUtils.StringInterest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*Class that represents the transaction structures*/
public class TransactionManager {

    /*Possible names for type values*/
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";

    /*Hash map of values in transaction*/
    private HashMap<String,Object> transactionFields = new HashMap<>();

    /*Arraylist with keys, ordered like they are read from file*/
    private ArrayList<String> keys = new ArrayList<>();

    public TransactionManager(String filePath){

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
                keys.add(info[0]);

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
    /*A transaction is a hashmap of names of fields and the corresponding field values*/
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

    /*stuff below this is for testing*/
    public HashMap<String,Object> createTransaction(ArrayList<Object> fields){
        int counter = 0;
        HashMap<String,Object> transaction = new HashMap<>();
        for(String key : keys){
            transaction.put(key,fields.get(counter));
            counter++;
        }
        return transaction;
    }

    /*Generate interest files for nodes from a given file with possible
    * values for each transaction variable. Then save them into separate files*/
    public void generateInterestFiles(String filePath,int breakPoints){
        /*Open and read from example file*/
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] info;

            /*Interests*/
            ArrayList<StringInterest> stringInterests = new ArrayList<>();
            ArrayList<DoubleInterest> doubleInterests = new ArrayList<>();
            ArrayList<IntegerInterest> integerInterests = new ArrayList<>();
            ArrayList<LongInterest> longInterests = new ArrayList<>();

            while (true) {
                line = br.readLine();
                if(line == null){
                    break;
                }

                /*get key and value*/
                info = line.split("\\s+");

                switch (info[1]){
                    case STRING:
                        String[] subArray = Arrays.copyOfRange(info,2,info.length);
                        ArrayList<String> subList = new ArrayList<>(Arrays.asList(subArray));
                        stringInterests.add(new StringInterest(info[0],subList));
                        break;
                    case DOUBLE:
                        doubleInterests.add(new DoubleInterest(info[0],
                                Double.parseDouble(info[2]),Double.parseDouble(info[3])));
                        break;
                    case LONG:
                        longInterests.add(new LongInterest(info[0],
                                Long.parseLong(info[2]),Long.parseLong(info[3])));
                        break;
                    case INTEGER:
                        integerInterests.add(new IntegerInterest(info[0],
                                Integer.parseInt(info[2]),Integer.parseInt(info[3])));
                        break;
                }

            }
        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }

        /*Now that interests are stored lets interest files for the nodes. the file names
        * have a specific format. There are files with only 1 interest and it goes on until
        * a set amount of max interests. Available formats:
        * S : string interest file
        * I : integer interest file
        * D : double interest file
        * L : long interest file
        * Also the files have a number, meaning how many interests there are.
        * Furthermore, in case of string interest, it is indicated how many values
        * the node is interested in.
        *
        * For example a string file looks like this:
        * 1_S_2.txt, means in this file is 1 String interest with
        * 2 values for some variable
        *
        * A numeric file looks like this:
        * 1_D.txt, means that there is 1 Double interest for some variable.
        *
        * For a node with multiple interests:
        * 2_S_1_L.txt, means that there is 2 interests, 1 string with
        * 1 value for some variable. Furthermore, there is a long interest in the file */

    }


}
