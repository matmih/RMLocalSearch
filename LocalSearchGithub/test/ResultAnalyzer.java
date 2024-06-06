
import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */
public class ResultAnalyzer {
    public static void main(String [] args){

        Statistics stat = new Statistics();
       // String directory = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\SinglePCTPerformanceTest\\Phenotype";
        //String directory1 = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\SinglePCTPerformanceTest\\";
        
        //String directory = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\Phenotypes\\All";
        //String directory1 = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\Phenotypes\\";
        
         //String directory = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\GeneratingTreesExtraTrees\\Phenotypes\\All";
         //String directory1 = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\GeneratingTreesExtraTrees\\Phenotypes";
        
         String directory = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\ViewRandomSubsetProjection\\AllPh";
         String directory1 = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\ViewRandomSubsetProjection";
        
         
         File dir = new File(directory);
  File[] directoryListing = dir.listFiles();
  if (directoryListing != null) {
    for (File child : directoryListing) {
        System.out.println(child.getName());
        if(child.getName().contains(".rr"))
            continue;
        stat.proces_data(child);
      // Do something with child
    }
  } 
  stat.compute_statistics();
  //stat.write_results(directory1);
  //stat.write_resultsGen(directory1);
  stat.write_resultsRSP(directory1);
    }
}
