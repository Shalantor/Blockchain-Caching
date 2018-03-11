package structure;

/*The data structure for the block*/

import com.sun.xml.internal.ws.api.message.Message;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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

    public Block(long index, String previousBlockHash, List<HashMap<String,Object>> transactions){

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

        /*First create array of hashes*/
        StringBuilder hashInput = new StringBuilder();
        ArrayList<String> initialHashes = new ArrayList<>();
        byte[] hash;
        for(HashMap<String,Object> transaction : transactions){
            for(Map.Entry entry : transaction.entrySet()){
                hashInput.append(entry.getKey().toString());
                hashInput.append(entry.getValue().toString());
            }
            hash = digest.digest(hashInput.toString().getBytes(StandardCharsets.UTF_8));
            initialHashes.add(DatatypeConverter.printHexBinary(hash));
        }


        timestamp = System.currentTimeMillis();
    }

}
