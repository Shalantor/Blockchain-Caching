package nodes;

import java.util.*;

/*Implementation of the normal node*/
public class NormalNode {

    /*value indicating no cache size limit*/
    public static final int NO_LIMIT = -1;

    /*Does the node have a maximum cache size?*/
    private long maxCacheSize = NO_LIMIT;
    private long timeRestraint = NO_LIMIT;

    /*Size in bytes of interests*/
    private long size;

    /*The constructor as of now*/
    public NormalNode(List<String> interests){
    }

    public NormalNode(List<String> interests,long maxCacheSize, long timeRestraint){
        this(interests);
        this.maxCacheSize = maxCacheSize;
        this.timeRestraint = timeRestraint;
    }

}
