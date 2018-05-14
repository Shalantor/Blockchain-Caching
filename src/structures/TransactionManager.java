package structures;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
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

    /*Hash map of values of the specific example*/
    private HashMap<String,HashMap<String,Object>> data = new HashMap<>();

    /*Arraylist with keys, ordered like they are read from file*/
    private ArrayList<String> keys = new ArrayList<>();

    private int[] indexes;

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
                        HashMap<String,Object> sData = new HashMap<>();
                        if(Arrays.asList(info).contains("-")){
                            sData.put("possible_values",null);
                            sData.put("max",info[4]);
                            sData.put("name",info[2]);
                        }
                        else{
                            sData.put("possible_values",Arrays.copyOfRange(info,2,info.length));
                        }
                        data.put(key,sData);
                        sData.put("type",STRING);
                        break;
                    case DOUBLE:
                        transactionFields.put(key,new Double(0));
                        HashMap<String,Object> dData = new HashMap<>();
                        dData.put("min",Double.parseDouble(info[2]));
                        dData.put("max",Double.parseDouble(info[3]));
                        dData.put("type",DOUBLE);
                        data.put(key,dData);
                        break;
                    case INTEGER:
                        transactionFields.put(key,new Integer(0));
                        HashMap<String,Object> iData = new HashMap<>();
                        iData.put("min",Integer.parseInt(info[2]));
                        iData.put("max",Integer.parseInt(info[3]));
                        iData.put("type",INTEGER);
                        data.put(key,iData);
                        break;
                    case LONG:
                        transactionFields.put(key,new Long(0));
                        HashMap<String,Object> lData = new HashMap<>();
                        lData.put("min",Long.parseLong(info[2]));
                        lData.put("max",Long.parseLong(info[3]));
                        lData.put("type",LONG);
                        data.put(key,lData);
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

        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();

        transactions = createRandomTransactions(1);

        return transactions.get(0);

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
    public void generateInterestFiles(String filePath, int maxInterests,
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
                                Double.parseDouble(info[2]),Double.parseDouble(info[3]),Integer.parseInt(info[4])));
                        break;
                    case LONG:
                        longInterests.add(new LongInterest(info[0],
                                Long.parseLong(info[2]),Long.parseLong(info[3]),Integer.parseInt(info[4])));
                        break;
                    case INTEGER:
                        integerInterests.add(new IntegerInterest(info[0],
                                Integer.parseInt(info[2]),Integer.parseInt(info[3]),Integer.parseInt(info[4])));
                        break;
                }

            }
        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }

        /*Now that interests are stored lets create interest files for the nodes. the file names
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
        int breakPoints = 0;

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
            count = 0;
        }

        /*Doubles*/
        count = 0;
        for(DoubleInterest d : doubleInterests){
            breakPoints = d.getBreakpoints();
            double value = 0;
            for(int i = 0; i < breakPoints-1; i++){
                try{
                    value += (d.getMaxValue() - d.getMinValue())/breakPoints;

                    String qualifier = "";
                    if(value <= (d.getMaxValue() - d.getMinValue())/2){
                        qualifier = "lower";
                    }
                    else{
                        qualifier = "greater";
                    }

                    /*create*/
                    String fileName = "1_D_" + fileCounter + ".txt";
                    fileCounter ++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tdouble\t1\t" + qualifier + "\t" + value);
                    out.flush();
                    out.close();

                    count++;
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
                if(count == maxInterests){
                    break;
                }
            }
        }

        /*Long*/
        count = 0;
        for(LongInterest d : longInterests){
            long value = 0;
            breakPoints = d.getBreakpoints();
            for(int i = 0; i < breakPoints-1; i++){
                try{
                    value += (d.getMaxValue() - d.getMinValue())/breakPoints;

                    String qualifier = "";
                    if(value <= (d.getMaxValue() - d.getMinValue())/2){
                        qualifier = "lower";
                    }
                    else{
                        qualifier = "greater";
                    }

                    /*create*/
                    String fileName = "1_L_" + fileCounter + ".txt";
                    fileCounter ++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tlong\t1\t" + qualifier + "\t" + value);
                    out.flush();
                    out.close();

                    count++;
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
                if(count == maxInterests){
                    break;
                }
            }
        }

        /*Integers*/
        count = 0;
        for(IntegerInterest d : integerInterests){
            int value = 0;
            breakPoints = d.getBreakpoints();
            for(int i = 0; i < breakPoints-1; i++){
                try{
                    value += (d.getMaxValue() - d.getMinValue())/breakPoints;

                    String qualifier = "";
                    if(value <= (d.getMaxValue() - d.getMinValue())/2){
                        qualifier = "lower";
                    }
                    else{
                        qualifier = "greater";
                    }

                    /*create*/
                    String fileName = "1_I_" + fileCounter + ".txt";
                    fileCounter ++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.println(d.getName() + "\tinteger\t1\t" + qualifier + "\t" + (int)value);
                    out.flush();
                    out.close();

                    count++;
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
                if(count == maxInterests){
                    break;
                }
            }
        }

    }

    /*Create interest files with two interests*/
    public void generateMultipleInterestsFiles(String destPath,String sourcePath,
                                               int maxInterests, int num) {

        /*Loop over interests*/
        /*First create simple files, so loop over each list*/
        int fileCounter = 0;
        int count = 0;
        int numName = num + 1;

        /*Strings*/
        for (StringInterest s : stringInterests) {
            List<String> values = s.getPossibleValues();
            count = 0;
            for (String value : values) {
                try {
                    String[] combineValues = new String[num];
                    for (int i = 1; i <= num; i++) {
                        combineValues[i - 1] = values.get((count + i) % values.size());
                    }
                    String fileName = numName + "_S_" + numName + "_" + fileCounter + ".txt";
                    fileCounter++;
                    count++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.print(s.getName() + "\tstring\t1\t" + value);
                    for (String cValue : combineValues) {
                        out.print("\t" + cValue);
                    }
                    out.println("");
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (count == maxInterests) {
                    break;
                }
            }
            /*Now case for a range file*/
            if (s.getPossibleValues().size() == 0) {
                try {
                    String fileName = numName + "_R_" + fileCounter + ".txt";
                    fileCounter++;
                    PrintWriter out = new PrintWriter(destPath + fileName);
                    out.print(s.getName() + "\tstring\t1\t" + s.getRangeName());
                    for (int i = 0; i <= num; i++) {
                        out.print("\t" + (s.getRangeStart() + i));
                    }
                    out.println("");
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        /*Now read from the previous files(1 interest) and combine their text*/
        File folder = new File(sourcePath);
        File[] listOfFiles = folder.listFiles();

        count = 0;
        int length = listOfFiles.length;
        Arrays.sort(listOfFiles);

        for(File f: listOfFiles){
            String output = "";
            String fileName = "";
            try(BufferedReader br = new BufferedReader(new FileReader(sourcePath + f.getName()))) {
                output += br.readLine() + "\n";
                br.close();
                fileName += numName + "_" + f.getName().substring(2,f.getName().lastIndexOf("_")) + "_";

                Random generator = new Random();
                int start = 0;
                for(int i = 1; i <= num; i++){

                    start = (start + 10*i) % listOfFiles.length;

                    File next = listOfFiles[start];
                    BufferedReader br1 = new BufferedReader(new FileReader(sourcePath + next.getName()));
                    String line = br1.readLine();
                    output += line + "\n";
                    br1.close();
                    fileName += next.getName().substring(2,next.getName().lastIndexOf("_")) + "_";
                }

                fileName += fileCounter + ".txt";
                fileCounter ++;
                count ++;
                PrintWriter out = new PrintWriter(destPath + fileName);
                out.println(output);
                out.flush();
                out.close();
                if(count >= maxInterests){
                    break;
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

        }

        /*Now do the same but for descending order*/
        Arrays.sort(listOfFiles,Collections.reverseOrder());

        count = 0;
        for(File f: listOfFiles){
            String output = "";
            String fileName = "";
            try(BufferedReader br = new BufferedReader(new FileReader(sourcePath + f.getName()))) {
                output += br.readLine() + "\n";
                br.close();
                fileName += numName + "_" + f.getName().substring(2,f.getName().lastIndexOf("_")) + "_";

                Random generator = new Random();
                int start = 0;
                for(int i = 1; i <= num; i++){

                    start = (start + 10*i) % listOfFiles.length;

                    File next = listOfFiles[start];
                    BufferedReader br1 = new BufferedReader(new FileReader(sourcePath + next.getName()));
                    String line = br1.readLine();
                    output += line + "\n";
                    br1.close();
                    fileName += next.getName().substring(2,next.getName().lastIndexOf("_")) + "_";
                }

                fileName += fileCounter + ".txt";
                fileCounter ++;
                count ++;
                PrintWriter out = new PrintWriter(destPath + fileName);
                out.println(output);
                out.flush();
                out.close();
                if(count >= maxInterests){
                    return;
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

        }
    }

    /*Generate transactions, based on the input, completely random*/
    public ArrayList<HashMap<String,Object>> createRandomTransactions(int count){

        Random generator = new Random();
        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
        for(int i =0; i < count; i++){
            HashMap<String,Object> tr = new HashMap<>();
            for(Map.Entry entry : data.entrySet()){
                HashMap<String,Object> info = (HashMap<String, Object>) entry.getValue();
                switch ((String)info.get("type")){
                    case  STRING:
                        String[] list = (String[]) info.get("possible_values");
                        if(list == null){
                            int max = Integer.parseInt((String) info.get("max"));
                            String name = (String) info.get("name");
                            int value = generator.nextInt(max);
                            tr.put(entry.getKey().toString(),name + value);
                        }
                        else{
                            tr.put(entry.getKey().toString(),list[generator.nextInt(list.length)]);
                        }
                        break;
                    case DOUBLE:
                        Double min = (Double) info.get("min");
                        Double max = (Double) info.get("max");
                        tr.put(entry.getKey().toString(),(generator.nextDouble()*max) + min);
                        break;
                    case LONG:
                        Long lmin = (Long) info.get("min");
                        Long lmax = (Long) info.get("max");
                        tr.put(entry.getKey().toString(),(generator.nextLong()*lmax) + lmin);
                        break;
                    case INTEGER:
                        Integer imin = (Integer) info.get("min");
                        Integer imax = (Integer) info.get("max");
                        tr.put(entry.getKey().toString(),(generator.nextInt(imax)) + imin);
                        break;
                }
            }
            transactions.add(tr);
        }

        return transactions;
    }

    /*For a distribution, we could make choose the indexes in string lists
    * by considering some distribution, for example zipf. For numbers we can bin
    * some values in the possible ranges given. Then, considering the count, we
    * choose the values based on the distribution.
    * For example we have count 20. We make a distribution with 20 numbers in
    * range 0 to size of possible values - 1. Then we choose those numbers the next
    * time we have to choose a value for that attribute of the transaction.
    * We can keep them in a hashmap with key the name of the attribute. And then
    * as a value we have those arrays with the integers*/

    /*Normal distribution*/
    public ArrayList<HashMap<String,Object>> createNormalTransactions(int count){

        Random generator = new Random();
        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();
        NormalDistribution normal = new NormalDistribution();
        for(int i =0; i < count; i++){
            HashMap<String,Object> tr = new HashMap<>();
            for(Map.Entry entry : data.entrySet()){
                HashMap<String,Object> info = (HashMap<String, Object>) entry.getValue();
                double gauss = normal.sample();
                while(Math.abs(gauss) > 1.0){
                    gauss = normal.sample();
                }
                switch ((String)info.get("type")){
                    case  STRING:
                        String[] list = (String[]) info.get("possible_values");
                        if(list == null){
                            int max = Integer.parseInt((String) info.get("max"));
                            String name = (String) info.get("name");
                            int value = (int)( Math.abs(gauss) * max);
                            tr.put(entry.getKey().toString(),name + value);
                        }
                        else{
                            tr.put(entry.getKey().toString(),list[(int)(Math.abs(gauss) * list.length)]);
                        }
                        break;
                    case DOUBLE:
                        Double min = (Double) info.get("min");
                        Double max = (Double) info.get("max");
                        tr.put(entry.getKey().toString(),(Math.abs(gauss*max)) + min);
                        break;
                    case LONG:
                        Long lmin = (Long) info.get("min");
                        Long lmax = (Long) info.get("max");
                        tr.put(entry.getKey().toString(),(long)((Math.abs(gauss)*lmax)) + lmin);
                        break;
                    case INTEGER:
                        Integer imin = (Integer) info.get("min");
                        Integer imax = (Integer) info.get("max");
                        tr.put(entry.getKey().toString(),(int)(Math.abs(gauss) * imax) + imin);
                        break;
                }
            }
            transactions.add(tr);
        }

        return transactions;
    }

    /*Create zipfian distribution*/
    public ArrayList<HashMap<String,Object>> createZipfianTransactions(int count){

        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();

        /*Generate zipf distributions */
        HashMap<String,ZipfDistribution> dist = new HashMap<>();
        for(Map.Entry entry : data.entrySet()){
            ZipfDistribution zipfDistribution = null;
            HashMap<String,Object> info = (HashMap<String, Object>) entry.getValue();
            switch ((String)info.get("type")){
                case  STRING:
                    String[] list = (String[]) info.get("possible_values");
                    if(list == null){
                        int max = Integer.parseInt((String) info.get("max"));
                        zipfDistribution = new ZipfDistribution(max+1,1);
                    }
                    else{
                        zipfDistribution = new ZipfDistribution(list.length-1,1);
                    }
                    break;
                case DOUBLE:
                    Double min = (Double) info.get("min");
                    Double max = (Double) info.get("max");
                    zipfDistribution = new ZipfDistribution((int)(max-min),1);

                    break;
                case LONG:
                    Long lmin = (Long) info.get("min");
                    Long lmax = (Long) info.get("max");
                    zipfDistribution = new ZipfDistribution((int)(lmax-lmin),1);
                    break;
                case INTEGER:
                    Integer imin = (Integer) info.get("min");
                    Integer imax = (Integer) info.get("max");
                    zipfDistribution = new ZipfDistribution((int)(imax-imin),1);
                    break;
            }
            dist.put(entry.getKey().toString(),zipfDistribution);
        }

        /*Now generate */
        for(int i =0; i < count; i++){
            HashMap<String,Object> tr = new HashMap<>();
            for(Map.Entry entry : data.entrySet()){
                HashMap<String,Object> info = (HashMap<String, Object>) entry.getValue();
                ZipfDistribution zipfDistribution = dist.get(entry.getKey().toString());
                switch ((String)info.get("type")){
                    case  STRING:
                        String[] list = (String[]) info.get("possible_values");
                        if(list == null){
                            int max = Integer.parseInt((String) info.get("max"));
                            String name = (String) info.get("name");
                            tr.put(entry.getKey().toString(),name + zipfDistribution.sample());
                        }
                        else{
                            tr.put(entry.getKey().toString(),list[zipfDistribution.sample()]);
                        }
                        break;
                    case DOUBLE:
                        Double min = (Double) info.get("min");
                        Double max = (Double) info.get("max");
                        tr.put(entry.getKey().toString(),(double) zipfDistribution.sample() + min);
                        break;
                    case LONG:
                        Long lmin = (Long) info.get("min");
                        Long lmax = (Long) info.get("max");
                        tr.put(entry.getKey().toString(),((long)zipfDistribution.sample()) + lmin);
                        break;
                    case INTEGER:
                        Integer imin = (Integer) info.get("min");
                        Integer imax = (Integer) info.get("max");
                        tr.put(entry.getKey().toString(),zipfDistribution.sample() + imin);
                        break;
                }
            }
            transactions.add(tr);
        }

        return transactions;
    }

    /*Create exponential distribution. With a mean of 1 the exponential distribution
    * after the value 5 is insignificant. So we can assume that 5 is the max value*/
    public ArrayList<HashMap<String,Object>> createExponentialTransactions(int count){

        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();

        /*Generate exponential distribution */
        ExponentialDistribution exp = new ExponentialDistribution(1);
        int maxExp = 5;

        /*Now generate */
        for(int i =0; i < count; i++){
            HashMap<String,Object> tr = new HashMap<>();
            for(Map.Entry entry : data.entrySet()){
                HashMap<String,Object> info = (HashMap<String, Object>) entry.getValue();
                double expValue = exp.sample();
                while(expValue > 5.0){
                    expValue = exp.sample();
                }
                switch ((String)info.get("type")){
                    case  STRING:
                        String[] list = (String[]) info.get("possible_values");
                        if(list == null){
                            int max = Integer.parseInt((String) info.get("max"));
                            String name = (String) info.get("name");
                            int pos = (int) (expValue * (max / maxExp));
                            tr.put(entry.getKey().toString(),name + pos);
                        }
                        else{
                            int pos = (int) (expValue * (list.length / maxExp));
                            tr.put(entry.getKey().toString(),list[pos]);
                        }
                        break;
                    case DOUBLE:
                        Double min = (Double) info.get("min");
                        Double max = (Double) info.get("max");
                        double value = expValue * (max / maxExp);
                        value = value < min ? value + min : value;
                        tr.put(entry.getKey().toString(),value);
                        break;
                    case LONG:
                        Long lmin = (Long) info.get("min");
                        Long lmax = (Long) info.get("max");
                        long lvalue = (long) (expValue * (lmax / maxExp));
                        lvalue = lvalue < lmin ? lvalue + lmin : lvalue;
                        tr.put(entry.getKey().toString(),lvalue);
                        break;
                    case INTEGER:
                        Integer imin = (Integer) info.get("min");
                        Integer imax = (Integer) info.get("max");
                        int ivalue = (int) (expValue * (imax / maxExp));
                        ivalue = ivalue < imin ? ivalue + imin : ivalue;
                        tr.put(entry.getKey().toString(),ivalue);
                        break;
                }
            }
            transactions.add(tr);
        }

        return transactions;
    }

    /*Poisson distribution. With l = 4, the values after 10 are insignificant
    * so we can safely assume that this is the max value.*/
    public ArrayList<HashMap<String,Object>> createPoissonTransactions(int count){

        ArrayList<HashMap<String,Object>> transactions = new ArrayList<>();

        /*Generate exponential distribution */
        PoissonDistribution pois = new PoissonDistribution(4);
        int maxPois = 10;

        /*Now generate */
        for(int i =0; i < count; i++){
            HashMap<String,Object> tr = new HashMap<>();
            for(Map.Entry entry : data.entrySet()){
                HashMap<String,Object> info = (HashMap<String, Object>) entry.getValue();
                int poisValue = pois.sample();
                while(poisValue > 10){
                    poisValue = pois.sample();
                }
                switch ((String)info.get("type")){
                    case  STRING:
                        String[] list = (String[]) info.get("possible_values");
                        if(list == null){
                            int max = Integer.parseInt((String) info.get("max"));
                            String name = (String) info.get("name");
                            int pos = poisValue * (max / maxPois);
                            tr.put(entry.getKey().toString(),name + pos);
                        }
                        else{
                            int newPoisValue = poisValue;
                            while (newPoisValue >= list.length){
                                newPoisValue = pois.sample();
                            }
                            tr.put(entry.getKey().toString(),list[newPoisValue]);
                        }
                        break;
                    case DOUBLE:
                        Double min = (Double) info.get("min");
                        Double max = (Double) info.get("max");
                        double value = (double)(poisValue * (max / maxPois));
                        value = value < min ? value + min : value;
                        tr.put(entry.getKey().toString(),value);
                        break;
                    case LONG:
                        Long lmin = (Long) info.get("min");
                        Long lmax = (Long) info.get("max");
                        long lvalue = (long) (poisValue * (lmax / maxPois));
                        lvalue = lvalue < lmin ? lvalue + lmin : lvalue;
                        tr.put(entry.getKey().toString(),lvalue);
                        break;
                    case INTEGER:
                        Integer imin = (Integer) info.get("min");
                        Integer imax = (Integer) info.get("max");
                        int ivalue = (int) (poisValue * (imax / maxPois));
                        ivalue = ivalue < imin ? ivalue + imin : ivalue;
                        tr.put(entry.getKey().toString(),ivalue);
                        break;
                }
            }
            transactions.add(tr);
        }

        return transactions;
    }

}
