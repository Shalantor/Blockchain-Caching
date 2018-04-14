package structures.minerUtils;

import structures.Block;
import structures.Interest;
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

    /*min and max block size*/
    private long minBlockSize;
    private long maxBlockSize;
    private long timeLimit;

    /*Absolute time limit for a transaction to be pending*/
    /*This should be really large because we may have to violate the min block size*/
    private long lastTimeLimit;

    /*parameter to ignore min block size*/
    private boolean ignoreMinSize = false;
    private long transactionsSize;

    /*time stamps of transactions*/
    ArrayList<Long> timeStamps;

    /*Transaction size approximation*/
    private long singleTransactionSize;

    public PopularityGroupManager(String interestFilePath){
        timeStamps = new ArrayList<>();
        interestInfo = new HashMap<>();
        interestTypes = new HashMap<>();

        /*TODO: CHANGE HARDCODE, BEST WOULD BE TO READ FROM FILE*/
        timeLimit = 10000;
        lastTimeLimit = 20000;
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
                                interests.put(p,new InterestInfo(false,STRING,p));
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
                            interests.put(dValue +"_" + qualifier,new InterestInfo(qualifier,dValues,breakPoints,DOUBLE,dValue +"_" + qualifier));
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
                            interests.put(lValue +"_" + qualifier,new InterestInfo(qualifier,lValues,breakPoints,LONG,lValue +"_" + qualifier));
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
                            interests.put(iValue +"_" + qualifier,new InterestInfo(qualifier,iValues,breakPoints,INTEGER,iValue +"_" + qualifier));
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

        /*timestamp*/
        timeStamps.add(System.currentTimeMillis());

        long calculateSize = Block.calculateSingleTransactionSize(transaction);
        size += calculateSize;

        /*Approximate single transaction size*/
        if(calculateSize > singleTransactionSize){
            singleTransactionSize = calculateSize;
        }

        /*count transactions*/
        countTransactions(transaction,transactions.size());

        transactionsSize = size;
        return size;
    }

    @Override
    public Block generateNewBlock(ArrayList<HashMap<String,Object>> transactions,Block lastBlock){

        /*Store transactions we want*/
        ArrayList<HashMap<String,Object>> chosenTransactions = new ArrayList<>();
        long currentSize = 0;
        long limit = ((maxBlockSize - minBlockSize) / 2 ) + minBlockSize;

        /*First check for transactions that are too old*/
        int pos = -1;
        long currentTime = System.currentTimeMillis();
        for(int i = timeStamps.size() - 1; i >= 0; i--){
            if(currentTime - timeStamps.get(i) > timeLimit){
                pos = i;
                break;
            }
        }

        /*Have we found any? if yes add to chosenTransactions*/
        if(pos >= 0){
            for(int i = pos; i>= 0; i--){
                chosenTransactions.add(transactions.get(pos));
                currentSize += Block.calculateSingleTransactionSize(transactions.get(pos));
                transactions.remove(pos);
                timeStamps.remove(pos);
            }
        }

        /*Check for minimum block*/
        if(currentSize >= minBlockSize && !ignoreMinSize){
            /*make it greater than limit*/
            currentSize = limit + 1;
        }

        if(transactionsSize <= minBlockSize && ignoreMinSize){
            currentSize = limit + 1;
        }

        /*Now the other transactions*/
        int numInterests = 0;
        /*already checked KEYS ()*/
        ArrayList<String> alreadyChecked = new ArrayList<>();
        /*Need split*/
        boolean needSplit = false;
        while(currentSize < limit){
            /*Get interest info with highest score*/
            /*First sort interests of same transaction attribute*/
            int thisLoopSize = 0;
            InterestInfo mostPopular = null;
            int howManyValues = 0;

            /*name of key*/
            String nameOfBest = null;
            for(Map.Entry e : interestInfo.entrySet()){

                /*Already checked this one so go to next*/
                if(alreadyChecked.contains(e.getKey().toString())){
                    continue;
                }

                HashMap<String,InterestInfo> infoMap = interestInfo.get(e.getKey());
                ArrayList<InterestInfo> list = new ArrayList<>();
                for(String key : infoMap.keySet()){
                    list.add(infoMap.get(key));
                }
                list.sort(InterestInfo::compareTo);

                /*Save best, so most popular*/
                if(mostPopular == null){
                    mostPopular = list.get(list.size()-1);
                    howManyValues = list.size();
                    nameOfBest = e.getKey().toString();
                }
                else{
                    float diff = (list.size() * 1.0F) / howManyValues;
                    InterestInfo current = list.get(list.size()-1);

                    if( (current.getCount() * 1.0F) > (diff * mostPopular.getCount()) ){
                        mostPopular = current;
                        howManyValues = list.size();
                        nameOfBest = e.getKey().toString();
                    }
                }

            }

            alreadyChecked.add(nameOfBest);
            if(mostPopular == null){
                alreadyChecked.clear();
                needSplit = true;
                continue;
            }
            /*Now check size of chosen transactions*/
            ArrayList<Integer> indices = mostPopular.getIndices();
            int stop = -1;
            for(int index = indices.size() - 1; index >= 0; index--){
                thisLoopSize += Block.calculateSingleTransactionSize(transactions.get(index));

                /*too large?*/
                if(currentSize + thisLoopSize > maxBlockSize){
                    stop = index;
                    break;
                }
            }

            /*Enough space?*/
            if(currentSize + thisLoopSize <= maxBlockSize){
                currentSize += thisLoopSize;
                for(int i = indices.size() - 1; i >= 0; i--) {
                    int index = indices.get(i);
                    chosenTransactions.add(transactions.get(index));
                    transactions.remove(index);
                    timeStamps.remove(index);
                }
                resetIndices(transactions);
            }
            /*One interest has more than max block size transactions*/
            else if(currentSize + thisLoopSize > maxBlockSize){
                /*In this case */
                if(numInterests == 0){
                    for(int i = indices.size() - 1; i > stop+1; i--) {
                        int index = indices.get(i);
                        chosenTransactions.add(transactions.get(index));
                        transactions.remove(index);
                        timeStamps.remove(index);
                    }
                    break;
                }
                else{ /*This case the transactions added make the block too large*/
                    numInterests++;
                    resetIndices(transactions);
                    /*After trying for all values greater than limit, we can se limit to minblocksize*/
                    if(numInterests == interestInfo.keySet().size()){
                        alreadyChecked.clear();
                        limit = minBlockSize;
                    }
                    else if(needSplit){
                        for(int i = indices.size() - 1; i > stop+1; i--) {
                            int index = indices.get(i);
                            chosenTransactions.add(transactions.get(index));
                            transactions.remove(index);
                            timeStamps.remove(index);
                        }
                        resetIndices(transactions);
                        break;
                    }
                    continue;
                }
            }

            /*Enough transactions?*/
            if(currentSize >= limit){
                break;
            }

            numInterests++;

            /*After trying for all values greater than limit, we can se limit to minblocksize*/
            if(numInterests == interestInfo.keySet().size()){
                alreadyChecked.clear();
                limit = minBlockSize;
            }
        }

        /*reset*/
        resetIndices(transactions);

        Block b = new Block(lastBlock.index + 1,lastBlock.getHeaderAsString(),new ArrayList<>(chosenTransactions));

        return b;
    }

    public boolean canCreateBlock(long size,long minSize,long maxSize){
        minBlockSize = minSize;
        maxBlockSize = maxSize;
        long diff = (maxBlockSize - minBlockSize)/2;

        /*TODO: test different values for the below one*/
        //System.out.println("SIZE IS " + size);
        //System.out.println("DIFF SIZE IS " + 2*(minSize + diff));
        /*So if min = 1000, max = 2000, we start when size = 3000 */
        if( size >= 2*(minSize + diff)){
            return true;
        }

        /*time check*/
        if(timeStamps.size() > 0){
            if(System.currentTimeMillis() - timeStamps.get(0) > lastTimeLimit){
                ignoreMinSize = true;
                return true;
            }
        }

        return false;
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
    public void resetIndices(ArrayList<HashMap<String,Object>> transactions){

        for(Map.Entry e : interestInfo.entrySet()){
            HashMap<String,InterestInfo> infoMap = interestInfo.get(e.getKey());
            for(Map.Entry entry : infoMap.entrySet()){
                InterestInfo info = (InterestInfo) entry.getValue();
                info.setIndices(new ArrayList<>());
                info.setCount(0);
            }
        }

        int i = 1;
        for(HashMap<String,Object> transaction : transactions){
            countTransactions(transaction,i);
            i++;
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
                    InterestInfo newInfo = new InterestInfo(true,STRING,(String) entry.getValue());
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
