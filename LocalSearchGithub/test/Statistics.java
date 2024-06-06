
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */
public class Statistics {
    HashMap<Integer,HashMap<Integer,ArrayList<ArrayList<Double>>>> results; //algo->interval->[[run1], [run2],...[run10]]
    //PCT0T ->0, SubxT ->1, ETxT ->2, ROSxT ->3
    //200_1700 ->0, 300_1800 ->1, 400_1900 ->2, 500_2000 ->3, 1000_4000 ->4, 2000_5000 ->5, 3000_6000 ->6
    
    HashMap<Integer,HashMap<Integer,ArrayList<ArrayList<Double>>>> final_stat; //algo -> interval-> [[averages],[deviations]]
    
    
    Statistics(){
        results = new HashMap<>();
        final_stat = new HashMap<>();
    }
    
    int algo_to_code(String algo){
        if(algo.contains("PCT"))
            return 0;
        else if(algo.contains("Sub"))
            return 1;
        else if(algo.contains("ET"))
            return 2;
        else if(algo.contains("ROS"))
            return 3;
        else return -1;
    }
    
    int algo_to_codeGen(String algo){
         if(algo.contains("PCT"))
            return 0;
        else if(algo.contains("ET1T"))
            return 1;
        else if(algo.contains("ET2T"))
            return 2;
        else if(algo.contains("ET4T"))
            return 3;
        else if(algo.contains("ET6T"))
            return 4;
        else return -1;
    }
    
    int algo_to_codeRSP(String algo){
         if(algo.contains("RVSubset"))
            return 1;
        else return 0;
    }
    
    
    int interval_to_code(int first, int second){
        if(first==200 && second == 1700)
            return 0;
        else if(first == 300 && second == 1800)
            return 1;
        else if(first ==400 && second == 1900)
            return 2;
        else if(first == 500 && second == 2000)
            return 3;
        else if(first == 1000 && second == 4000)
            return 4;
        else if(first ==2000 && second == 5000)
            return 5;
        else if(first == 3000 && second == 6000)
            return 6;
        else return -1;
    }
    
