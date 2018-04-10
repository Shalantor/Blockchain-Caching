package structures.minerUtils;

import structures.Block;
import structures.minerUtils.groupInfo.InterestInfo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PopularityGroupManager extends GroupManager{

    /*Possible names for type values*/
    private static final String STRING = "string";
    private static final String DOUBLE = "double";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";
    private static final String GREATER = "greater";
    private static final String LOWER = "lower";

    /*Key is the name of the interest. The hashmap is sorted based on values*/
    private HashMap<String,HashMap<String,InterestInfo>> interestInfo;

    public PopularityGroupManager(String interestFilePath){
        /*Open and read from config file*/
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
                key = info[0];
                value = info[1];

                switch (value){
                    case STRING:
                        if(!info[3].equals("-")){
                            String[] possibleValues = Arrays.copyOfRange(info,2,info.length);
                            HashMap<String,InterestInfo> interests = new HashMap<>();
                            for(String p: possibleValues){
                                interests.put(p,new InterestInfo(STRING));
                            }
                            interestInfo.put(key,interests);
                        }
                        else{
                            interestInfo.put(key,new HashMap<>());
                        }
                        break;
                    case DOUBLE:
                        HashMap<String,InterestInfo> interests = new HashMap<>();
                        Double dMax = Double.parseDouble(info[3]);
                        Double dMin = Double.parseDouble(info[2]);
                        Double dValue = 0.0;
                        int breakPoints = Integer.parseInt(info[4]);

                        for(int i = 0; i < breakPoints-1; i++){
                            dValue += (dMax - dMin)/breakPoints;

                            String qualifier = "";
                            if(dValue <= (dMax - dMin)/2){
                                qualifier = "lower";
                            }
                            else{
                                qualifier = "greater";
                            }
                            interests.put(dValue +"_" + qualifier,new InterestInfo(DOUBLE,qualifier,dValue));
                        }
                        interestInfo.put(key,interests);
                        break;
                    case LONG:
                        interests = new HashMap<>();
                        Long lMax = Long.parseLong(info[3]);
                        Long lMin = Long.parseLong(info[2]);
                        Long lValue = 0L;
                        breakPoints = Integer.parseInt(info[4]);

                        for(int i = 0; i < breakPoints-1; i++){
                            lValue += (lMax - lMin)/breakPoints;

                            String qualifier = "";
                            if(lValue <= (lMax - lMin)/2){
                                qualifier = "lower";
                            }
                            else{
                                qualifier = "greater";
                            }
                            interests.put(lValue +"_" + qualifier,new InterestInfo(LONG,qualifier,lValue));
                        }
                        interestInfo.put(key,interests);
                        break;
                    case INTEGER:
                        interests = new HashMap<>();
                        Integer iMax = Integer.parseInt(info[3]);
                        Integer iMin = Integer.parseInt(info[2]);
                        Integer iValue = 0;
                        breakPoints = Integer.parseInt(info[4]);

                        for(int i = 0; i < breakPoints-1; i++){
                            iValue += (iMax - iMin)/breakPoints;

                            String qualifier = "";
                            if(iValue <= (iMax - iMin)/2){
                                qualifier = "lower";
                            }
                            else{
                                qualifier = "greater";
                            }
                            interests.put(iValue +"_" + qualifier,new InterestInfo(INTEGER,qualifier,iValue));
                        }
                        interestInfo.put(key,interests);
                        break;
                }
            }
            br.close();

        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }
    }

    @Override
    public long addTransaction(HashMap<String,Object> transaction,
                               ArrayList<HashMap<String,Object>> transactions,
                               long size){
        transactions.add(transaction);
        size += Block.calculateSingleTransactionSize(transaction);
        return size;
    }

}
