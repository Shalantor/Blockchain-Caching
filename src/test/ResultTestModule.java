package test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ResultTestModule{

    public static void main(String[] args) {

        int[] blockSizes = new int[]{500,1000,1500,2000,2500};
        int[] thresholds = new int[]{1,2,3,4,5};
        int[] thresholdsWeight = new int[]{1,2,3,4,5,6,7,8,9,10};

        String resultsFolder = "results/";

        //delete directory
        try{
            FileUtils.deleteDirectory(new File(resultsFolder));
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        String[] categories = new String[]{"marketplace","voting","payment"};

        String[] topLevelFolders = new String[]{
                "interest_based_IB",
                "threshold_based_TB",
                "threshold_weight_based_WTB",
                "interest_based_block_size_IBBS",
                "interest_based_recency_IBR",
                "threshold_based_block_size_TBBS",
                "threshold_based_recency_TBR"
        };

        String[] resFileNames = new String[]{
                "exponential_distribution.txt",
                "poisson_distribution.txt",
                "normal_distribution.txt",
                "uniform_distribution.txt",
                "zipfian_distribution.txt"
        };

        //Folders that indicate whether miner groups transactions or not
        String[] groupFolders = new String[]{
                "transaction_group",
                "no_transaction_group"
        };

        //names for folders that indicate block size
        String[] blockSizeFolders = new String[blockSizes.length-1];
        String prefix = "block_size_min";
        String middle = "_max";

        for(int i = 0; i < blockSizes.length - 1; i++){
            blockSizeFolders[i] = prefix + blockSizes[i] + middle + blockSizes[i+1];
        }
        //folders for thresholds
        String[] thresholdFolders = new String[thresholds.length];
        String[] thresholdWeightFolders = new String[thresholdsWeight.length];

        prefix = "threshold_";
        for(int i = 0; i < thresholds.length;i++){
            thresholdFolders[i] = prefix + (i+1);
        }

        prefix = "threshold_weight_";
        for(int i = 0; i < thresholdsWeight.length;i++){
            thresholdWeightFolders[i] = prefix + (i+1);
        }

        /*Now that everything is setup create folder structure*/
        for(String s0 : categories){
            new File(resultsFolder + s0);
            for(String s1 : topLevelFolders){
                new File(resultsFolder + s0 + "/" + s1).mkdirs();
                for(String s2 : blockSizeFolders){
                    new File(resultsFolder + s0 + "/" + s1 + "/" + s2).mkdirs();
                    for(String s3 : groupFolders){
                        new File(resultsFolder + s0 + "/" + s1 + "/" + s2 + "/" + s3).mkdirs();
                        if(s1.contains("threshold_weight")){
                            for(String s4 : thresholdWeightFolders){
                                new File(resultsFolder + s0 + "/" + s1 + "/" + s2 + "/" + s3 + "/" + s4).mkdirs();
                            }
                        }
                        else if(s1.contains("threshold")){
                            for(String s4 : thresholdFolders){
                                new File(resultsFolder + s0 + "/" + s1 + "/" + s2 + "/" + s3 + "/" + s4).mkdirs();
                            }
                        }
                    }
                }
            }
        }
    }

}
