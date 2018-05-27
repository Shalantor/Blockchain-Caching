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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AverageHitRateTestModule {

    private static final String VOTING = "voting";
    private static final String MARKETPLACE = "marketplace";
    private static final String PAYMENT = "payment";

    public static void main(String[] args){
        /*Top level group-no_group, then block sizes and then replacement policies*/
        String[] cacheStrategies = new String[]{
                "interest_based_IB",
                "threshold_based_TB",
                "threshold_weight_based_WTB",
                "interest_based_block_size_IBBS",
                "threshold_based_block_size_TBBS",
                "threshold_based_recency_TBR",
                "pyramid_scheme_interest_based_IBPS"
        };

        /*Distributions*/
        String[] distributions = new String[]{
                "exponential_distribution",
                "poisson_distribution",
                "normal_distribution",
                "uniform_distribution",
                "zipfian_distribution"
        };

        //Start,end and step in parentheses
        ArrayList<Integer> sizes = new ArrayList<>();
        int value = 500;
        while (value <= 4000){
            sizes.add(value);
            value += 500;
        }

        //Random Sizes
        int[] randomSizes = new int[]{1,2000,2000,4000,1,4000};

        String[] groupOptions = new String[]{"group","no_group"};

        /*remove folder*/
        String destFolder = "averageHitRate/";
        try{
            FileUtils.deleteDirectory(new File(destFolder));
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        new File(destFolder).mkdirs();

        String configContent =
                "time_restraint     90000\n" +
                "network_topology    0   7000 7019   2\n" +
                "miner_node          localhost   7020\n" +
                "full_node           localhost   7021\n" +
                "storage             0\n";

        String configPath = "src/test/resources/node_config.txt";

        for(String option : groupOptions){
            System.out.println("in option " + option);
            for(String distribution : distributions){
                System.out.println("------in distribution " + distribution);

                PrintWriter writer = null;
                new File(destFolder + option + "_" + distribution).mkdirs();
                try{
                    writer = new PrintWriter(new File(destFolder + option + "_" + distribution + "/" + distribution + ".txt"));
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }

                for(Integer size : sizes){
                    System.out.println("------------------in size " +size);

                    writer.write(size + "");

                    for(String strategy : cacheStrategies){
                        System.out.println("--------------------------------in strategy " + strategy);

                        /*First create config file*/
                        new File(configPath).delete();
                        File configFile = new File(configPath);
                        try {
                            PrintWriter printWriter = new PrintWriter(configFile);
                            printWriter.write(configContent);

                            printWriter.write(getGroupConfig(option));
                            printWriter.write(getSizeConfig(size,-1,option));
                            printWriter.write(getCacheSizeConfig(size));
                            printWriter.write(getCacheConfig(strategy));

                            printWriter.close();
                        }
                        catch (IOException ex){
                            ex.printStackTrace();
                        }

                        /*test*/
                        doTest(distribution,writer,false);

                    }

                    writer.write("\n");

                }

                /*Now for ranges for random mode in miner*/
                for(int k=0; k < randomSizes.length; k+=2){
                    System.out.println("------------------in random size " +
                            randomSizes[k] + " and " + randomSizes[k+1]);

                    writer.write(randomSizes[k] + "__" + randomSizes[k+1]);

                    for(String strategy : cacheStrategies){
                        System.out.println("--------------------------------in strategy " + strategy);
                        /*First create config file*/
                        new File(configPath).delete();
                        File configFile = new File(configPath);
                        try {
                            PrintWriter printWriter = new PrintWriter(configFile);
                            printWriter.write(configContent);

                            printWriter.write(getGroupConfig(option));
                            printWriter.write(getSizeConfig(randomSizes[k],randomSizes[k+1],option));
                            printWriter.write(getRandomCacheSizeConfig(randomSizes[k],randomSizes[k+1]));
                            printWriter.write(getCacheConfig(strategy));

                            printWriter.close();
                        }
                        catch (IOException ex){
                            ex.printStackTrace();
                        }

                        /*test*/
                        doTest(distribution,writer,true);
                    }
                    writer.write("\n");
                }
                writer.flush();
                writer.close();

            }
        }
    }

    public static String getCacheSizeConfig(Integer size){
        int configSize = size * 40;
        return "max_cache_size\t" + configSize +"\n";
    }

    public static String getRandomCacheSizeConfig(Integer size,Integer nextSize){
        int configSize = ((nextSize - size) / 2 ) * 40;
        return "max_cache_size\t" + configSize +"\n";
    }

    public static String getCacheConfig(String measure){
        switch (measure){
            case "interest_based_IB":
                return "cache_configuration\t1\t0\n";
            case "threshold_based_TB":
                return "cache_configuration\t6\t4\n";
            case "threshold_weight_based_WTB":
                return "cache_configuration\t2\t10\n";
            case "interest_based_block_size_IBBS":
                return "cache_configuration\t3\t0\n";
            case "threshold_based_block_size_TBBS":
                return "cache_configuration\t4\t10\n";
            case "threshold_based_recency_TBR":
                return "cache_configuration\t5\t10\n";
            case "pyramid_scheme_interest_based_IBPS":
                return "cache_configuration\t7\t10\n";

        }
        return "\n";
    }

    public static String getSizeConfig(int size,int nextSize,String group){

        if(nextSize >= 0){
            return "min_block_size\t" + size + "\nmax_block_size\t" + nextSize + "\n";
        }

        switch (group){
            case "no_group":
                return "min_block_size\t" + size + "\nmax_block_size\t" + (2*size) + "\n";
            case "group":
                return "min_block_size\t" + (size - 499) + "\nmax_block_size\t" + (size+500) + "\n";
        }
        return "\n";
    }

    public static String getGroupConfig(String group){
        switch (group){
            case "no_group":
                return "group_content\tfalse\t0\n";
            case "group":
                return "group_content\ttrue\t0\n";
        }
        return "\n";
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

    public static void doTest(String distribution,PrintWriter writer,boolean mode){
        /*How many blocks to create?*/
        TestUtilities testUtilities = new TestUtilities(MARKETPLACE);
        Block block;
        HashMap<String,Object> transaction;
        /*create normal and light nodes. The nodes are now setup*/
        testUtilities.initLocal(100,100,new int[]{100,0,0},new int[]{100,0,0});
        Node[] nodes = testUtilities.nodes;
        float overall = 0;

        int count = 0;
        int allCount = 0;
        float hitRate = 0;
        for(int j = 0; j < 10; j++){

            MinerNode minerNode = testUtilities.createMiner();
            if(mode){
                minerNode.enableRandomMode();
            }
            for(int i =0; i < 200; i++){
                while(true) {
                    /*Add transactions until enough for block*/
                    transaction = getTransaction(testUtilities,distribution);
                    block = minerNode.addTransactionLocal(transaction);
                    if (block != null) {
                        break;
                    }
                }

                /*Now add block to each node*/
                for(Node n : nodes){
                    if(n instanceof NormalNode){
                        ((NormalNode) n).checkBlock(block);
                    }
                    else if(n instanceof LightNode){
                        ((LightNode) n).checkBlock(block);
                    }
                }

            }

            count = 0;
            allCount = 0;
            hitRate = 0;
            for(Node n : nodes){
                if(n instanceof NormalNode){
                    //System.out.println(((NormalNode) n).cacheManager.getBlocksInCache().size());
                    //System.out.println(((NormalNode) n).cacheManager.getSizeOfCachedBlocks());
                    count = ((NormalNode) n).cacheManager.getBlocksInCache().size();
                    allCount = ((NormalNode) n).cacheManager.interestedBlocks;
                    if(allCount == 0){
                        hitRate += 1.0f;
                        continue;
                    }
                    hitRate += (1.0f*count) / allCount;
                    ((NormalNode) n).cacheManager.clearAll();
                }
                else if(n instanceof LightNode){
                    //System.out.println(((LightNode) n).cacheManager.getBlocksInCache().size());
                    //System.out.println(((LightNode) n).cacheManager.getSizeOfCachedBlocks());
                    count = ((LightNode) n).cacheManager.getBlocksInCache().size();
                    allCount = ((LightNode) n).cacheManager.interestedBlocks;
                    if(allCount == 0){
                        hitRate += 1.0f;
                        continue;
                    }
                    hitRate += (1.0f*count) / allCount;
                    ((LightNode) n).cacheManager.clearAll();
                }
            }
            //System.out.println("Count is " + count);
            overall += (hitRate / 200.0f);
        }

        //System.out.println("overall is " + overall);
        writer.write(" " + (new DecimalFormat("##.##").format(overall/10)));

    }
}
