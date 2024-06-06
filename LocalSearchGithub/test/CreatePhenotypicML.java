
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */
public class CreatePhenotypicML {
    public static void main(String [] args){
        //String input="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\ProteomComposition.txt";
        // String input="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\MetagenomicCo-occurenceNew.txt";
         String input="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\TranslationEfficiency.txt";
         int createFrequencies = 0;
          FileWriter fw = null;
         
         if(createFrequencies == 1){
        Path p = Paths.get(input);
        BufferedReader reader = null;
       
       
        HashMap<String,Integer> termCount = new HashMap<>();
        
        try{
                reader = Files.newBufferedReader(p);
                
                String line, tmp[];
                
                while((line = reader.readLine())!=null){
                    tmp = line.split("\t");
                    //System.out.println(line);
                    for(int k=0;k<tmp.length;k++){
                        if(termCount.containsKey(tmp[k].trim())){
                            termCount.put(tmp[k].trim(), termCount.get(tmp[k].trim())+1);
                        }
                        else termCount.put(tmp[k].trim(), 1);
                    }
                }
               reader.close();
        }
        catch(IOException e){         
        }
        
        try{
             fw = new FileWriter("C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\outputTrEff.txt");
             
             Iterator<String> it = termCount.keySet().iterator();
             
             while(it.hasNext()){
                 String el = it.next();
                 int c = termCount.get(el);
                // System.out.println("c: "+c);
                 if(c>100)
                 fw.write(el+"\t"+c+"\n");
             }
             
        fw.close();
        }
        catch(IOException e){
            
        }
      }
         
         
        //deal with entities
         int CreateentityOverlap = 1;
        
         if(CreateentityOverlap == 1){
        String inputEntities1="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\EntitiesN.txt";
        String inputEntities2="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\EntitiesN1.txt";
        String inputEntities3="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\EntitiesN2.txt";
        String outputEntities = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\EntitiesNFinal.txt";
        
        HashSet<String> en1 = new HashSet<>();
        HashSet<String> en2 = new HashSet<>();
        HashSet<String> en3 = new HashSet<>();
        HashSet<String> inter = new HashSet<>();
        
        BufferedReader read1 = null;
         Path p = Paths.get(inputEntities1);
        
        try{
                read1 = Files.newBufferedReader(p);
                
                String line;
                
                while((line = read1.readLine())!=null){
                  en1.add(line.trim());
                }
           read1.close();     
        }
        catch(IOException e){         
        }
        
             p = Paths.get(inputEntities2);
        
        try{
                read1 = Files.newBufferedReader(p);
                
                String line;
                
                while((line = read1.readLine())!=null){
                  en2.add(line.trim());
                }
              read1.close();
        }
        catch(IOException e){         
        }
        
        
             p = Paths.get(inputEntities3);
        
        try{
                read1 = Files.newBufferedReader(p);
                
                String line;
                
                while((line = read1.readLine())!=null){
                  en3.add(line.trim());
                }
              read1.close();
        }
        catch(IOException e){         
        }
        
        
        for(String s:en1){
            if(en2.contains(s) && en3.contains(s))
                inter.add(s);
        }
        
         try{
             fw = new FileWriter(outputEntities);
             
             
             for(String s:inter)
                 fw.write(s+"\n");     
        fw.close();
        }
        catch(IOException e){
            
        }
       }
         
          String inputEntities="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\EntitiesNFinal.txt";
          String dataInput1="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\MetagenomicCo-occurenceAllNew.txt";
          String dataInput2="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\TranslationEfficiencyAll.txt";
          String dataInput3="C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\ProteomCompositionAll.txt";
         String outputView1 = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\metagenomic.arff";
         String outputView2 = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\translation.arff";
         String outputView3 = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\proteom.arff";
         
         BufferedReader read = null;
         
         HashSet<String> entities = new HashSet<>();
         
            Path p = Paths.get(inputEntities);
        
        try{
                read = Files.newBufferedReader(p);
                
                String line;
                
                while((line = read.readLine())!=null){
                  entities.add(line.trim());
                }
                read.close();
        }
        catch(IOException e){         
        }
        
        
        String attributes = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\outputMetCo-occ.txt";
        HashSet<String> attributesS = new HashSet<>();
        HashMap<String, Integer> attributesIndex = new HashMap<>();
        HashMap<Integer, String> indexAttributes = new HashMap<>();
        
        try{
                p = Paths.get(attributes);
                BufferedReader r2 = Files.newBufferedReader(p);
                int atInd = 0;
               String line, tmp[];
                int count = 0;
                
                while((line = r2.readLine())!=null){
                        tmp = line.split("\t");
                        attributesS.add(tmp[0].trim());
                        attributesIndex.put(tmp[0], count++);
                        indexAttributes.put(count-1,tmp[0]);
                }
                r2.close();
           }
        catch(IOException e){
            e.printStackTrace();
        }
        
        //create dat1
            p = Paths.get(dataInput1);
            
            ArrayList<String> header = new ArrayList<>();
             HashMap<String,ArrayList<Double>> dat = new HashMap<>();  
             HashMap<String, Integer> entityID = new HashMap<>();
             HashMap<Integer, String> IDEntity = new HashMap<>();
                
        try{
                read = Files.newBufferedReader(p);
                
                String line, tmp[];
                int count = 0;
                
                while((line = read.readLine())!=null){
                 tmp = line.split("\t");
                 if(count ==0){
                     for(int i=0;i<tmp.length;i++){
                     header.add(tmp[i].trim());
                 }
                 
                  int entCount = 0;
                  for(int i=1;i<tmp.length;i+=2){
                        if(entities.contains(header.get(i))){
                            entityID.put(tmp[i].trim(), entCount++);
                            IDEntity.put(entCount-1, tmp[i].trim());
                        }
                  }
                     
                     count = 1;
                     continue;
                 }
                 

                 
                
                 for(int i=1;i<tmp.length;i+=2){
                     if(entities.contains(header.get(i))){
                         if(!dat.containsKey(""+entityID.get(header.get(i)))){
                             dat.put(""+entityID.get(header.get(i)), new ArrayList<Double>(Collections.nCopies(attributesS.size(), Double.POSITIVE_INFINITY)));
                            // entCount++;
                         }
                         
                         if(!attributesS.contains(tmp[i])){
                             continue;
                         }
                         
                         if(entityID.get(header.get(i)) == 0 && attributesIndex.get(tmp[i]) == 0){
                             System.out.println("in: "+i+" "+header.get(i)+" "+entityID.get(header.get(i))+" "+attributesIndex.get(tmp[i])+" "+tmp[i+1]);
                             System.out.println(tmp[0]+" "+tmp[1]+" "+tmp[2]+" "+tmp[3]);
                         }
                         
                         if(!tmp[i+1].equals("?"))
                             dat.get(""+entityID.get(header.get(i))).set(attributesIndex.get(tmp[i]),Double.parseDouble(tmp[i+1].replace(",", ".")));
                     }
                 }
                 
                }
                read.close();
        }
        catch(IOException e){         
        }
        
        
        Iterator<String> it = dat.keySet().iterator();
        
        try{
                 fw = new FileWriter(outputView1);
                 FileWriter fw1 = new FileWriter("C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\entityMapping.txt");
                 
                 for(int j=0;j<entityID.keySet().size();j++)
                     if(IDEntity.containsKey(j)){
                         String el = IDEntity.get(j);
                          fw1.write(el+"\t"+entityID.get(el)+"\n");
                     }
                             
                 
                 fw1.close();
                 
                 fw.write("@relation MetagenomicCooc\n\n");
                 
                 fw.write("@attribute PID string\n");
                 
                 for(int i=0;i<attributesIndex.keySet().size();i++){
                     fw.write("@attribute "+indexAttributes.get(i).replace("_", "")+" numeric\n");
                 }
                 
                 fw.write("\n@data\n");
                 
                 String el="";
                 String outLine = "";
                 
                 int numEl = dat.keySet().size();
                 
                 for(int i1=0;i1<numEl;i1++){
                 
                     outLine = "";
                     el = ""+i1;  //it.next();
                     System.out.println("ent: "+el);
                     outLine = el+",";
                     ArrayList<Double> d = dat.get(el); System.out.println("ar size: "+d.size());
                     
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
        
         
        //create dat2
        
        
        attributes = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\outputTrans-efff.txt";
         attributesS = new HashSet<>();
        attributesIndex = new HashMap<>();
         indexAttributes = new HashMap<>();
        
        try{
                p = Paths.get(attributes);
                BufferedReader r2 = Files.newBufferedReader(p);
                int atInd = 0;
               String line, tmp[];
                int count = 0;
                
                while((line = r2.readLine())!=null){
                        tmp = line.split("\t");
                        attributesS.add(tmp[0].trim());
                        attributesIndex.put(tmp[0], count++);
                        indexAttributes.put(count-1,tmp[0]);
                }
                r2.close();
           }
        catch(IOException e){
            e.printStackTrace();
        }
        
          p = Paths.get(dataInput2);
            
             header = new ArrayList<>();
              dat = new HashMap<>();  
                
        try{
                read = Files.newBufferedReader(p);
                
                String line, tmp[];
                int count = 0;
                
                while((line = read.readLine())!=null){
                   // System.out.println(line);
                 tmp = line.split("\t");
                 if(count ==0){
                     for(int i=0;i<tmp.length;i++){
                     header.add(tmp[i].trim());
                 }
                     
                     count = 1;
                     continue;
                 }
                 
                 for(int i=1;i<tmp.length;i+=2){
                     if(entities.contains(header.get(i))){
                         if(!dat.containsKey(""+entityID.get(header.get(i)))){
                             dat.put(""+entityID.get(header.get(i)), new ArrayList<Double>(Collections.nCopies(attributesS.size(), Double.POSITIVE_INFINITY)));
                         }
                         
                         if(!attributesS.contains(tmp[i])){
                             continue;
                         }
                         
                         if(entityID.get(header.get(i)) == 0 && attributesIndex.get(tmp[i]) == 0){
                             System.out.println("tmp[i]: "+tmp[i]);
                             System.out.println("in: "+i+" "+header.get(i)+" "+entityID.get(header.get(i))+" "+attributesIndex.get(tmp[i])+" "+tmp[i+1]);
                             System.out.println(tmp[0]+" "+tmp[1]+" "+tmp[2]+" "+tmp[3]);
                         }
                         
                         if(!tmp[i+1].equals("?"))
                             dat.get(""+entityID.get(header.get(i))).set(attributesIndex.get(tmp[i]),Double.parseDouble(tmp[i+1].replace(",", ".")));
                     }
                 }
                 
                }
                read.close();
        }
        catch(IOException e){         
        }
        
        
         it = dat.keySet().iterator();
        
        try{
                 fw = new FileWriter(outputView2);
                 
                 fw.write("@relation TranslationEfficiency\n\n");
                 
                 fw.write("@attribute CID string\n");
                 
                 for(int i=0;i<attributesIndex.keySet().size();i++){
                     fw.write("@attribute "+indexAttributes.get(i).replace("_", "")+" numeric\n");
                 }
                 
                 fw.write("\n@data\n");
                 
                 String el="";
                 String outLine = "";
                 
                 int numEl = dat.keySet().size();
                 
                 for(int i1=0;i1<numEl;i1++){
                 
                     outLine = "";
                     el = ""+i1;  //it.next();
                     System.out.println("ent: "+el);
                     outLine = el+",";
                     ArrayList<Double> d = dat.get(el); System.out.println("ar size: "+d.size());
                     
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
        
        //create dat3
        
        attributes = "C:\\Users\\matej\\Documents\\PhenotypeMLMultiViewDataset\\outputProt-comp.txt";
         attributesS = new HashSet<>();
         attributesIndex = new HashMap<>();
         indexAttributes = new HashMap<>();
        
        try{
                p = Paths.get(attributes);
                BufferedReader r2 = Files.newBufferedReader(p);
                int atInd = 0;
               String line, tmp[];
                int count = 0;
                
                while((line = r2.readLine())!=null){
                        tmp = line.split("\t");
                        attributesS.add(tmp[0].trim());
                        attributesIndex.put(tmp[0], count++);
                        indexAttributes.put(count-1,tmp[0]);
                }
                r2.close();
           }
        catch(IOException e){
            e.printStackTrace();
        }
        
         p = Paths.get(dataInput3);
            
             header = new ArrayList<>();
              dat = new HashMap<>();  
                
        try{
                read = Files.newBufferedReader(p);
                
                String line, tmp[];
                int count = 0;
                
                while((line = read.readLine())!=null){
                 tmp = line.split("\t");
                 if(count ==0){
                     for(int i=0;i<tmp.length;i++){
                     header.add(tmp[i].trim());
                 }
                     
                     count = 1;
                     continue;
                 }
                 
                 for(int i=1;i<tmp.length;i+=2){
                     if(entities.contains(header.get(i))){
                         if(!dat.containsKey(""+entityID.get(header.get(i)))){
                             dat.put(""+entityID.get(header.get(i)), new ArrayList<Double>(Collections.nCopies(attributesS.size(), Double.POSITIVE_INFINITY)));
                         }
                         
                         if(!attributesS.contains(tmp[i])){
                             continue;
                         }
                         
                         if(entityID.get(header.get(i)) == 0 && attributesIndex.get(tmp[i]) == 0){
                             System.out.println("in: "+i+" "+header.get(i)+" "+entityID.get(header.get(i))+" "+attributesIndex.get(tmp[i])+" "+tmp[i+1]);
                             System.out.println(tmp[0]+" "+tmp[1]+" "+tmp[2]+" "+tmp[3]);
                         }
                         
                         if(!tmp[i+1].equals("?"))
                             dat.get(""+entityID.get(header.get(i))).set(attributesIndex.get(tmp[i]),Double.parseDouble(tmp[i+1].replace(",", ".")));
                     }
                 }
                 
                }
                read.close();
        }
        catch(IOException e){         
        }
        
        
         it = dat.keySet().iterator();
        
        try{
                 fw = new FileWriter(outputView3);
                 
                 fw.write("@relation ProteomComposition\n\n");
                 
                 fw.write("@attribute PrID string\n");
                 
                 for(int i=0;i<attributesIndex.keySet().size();i++){
                     fw.write("@attribute "+indexAttributes.get(i).replace("_", "")+" numeric\n");
                 }
                 
                 fw.write("\n@data\n");
                 
                 String el="";
                 String outLine = "";
                 
                 int numEl = dat.keySet().size();
                 
                 for(int i1=0;i1<numEl;i1++){
                 
                     outLine = "";
                     el = ""+i1;  //it.next();
                     System.out.println("ent: "+el);
                     outLine = el+",";
                     ArrayList<Double> d = dat.get(el); System.out.println("ar size: "+d.size());
                     
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
        
    }
}