    void proces_data(File input){
        String fname = input.getName();
        String t[] = fname.split("_");
        int algo_code = algo_to_codeRSP(t[5]);
        t[5] = t[5].replaceAll("RVSubset", "");
        System.out.println("t[5] "+t[5]);
        int /*algo_to_code(t[3]),*/ interval_code = interval_to_code(Integer.parseInt(t[4].trim()),Integer.parseInt(t[5].replaceAll(".csv", "").trim()));
        int run = Integer.parseInt(t[1].replace("S", "").trim());
        
     
        System.out.println("algo_code: "+algo_code);
        System.out.println("interval_code: "+interval_code);
        System.out.println("run: "+run);
        
        if(!results.containsKey(algo_code))
            results.put(algo_code, new HashMap<Integer,ArrayList<ArrayList<Double>>>());
        
        HashMap<Integer,ArrayList<ArrayList<Double>>> tmp = results.get(algo_code);
        
        if(!tmp.containsKey(interval_code)){
            tmp.put(interval_code, new ArrayList<ArrayList<Double>>());
            ArrayList<ArrayList<Double>> tt = tmp.get(interval_code);
            for(int i=0;i<10;i++){
                tt.add(new ArrayList<Double>(Collections.nCopies(12, 0.0)));
            }
        }
        
         double minPval=17.0;
        
        try{
              Path p = Paths.get(input.getAbsolutePath());
                BufferedReader read = Files.newBufferedReader(p);
                
                 ArrayList<ArrayList<Double>> tt = tmp.get(interval_code);
                int count = 0;
                String line = "";
                while((line = read.readLine())!=null){
                    String tmpS[] = line.split("\t");
                    
                    if(tmpS.length<6)
                        break;
                    
                    count++;
                    for(int i=0;i<tmpS.length;i++){
                        if(i==0)
                            tt.get(run).set(i, tt.get(run).get(0)+Double.parseDouble(tmpS[i]));
                        else if(i==1)
                            continue;
                        else if(i==2){
                             double pv=Math.log10(Double.parseDouble(tmpS[i]))/minPval+1.0;
                             if(Double.parseDouble((tmpS[i]))==0)
                                 pv = 0.0;
                             tt.get(run).set(i-1, tt.get(run).get(i-1)+pv);
                        } 
                        else  tt.get(run).set(i-1, tt.get(run).get(i-1)+Double.parseDouble(tmpS[i]));
                    }  
                }
                
                read.close();
                
                    for(int i=6;i<11;i++)
                        tt.get(run).set(i, tt.get(run).get(i-6));
                    
                    if(count<200){
                        for(int i=count+1;i<200;i++){
                            for(int j=6;j<11;j++)
                                if(j==6)
                                     tt.get(run).set(j, tt.get(run).get(j)+0.0);
                                else
                                    tt.get(run).set(j, tt.get(run).get(j)+1.0);
                        }
                    }
               
                    if(count!=0){
                  for(int i=0;i<5;i++){
                         tt.get(run).set(i,tt.get(run).get(i)/count);
                     }
                    }
                  
                  for(int i=6;i<11;i++)
                      tt.get(run).set(i,tt.get(run).get(i)/200.0);
                
                  
                  double sc = 0.0;
                  
                  for(int i=0;i<5;i++){
                      if(i==0)
                          sc+=0.2*(1.0-tt.get(run).get(i));
                      else sc+=0.2*(tt.get(run).get(i));
                          
                  }
                  
                  tt.get(run).set(5, sc);
                  
                  double sc1 = 0.0;
                  
                  for(int i=6;i<11;i++){
                      if(i==6)
                          sc1+=0.2*(1.0-tt.get(run).get(i));
                      else sc1+=0.2*(tt.get(run).get(i));
                          
                  }
                  
                   tt.get(run).set(11, sc1);
                   
                   for(int i=0;i<tt.get(run).size();i++)
                       System.out.print(tt.get(run).get(i)+" ");
                   System.out.println();
                
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    void compute_statistics(){
        
        StandardDeviation std = new StandardDeviation();
        double v[] = new double[10];
        
        for(int alg_code = 0;alg_code<2/*5*//*4*/;alg_code++){
           
            /*if(alg_code>0)
                continue;*/
            
            HashMap<Integer,ArrayList<ArrayList<Double>>> tmp = results.get(alg_code);
            if(!final_stat.containsKey(alg_code)){
                    final_stat.put(alg_code, new HashMap<Integer,ArrayList<ArrayList<Double>>>());
                   }
            for(int int_code = 6/*0*/;int_code<7;int_code++){
                ArrayList<ArrayList<Double>> tt = tmp.get(int_code);
                
                if(!final_stat.get(alg_code).containsKey(int_code)){
                    HashMap<Integer,ArrayList<ArrayList<Double>>> tmpFR = final_stat.get(alg_code);
                    tmpFR.put(int_code, new ArrayList<ArrayList<Double>>());
                     ArrayList<ArrayList<Double>> st = final_stat.get(alg_code).get(int_code);
                    st.add(new ArrayList<Double>()); st.add(new ArrayList<Double>());
                }
                
                
                for(int i=0;i<tt.get(0).size();i++){
                    double sum=0.0;
                    for(int j=0;j<tt.size();j++){
                        v[j] = tt.get(j).get(i);
                        sum+=tt.get(j).get(i);
                    }
                    sum/=tt.size();
                   double stdV=std.evaluate(v);
                   final_stat.get(alg_code).get(int_code).get(0).add(sum);
                   final_stat.get(alg_code).get(int_code).get(1).add(stdV);
                }
                
               // HashMap<Integer,ArrayList<ArrayList<Double>>> final_stat; //algo -> [[averages],[deviations]] 
            }
          }
    }
    
    void write_results(String path){
        //svaki algoritam 2 -file-a
        //1. file mjere (svaki redak je jedna mjera kroz sve intervale)
        //2. file devijacija (svaki redak je jedna mjera kroz sve intervale)

        String outputName = path+"\\FinResM";
        String outputName1 = path+"\\FinResStd";
        FileWriter fw = null;
        FileWriter fw1 = null;
         
        try{
         for(int alg_code = 0;alg_code<4;alg_code++){
             outputName = path+"\\FinResM";
             outputName1 = path+"\\FinResStd";
             /*if(alg_code>0)
                 continue;*/
             if(alg_code==0){
                outputName+="PCT.txt";
                outputName1+="PCT.txt";
             }
             else if(alg_code == 1){
                 outputName+="Sub.txt";
                 outputName1+="Sub.txt";
             }
             else if(alg_code == 2){
                 outputName+="ET.txt";
                 outputName1+="ET.txt";
             }
             else if(alg_code == 3){
                 outputName+="ROS.txt";
                 outputName1+="ROS.txt";
             }
             
             fw = new FileWriter(outputName);
             fw1 = new FileWriter(outputName1);
             
        for(int i=0;i<12;i++){     
          for(int int_code = 0;int_code<7;int_code++){
              System.out.println(alg_code+" "+int_code);
                 ArrayList<ArrayList<Double>> tt = final_stat.get(alg_code).get(int_code);
                 if(int_code<6)
                    fw.write(tt.get(0).get(i)+" ");
                 else    fw.write(tt.get(0).get(i)+"\n");
                 
                 if(int_code<6)
                    fw1.write(tt.get(1).get(i)+" ");
                 else  fw1.write(tt.get(1).get(i)+"\n");
          }
         }
        fw.close(); fw1.close();
       }
    }
    catch(IOException e){
        e.printStackTrace();
    }
   }
    
    void write_resultsGen(String path){
        //svaki algoritam 2 -file-a
        //1. file mjere (svaki redak je jedna mjera kroz sve intervale)
        //2. file devijacija (svaki redak je jedna mjera kroz sve intervale)

        String outputName = path+"\\FinResGenM";
        String outputName1 = path+"\\FinResGenStd";
        FileWriter fw = null;
        FileWriter fw1 = null;
         
        try{
         for(int alg_code = 0;alg_code<5;alg_code++){
             outputName = path+"\\FinResGenM";
             outputName1 = path+"\\FinResGenStd";
             /*if(alg_code>0)
                 continue;*/
             if(alg_code==0){
                outputName+="PCT.txt";
                outputName1+="PCT.txt";
             }
             else if(alg_code == 1){
                 outputName+="ET1T.txt";
                 outputName1+="ET1T.txt";
             }
             else if(alg_code == 2){
                 outputName+="ET2T.txt";
                 outputName1+="ET2T.txt";
             }
             else if(alg_code == 3){
                 outputName+="ET4T.txt";
                 outputName1+="ET4T.txt";
             }
             else if(alg_code == 4){
                 outputName+="ET6T.txt";
                 outputName1+="ET6T.txt";
             }
             
             fw = new FileWriter(outputName);
             fw1 = new FileWriter(outputName1);
             
        for(int i=0;i<12;i++){     
          for(int int_code = 6;int_code<7;int_code++){
              System.out.println(alg_code+" "+int_code);
                 ArrayList<ArrayList<Double>> tt = final_stat.get(alg_code).get(int_code);
                        fw.write(tt.get(0).get(i)+"\n");
                        fw1.write(tt.get(1).get(i)+"\n");
          }
         }
        fw.close(); fw1.close();
       }
    }
    catch(IOException e){
        e.printStackTrace();
    }
   }
    
     void write_resultsRSP(String path){
        //svaki algoritam 2 -file-a
        //1. file mjere (svaki redak je jedna mjera kroz sve intervale)
        //2. file devijacija (svaki redak je jedna mjera kroz sve intervale)

        String outputName = path+"\\FinResRSPM";
        String outputName1 = path+"\\FinResRSPStd";
        FileWriter fw = null;
        FileWriter fw1 = null;
         
        try{
         for(int alg_code = 0;alg_code<2;alg_code++){
             outputName = path+"\\FinResRSPM";
             outputName1 = path+"\\FinResRSPStd";
             /*if(alg_code>0)
                 continue;*/
             if(alg_code==0){
                outputName+="PCT.txt";
                outputName1+="PCT.txt";
             }
             else if(alg_code == 1){
                 outputName+="PCTRSP.txt";
                 outputName1+="PCTRSP.txt";
             }
             
             fw = new FileWriter(outputName);
             fw1 = new FileWriter(outputName1);
             
        for(int i=0;i<12;i++){     
          for(int int_code = 6;int_code<7;int_code++){
              System.out.println(alg_code+" "+int_code);
                 ArrayList<ArrayList<Double>> tt = final_stat.get(alg_code).get(int_code);
                        fw.write(tt.get(0).get(i)+"\n");
                        fw1.write(tt.get(1).get(i)+"\n");
          }
         }
        fw.close(); fw1.close();
       }
    }
    catch(IOException e){
        e.printStackTrace();
    }
   }
}
