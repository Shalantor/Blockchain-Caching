package nodes;

import cacheManager.CacheManager;
import cacheManager.SimpleCacheManager;
import structures.Block;
import structures.Interest;
import structures.StrippedBlock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LightNode extends NormalNode{

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
    public static final int NO_LIMIT = 0;

    /*Does the node have a maximum cache size?*/
    private long maxCacheSize;
    private long timeRestraint;

    /*Interest name with the corresponding interest object*/
    private Map<String, Interest> interests = new HashMap<>();

    /*ArrayList of interested blocks*/
    private ArrayList<StrippedBlock> blocksInCache = new ArrayList<>();

    /*Cache manager*/
    /*TODO:Change hardcode*/
    private CacheManager cacheManager = new SimpleCacheManager();

    /*The constructor as of now*/
    public LightNode(String configFilePath,String interestFilePath){

        super(configFilePath,interestFilePath);
    }

    /*Below stuff is just for printing*/
    public void printInterests(){
        for(Map.Entry entry : interests.entrySet()){
            System.out.println("Interest name: " + entry.getKey());
            ((Interest)entry.getValue()).printInfo();
        }
    }

    public boolean checkBlock(Block block){
        for (Map.Entry entry : interests.entrySet()){
            if(((Interest)entry.getValue()).checkBlock(block)){
                System.out.println("YES MOTHERFUCKERS INTERESTED");

                return true;
            }
        }
        return false;
    }
}
