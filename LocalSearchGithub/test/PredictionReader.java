
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */
public class PredictionReader {
    
    void readPredictions(File input, HashMap<Integer,ArrayList<Integer>> map){
        
        Path p = Paths.get(input.getAbsolutePath());
         try{
            BufferedReader read = Files.newBufferedReader(p);
            String line = "";
            String tmp[];
            
            while((line=read.readLine())!=null){
                    tmp = line.split(",");
                    
                    map.put(Integer.parseInt(tmp[0]), new ArrayList<Integer>());
                    for(int i=1;i<tmp.length;i++){
                         map.get(Integer.parseInt(tmp[0])).add(Integer.parseInt(tmp[i]));
                    }
                    
               }
            read.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
}
