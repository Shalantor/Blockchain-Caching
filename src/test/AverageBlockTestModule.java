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

public class AverageBlockTestModule {

    private static final String VOTING = "voting";
    private static final String MARKETPLACE = "marketplace";
    private static final String PAYMENT = "payment";

    public static void main(String[] args) {

        /*Distributions*/
        String[] distributions = new String[]{
                "exponential_distribution",
                "poisson_distribution",
                "normal_distribution",
                "uniform_distribution",
                "zipfian_distribution"
        };

        String[] groupOptions = new String[]{"no_group","group"};

        /*What to measure*/
        String[] measures = new String[]{"IB","TB_2","TB_4","TB_6","TWB_6","TWB_8","TWB_10"};

        //Start,end and step in parentheses
        int[] sizes = new int[(4000 - 500) / 500];
        int value = 500;
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = value;
            value += 500;
        }

        String destFolder = "averageSizes/";
        try{
            FileUtils.deleteDirectory(new File(destFolder));
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        new File(destFolder).mkdirs();

        String configContent = "max_cache_size     100000000\n" +
                "time_restraint     90000\n" +
                "network_topology    0   7000 7019   2\n" +
                "miner_node          localhost   7020\n" +
                "full_node           localhost   7021\n" +
                "storage             0\n";

        String configPath = "src/test/resources/node_config.txt";

        for (String distribution : distributions) {
            System.out.println("in distribution " + distribution);
            for(String option : groupOptions){
                System.out.println("-----in option " + option);

                /*Ready file*/
                new File(destFolder + "/" + option + "_" +  distribution).mkdirs();
                File destFile = new File(destFolder + "/" + option + "_" +  distribution + "/" + distribution + ".txt");
                PrintWriter writer = null;
                try{
                    writer = new PrintWriter(destFile);
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }

                for(int size : sizes){

                    System.out.println("------------------in size " +size);

                    writer.write(size);

                    for(String measure : measures){
                        System.out.println("--------------------------------in measure " + measure);
                        /*First create config file*/
                        new File(configPath).delete();
                        File configFile = new File(configPath);
                        try {
                            PrintWriter printWriter = new PrintWriter(configFile);
                            printWriter.write(configContent);

                            printWriter.write(getGroupConfig(option));
                            printWriter.write(getSizeConfig(size,option));
                            printWriter.write(getCacheConfig(measure));

                            printWriter.close();
                        }
                        catch (IOException ex){
                            ex.printStackTrace();
                        }

                        /*test*/
                        doTest(distribution,writer);
                    }

                    /*close the file again*/
                    writer.write("\n");
                    writer.close();

                }
            }
        }
    }

    public static String getCacheConfig(String measure){
        switch (measure.substring(0,2)){
            case "IB":
                return "cache_configuration\t1\t0\n";
            case "TB":
                return "cache_configuration\t6\t" + measure.substring( (measure.indexOf("_") + 1) ) +"\n";
            case "TW":
                return "cache_configuration\t2\t" + measure.substring( (measure.indexOf("_") + 1) ) +"\n";
        }
        return "\n";
    }

    public static String getSizeConfig(int size,String group){
        switch (group){
            case "no_group":
                return "min_block_size\t" + size + "\nmax_block_size\t" + (2*size) + "\n";
            case "group":
                return "min_block_size\t" + (size - 250) + "\nmax_block_size\t" + (size+250) + "\n";
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

    public static void doTest(String distribution,PrintWriter writer){
        /*How many blocks to create?*/
        TestUtilities testUtilities = new TestUtilities(MARKETPLACE);
        Block block;
        HashMap<String,Object> transaction;
        /*create normal and light nodes. The nodes are now setup*/
        testUtilities.initLocal(100,100,new int[]{100,0,0},new int[]{100,0,0});
        Node[] nodes = testUtilities.nodes;
        float overall = 0;

        MinerNode minerNode = testUtilities.createMiner();

        int count = 0;
        for(int j = 0; j < 10; j++){
            for(int i =0; i < 500; i++){
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
            for(Node n : nodes){
                if(n instanceof NormalNode){
                    //System.out.println(((NormalNode) n).cacheManager.getBlocksInCache().size());
                    //System.out.println(((NormalNode) n).cacheManager.getSizeOfCachedBlocks());
                    count += ((NormalNode) n).cacheManager.getBlocksInCache().size();
                    ((NormalNode) n).cacheManager.clearAll();
                }
                else if(n instanceof LightNode){
                    //System.out.println(((LightNode) n).cacheManager.getBlocksInCache().size());
                    //System.out.println(((LightNode) n).cacheManager.getSizeOfCachedBlocks());
                    count += ((LightNode) n).cacheManager.getBlocksInCache().size();
                    ((LightNode) n).cacheManager.clearAll();
                }
            }
            //System.out.println("Count is " + count);
            overall += (count / 200.0f);
        }

        //System.out.println("overall is " + overall);
        writer.write(" " + (overall/10));

    }
}
