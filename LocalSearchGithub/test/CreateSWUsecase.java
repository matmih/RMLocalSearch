
import java.io.BufferedReader;
import java.io.FileWriter;
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
public class CreateSWUsecase {
    static public void main(String [] args){
       // String inputPath="C:\\Users\\matej\\Downloads\\Slovenian rivers\\wc.test.predPCTSub.arff";
      //  String inputPath="C:\\Users\\matej\\Downloads\\Slovenian rivers\\wc.test.predET.arff";
         String inputPath="C:\\Users\\matej\\Downloads\\Slovenian rivers\\wc.test.predROS.arff";
        String outputPath="C:\\Users\\matej\\Downloads\\Slovenian rivers\\swusecaseinput4.arff";
        String realLabs="C:\\Users\\matej\\Downloads\\Slovenian rivers\\labs.txt";
        
       ArrayList<String> attributes = new ArrayList<>();
       HashMap<Integer,ArrayList<Integer>> data = new HashMap<>();        
       HashMap<Integer,ArrayList<Integer>> realLabsD = new HashMap<>(); 
       ArrayList<Integer> entities = new ArrayList<>();
        Path p = Paths.get(inputPath);
        
        int loadLabs=0;
        
       BufferedReader reader=null;
        try{
                reader = Files.newBufferedReader(p);
                
                String line, tmp[], tmp1[];
                int count =0;
                int dataS=0;
                while((line = reader.readLine())!=null){
                    tmp = line.split(" ");
                   System.out.println(line);
                    if(line.contains("@ATTRIBUTE") && !line.contains("Forest")){
                        attributes.add(tmp[1].trim());
                        count++;
                        continue;
                    }
                    else if(line.contains("@ATTRIBUTE"))
                        continue;
                    else if(line.contains("@DATA")){
                        dataS=1;
                        continue;
                    }
                    
                    if(dataS == 1){
                        tmp = line.split(",");
                        int kljuc = Integer.parseInt(tmp[0]);
                        System.out.println("kljuc: "+kljuc);
                        entities.add(kljuc);
                         if(!data.containsKey(kljuc)){
                                    data.put(kljuc, new ArrayList<Integer>());
                                }
                         
                          if(!realLabsD.containsKey(kljuc) && loadLabs==1){
                                    realLabsD.put(kljuc, new ArrayList<Integer>());
                                }
                         
                          for(int k=1;k<2*attributes.size()-1;k++){
                              System.out.print("tmp[k]: "+tmp[k]);
                              if(k<attributes.size() && loadLabs == 1)
                                realLabsD.get(kljuc).add(Integer.parseInt(tmp[k]));
                              else if(k>=attributes.size())
                                  data.get(kljuc).add(Integer.parseInt(tmp[k]));
                            }
                          System.out.println();
                    }
                }
               reader.close();
        }
        catch(IOException e){         
        }
        
       FileWriter fw=null;
        int modelB = 1;
        try{
                 fw = new FileWriter(outputPath);
                 
                 fw.write("@relation SWUsecase\n\n");
                 
                // fw.write("@attribute LID string\n");
                 
                 for(int i=0;i<attributes.size();i++){
                     if(i==0)
                         fw.write("@attribute "+attributes.get(i)+"M"+" string\n");
                         else
                     fw.write("@attribute "+attributes.get(i)+"M"+modelB+" numeric\n");
                 }
                 
                 fw.write("\n@data\n");
                 
                 String el="";
                 String outLine = "";
                 
               //  int numEl = dat.keySet().size();
                 
                 for(int i1=0;i1<entities.size();i1++){
                 
                     outLine = "";
                     el = ""+entities.get(i1);  //it.next();
                     System.out.println("ent: "+el);
                     outLine = el+",";
                     ArrayList<Integer> d = data.get(entities.get(i1)); System.out.println("ar size: "+d.size());
                     
                     for(int i=0;i<d.size();i++)
                         if(d.get(i)!=Double.POSITIVE_INFINITY)
                             if(i+1<d.size())
                                outLine+=d.get(i)+",";
                             else outLine+=d.get(i)+"\n";
                         else{
                              if(i+1<d.size())
                                outLine+="?"+",";
                             else outLine+="?"+"\n";
                         }
                     fw.write(outLine);
                     
                 }
                 fw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        try{
        if(loadLabs == 1){
             String el="";
                 String outLine = "";
                 fw = new FileWriter(realLabs);
             for(int i1=0;i1<entities.size();i1++){
                 
                     outLine = "";
                     el = ""+entities.get(i1);  //it.next();
                     System.out.println("ent: "+el);
                     outLine = el+",";
                     ArrayList<Integer> d = realLabsD.get(entities.get(i1)); System.out.println("ar size: "+d.size());
                     
                     for(int i=0;i<d.size();i++)
                         if(d.get(i)!=Double.POSITIVE_INFINITY)
                             if(i+1<d.size())
                                outLine+=d.get(i)+",";
                             else outLine+=d.get(i)+"\n";
                         else{
                              if(i+1<d.size())
                                outLine+="?"+",";
                             else outLine+="?"+"\n";
                         }
                     fw.write(outLine);
                     
                 }
                 fw.close();
        }
       }
        catch(IOException e){
            e.printStackTrace();
        }
  
    }
}
