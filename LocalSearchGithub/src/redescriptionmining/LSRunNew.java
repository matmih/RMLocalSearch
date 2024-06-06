/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package redescriptionmining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 *
 * @author matmih
 */
public class LSRunNew implements Callable {
    HashSet<Integer> indices;
    RedescriptionSet set, setAll;
    ApplicationSettings as;
    Mappings map;
    DataSetCreator dat;
    double [] weights;
    int jsType;
    
    public LSRunNew(HashSet<Integer> choices, RedescriptionSet iS, RedescriptionSet iSAll, ApplicationSettings ap, Mappings m, DataSetCreator d, ArrayList<Double> w, int jT){
        indices = choices;
        set = iS;
        setAll = iSAll;
        as = ap;
        map = m;
        dat = d;
        weights = new double[w.size()];
        for(int i=0;i<w.size();i++)
            weights[i] = w.get(i);
        jsType = jT;
    }
    
    @Override
   public LSCandidate call(){
            RedescriptionSet tmp = new RedescriptionSet();
         ArrayList<double[]> t = new ArrayList<double []>();
         t.add(weights);
       return tmp.createScoreRedescriptionSetSamplingNew1(setAll, set,t , jsType, as, dat, map, indices);
    }
    
}
