package test.utils;

import nodes.LightNode;
import nodes.Node;
import nodes.NormalNode;

import java.io.File;

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

    public Node[] generateUniformInt(String[] filePaths,String config,int port, int timeOut,String host){

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

            /*Index for files*/
            int indexFile = 0;

            for(int j = 0; j < nodes; j++){

                /*Create new node*/
                System.out.println("File name is " + files[indexFile].getName());
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
}
