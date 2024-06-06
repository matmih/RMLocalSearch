
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import org.javatuples.Pair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */
public class PrepareDataset {
     public static void main(String [] args){
         String featOrder = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\FeatureOrder.txt";
         String attributes = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\matej-feature_imp\\attributes.txt";
         String factorMap = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\matej-feature_imp\\NMF_clusters_mapping.txt";
         String output="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\MetagenomicCo-occurenceAllNew.txt";
         String path = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\matej-feature_imp\\All\\";
         
         HashMap<Integer,String> entityID = new HashMap<>();
         HashMap<String,String> attributeID = new HashMap<>();
          HashMap<String,String> iDAttribute = new HashMap<>();
         HashMap<String,ArrayList<Pair<String,Double>>> data = new HashMap<>();
        
         File attr = new File(attributes);
          Path p = Paths.get(attr.getAbsolutePath());
          BufferedReader buf=null;
          
          try{
                   buf = Files.newBufferedReader(p, StandardCharsets.UTF_8);
                   String line = "", tmp[];
                   String id = "";
                   int count = 0;
                   
                   while((line = buf.readLine())!=null){
                       id = "";
                       tmp = line.split(" ");
                       id = "taxID_"+tmp[1];
                       entityID.put(count++, id);
                   }
                   buf.close();
          }
          catch(IOException e){
              e.printStackTrace();
          }
          
           File featOrderF = new File(featOrder);
           p = Paths.get(featOrderF.getAbsolutePath());
           
           ArrayList<String> featureOrder = new ArrayList<>();
         
     try{
                   buf = Files.newBufferedReader(p, StandardCharsets.UTF_8);
                   String line = "";
                   
                   while((line = buf.readLine())!=null){
                       featureOrder.add(line.trim());
                   }
                   buf.close();
          }
          catch(IOException e){
              e.printStackTrace();
          }
         
          File fmF = new File(factorMap);
           p = Paths.get(fmF.getAbsolutePath());
          
          try{
                   buf = Files.newBufferedReader(p, StandardCharsets.UTF_8);
                   String line = "", tmp[];
                   String id = "";
                   int count = 0;
                   
                   while((line = buf.readLine())!=null){
                       id = "";
                       tmp = line.split("\t");
                       attributeID.put(tmp[0].trim(), tmp[1].trim());
                       iDAttribute.put(tmp[1].trim(), tmp[0].trim());
                   }
                   buf.close();
          }
          catch(IOException e){
              e.printStackTrace();
          }
          
          try{
              File inputT;
              String in, line = "";
              String t[], entID;
              int id, fl;
              double val;
              
              for(int i=1;i<featureOrder.size();i+=2){
                  in = featureOrder.get(i);
                  in = in.replace("_feature_name", "");
                  
                 if(iDAttribute.containsKey(in)){
                          in=iDAttribute.get(in);
                  }
                  
                  in+="_importances.txt";
                  System.out.println(in);
                  inputT = new File(path+in);
                  p = Paths.get(inputT.getAbsolutePath());
                  buf = Files.newBufferedReader(p, StandardCharsets.UTF_8);
                  
                    line = "";
                    
                    if(!data.containsKey(featureOrder.get(i)))
                        data.put(featureOrder.get(i), new ArrayList<Pair<String,Double>>());
                    fl =0;
                    while((line = buf.readLine())!=null){
                        if(fl==0){
                            fl=1;
                            continue;
                        }
                        t = line.split(" ");
                        id = Integer.parseInt(t[2]);
                        val = Double.parseDouble(t[3].replaceAll("\\(", "").replaceAll("\\)", ""));
                        entID = entityID.get(id);
                        data.get(featureOrder.get(i)).add(new Pair(entID,val));
                    }
                  
              }
              
              buf.close();
              
              FileWriter fw = new FileWriter(output);
              ArrayList<Pair<String,Double>> els;
              
               for(int i=0;i<featureOrder.size();i++)
                   if(i+1<featureOrder.size())
                     fw.write(featureOrder.get(i)+"\t");
                   else fw.write(featureOrder.get(i));
               
               fw.write("\n"); int count = 1;
            for(int g=0;g<1000;g++){
                fw.write(count+"\t");
                  count++;
              for(int i=1;i<featureOrder.size();i+=2){
                  els = data.get(featureOrder.get(i));
                  
                  if(els.size()<=g){
                      if(i+2<featureOrder.size())
                          fw.write(""+"\t"+""+"\t");
                      else  fw.write(""+"\t"+""+"\n");
                      continue;
                  }
                 
                  if(i+2<featureOrder.size())
                    fw.write(els.get(g).getValue0()+"\t"+els.get(g).getValue1()+"\t");
                  else  fw.write(els.get(g).getValue0()+"\t"+els.get(g).getValue1()+"\n");
              } 
            }
              fw.close();
          }
          catch(IOException e){
              e.printStackTrace();
          }
          
         
     }
}
