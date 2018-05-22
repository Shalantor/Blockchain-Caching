package test;

import nodes.LightNode;
import nodes.MinerNode;
import nodes.Node;
import nodes.NormalNode;
import org.apache.commons.io.FileUtils;
import structures.Block;
import test.utils.TestUtilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ResultTestModule{

    public static void main(String[] args) {

        int[] blockSizes = new int[]{500,1000,1500,2000,2500};
        int[] thresholds = new int[]{1,2,3};
        int[] thresholdsWeight = new int[]{1,2,3,4,5};

        String resultsFolder = "results/";

        //content of config file
        String configContent = "max_cache_size     900000\n" +
                                "time_restraint     90000\n" +
                                "network_topology    0   7000 7019   2\n" +
                                "miner_node          localhost   7020\n" +
                                "full_node           localhost   7021\n" +
                                "storage             0";

        //delete directory
        try{
            FileUtils.deleteDirectory(new File(resultsFolder));
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        String[] categories = new String[]{"marketplace"};

        String[] topLevelFolders = new String[]{
                "interest_based_IB",
                "threshold_based_TB",
                "threshold_weight_based_WTB",
                "interest_based_block_size_IBBS",
                "threshold_based_block_size_TBBS",
                "threshold_based_recency_TBR",
                "pyramid_scheme_interest_based_IBPS"
        };

        String[] distributions = new String[]{
                "exponential_distribution",
                "poisson_distribution",
                "normal_distribution",
                "uniform_distribution",
                "zipfian_distribution"
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
                System.out.println("IN CONFIGURATION " + s1);
                new File(resultsFolder + s0 + "/" + s1).mkdirs();
                for(String s2 : blockSizeFolders){
                    new File(resultsFolder + s0 + "/" + s1 + "/" + s2).mkdirs();
                    for(String s3 : groupFolders){
                        new File(resultsFolder + s0 + "/" + s1 + "/" + s2 + "/" + s3).mkdirs();
                        if(s1.contains("threshold_weight")){
                            for(String s4 : thresholdWeightFolders){
                                String dest = resultsFolder + s0 + "/" + s1 + "/" + s2 + "/" + s3 + "/" + s4;
                                new File(dest).mkdirs();
                                createConfigFile(configContent,s2,s3,s1,s4);
                                testLocal( dest + "/", s0,distributions);
                            }
                        }
                        else if(s1.contains("threshold")){
                            for(String s4 : thresholdFolders){
                                String dest = resultsFolder + s0 + "/" + s1 + "/" + s2 + "/" + s3 + "/" + s4;
                                new File(dest).mkdirs();
                                createConfigFile(configContent,s2,s3,s1,s4);
                                testLocal(dest + "/", s0,distributions);
                            }
                        }
                        else{
                            String dest = resultsFolder + s0 + "/" + s1 + "/" + s2 + "/" + s3 + "/";
                            createConfigFile(configContent,s2,s3,s1,"dummy_1");
                            testLocal(dest,s0,distributions);
                        }
                    }
                }
            }
        }
    }

    public static void testLocal(String destPath,String category, String[] distributions){

        TestUtilities testUtilities = new TestUtilities(category,false);
        /*create normal and light nodes. The nodes are now setup*/
        testUtilities.initLocalOldFiles(10, 10, new int[]{70, 20, 10}, new int[]{70, 20, 10});
        /*create miner node*/
        MinerNode minerNode = testUtilities.createMiner();
        for(String distribution : distributions) {

            /*How many blocks to create?*/
            Block block;
            HashMap<String, Object> transaction;
            Node[] nodes = testUtilities.nodes;
            for(Node n : nodes){
                if (n instanceof NormalNode) {
                    ((NormalNode) n).cacheManager.clearAll();
                } else if (n instanceof LightNode) {
                    ((LightNode) n).cacheManager.clearAll();
                }
            }
            long blockChainSize = 0;
            for (int i = 0; i < 10; i++) {
                while (true) {
                    /*Add transactions until enough for block*/
                    transaction = getTransaction(testUtilities,distribution);
                    block = minerNode.addTransactionLocal(transaction);
                    if (block != null) {
                        blockChainSize += block.blockSize;
                        break;
                    }
                }

                /*Now add block to each node*/
                for (Node n : nodes) {
                    if (n instanceof NormalNode) {
                        ((NormalNode) n).checkBlock(block);
                    } else if (n instanceof LightNode) {
                        ((LightNode) n).checkBlock(block);
                    }
                }

            }

            File blockResults = new File(destPath + distribution + "_blocks.txt");
            File sizeResultsNormal = new File(destPath + distribution + "_size_normal.txt");
            File sizeResultsLight = new File(destPath + distribution + "_size_light.txt");
            File overallSize = new File(destPath + distribution + "full_block_chain_size.txt");

            try{
                PrintWriter writerBlocks = new PrintWriter(blockResults);
                PrintWriter writerSizeNormal = new PrintWriter(sizeResultsNormal);
                PrintWriter writerSizeLight = new PrintWriter(sizeResultsLight);
                PrintWriter allSizeWriter = new PrintWriter(overallSize);

                allSizeWriter.println(blockChainSize);

                for (Node n : nodes) {
                    if (n instanceof NormalNode) {
                        writerBlocks.println(((NormalNode) n).cacheManager.getBlocksInCache().size());
                        writerSizeNormal.println(((NormalNode) n).cacheManager.getSizeOfCachedBlocks());

                    } else if (n instanceof LightNode) {
                        writerBlocks.println(((LightNode) n).cacheManager.getBlocksInCache().size());
                        writerSizeLight.println(((LightNode) n).cacheManager.getSizeOfCachedBlocks());
                    }
                }
                writerBlocks.flush();
                writerSizeNormal.flush();
                writerSizeLight.flush();

                writerBlocks.close();
                writerSizeNormal.close();
                writerSizeLight.close();

                allSizeWriter.flush();
                allSizeWriter.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }


    }

    public static HashMap<String,Object> getTransaction(TestUtilities t, String distribution){

        HashMap<String,Object> transaction;
        switch (distribution){
            case "exponential_distribution":
                transaction = t.getTransactionExponential();
                break;
            case "poisson_distribution":
                transaction = t.getTransactionPoisson();
                break;
            case "normal_distribution":
                transaction = t.getTransactionNormal();
                break;
            case "uniform_distribution":
                transaction = t.getTransactionUniform();
                break;
            case "zipfian_distribution":
                transaction = t.getTransactionZipfian();
                break;
                default:
                    transaction = null;
        }
        return transaction;
    }

    public static void createConfigFile(String configContent,String sizes,
                                        String group,String cacheConfig, String thresholdString){

        String config = "cache_configuration\t";

        int threshold = Integer.parseInt(thresholdString.substring(thresholdString.length()-1));

        switch (cacheConfig){
            case "interest_based_IB":
                config += "1\t1";
                break;
            case "threshold_based_TB":
                config += "6\t" + threshold;
                break;
            case "threshold_weight_based_WTB":
                config += "2\t" + threshold;
                break;
            case "interest_based_block_size_IBBS":
                config += "3\t1";
                break;
            case "threshold_based_block_size_TBBS":
                config += "4\t" + threshold;
                break;
            case "threshold_based_recency_TBR":
                config += "5\t" + threshold;
                break;
            case "pyramid_scheme_interest_based_IBPS":
                config+= "7\t1";
        }

        boolean groupTransactions = false;
        if(group.equals("transaction_group")){
            groupTransactions = true;
        }
        else if(group.equals("no_transaction_group")){
            groupTransactions = false;
        }

        int min = Integer.parseInt(sizes.substring(sizes.indexOf("min")+3,sizes.indexOf("max")-1));
        int max = Integer.parseInt(sizes.substring(sizes.indexOf("max")+3));

        /*have to create config file*/
        File configFile = new File("src/test/resources/node_config.txt");
        try{
            PrintWriter writer = new PrintWriter(configFile);
            writer.println(configContent);

            writer.println("min_block_size\t" + min);
            writer.println("max_block_size\t" + max);
            writer.println("group_content\t" + groupTransactions + "\t0");
            writer.println(config);

            writer.flush();
            writer.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
