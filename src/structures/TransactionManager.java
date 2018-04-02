package structures;

import structures.managerUtils.DoubleInterest;
import structures.managerUtils.IntegerInterest;
import structures.managerUtils.LongInterest;
import structures.managerUtils.StringInterest;

import java.io.*;
import java.util.*;

/*Class that represents the transaction structures*/
public class TransactionManager {

    /*Possible names for type values*/
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";

    /*Interests*/
    private ArrayList<StringInterest> stringInterests = new ArrayList<>();
    private ArrayList<DoubleInterest> doubleInterests = new ArrayList<>();
    private ArrayList<IntegerInterest> integerInterests = new ArrayList<>();
    private ArrayList<LongInterest> longInterests = new ArrayList<>();

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
    public void generateInterestFiles(String filePath,int breakPoints, int maxInterests,
                                      String destPath){

        /*Open and read from example file*/
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] info;


            while (true) {
                line = br.readLine();
                if(line == null){
                    break;
                }

                /*get key and value*/
                info = line.split("\\s+");

                switch (info[1]){
                    case STRING:
                        if(info[3].equals("-")){
                            stringInterests.add(new StringInterest(info[0],info[2],0,
                                    Integer.parseInt(info[4])));
                        }
                        else{
                            String[] subArray = Arrays.copyOfRange(info,2,info.length);
                            ArrayList<String> subList = new ArrayList<>(Arrays.asList(subArray));
                            stringInterests.add(new StringInterest(info[0],subList));
                        }
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

        /*First create simple files, so loop over each list*/
        int fileCounter = 0;
        int count = 0;

        /*Strings*/
        for(StringInterest s : stringInterests){
            for(String value : s.getPossibleValues()){
                try{
                    String fileName = "1_S_1_" + fileCounter + ".txt";
                    fileCounter ++;
                    count++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(s.getName() + "\tstring\t1\t" + value);
                    out.flush();
                    out.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
                if(count == maxInterests){
                    break;
                }
            }
            /*Now case for a range file*/
            if(s.getPossibleValues().size() == 0){
                try{
                    String fileName = "1_R_" + fileCounter + ".txt";
                    fileCounter ++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(s.getName() + "\tstring\t1\t" +
                            s.getRangeName() + "\t" + s.getRangeStart() + "\t" +s.getRangeEnd());
                    out.flush();
                    out.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }

        /*Doubles*/
        count = 0;
        for(DoubleInterest d : doubleInterests){
            double value = 0;
            for(int i = 0; i < breakPoints; i++){
                try{
                    value += (d.getMaxValue() - d.getMinValue())/breakPoints;

                    /*greater*/
                    String fileName = "1_D_" + fileCounter + ".txt";
                    fileCounter ++;
                    count++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tdouble\t1\tgreater\t" + value);
                    out.flush();
                    out.close();

                    /*lower*/
                    fileName = "1_D_" + fileCounter + ".txt";
                    fileCounter ++;
                    out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tdouble\t1\tlower\t" + value);
                    out.flush();
                    out.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            if(count == maxInterests){
                break;
            }
        }

        /*Long*/
        count = 0;
        for(LongInterest d : longInterests){
            double value = 0;
            for(int i = 0; i < breakPoints; i++){
                try{
                    value += (d.getMaxValue() - d.getMinValue())/breakPoints;

                    /*greater*/
                    String fileName = "1_L_" + fileCounter + ".txt";
                    fileCounter ++;
                    count ++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tlong\t1\tgreater\t" + value);
                    out.flush();
                    out.close();

                    /*lower*/
                    fileName = "1_L_" + fileCounter + ".txt";
                    fileCounter ++;
                    count ++;
                    out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tlong\t1\tlower\t" + value);
                    out.flush();
                    out.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            if(count >= maxInterests){
                break;
            }
        }

        /*Integers*/
        count = 0;
        for(IntegerInterest d : integerInterests){
            double value = 0;
            for(int i = 0; i < breakPoints; i++){
                try{
                    value += (d.getMaxValue() - d.getMinValue())/breakPoints;

                    /*greater*/
                    String fileName = "1_I_" + fileCounter + ".txt";
                    fileCounter ++;
                    count ++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tinteger\t1\tgreater\t" + value);
                    out.flush();
                    out.close();

                    /*lower*/
                    fileName = "1_I_" + fileCounter + ".txt";
                    fileCounter ++;
                    count ++;
                    out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tinteger\t1\tlower\t" + value);
                    out.flush();
                    out.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            if(count >= maxInterests){
                break;
            }
        }

    }

    /*Create interest files with two interests*/
    public void generateMultipleInterestsFiles(String destPath,int maxInterests,
                                                int breakPoints,int num){

        /*Loop over interests*/
        /*First create simple files, so loop over each list*/
        int fileCounter = 0;
        int count = 0;
        int numName = num + 1;

        /*Strings*/
        for(StringInterest s : stringInterests){
            List<String> values = s.getPossibleValues();
            count = 0;
            for(String value : values){
                try{
                    String[] combineValues = new String[num];
                    for(int i = 1; i <= num; i++){
                        combineValues[i-1] = values.get((count + i) % values.size() );
                    }
                    String fileName = numName + "_S_" + numName + "_" + fileCounter + ".txt";
                    fileCounter ++;
                    count++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.print(s.getName() + "\tstring\t1\t" + value);
                    for(String cValue : combineValues){
                        out.print("\t" + cValue);
                    }
                    out.println("");
                    out.flush();
                    out.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
                if(count == maxInterests){
                    break;
                }
            }
            /*Now case for a range file*/
            if(s.getPossibleValues().size() == 0){
                try{
                    String fileName = numName + "_R_" + fileCounter + ".txt";
                    fileCounter ++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.print(s.getName() + "\tstring\t1\t" + s.getRangeName());
                    for(int i =0; i <= num; i++){
                        out.print("\t" + (s.getRangeStart()+i) );
                    }
                    out.println("");
                    out.flush();
                    out.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }

        /*Since nodes cannot have multiple interests for the same number type,
        * we just combine them either with string interests or other number interests*/
        /*Doubles*/
        int combineInterests = maxInterests / 2;
        count = 0;

        for(DoubleInterest d : doubleInterests){
            try{
                String fileName = numName + "_D_";
                String outputString = "";
                int integerCount=0,longCount = 0;
                boolean notEnough = false;
                for(int i = 0; i < num; i++){
                    boolean change = false;
                    if(integerInterests.size() > integerCount){
                        change = true;
                        fileName += "I_";
                        IntegerInterest in = integerInterests.get(integerCount);
                        integerCount += 1;
                        outputString += in.getName() + "\tinteger\t1\tgreater\t" + (in.getMaxValue() / breakPoints);
                    }
                    else if(longInterests.size() > longCount){
                        change = true;
                        fileName += "L_";
                        LongInterest in = longInterests.get(longCount);
                        longCount += 1;
                        outputString += in.getName() + "\tlong\t1\tgreater\t" + (in.getMaxValue() / breakPoints);
                    }
                    else if(doubleInterests.size() > 1){
                        int nextIndex = (doubleInterests.indexOf(d) + 1) % doubleInterests.size();
                        change = true;
                        fileName += "D_";
                        DoubleInterest nextD = doubleInterests.get(nextIndex);
                        outputString += nextD.getName() + "\tdouble\t1\tgreater\t" + (nextD.getMaxValue() / breakPoints);
                    }
                    outputString += "\n";
                    if(!change){
                        notEnough = true;
                        break;
                    }
                }
                if(fileName.equals(numName + "_D_") || notEnough){
                    break;
                }
                fileCounter++;
                fileName += fileCounter + ".txt";
                PrintWriter out = new PrintWriter(destPath + fileName);
                out.println(d.getName() + "\tdouble\t1\tlower\t" + (d.getMaxValue() / breakPoints));
                out.println(outputString);
                out.flush();
                out.close();
                count++;
                if(count > combineInterests){
                    break;
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }

        /*Not enought combined interests?*/
        if(count < combineInterests){
            for(IntegerInterest d : integerInterests){
                try{
                    String fileName = numName + "_I_";
                    String outputString = "";
                    int longCount = 0;
                    boolean notEnough = false;
                    for(int i = 0; i < num; i++){
                        boolean change = false;
                        if(longInterests.size() > longCount){
                            change = true;
                            fileName += "L_";
                            LongInterest in = longInterests.get(longCount);
                            longCount += 1;
                            outputString += in.getName() + "\tlong\t1\tgreater\t" + (in.getMaxValue() / breakPoints);
                        }
                        outputString += "\n";
                        if(!change){
                            notEnough = true;
                            break;
                        }
                    }
                    if(fileName.equals(numName + "_D_") || notEnough){
                        break;
                    }
                    fileCounter++;
                    fileName += fileCounter + ".txt";
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tinteger\t1\tlower\t" + (d.getMaxValue() / breakPoints));
                    out.println(outputString);
                    out.flush();
                    out.close();
                    count++;
                    if(count > combineInterests){
                        break;
                    }
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

}
