package structures;

/*The data structures for the block*/

import nodes.Node;
import org.json.JSONArray;
import org.json.JSONObject;
import storage.storageUtils.BlockExplorer;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Block implements Comparable<Block>{

    /*Every block has these fields*/
    /*This is the block header*/
    public int index;
    public long timestamp;
    public String transactionHash;
    public String previousBlockHash;
    public long blockSize;

    /*This is the data of the block*/
    public int numTransactions;
    public ArrayList<HashMap<String,Object>> transactions;

    public Block(){

    }

    /*Create block from json object*/
    public Block(JSONObject jsonObject,Node node){

        /*get headers*/
        index = (int)jsonObject.get("index");
        timestamp = (long)jsonObject.get("timestamp");
        transactionHash = (String)jsonObject.get("transaction_hash");
        previousBlockHash = (String)jsonObject.get("previous_block_hash");
        blockSize = jsonObject.getLong("block_size");
        numTransactions = (int)jsonObject.get("number_transactions");

        /*Extract transactions*/
        JSONArray jsonTransactions = jsonObject.getJSONArray("transactions");
        transactions = new ArrayList<>();

        for(int i=0;i < jsonTransactions.length(); i++){
            JSONObject jsonTransaction = jsonTransactions.getJSONObject(i);
            HashMap<String,Object> newTransaction;
            newTransaction = node.JSONToTransaction(jsonTransaction);

            transactions.add(newTransaction);
        }
    }

    public Block(int index, String previousBlockHeader,
                 ArrayList<HashMap<String,Object>> transactions){

        /*Assign values that do not need calculation*/
        this.index = index;
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

        byte[] hash;

        /*Calculate previous hash*/
        hash = digest.digest(previousBlockHeader.getBytes());
        this.previousBlockHash = DatatypeConverter.printHexBinary(hash);

        /*First create array of hashes of transactions*/
        StringBuilder hashInput = new StringBuilder();
        ArrayList<String> initialHashes = new ArrayList<>();
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

        //System.out.println(blockSize);
    }

    /*TODO: Make this dynamic based on object variables, without any prior knowledge*/
    private long calculateSize(){

        /*Static variables*/
        /*both hashes have size 32Bytes, so 64Bytes all in all*/
        /*3 * 8 bytes for the long fields = 24 bytes*/
        /* + 4 bytes the int*/
        /*So 32 + 32 + 8 + 8 + 8 + 4 = 92 bytes*/
        long size = 92;

        return calculateTransactionSize(transactions,size);
    }

    /*Used to calculate previous hash*/
    public String getHeaderAsString(){
        return index + timestamp + transactionHash + previousBlockHash + blockSize;
    }

    public static long calculateTransactionSize(ArrayList<HashMap<String,Object>> transactions,long size){
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

    public static long calculateSingleTransactionSize(HashMap<String,Object> transaction){
        long size = 0;
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
        return size;
    }


    public long getHeaderSize(){
        return 92;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return index == block.index &&
                Objects.equals(transactionHash, block.transactionHash);
    }

    @Override
    public int hashCode() {

        return Objects.hash(index, transactionHash);
    }

    @Override
    public int compareTo(Block block){
        return index - block.index;
    }

    /*Below stuff is for testing*/
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(HashMap<String,Object> tr : transactions){
            str.append(tr.toString());
            str.append("\n");
        }
        return "Index in Blockchain: " + index + "\n" +
                "Timestamp: " + timestamp + "\n" +
                "Block size: " + blockSize + "\n" +
                "Previous block hash: " + previousBlockHash + "\n" +
                "Transaction root hash: " + transactionHash + "\n" +
                "Number of transactions in block " + numTransactions + "\n" +
                "Transactions in block:\n " + str + "\n";
    }

}
