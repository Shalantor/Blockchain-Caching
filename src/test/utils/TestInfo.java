package test.utils;

import java.util.HashMap;

public class TestInfo {

    /*Number of light and normal nodes*/
    private int numNormalNodes,numLightNodes;

    /*Percentages for the number of interests each node will have. They describe
    * what percentage of nodes will have 1, 2 and 3 interests. So these
    * arrays do have only 3 values*/
    private int[] normalNumPerc;
    private int[] lightNumPerc;

    /*Percentages of nodes that will have a certain type of interest. The type
    * can be string, double, long or integer. */
    /*I decided to put a hashmap with keys
    * S = string
    * L = long
    * D = double
    * I = integer.
    * The array can have any length (for me only 3, because I set for my project
    * a maximum of 3 interests per node, but nothing stops it from having more).
    * We have an array for each of the interest numbers that there can be.*/
    private HashMap<String,Integer>[] interestPercentages;

    /*Finally we have percentages for each interest. For example 20% of nodes may
    * have the exact same interest, that's what this represents. We have an array
    * of arrays. In my example i would have 3 arrays with percentages.*/
    private int[][] sameInterestPerc;

    public TestInfo(){

    }

    public void setNumNormalNodes(int numNormalNodes) {
        this.numNormalNodes = numNormalNodes;
    }

    public void setNumLightNodes(int numLightNodes) {
        this.numLightNodes = numLightNodes;
    }

    public void setNormalNumPerc(int[] normalNumPerc) {
        this.normalNumPerc = normalNumPerc;
    }

    public void setLightNumPerc(int[] lightNumPerc) {
        this.lightNumPerc = lightNumPerc;
    }

    public void setInterestPercentages(HashMap<String, Integer>[] interestPercentages) {
        this.interestPercentages = interestPercentages;
    }

    public void setSameInterestPerc(int[][] sameInterestPerc) {
        this.sameInterestPerc = sameInterestPerc;
    }
}
