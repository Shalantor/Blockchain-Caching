package structure;

/*The data structure for the block*/

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block {

    /*Every block has these fields*/
    /*This is the block header*/
    public long index;
    public long timestamp;
    public String transactionHash;
    public String previousBlockHash;
    public long blockSize;

    /*This is the data of the block*/
    public int numTransactions;
    public List<HashMap<String,Object>> transactions;

    public Block(long index, String previousBlockHash,
                 List<HashMap<String,Object>> transactions){

        /*Assign values that do not need calculation*/
        this.index = index + 1;
        this.previousBlockHash = previousBlockHash;
        this.transactions = transactions;
        numTransactions = transactions.size();

        /*calculate merkle root of merkle tree*/

        /*Initialize digest module*/
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch(NoSuchAlgorithmException ex){
            System.out.println("NOPE");
            System.exit(1);
        }

        /*First create array of hashes of transactions*/
        StringBuilder hashInput = new StringBuilder();
        ArrayList<String> initialHashes = new ArrayList<>();
        byte[] hash;
        for(HashMap<String,Object> transaction : transactions){
            for(Map.Entry entry : transaction.entrySet()){
                hashInput.append(entry.getKey().toString());
                hashInput.append(entry.getValue().toString());
            }
            hash = digest.digest(hashInput.toString().getBytes());
            initialHashes.add(DatatypeConverter.printHexBinary(hash));
        }

        /*Now calculate the root of the merkle tree from the hashes*/
        int pos;
        String combinedHash;
        while(initialHashes.size() > 1){
            pos = 0;
            for(int i =0; i < initialHashes.size(); i = i+2 ){
                if(i == initialHashes.size() - 1){
                    combinedHash = initialHashes.get(i) + initialHashes.get(i);
                }
                else{
                    combinedHash = initialHashes.get(i) + initialHashes.get(i+1);
                }
                hash = digest.digest(combinedHash.getBytes());
                initialHashes.set(pos,DatatypeConverter.printHexBinary(hash));
                pos++;
            }

            initialHashes.subList(pos,initialHashes.size()).clear();
        }

        transactionHash = initialHashes.get(0);

        timestamp = System.currentTimeMillis();

        blockSize = calculateSize();

        System.out.println(blockSize);
    }

    /*TODO: Make this dynamic based on object variables, without any prior knowledge*/
    private long calculateSize(){

        /*Static variables*/
        /*both hashes have size 32Bytes, so 64Bytes all in all*/
        /*3 * 8 bytes for the long fields = 24 bytes*/
        /* + 4 bytes the int*/
        /*So 32 + 32 + 8 + 8 + 8 + 4 = 92 bytes*/
        long size = 92;

        for(HashMap<String,Object> transaction : transactions){
            for(Map.Entry entry : transaction.entrySet()){
                try {
                    size = size + entry.getKey().toString().getBytes("UTF-8").length;
                }
                catch(UnsupportedEncodingException ex){
                    ex.printStackTrace();
                    System.exit(1);
                }
                Object value = entry.getValue();
                if(value instanceof Double){
                    size += 8;
                }
                else if(value instanceof Long){
                    size += 8;
                }
                else if(value instanceof Integer){
                    size += 4;
                }
                else if(value instanceof String){
                    try {
                        size = size + entry.getKey().toString().getBytes("UTF-8").length;
                    }
                    catch(UnsupportedEncodingException ex){
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }


        return size;
    }

}
