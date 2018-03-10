package structure;

import java.io.BufferedReader;;
import java.io.FileReader;
import java.io.IOException;

/*Class that represents the transaction structure*/
public class Transaction {

    private static final String configFilename = "example.txt";
    private static final String filePath = "src/test/resources/" + configFilename;

    public Transaction(){

        /*Open and read from example file*/
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            while (line != null) {
                line = br.readLine();
                System.out.println(line);
            }

        }
        catch(IOException ex){
            System.out.println("Io exception occurred");
            ex.printStackTrace();
        }


    }

}
