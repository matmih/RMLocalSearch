
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matej
 */
public class CheckRedescriptionsWithTargets {
    static public void main(String [] args){
        
        String support="318 317 309 308 305 304 303 302 297 296 295 294 289 285 282 280 278 276 272 270 265 255 253 250 246 245 244 238 230 229 228 226 223 216 213 208 206 200 199 198 197 196 189 188 187 179 178 175 172 167 164 163 162 161 155 147 143 139 138 137 135 131 129 127 126 119 118 116 115 108 107 106 105 99 96 91 90 88 84 81 80 73 66 65 63 62 59 57 54 52 43 37 26 25 18 8 7 4 ";
        int indeks = 1;
        HashSet<Integer> supp = new HashSet<>();
        HashMap<Integer,ArrayList<Integer>> m1 = new HashMap<>();
        HashMap<Integer,ArrayList<Integer>> m2 = new HashMap<>();
        HashMap<Integer,ArrayList<Integer>> m3 = new HashMap<>();
        
        String t[] = support.split(" ");
        
        for(int i=0;i<t.length;i++)
            supp.add(Integer.parseInt(t[i]));
        
        String in_str = "C:\\Users\\matej\\Downloads\\Slovenian rivers\\labs.txt";
        String in_strMod1=  "C:\\Users\\matej\\Downloads\\Slovenian rivers\\mod1.txt";
        String in_strMod2 = "C:\\Users\\matej\\Downloads\\Slovenian rivers\\mod2.txt";
        String in_strMod3 = "C:\\Users\\matej\\Downloads\\Slovenian rivers\\mod3.txt";
        
        PredictionReader pr = new PredictionReader();
        pr.readPredictions(new File(in_strMod1), m1);
        pr.readPredictions(new File(in_strMod2), m2);
        pr.readPredictions(new File(in_strMod3), m3);
        
        File input = new File(in_str);
        Path p = Paths.get(input.getAbsolutePath());
        HashMap<Integer,Integer> val = new HashMap<>();
        try{
            BufferedReader read = Files.newBufferedReader(p);
            String line = "";
            String tmp[];
            
            while((line=read.readLine())!=null){
                    tmp = line.split(",");
                    /*if(tmp[0].equals("155")){
                        System.out.println(tmp[indeks]);
                        System.out.println(line);
                    }*/
                    val.put(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[indeks]));
                    
               }
            read.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        
        ArrayList<Integer> v = new ArrayList<Integer>(Collections.nCopies(6, 0));
        ArrayList<Integer> vt = new ArrayList<Integer>(Collections.nCopies(3, 0));
        ArrayList<Integer> vtR = new ArrayList<Integer>(Collections.nCopies(3, 0));
        double count=0.0, count2 = 0.0, countpct=0.0, countet=0.0, countros = 0.0;
        for(int i:supp){
            int d = val.get(i);

            if(d == m1.get(i).get(indeks-1))
                countpct++;
            if(d == m2.get(i).get(indeks-1))
                countet++;
            if(d == m3.get(i).get(indeks-1))
                countros++;
            
            System.out.println(i+": "+m1.get(i).get(indeks-1)+" "+m2.get(i).get(indeks-1)+" "+m3.get(i).get(indeks-1)+" |"+d);
          if(d == m1.get(i).get(indeks-1) && d == m2.get(i).get(indeks-1) && d == m3.get(i).get(indeks-1)){
              if(d == 1)
                  vt.set(0,vt.get(0)+1);
              else if(d==3)
                  vt.set(1,vt.get(1)+1);
              else if(d==5)
                  vt.set(2,vt.get(2)+1);
              count++; 
          }
          if(m1.get(i).get(indeks-1) == m2.get(i).get(indeks-1) && m1.get(i).get(indeks-1) == m3.get(i).get(indeks-1)){
              count2++;
             if(d == 1)
                  vtR.set(0,vtR.get(0)+1);
              else if(d==3)
                  vtR.set(1,vtR.get(1)+1);
              else if(d==5)
                  vtR.set(2,vtR.get(2)+1);
          }
            /* if(d==0)
                System.out.println(i);*/
            v.set(d,v.get(d)+1);
        }
        
        System.out.println("Perf pct: "+(countpct/supp.size()));
        System.out.println("Perf et: "+(countet/supp.size()));
        System.out.println("Perf ros: "+(countros/supp.size()));
        System.out.println("Perf cons: "+(count/supp.size()));
        System.out.println(count/count2);
        
        for(int j=0;j<3;j++){
            System.out.print((vt.get(j)/(double)vtR.get(j))+" ");
        }
        System.out.println();
        
        for(int i=0;i<v.size();i++)
            System.out.print(i+": "+v.get(i)+" ");
        System.out.println();
        
    }
}
