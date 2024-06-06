/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redescriptionmining;

/**
 *
 * @author matej
 */
public class Utilities {
    
    public void createViewDataInitial(ApplicationSettings appset, SettingsReader initSettings, DataSetCreator datJ, int view){
        if(appset.system.equals("windows"))
             initSettings.setPath(appset.outFolderPath+"\\view"+(view+1)+".s");
        else
             initSettings.setPath(appset.outFolderPath+"/view"+(view+1)+".s");
        
        int startIndex=0,endIndex=0;
        
        if(view==0 && appset.useNC.get(0) == false)
            startIndex = 3;
        else if(view==0 && appset.useNC.get(0) == true)
            startIndex = 4;
        else if(view>0 && appset.useNC.get(0) == false)
            startIndex = datJ.W2indexs.get(view-1)+1;
        else if(view>0 && appset.useNC.get(0) == true)
            startIndex = datJ.W2indexs.get(view-1)+2;
        
        if(view==(datJ.W2indexs.size()))
            endIndex = datJ.schema.getNbAttributes()+1;
        else
          endIndex = datJ.W2indexs.get(view);
        
        
             initSettings.createInitialSettingsGen(view, startIndex, endIndex, datJ.schema.getNbAttributes(), appset,1);
    }
    
    public void createRedescriptions(){
            
    }
    
}
