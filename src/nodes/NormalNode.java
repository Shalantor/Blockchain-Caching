package nodes;

import structures.Interest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*Implementation of the normal node*/
public class NormalNode {

    /*Possible names for type values*/
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";
    private static final String GREATER = "greater";
    private static final String LOWER = "lower";

    /*Available configurations*/
    private static final String MAX_CACHE_SIZE = "max_cache_size";
    private static final String TIME_RESTRAINT = "time_restraint";

    /*value indicating no cache size limit*/
    private static final int NO_LIMIT = 0;

    /*Does the node have a maximum cache size?*/
    private long maxCacheSize;
    private long timeRestraint;

    /*Interest name with the corresponding interest object*/
    private HashMap<String, Interest> interests = new HashMap<>();

    /*The constructor as of now*/
    public NormalNode(String configFilePath,String interestFilePath){

        /*Open and read from config file*/
        try(BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line,key,value;
            String[] info;

            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }

                /*get key and value*/
                info = line.split("\\s+");
                key = info[0];
                value = info[1];

                switch (key){
                    case MAX_CACHE_SIZE:
                        maxCacheSize = Long.parseLong(value);
                        break;
                    case TIME_RESTRAINT:
                        timeRestraint = Long.parseLong(value);
                }
            }

        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }

        /*Now open config file for interests*/
        try(BufferedReader br = new BufferedReader(new FileReader(interestFilePath))) {
            String line,key,value;
            String[] info;

            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }

                /*get key and value*/
                info = line.split("\\s+");
                value = info[1];

                switch (value){
                    case STRING:
                        String[] subArray = Arrays.copyOfRange(info,3,info.length);
                        ArrayList<String> subList = new ArrayList<>(Arrays.asList(subArray));
                        interests.put(info[0],new Interest(Interest.STRING_TYPE,
                                0,Integer.parseInt(info[2]),info[0],null,subList));
                        break;
                    case DOUBLE:
                        int operationType = 0;
                        if(info[3].equals(GREATER)){
                            operationType = Interest.NUMERIC_GREATER;
                        }
                        else if(info[3].equals(LOWER)){
                            operationType = Interest.NUMERIC_LOWER;
                        }
                        interests.put(info[0],new Interest(Interest.NUMERIC_TYPE,
                                operationType,Integer.parseInt(info[2]),info[0],
                                Double.parseDouble(info[4]),null));
                        break;
                    case INTEGER:
                        operationType = 0;
                        if(info[3].equals(GREATER)){
                            operationType = Interest.NUMERIC_GREATER;
                        }
                        else if(info[3].equals(LOWER)){
                            operationType = Interest.NUMERIC_LOWER;
                        }
                        interests.put(info[0],new Interest(Interest.NUMERIC_TYPE,
                                operationType,Integer.parseInt(info[2]),info[0],
                                Integer.parseInt(info[4]),null));
                        break;
                    case LONG:
                        operationType = 0;
                        if(info[3].equals(GREATER)){
                            operationType = Interest.NUMERIC_GREATER;
                        }
                        else if(info[3].equals(LOWER)){
                            operationType = Interest.NUMERIC_LOWER;
                        }
                        interests.put(info[0],new Interest(Interest.NUMERIC_TYPE,
                                operationType,Integer.parseInt(info[2]),info[0],
                                Long.parseLong(info[4]),null));
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
