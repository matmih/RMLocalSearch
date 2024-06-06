/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package redescriptionmining;

import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 *
 * @author matmih
 */
public class MCRun implements Callable {
    ArrayList<Integer> indices;
    int numRetRed = 0;
    RedescriptionSet set;
    ApplicationSettings as;
    Mappings map;
    DataSetCreator dat;
    
    
    public MCRun(int nr,  ArrayList<Integer> choices, RedescriptionSet iS, ApplicationSettings ap, Mappings m, DataSetCreator d){
        indices = choices;
        numRetRed = nr;
        set = iS;
        as = ap;
        map = m;
        dat = d;
    }
    
    @Override
   public Double call(){
            RedescriptionSet tmp = new RedescriptionSet();
       
       
       for(int i=0;i<numRetRed;i++){
           int index = indices.get(i);
           
           tmp.redescriptions.add(set.redescriptions.get(index));
           
       }
       
       return tmp.computeRedescriptionSetScoreGen(as.preferences.get(0),as.parameters.get(0).get(2).intValue(),new double[2],dat,as,map);
    }
}
