package test.utils;

import nodes.LightNode;
import nodes.Node;
import nodes.NormalNode;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class TestInfo {

    /*Number of light and normal nodes*/
    private int numNormalNodes,numLightNodes;

    /*Percentages for the number of interests each node will have. They describe
    * what percentage of nodes will have 1, 2 and 3 interests. So these
    * arrays do have only 3 values*/
    private int[] normalNumPerc;
    private int[] lightNumPerc;

    public TestInfo(int numNormalNodes,int numLightNodes,
                    int[] normalNumPerc,int[] lightNumPerc){
        this.numNormalNodes = numNormalNodes;
        this.numLightNodes = numLightNodes;
        this.normalNumPerc = normalNumPerc;
        this.lightNumPerc = lightNumPerc;
    }

    /*generate interests with uniform distribution*/
    public Node[] generateInterests(String[] filePaths,String config,int port, int timeOut,String host){

        /*Index in array to store node*/
        int indexNode = 0;
        Node[] returnNodes = new Node[numNormalNodes + numLightNodes];

        /*First the normal nodes*/
        for(int i =0; i < normalNumPerc.length; i++){

            /*How many nodes have this amount of interests?*/
            int nodes = normalNumPerc[i] * numNormalNodes / 100;

            /*Folder and files in that folder*/
            File folder = new File(filePaths[i]);
            File[] files = folder.listFiles();
            Arrays.sort(files, Collections.reverseOrder());

            /*Index for files*/
            int indexFile = 0;

            for(int j = 0; j < nodes; j++){

                /*Create new node*/
                NormalNode n = new NormalNode(config,filePaths[i] + files[indexFile].getName(),
                        port + indexNode,timeOut,host);

                /*Add to nodes array*/
                returnNodes[indexNode] = n;

                /*Increment valid indexes*/
                indexFile = (indexFile + 1) % files.length;
                indexNode ++;
            }

        }

        /*Now light nodes*/
        for(int i =0; i < lightNumPerc.length; i++){

            /*How many nodes have this amount of interests?*/
            int nodes = lightNumPerc[i] * numLightNodes / 100;

            /*Folder and files in that folder*/
            File folder = new File(filePaths[i]);
            File[] files = folder.listFiles();
            Arrays.sort(files, Collections.reverseOrder());

            /*Index for files*/
            int indexFile = 0;

            for(int j = 0; j < nodes; j++){

                /*Create new node*/
                LightNode n = new LightNode(config,filePaths[i] + files[indexFile].getName(),
                        port + indexNode,timeOut,host);

                /*Add to nodes array*/
                returnNodes[indexNode] = n;

                /*Increment valid indexes*/
                indexFile = (indexFile + 1) % files.length;
                indexNode ++;
            }
        }

        return returnNodes;
    }

    /*generate interests with skewed distribution*/
    public Node[] generateInterestsZipf(String[] filePaths,String config,int port, int timeOut,String host){

        /*Index in array to store node*/
        int indexNode = 0;
        Node[] returnNodes = new Node[numNormalNodes + numLightNodes];

        /*First the normal nodes*/
        for(int i =0; i < normalNumPerc.length; i++){

            /*How many nodes have this amount of interests?*/
            int nodes = normalNumPerc[i] * numNormalNodes / 100;

            /*Folder and files in that folder*/
            File folder = new File(filePaths[i]);
            File[] files = folder.listFiles();
            Arrays.sort(files, Collections.reverseOrder());

            /*Index for files*/
            ZipfDistribution zipf = new ZipfDistribution(files.length-1,1);

            for(int j = 0; j < nodes; j++){


                /*Create new node*/
                NormalNode n = new NormalNode(config,filePaths[i] + files[zipf.sample()].getName(),
                        port + indexNode,timeOut,host);

                /*Add to nodes array*/
                returnNodes[indexNode] = n;

                indexNode ++;
            }

        }

        /*Now light nodes*/
        for(int i =0; i < lightNumPerc.length; i++){

            /*How many nodes have this amount of interests?*/
            int nodes = lightNumPerc[i] * numLightNodes / 100;

            /*Folder and files in that folder*/
            File folder = new File(filePaths[i]);
            File[] files = folder.listFiles();
            Arrays.sort(files, Collections.reverseOrder());

            /*Index for files*/
            ZipfDistribution zipf = new ZipfDistribution(files.length-1,1);

            for(int j = 0; j < nodes; j++){

                /*Create new node*/
                LightNode n = new LightNode(config,filePaths[i] + files[zipf.sample()].getName(),
                        port + indexNode,timeOut,host);

                /*Add to nodes array*/
                returnNodes[indexNode] = n;

                indexNode ++;
            }
        }

        return returnNodes;
    }

    /*generate interest files with normal distribution*/
    /*generate interests with skewed distribution*/
    public Node[] generateInterestsNormal(String[] filePaths,String config,int port, int timeOut,String host){

        /*Index in array to store node*/
        int indexNode = 0;
        Node[] returnNodes = new Node[numNormalNodes + numLightNodes];

        /*First the normal nodes*/
        for(int i =0; i < normalNumPerc.length; i++){

            /*How many nodes have this amount of interests?*/
            int nodes = normalNumPerc[i] * numNormalNodes / 100;

            /*Folder and files in that folder*/
            File folder = new File(filePaths[i]);
            File[] files = folder.listFiles();
            Arrays.sort(files, Collections.reverseOrder());

            /*Index for files*/
            NormalDistribution normal = new NormalDistribution();
            double gauss;

            for(int j = 0; j < nodes; j++){


                /*Create new node*/
                gauss = normal.sample();
                while(Math.abs(gauss) > 1.0){
                    gauss = normal.sample();
                }
                NormalNode n = new NormalNode(config,filePaths[i] + files[(int)(Math.abs(gauss) * files.length)].getName(),
                        port + indexNode,timeOut,host);

                /*Add to nodes array*/
                returnNodes[indexNode] = n;

                indexNode ++;
            }

        }

        /*Now light nodes*/
        for(int i =0; i < lightNumPerc.length; i++){

            /*How many nodes have this amount of interests?*/
            int nodes = lightNumPerc[i] * numLightNodes / 100;

            /*Folder and files in that folder*/
            File folder = new File(filePaths[i]);
            File[] files = folder.listFiles();
            Arrays.sort(files, Collections.reverseOrder());

            /*Index for files*/
            NormalDistribution normal = new NormalDistribution();
            double gauss;

            for(int j = 0; j < nodes; j++){

                /*Create new node*/
                /*Create new node*/
                gauss = normal.sample();
                while(Math.abs(gauss) > 1.0){
                    gauss = normal.sample();
                }
                LightNode n = new LightNode(config,filePaths[i] + files[(int)(Math.abs(gauss) * files.length)].getName(),
                        port + indexNode,timeOut,host);

                /*Add to nodes array*/
                returnNodes[indexNode] = n;

                indexNode ++;
            }
        }

        return returnNodes;
    }
}
