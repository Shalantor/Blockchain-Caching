package structures.minerUtils;

import structures.Block;
import structures.minerUtils.groupInfo.InterestInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class PopularityGroupManager extends GroupManager{

    private String interestFilePath;

    /*Key is the name of the interest. The hashmap is sorted based on values*/
    private HashMap<String,InterestInfo> interestInfo;

    public PopularityGroupManager(String interestFilePath){
        this.interestFilePath = interestFilePath;
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
