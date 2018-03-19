package nodes;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Node {

    /*Read configuration from text file*/
    public Node(String configFilePath){

    }

    /*Create JSON object from transaction*/
    public JSONObject transactionToJSON(HashMap<String,Object> transaction){
        JSONObject jsonObject = new JSONObject();

        for(Map.Entry entry : transaction.entrySet()){
            jsonObject.put(entry.getKey().toString(),entry.getValue());
        }

        System.out.println(jsonObject);
        return jsonObject;
    }



}
