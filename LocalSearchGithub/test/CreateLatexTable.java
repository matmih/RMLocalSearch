
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
public class CreateLatexTable {
    static public void main(String [] args){
        
        HashMap<Integer,ArrayList<Double>> pct = new HashMap<>();
        HashMap<Integer,ArrayList<Double>> pctSub = new HashMap<>();
        HashMap<Integer,ArrayList<Double>> pctET = new HashMap<>();
        HashMap<Integer,ArrayList<Double>> pctROS = new HashMap<>();
        
        HashMap<Integer,ArrayList<Double>> pctStd = new HashMap<>();
        HashMap<Integer,ArrayList<Double>> pctSubStd = new HashMap<>();
        HashMap<Integer,ArrayList<Double>> pctETStd = new HashMap<>();
        HashMap<Integer,ArrayList<Double>> pctROSStd = new HashMap<>();
        
        HashMap<Integer,ArrayList<Double>> maxes = new HashMap<>();
        HashMap<Integer,ArrayList<Double>> maxesStds = new HashMap<>();
        HashMap<Integer,ArrayList<Integer>> indexes = new HashMap<>();
        HashMap<Integer,ArrayList<Integer>> indexesStd = new HashMap<>();
        
       // String inputPath = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\World Countries50T\\";
       // String output = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\World Countries50T\\table.txt";
        
        String inputPath = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\World Countries\\";
       String output = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\World Countries\\table.txt";
        
        
        //String inputPath = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\SlovenianWaters50T\\";
        //String output = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\SlovenianWaters50T\\table.txt";
        
        // String inputPath = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\SlovenianWaters\\";
       // String output = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\SlovenianWaters\\table.txt";
        
        
       // String inputPath = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\Phenotypes50T\\";
        //String output = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\Phenotypes50T\\table.txt";
        
        //String inputPath = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\Phenotypes\\";
        //String output = "C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\Experiments ML multi-view\\200RedsNew_minJ = 0.6\\Phenotypes\\table.txt";
        
        
        String tmp;
        File in;
        
        BufferedReader read;
        Path p;
        
        DecimalFormat df = new DecimalFormat("#.##");
        //izracunati maksimum za svaki interval za svaki kriterij
        try{
        for(int i=0;i<4;i++){
            tmp = "";
            if(i==0)
                tmp = inputPath+"FinResMPCT.txt";
            else if(i==1)
                tmp = inputPath+"FinResMSub.txt";
            else if(i==2)
                tmp = inputPath+"FinResMET.txt";
            else if(i==3)
                tmp = inputPath+"FinResMROS.txt";
            
            in = new File(tmp);
            p = Paths.get(in.getAbsolutePath());
            read = Files.newBufferedReader(p);
            String line = "";
            int cnt = 0;
            while((line=read.readLine())!=null ){
                String tmpS[] = line.split(" ");
                
                if(!maxes.containsKey(cnt)){
                    if(cnt!=0 && cnt!=6)
                          maxes.put(cnt, new ArrayList<Double>(Collections.nCopies(7,Double.POSITIVE_INFINITY)));
                    else  maxes.put(cnt, new ArrayList<Double>(Collections.nCopies(7,0.0)));
                    indexes.put(cnt,new ArrayList<Integer>(Collections.nCopies(7, 0)));
                }
                
                for(int j=0;j<tmpS.length;j++){
                    double t = Double.parseDouble(tmpS[j].trim());
                    if(cnt == 0 || cnt == 6){
                         if(t>maxes.get(cnt).get(j)){
                             maxes.get(cnt).set(j, t);
                             indexes.get(cnt).set(j,i);
                     }     
                   }
                    else{
                        if(t<maxes.get(cnt).get(j)){
                            maxes.get(cnt).set(j, t);
                             indexes.get(cnt).set(j,i);
                        }
                    }
                }
                
                cnt++;
                
                for(int j=0;j<tmpS.length;j++){
                    if(i==0){
                        if(!pct.containsKey(j)){
                            pct.put(j, new ArrayList<Double>());
                        }
                        double p1 = Double.valueOf(df.format(Double.parseDouble(tmpS[j].trim())).replaceAll(",", "."));
                        pct.get(j).add(p1);
                    }
                    else if(i==1){
                         if(!pctSub.containsKey(j)){
                            pctSub.put(j, new ArrayList<Double>());
                        }
                        double p1 = Double.valueOf(df.format(Double.parseDouble(tmpS[j].trim())).replaceAll(",", "."));
                        pctSub.get(j).add(p1);
                    }
                    else if(i==2){
                        if(!pctET.containsKey(j)){
                            pctET.put(j, new ArrayList<Double>());
                        }
                        double p1 = Double.valueOf(df.format(Double.parseDouble(tmpS[j].trim())).replaceAll(",", "."));
                        pctET.get(j).add(p1);
                    }
                    else if(i==3){
                        if(!pctROS.containsKey(j)){
                            pctROS.put(j, new ArrayList<Double>());
                        }
                        double p1 = Double.valueOf(df.format(Double.parseDouble(tmpS[j].trim())).replaceAll(",", "."));
                        pctROS.get(j).add(p1);
                    }
                }
            }
            
            read.close();
            //std
             tmp = "";
            if(i==0)
                tmp = inputPath+"FinResStdPCT.txt";
            else if(i==1)
                tmp = inputPath+"FinResStdSub.txt";
            else if(i==2)
                tmp = inputPath+"FinResStdET.txt";
            else if(i==3)
                tmp = inputPath+"FinResStdROS.txt";
            
            in = new File(tmp);
            p = Paths.get(in.getAbsolutePath());
            read = Files.newBufferedReader(p);
             line = "";
            cnt =0;
            while((line=read.readLine())!=null ){
                String tmpS[] = line.split(" ");
                
                if(!maxesStds.containsKey(cnt)){
                    maxesStds.put(cnt, new ArrayList<Double>(Collections.nCopies(7,Double.POSITIVE_INFINITY)));
                    indexesStd.put(cnt,new ArrayList<Integer>(Collections.nCopies(7, 0)));
                }
                
                 for(int j=0;j<tmpS.length;j++){
                    double t = Double.parseDouble(tmpS[j].trim());
                         if(t<maxesStds.get(cnt).get(j)){
                            maxesStds.get(cnt).set(j, t);
                             indexesStd.get(cnt).set(j,i);
                    }
                }
                
                cnt++;
                
                for(int j=0;j<tmpS.length;j++){
                    if(i==0){
                        if(!pctStd.containsKey(j)){
                            pctStd.put(j, new ArrayList<Double>());
                        }
                        double p1 = Double.valueOf(df.format(Double.parseDouble(tmpS[j].trim())).replaceAll(",", "."));
                        pctStd.get(j).add(p1);
                    }
                    else if(i==1){
                         if(!pctSubStd.containsKey(j)){
                            pctSubStd.put(j, new ArrayList<Double>());
                        }
                        double p1 = Double.valueOf(df.format(Double.parseDouble(tmpS[j].trim())).replaceAll(",", "."));
                        pctSubStd.get(j).add(p1);
                    }
                    else if(i==2){
                        if(!pctETStd.containsKey(j)){
                            pctETStd.put(j, new ArrayList<Double>());
                        }
                        double p1 = Double.valueOf(df.format(Double.parseDouble(tmpS[j].trim())).replaceAll(",", "."));
                        pctETStd.get(j).add(p1);
                    }
                    else if(i==3){
                        if(!pctROSStd.containsKey(j)){
                            pctROSStd.put(j, new ArrayList<Double>());
                        }
                        double p1 = Double.valueOf(df.format(Double.parseDouble(tmpS[j].trim())).replaceAll(",", "."));
                        pctROSStd.get(j).add(p1);
                    }
                }
            }
            read.close();
            
        }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        
        try{
              FileWriter fw = new FileWriter(output);
              
              for(int i=0;i<7;i++){
                 
                for(int j=0;j<4;j++){
                     ArrayList<Double> t= null;
                     
                     if(j==0)
                        t= pct.get(i);
                     else if(j==1)
                         t = pctSub.get(i);
                     else if(j==2)
                         t = pctET.get(i);
                     else if(j==3)
                         t = pctROS.get(i);
                  fw.write("& ");
                  if(j==0)
                      fw.write("$PCT$ &");
                  else if(j==1)
                       fw.write("$PCT_{Sub}$ &");
                  else if(j==2)
                       fw.write("$PCT_{ET}$ &");
                  else if(j==3)
                       fw.write("$PCT_{ROS}$ &");
                  for(int z=0;z<t.size();z++){
                      if(z+1<t.size() && indexes.get(z).get(i) != j)   
                        fw.write("$"+t.get(z)+"$"+" & ");
                      else if(indexes.get(z).get(i) != j) fw.write("$"+t.get(z)+"$"+"\\\\\n");
                      else if((indexes.get(z).get(i) == j) && z+1<t.size()) fw.write("$\\mathbf{"+t.get(z)+"}$"+" &");
                      else if((indexes.get(z).get(i) == j) ) fw.write("$\\mathbf{"+t.get(z)+"}$"+"\\\\\n");
                  }
                  
                
                  if(j!=1)
                   fw.write("& ");
                  else fw.write((i+1)+"& ");
                  
                    fw.write("& ");
                  
                  if(j==0)
                        t= pctStd.get(i);
                     else if(j==1)
                         t = pctSubStd.get(i);
                     else if(j==2)
                         t = pctETStd.get(i);
                     else if(j==3)
                         t = pctROSStd.get(i);
                  
                  for(int z=0;z<t.size();z++){
                      if(z+1<t.size() && indexesStd.get(z).get(i) != j)
                        fw.write("$"+t.get(z)+"$"+" & ");
                      else if((z+1)<t.size() && indexesStd.get(z).get(i) == j)
                           fw.write("$\\mathbf{"+t.get(z)+"}$"+" &");
                       else if(j!=3 && indexesStd.get(z).get(i) != j) fw.write("$"+t.get(z)+"$"+"\\\\\n");
                      else if(j==3 && indexesStd.get(z).get(i) != j) fw.write("$"+t.get(z)+"$"+"\\\\\\hline\n");
                      else if(j!=3 && indexesStd.get(z).get(i) == j) fw.write("$\\mathbf{"+t.get(z)+"}$"+"\\\\\n");
                      else if(j==3 && indexesStd.get(z).get(i) == j) fw.write("$\\mathbf{"+t.get(z)+"}$"+"\\\\\\hline\n");
                  }
                  
              }
            }
             fw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
            
        
        
        
        
    } 
}
