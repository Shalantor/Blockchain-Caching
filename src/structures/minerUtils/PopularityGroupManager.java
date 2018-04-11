package structures.minerUtils;

import structures.Block;
import structures.minerUtils.groupInfo.InterestInfo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    /*holds the types of the interests*/
    private HashMap<String,String> interestTypes;

    public PopularityGroupManager(String interestFilePath){
        interestInfo = new HashMap<>();
        interestTypes = new HashMap<>();
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
                                interests.put(p,new InterestInfo(false));
                            }
                            interestInfo.put(key,interests);
                        }
                        else{
                            interestInfo.put(key,new HashMap<>());
                        }
                        interestTypes.put(key,STRING);
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
                            Double[] dValues = new Double[]{dValue,dMin,dMax};
                            interests.put(dValue +"_" + qualifier,new InterestInfo(qualifier,dValues,breakPoints));
                        }
                        interestInfo.put(key,interests);
                        interestTypes.put(key,DOUBLE);
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
                            Long[] lValues = new Long[]{lValue,lMin,lMax};
                            interests.put(lValue +"_" + qualifier,new InterestInfo(qualifier,lValues,breakPoints));
                        }
                        interestInfo.put(key,interests);
                        interestTypes.put(key,LONG);
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
                            Integer[] iValues = new Integer[]{iValue,iMin,iMax};
                            interests.put(iValue +"_" + qualifier,new InterestInfo(qualifier,iValues,breakPoints));
                        }
                        interestInfo.put(key,interests);
                        interestTypes.put(key,INTEGER);
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

        /*count transactions*/
        countTransactions(transaction,transactions.size());

        return size;
    }

    @Override
    public Block generateNewBlock(ArrayList<HashMap<String,Object>> transactions,Block lastBlock){

        /*Get interest info with highest score*/

        return null;
    }

    public boolean canCreateBlock(long size,long minSize){
        return size >= (minSize * 1.5f);
    }

    /*Print everyone and everything*/
    public void printInfo(){
        for(Map.Entry e : interestInfo.entrySet()){
            System.out.println("----------");
            System.out.println(e.getKey());
            HashMap<String,InterestInfo> infoMap = interestInfo.get(e.getKey());
            for(Map.Entry entry : infoMap.entrySet()){
                System.out.print(entry.getKey() + " : ");
                System.out.print(infoMap.get(entry.getKey()).getCount());
                System.out.println(" ---> " + infoMap.get(entry.getKey()).getIndices());
            }
        }
    }

    /*reset indices because those transactions were removed*/
    public void resetIndices(int[] indices,ArrayList<HashMap<String,Object>> transactions){

        for(Map.Entry e : interestInfo.entrySet()){
            HashMap<String,InterestInfo> infoMap = interestInfo.get(e.getKey());
            for(Map.Entry entry : infoMap.entrySet()){
                InterestInfo info = (InterestInfo) entry.getValue();
                info.setIndices(new ArrayList<>());
            }
        }

        for(HashMap<String,Object> transaction : transactions){
            countTransactions(transaction,transaction.size());
        }

    }

    public void countTransactions(HashMap<String,Object> transaction,int size){
        /*Count transactions*/
        for(Map.Entry<String,Object> entry : transaction.entrySet()){
            HashMap<String,InterestInfo> infoMap = interestInfo.get(entry.getKey());
            String type = interestTypes.get(entry.getKey());
            if(type.equals(STRING)){
                InterestInfo info = infoMap.get((String)entry.getValue());
                if(info != null){
                    info.setCount(info.getCount() + 1);
                    info.addIndex(size - 1);
                }
                else{
                    InterestInfo newInfo = new InterestInfo(true);
                    newInfo.setCount(1);
                    newInfo.addIndex(size - 1);
                    infoMap.put((String) entry.getValue(),newInfo);
                }
            }
            else if(type.equals(DOUBLE)){
                for(Map.Entry doubleEntry : infoMap.entrySet()){
                    String key = (String) doubleEntry.getKey();
                    Double infoValue = Double.parseDouble(key.substring(0,key.indexOf("_")));
                    String comparator = key.substring(key.indexOf("_")+1);
                    Double transactionValue = (Double) entry.getValue();

                    if(comparator.equals(GREATER) && transactionValue > infoValue){
                        InterestInfo info = (InterestInfo) doubleEntry.getValue();
                        info.setCount(info.getCount()+1);
                        info.addIndex(size - 1);
                    }
                    else if(comparator.equals(LOWER) && transactionValue < infoValue){
                        InterestInfo info = (InterestInfo) doubleEntry.getValue();
                        info.setCount(info.getCount()+1);
                        info.addIndex(size - 1);
                    }
                }
            }
            else if(type.equals(LONG)){
                for(Map.Entry longEntry : infoMap.entrySet()){
                    String key = (String) longEntry.getKey();
                    Long infoValue = Long.parseLong(key.substring(0,key.indexOf("_")));
                    String comparator = key.substring(key.indexOf("_")+1);
                    Long transactionValue = (Long) entry.getValue();

                    if(comparator.equals(GREATER) && transactionValue > infoValue){
                        InterestInfo info = (InterestInfo) longEntry.getValue();
                        info.setCount(info.getCount()+1);
                        info.addIndex(size - 1);
                    }
                    else if(comparator.equals(LOWER) && transactionValue < infoValue){
                        InterestInfo info = (InterestInfo) longEntry.getValue();
                        info.setCount(info.getCount()+1);
                        info.addIndex(size - 1);
                    }
                }
            }
            else if(type.equals(INTEGER)){
                for(Map.Entry intEntry : infoMap.entrySet()){
                    String key = (String) intEntry.getKey();
                    Integer infoValue = Integer.parseInt(key.substring(0,key.indexOf("_")));
                    String comparator = key.substring(key.indexOf("_")+1);
                    Integer transactionValue = (Integer) entry.getValue();

                    if(comparator.equals(GREATER) && transactionValue > infoValue){
                        InterestInfo info = (InterestInfo) intEntry.getValue();
                        info.setCount(info.getCount()+1);
                        info.addIndex(size - 1);
                    }
                    else if(comparator.equals(LOWER) && transactionValue < infoValue){
                        InterestInfo info = (InterestInfo) intEntry.getValue();
                        info.setCount(info.getCount()+1);
                        info.addIndex(size - 1);
                    }
                }
            }
        }
    }

}
