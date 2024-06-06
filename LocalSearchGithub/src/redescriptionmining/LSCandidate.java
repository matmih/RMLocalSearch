/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package redescriptionmining;

/**
 *
 * @author matmih
 */
public class LSCandidate {
    RedescriptionSet candidateSet = null;
    double score = 0;

    public void setValues(double s, RedescriptionSet st){
        candidateSet = new RedescriptionSet();
        candidateSet.redescriptions.addAll(st.redescriptions);
        score = s;
    }
    
    public double getScore(){
        return score;
    }
    
    public RedescriptionSet returnSet(){
        return candidateSet;
    }
    
}
