/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redescriptionmining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import static redescriptionmining.DataSetCreator.writeArff;

/**
 *
 * @author matej
 */
public class GeneratingRandomForest {

    public static void main(String[] args) {

        //1. compute pareto optimal front of rules
        //2. visualizations
        //3. add several initial clustering methods (using target-descriptive and both), future work
        //4. add settings element for network approach, future work...
        //5. if return redescription = all return all redescriptions and do not compute rule score (perhaps do not compute vizaulization set also?)
        //6. multi-view approach

                long startTime = System.currentTimeMillis();

        ApplicationSettings appset=new ApplicationSettings();
        appset.readSettings(new File(args[0]));
             appset.readPreference(); 
        System.out.println("Num targets: "+appset.numTargets);
        System.out.println("Num trees in RS: "+appset.numTreesinForest);
        System.out.println("Average tree depth in RS: "+appset.aTreeDepth);
        System.out.println("Allow left side rule negation: "+appset.leftNegation);
        System.out.println("Allow right side rule negation: "+appset.rightNegation);
        System.out.println("Allow left side rule disjunction: "+appset.leftDisjunction);
        System.out.println("Allow right side rule disjunction: "+appset.rightDisjunction);
        System.out.println("Types of LSTrees: "+appset.treeTypes.get(0));
        System.out.println("Types of RSTrees: "+appset.treeTypes.get(1));
        System.out.println("Use Network information: "+appset.useNC.toString());
        System.out.println("Spatial matrix: "+appset.spatialMatrix.toString());
        System.out.println("Spatial measure: "+appset.spatialMeasures.toString());
        
        double countNumProduced=0.0, tmpPr=0.0,tmpPr1=0.0;
        
        System.out.println("Attribute importance gen: ");
        for(int i=0;i<appset.attributeImportanceGen.size();i++)
              System.out.print(appset.attributeImportanceGen.get(i)+" ");
        System.out.println();
        
        System.out.println("Important attributes: ");
        for(int i=0;i<appset.importantAttributes.size();i++){
            for(int j=0;j<appset.importantAttributes.get(i).size();j++){
                for(int k=0;k<appset.importantAttributes.get(i).get(j).size();k++){
                    if(k<appset.importantAttributes.get(i).get(j).size())
                        System.out.print(appset.importantAttributes.get(i).get(j).get(k)+" , ");
                }
                System.out.print(" + ");
            }
        System.out.println();
        }

        Mappings fid=new Mappings();
        Mappings fidFull=new Mappings();
         Mappings fidTest=new Mappings();
        
        DataSetCreator datJ=new DataSetCreator(appset.viewInputPaths, appset.outFolderPath,appset);

        DataSetCreator datJFull=null;
        DataSetCreator datJTest=null;

           if(appset.system.equals("windows"))
            fid.createIndex(appset.outFolderPath+"\\Jinput.arff");
        else
            fid.createIndex(appset.outFolderPath+"/Jinput.arff");
           
           if(appset.useSplitTesting==true){
               if(appset.system.equals("windows")){
                        fidFull.createIndex(appset.outFolderPath+"\\Jinput.arff");
                        fidTest.createIndex(appset.outFolderPath+"\\Jinput.arff");
               }
               else{
                   fidFull.createIndex(appset.outFolderPath+"/Jinput.arff");
                   fidTest.createIndex(appset.outFolderPath+"/Jinput.arff");
               }
           }
           
           if(appset.useSplitTesting==true){
               ArrayList<DataSetCreator> rDat = new ArrayList<>();
               datJFull=datJ;
               datJTest = datJ;
               if(appset.trainFileName.equals("") || appset.testFileName.equals("")){
               rDat=datJ.createSplit(appset.percentageForTrain);
               datJ = rDat.get(0);
               datJTest = rDat.get(1);
                            
                try{
             if(appset.system.equals("windows")){
                 writeArff(appset.outFolderPath+"\\JinputTrain.arff", datJ.data);
                 writeArff(appset.outFolderPath+"\\JinputTest.arff", datJTest.data);
             }
             else{
                writeArff(appset.outFolderPath+"/JinputTrain.arff", datJ.data);
                 writeArff(appset.outFolderPath+"/JinputTest.arff", datJTest.data); 
             }
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }
               }
               else{
                   if(appset.system.equals("windows")){
                        datJ = new DataSetCreator(appset.outFolderPath+"\\"+appset.trainFileName);
                        datJTest = new DataSetCreator(appset.outFolderPath+"\\"+appset.testFileName);
                   }
                   else{
                        datJ = new DataSetCreator(appset.outFolderPath+"/"+appset.trainFileName);
                        datJTest = new DataSetCreator(appset.outFolderPath+"/"+appset.testFileName);
                   }
                   
                    try{
                             datJ.readDataset();
                             datJTest.readDataset();
                         }
                     catch(IOException e){
                              e.printStackTrace();
                          }
        
                         datJ.W2indexs.addAll(datJFull.W2indexs);
                         datJTest.W2indexs.addAll(datJFull.W2indexs);
                   
            }
               
         fid.clearMaps();
         if(appset.system.equals("windows"))
             fid.createIndex(appset.outFolderPath+"\\JinputTrain.arff");
         else fid.createIndex(appset.outFolderPath+"/JinputTrain.arff");
          System.out.println("Full: "+datJFull.numExamples);
        System.out.println("Part: "+datJ.numExamples);
        }
        
        Random r=new Random();
        RedescriptionSet rs=new RedescriptionSet();
        ArrayList<RuleReader> ruleReaders = new ArrayList<>();
        
        for(int i=0;i<appset.viewInputPaths.size();i++)//ruleReaders for multi-view execution
            ruleReaders.add(new RuleReader());

         boolean oom[]= new boolean[1];
         
        int elemFreq[]=null;
        int attrFreq[]=null;
        ArrayList<Double> redScores=null;
        ArrayList<Double> redScoresAtt=null;
        ArrayList<Double> targetAtScore=null;
        ArrayList<Double> redDistCoverage=null;
        ArrayList<Double> redDistCoverageAt=null;
        ArrayList<Double> redDistNetwork=null;
         double Statistics[]={0.0,0.0,0.0};//previousMedian - 0, numberIterationsStable - 1, minDifference - 2
         ArrayList<Double> maxDiffScoreDistribution = null;
      
       if(appset.optimizationType == 0){//not implemented in multi-view mode
        if(appset.redesSetSizeType==1 && appset.numRetRed!=Integer.MAX_VALUE)
            appset.numInitial=appset.numRetRed;
        else{
            if(appset.numRetRed!=Integer.MAX_VALUE && appset.numRetRed!=-1)
                appset.numInitial=appset.numRetRed;
            else
                appset.numInitial=20;
        }
       }
        
        
        if(appset.optimizationType==0){
                          
         elemFreq=new int[datJ.numExamples];
         attrFreq=new int[datJ.schema.getNbAttributes()];  
            
          System.out.println("Number of redescriptions: "+appset.numInitial);
        
        redScores=new ArrayList<>(appset.numInitial);
        redScoresAtt=new ArrayList<>(appset.numInitial);
        redDistCoverage=new ArrayList<>(appset.numInitial);
        redDistCoverageAt=new ArrayList<>(appset.numInitial);
        if(appset.useNetworkAsBackground==true)
              redDistNetwork=new ArrayList<>(appset.numInitial);
         targetAtScore=null;

        maxDiffScoreDistribution=new ArrayList<>(appset.numInitial);
        
        if(appset.attributeImportance!=0)
            targetAtScore = new ArrayList<>(appset.numInitial);
        
        for(int z=0;z<appset.numInitial;z++){
            redScores.add(Double.NaN);
            redScoresAtt.add(Double.NaN);
            redDistCoverage.add(Double.NaN);
            redDistCoverageAt.add(Double.NaN);
            maxDiffScoreDistribution.add(Double.NaN);
            if(appset.useNetworkAsBackground==true)
                 redDistNetwork.add(Double.NaN);
            if(appset.attributeImportance!=0)
                targetAtScore.add(Double.NaN);
        }   
      }
                    
        NHMCDistanceMatrix nclMatInit=null;
        if(appset.distanceFilePaths.size()>0){
            nclMatInit=new NHMCDistanceMatrix(datJ.numExamples,appset);
            nclMatInit.loadDistance(new File(appset.distanceFilePaths.get(0)), fid);
            if(appset.distanceFilePaths.size()>0){
             nclMatInit.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
             nclMatInit.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
            }
            else{
                nclMatInit.resetFile(new File(appset.outFolderPath+"/distances.csv"));
                nclMatInit.writeToFile(new File(appset.outFolderPath+"/distances.csv"), fid,appset);
            }
             nclMatInit=null;
        }
        
   Utilities ut = new Utilities();     
        
 //start function   
 
        for(int runTest=0;runTest<appset.numRandomRestarts;runTest++){  
         
            System.out.println("TestRand: "+(runTest+1));
            
          DataSetCreator datJInit=null;
          
       if(!appset.useSplitTesting){ 
        if(appset.initClusteringFileName.equals("")){
            if(appset.system.equals("windows"))
                datJInit = new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
            else
                datJInit = new DataSetCreator(appset.outFolderPath+"/Jinput.arff");
        }
        else{
                     if(appset.system.equals("windows"))
                            datJInit = new DataSetCreator(appset.outFolderPath+"\\"+appset.initClusteringFileName);
                     else
                            datJInit = new DataSetCreator(/*appset.outFolderPath+"/"+*/appset.initClusteringFileName);
               
            }
       }
       else{
           if(appset.trainFileName.equals("") || appset.testFileName.equals("")){
            if(appset.system.equals("windows"))
                datJInit = new DataSetCreator(appset.outFolderPath+"\\JinputTrain.arff");
            else
                datJInit = new DataSetCreator(appset.outFolderPath+"/JinputTrain.arff");
           }
           else{
               if(appset.system.equals("windows"))
                datJInit = new DataSetCreator(appset.outFolderPath+"\\"+appset.trainFileName);
            else
                datJInit = new DataSetCreator(appset.outFolderPath+"/"+appset.trainFileName);
           }
       }
        
                try{
        datJInit.readDataset();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        datJInit.W2indexs.addAll(datJ.W2indexs);
        
        if(appset.initClusteringFileName.equals(""))
            datJInit.initialClusteringGen1(appset.outFolderPath,appset,datJ.schema.getNbDescriptiveAttributes(),r);
        
        SettingsReader initSettings=new SettingsReader();
        
        if(appset.initClusteringFileName.equals(""))
             if(appset.system.equals("windows"))
                 initSettings.setDataFilePath(appset.outFolderPath+"\\JinputInitial.arff");
             else
                  initSettings.setDataFilePath(appset.outFolderPath+"/JinputInitial.arff");
        else{
            if(appset.system.equals("windows"))
                 initSettings.setDataFilePath(appset.outFolderPath+"\\"+appset.initClusteringFileName);
            else
                initSettings.setDataFilePath(/*appset.outFolderPath+"/"+*/appset.initClusteringFileName);
        }
        
        //make initial cluster analysis
        int view1=0,view2=1;
        
        for(view1=0;view1<datJ.W2indexs.size();view1++){
            for(view2=view1+1;view2<datJ.W2indexs.size()+1;view2++){
        
        System.out.println("WIndexes size: "+datJ.W2indexs.size());
        System.out.println("View1: "+view1);
        System.out.println("View2: "+view2);   
        //for starts just create a function that works with views 0 and 1   
        
        ut.createViewDataInitial(appset, initSettings, datJ, view1);
            
        ClusProcessExecutor exec=new ClusProcessExecutor();

        //RunInitW1S1
        exec.run(appset.javaPath,appset.clusPath ,appset.outFolderPath,"view"+(view1+1)+".s",0, appset.clusteringMemory);//was 1 before for rules
        System.out.println("Process 1 side 1 finished!");
         
        //read the rules obtained from first attribute set
          String input1="";
          if(appset.system.equals("windows"))
             input1=appset.outFolderPath+"\\view"+(view1+1)+".out";
          else
              input1=appset.outFolderPath+"/view"+(view1+1)+".out"; 
           
          if(appset.generatingModelType<=1)
             ruleReaders.get(view1).extractRules(input1,fid,datJInit,appset,0);
          else ruleReaders.get(view1).extractRules(input1,fid,datJInit,appset,1);
         
           if(appset.distanceFilePaths.size()>1){
            nclMatInit=new NHMCDistanceMatrix(datJ.numExamples,appset);
            nclMatInit.loadDistance(new File(appset.distanceFilePaths.get(1)), fid);
            if(appset.system.equals("windows")){
             nclMatInit.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
             nclMatInit.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
            }
            else{
                nclMatInit.resetFile(new File(appset.outFolderPath+"/distances.csv"));
                nclMatInit.writeToFile(new File(appset.outFolderPath+"/distances.csv"), fid,appset);
            }
             nclMatInit=null;
        }
       
        SettingsReader set=null;
        SettingsReader set1=null;
        SettingsReader setF=null;
        SettingsReader setF1=null;
       
        ut.createViewDataInitial(appset, initSettings, datJ, view2);

        exec.run(appset.javaPath,appset.clusPath, appset.outFolderPath, "view"+(view2+1)+".s", 0,appset.clusteringMemory);//was 1 before
        System.out.println("Process 1 side 2 finished!");

        //read the rules obtained from first attribute set
       if(appset.system.equals("windows"))
        input1=appset.outFolderPath+"\\view"+(view2+1)+".out";
       else
           input1=appset.outFolderPath+"/view"+(view2+1)+".out";
       if(appset.generatingModelType<=1)
             ruleReaders.get(view2).extractRules(input1,fid,datJInit,appset,0);
       else ruleReaders.get(view2).extractRules(input1,fid,datJInit,appset,1);
           
        int leftSide=1, rightSide=0;//set left to 1 when computing lf, otherwise right
        int leftSide1=0, rightSide1=1; //left, right side for Side 2
        int it=0;
        Jacard js=new Jacard();
        Jacard jsN[]=new Jacard[3];
        
        for(int i=0;i<jsN.length;i++)
            jsN[i]=new Jacard();
       
        int newRedescriptions=1;
        int numIter=0;
        int RunInd=0;
       
        int naex=datJ.numExamples;
        
        //add arrayList of view rules
        ArrayList<RuleReader> readers=new ArrayList<>();
        int oldRIndex[]={0};
        
        NHMCDistanceMatrix nclMat=null;
        NHMCDistanceMatrix nclMat1=null;
       
        //could be a functin
        while(newRedescriptions!=0 && RunInd<appset.numIterations){
            
       DataSetCreator dsc=null;//new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
       DataSetCreator dsc1=null;//new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
       
       ruleReaders.get(view2).setSize();
       ruleReaders.get(view1).setSize();

       int nARules=0, nARules1=0;
       int oldIndexRR = ruleReaders.get(view2).newRuleIndex;
        int oldIndexRR1=ruleReaders.get(view1).newRuleIndex;
       System.out.println("OOIndRR: "+oldIndexRR);
       System.out.println("OOIndRR1: "+oldIndexRR1);
       int endIndexRR=0, endIndexRR1=0;
             newRedescriptions=0;
            System.out.println("Iteration: "+(++numIter));

             //do rule creation with various generality levels
        double percentage[]=new double[]{0,0.2,0.4,0.6,0.8,1.0};

         int numBins=0;
        int Size=Math.max(/*rr.rules.size()*/ruleReaders.get(view2).rules.size()-oldIndexRR, /*rr1.rules.size()*/ruleReaders.get(view1).rules.size()-oldIndexRR1);
        if(Size%appset.numTargets==0)
            numBins=Size/appset.numTargets;
        else numBins=Size/appset.numTargets+1;
        
        for(int z=0;z<numBins;z++){//percentage.length-1;z++){

            nARules=0; nARules1=0;
            double startPerc=0;//percentage[z];
            double endPerc=1;//percentage[z+1];

            System.out.println("startPerc: "+startPerc);
            System.out.println("endPerc: "+endPerc);

            int cuttof=0,cuttof1=0;

            if(z==0){
                endIndexRR=ruleReaders.get(view2).rules.size();//rr.rules.size();
                endIndexRR1=ruleReaders.get(view1).rules.size();
               
                //compute network things only here
                if(appset.useNC.get(1)==true && appset.networkInit==false && appset.useNetworkAsBackground==false){
                    nclMat.reset(appset);
                    nclMat1.reset(appset); 
               if(leftSide==1){
                    if(appset.distanceFilePaths.size()>=1){
                             nclMat.loadDistance(new File(appset.distanceFilePaths.get(1)), fid);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat.computeDistanceMatrix(ruleReaders.get(view1)/*rr1*/, fid, appset.maxDistance, datJ.numExamples);
                     }
                   }
                }
                 if(appset.useNC.get(0)==true && appset.networkInit==false && appset.useNetworkAsBackground==false){
                 if(rightSide==1){
                    if(appset.distanceFilePaths.size()>=2){
                             nclMat.loadDistance(new File(appset.distanceFilePaths.get(0)), fid);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat.computeDistanceMatrix(ruleReaders.get(view2)/*rr*/, fid, appset.maxDistance, datJ.numExamples);
                     }
                   }
                 }
                  if(appset.useNC.get(1)==true && appset.networkInit==false && appset.useNetworkAsBackground==false){
                if(leftSide1==1){
                    if(appset.distanceFilePaths.size()>=1){
                             nclMat1.loadDistance(new File(appset.distanceFilePaths.get(1)), fid);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat1.computeDistanceMatrix(ruleReaders.get(view1)/*rr1*/, fid, appset.maxDistance, datJ.numExamples);
                     }
                }
                  }
                   if(appset.useNC.get(0)==true && appset.networkInit==false && appset.useNetworkAsBackground==false){
                    if(rightSide1==1){
                    if(appset.distanceFilePaths.size()>=2){
                             nclMat1.loadDistance(new File(appset.distanceFilePaths.get(0)), fid);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat1.computeDistanceMatrix(ruleReaders.get(view2)/*rr*/, fid, appset.maxDistance, datJ.numExamples);
                     }
                  }
                }
            }
          
         if(!appset.useSplitTesting==true){    
          if(appset.system.equals("windows")){         
                dsc=new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
                dsc1=new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
          }
          else{
                dsc=new DataSetCreator(appset.outFolderPath+"/Jinput.arff");
                dsc1=new DataSetCreator(appset.outFolderPath+"/Jinput.arff");
          }
         }
         else{
              if(appset.trainFileName.equals("") || appset.testFileName.equals("")){
                     if(appset.system.equals("windows")){    
                             dsc=new DataSetCreator(appset.outFolderPath+"\\JinputTrain.arff");
                             dsc1=new DataSetCreator(appset.outFolderPath+"\\JinputTrain.arff");
                         }
                     else{
                             dsc=new DataSetCreator(appset.outFolderPath+"/JinputTrain.arff");
                             dsc1=new DataSetCreator(appset.outFolderPath+"/JinputTrain.arff"); 
                }
             }
              else{
                  if(appset.system.equals("windows")){    
                             dsc=new DataSetCreator(appset.outFolderPath+"\\"+appset.trainFileName);
                             dsc1=new DataSetCreator(appset.outFolderPath+"\\"+appset.trainFileName);
                         }
                     else{
                             dsc=new DataSetCreator(appset.outFolderPath+"/"+appset.trainFileName);
                             dsc1=new DataSetCreator(appset.outFolderPath+"/"+appset.trainFileName); 
                }
              }
         }
    
            try{
        dsc.readDataset();
        }
        catch(IOException e){
            e.printStackTrace();
        }
            naex=dsc.data.getNbRows();
        //read dataset for cicle 2
        try{
        dsc1.readDataset();
        }
        catch(IOException e){
            e.printStackTrace();
        }
            
            //create and modify settings for cicle 1
          if(leftSide==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
              //createSettings
            if(appset.system.equals("windows")){    
             set=new SettingsReader(appset.outFolderPath+"\\view"+(view2+1)+"tmp.s",appset.outFolderPath+"\\view"+(view2+1)+".s");
             set.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff");
            }
            else{
                 set=new SettingsReader(appset.outFolderPath+"/view"+(view2+1)+"tmp.s",appset.outFolderPath+"/view"+(view2+1)+".s");
                 set.setDataFilePath(appset.outFolderPath+"/Jinputnew.arff"); 
            }
             if(appset.numSupplementTrees>0){
                 if(appset.system.equals("windows")){
                     setF=new SettingsReader(appset.outFolderPath+"\\view"+(view2+1)+"tmpF.s",appset.outFolderPath+"\\view"+(view2+1)+".s");
                     setF.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff");
                 }
                 else{
                     setF=new SettingsReader(appset.outFolderPath+"/view"+(view2+1)+"tmpF.s",appset.outFolderPath+"/view"+(view2+1)+".s");
                     setF.setDataFilePath(appset.outFolderPath+"/Jinputnew.arff");
                 }
             }

             int endTmp=0;
             if((z+1)*appset.numTargets>(endIndexRR1-oldIndexRR1))
                 endTmp=endIndexRR1;
             else endTmp=(z+1)*appset.numTargets+oldIndexRR1;
             int startIndexRR1=oldIndexRR1+z*appset.numTargets;
             
             for(int i=startIndexRR1;i<endTmp;i++){ //do on the fly when reading rules
                 if( /*rr1.rules.get(i).elements.size()*/ ruleReaders.get(view1).rules.get(i).elements.size()>=appset.minSupport) //do parameters analysis in this step
                        nARules++;
             }

             if(appset.generatingModelType == 0)
                  set.ModifySettings(nARules,dsc.schema.getNbAttributes());
             else set.ModifySettingsGF(nARules, dsc.schema.getNbAttributes(), appset);
             if(appset.numSupplementTrees>0)
                setF.ModifySettingsF(nARules,dsc.schema.getNbAttributes(),appset);
       //modify settings
          }
          else if(rightSide==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
              if(appset.system.equals("windows")){
                 set=new SettingsReader(appset.outFolderPath+"\\view"+(view1+1)+"tmp.s",appset.outFolderPath+"\\view"+(view1+1)+".s");
                 set.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff");
                    }
              else{
                 set=new SettingsReader(appset.outFolderPath+"/view"+(view1+1)+"tmp.s",appset.outFolderPath+"/view"+(view1+1)+".s");
                 set.setDataFilePath(appset.outFolderPath+"/Jinputnew.arff");  
              }
                 if(appset.numSupplementTrees>0){
                     if(appset.system.equals("windows")){
                        setF=new SettingsReader(appset.outFolderPath+"\\view"+(view1+1)+"tmpF.s",appset.outFolderPath+"\\view"+(view1+1)+".s");
                        setF.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff");
                     }
                     else{
                         setF=new SettingsReader(appset.outFolderPath+"/view"+(view1+1)+"tmpF.s",appset.outFolderPath+"/view"+(view1+1)+".s");
                         setF.setDataFilePath(appset.outFolderPath+"/Jinputnew.arff");
                     }
                 }

                  int endTmp=0;
             if((z+1)*appset.numTargets>(endIndexRR-oldIndexRR))
                 endTmp=endIndexRR;
             else endTmp=(z+1)*appset.numTargets+oldIndexRR;
                 
             int startIndexRR=oldIndexRR+z*appset.numTargets;
             
                 for(int i=startIndexRR;i<endTmp;i++) //do on the fly when reading rules
                        if(/*rr.rules.get(i).elements.size()*/ ruleReaders.get(view2).rules.get(i).elements.size()>=appset.minSupport)
                            nARules++;
                 if(appset.generatingModelType == 0)
                     set.ModifySettings(nARules,dsc1.schema.getNbAttributes());
                 else set.ModifySettingsGF(nARules, dsc1.schema.getNbAttributes(), appset);
                if(appset.numSupplementTrees>0)
                    setF.ModifySettingsF(nARules,dsc1.schema.getNbAttributes(),appset);
          }

            //create and modify settings for cicle 2
        if(leftSide1==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
            if(appset.system.equals("windows")){
                set1=new SettingsReader(appset.outFolderPath+"\\view"+(view2+1)+"tmp1.s",appset.outFolderPath+"\\view"+(view2+1)+".s");
                set1.setDataFilePath(appset.outFolderPath+"\\Jinputnew1.arff");
            }
            else{
                set1=new SettingsReader(appset.outFolderPath+"/view"+(view2+1)+"tmp1.s",appset.outFolderPath+"/view"+(view2+1)+".s");
                set1.setDataFilePath(appset.outFolderPath+"/Jinputnew1.arff");
            }
            if(appset.numSupplementTrees>0){
                if(appset.system.equals("windows")){
                     setF1=new SettingsReader(appset.outFolderPath+"\\view"+(view2+1)+"tmpF1.s",appset.outFolderPath+"\\view"+(view2+1)+".s");
                     setF1.setDataFilePath(appset.outFolderPath+"\\Jinputnew1.arff");
                }
                else{
                     setF1=new SettingsReader(appset.outFolderPath+"/view"+(view2+1)+"tmpF1.s",appset.outFolderPath+"/view"+(view2+1)+".s");
                     setF1.setDataFilePath(appset.outFolderPath+"/Jinputnew1.arff");
                }
            }
            
             int endTmp=0;
             if((z+1)*appset.numTargets>(endIndexRR1-oldIndexRR1))
                 endTmp=endIndexRR1;
             else endTmp=oldIndexRR1+(z+1)*appset.numTargets;
             
             int startIndexRR1=oldIndexRR1+z*appset.numTargets;
             
             for(int i=startIndexRR1;i<endTmp;i++)
                  if(/*rr1.rules.get(i).elements.size()*/ruleReaders.get(view1).rules.get(i).elements.size()>=appset.minSupport)
                       nARules1++;
             if(appset.generatingModelType == 0)
                   set1.ModifySettings(nARules1,dsc.schema.getNbAttributes());
             else set1.ModifySettingsGF(nARules1, dsc.schema.getNbAttributes(), appset);
             if(appset.numSupplementTrees>0)
                setF1.ModifySettingsF(nARules1,dsc.schema.getNbAttributes(),appset);
          }
          else if(rightSide1==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){

            if(appset.system.equals("windows")){  
              set1=new SettingsReader(appset.outFolderPath+"\\view"+(view1+1)+"tmp1.s",appset.outFolderPath+"\\view"+(view1+1)+".s");
              set1.setDataFilePath(appset.outFolderPath+"\\Jinputnew1.arff");
            }
            else{
               set1=new SettingsReader(appset.outFolderPath+"/view"+(view1+1)+"tmp1.s",appset.outFolderPath+"/view"+(view1+1)+".s");
               set1.setDataFilePath(appset.outFolderPath+"/Jinputnew1.arff"); 
            }
              if(appset.numSupplementTrees>0){
                  if(appset.system.equals("windows")){ 
                        setF1=new SettingsReader(appset.outFolderPath+"\\view"+(view1+1)+"tmpF1.s",appset.outFolderPath+"\\view"+(view1+1)+".s");
                        setF1.setDataFilePath(appset.outFolderPath+"\\Jinputnew1.arff");
                  }
                  else{
                        setF1=new SettingsReader(appset.outFolderPath+"/view"+(view1+1)+"tmpF1.s",appset.outFolderPath+"/view"+(view1+1)+".s");
                        setF1.setDataFilePath(appset.outFolderPath+"/Jinputnew1.arff"); 
                  }
              }

              int endTmp=0;
             if((z+1)*appset.numTargets>(endIndexRR-oldIndexRR))
                 endTmp=endIndexRR;
             else endTmp=(z+1)*appset.numTargets+oldIndexRR;
              
             int startIndexRR=oldIndexRR+z*appset.numTargets;
             
              for(int i=startIndexRR;i<endTmp;i++)
                  if(/*rr.rules.get(i).elements.size()*/ruleReaders.get(view2).rules.get(i).elements.size()>=appset.minSupport)
                        nARules1++;
              if(appset.generatingModelType == 0)
                    set1.ModifySettings(nARules1,dsc1.schema.getNbAttributes());
              else set1.ModifySettingsGF(nARules1, dsc1.schema.getNbAttributes(), appset);
              if(appset.numSupplementTrees>0)
                    setF1.ModifySettingsF(nARules1,dsc1.schema.getNbAttributes(),appset);
          }

        RuleReader ItRules=new RuleReader();
        RuleReader ItRules1=new RuleReader();
        RuleReader ItRulesF=new RuleReader();
        RuleReader ItRulesF1=new RuleReader();

       //modify dataset for cicle 1
       if(leftSide==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets ){
           int startIndexRR1=oldIndexRR1+z*appset.numTargets;
           int endTmp=0;
             if((z+1)*appset.numTargets>(endIndexRR1-oldIndexRR1))
                 endTmp=endIndexRR1;
             else endTmp=(z+1)*appset.numTargets+oldIndexRR1;
        try{
         if(appset.treeTypes.get(1)==1/*appset.typeOfRSTrees==1*/) 
             if(appset.system.equals("windows")){ 
                 dsc.modifyDatasetS(startIndexRR1,endTmp,ruleReaders.get(view1)/*rr1*/,appset.outFolderPath+"\\Jinputnew.arff",fid,appset);
             }
             else{
                dsc.modifyDatasetS(startIndexRR1,endTmp,ruleReaders.get(view1)/*rr1*/,appset.outFolderPath+"/Jinputnew.arff",fid,appset); 
             }
         else if(appset.treeTypes.get(1)==0/*appset.typeOfRSTrees==0*/)
             if(appset.system.equals("windows")){ 
                 dsc.modifyDatasetCat(startIndexRR1,endTmp,ruleReaders.get(view1)/*rr1*/,appset.outFolderPath+"\\Jinputnew.arff",fid,appset);
             }
             else{
                dsc.modifyDatasetCat(startIndexRR1,endTmp,ruleReaders.get(view1)/*rr1*/,appset.outFolderPath+"/Jinputnew.arff",fid,appset); 
             }
        }
        catch(IOException e){
            e.printStackTrace();
        }
       }
       else if(rightSide==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
           int endTmp=0;
             if((z+1)*appset.numTargets>(endIndexRR-oldIndexRR))
                 endTmp=endIndexRR;
             else endTmp=(z+1)*appset.numTargets+oldIndexRR;
              
             int startIndexRR=oldIndexRR+z*appset.numTargets;
             
         try{
             if(appset.treeTypes.get(0)==1/*appset.typeOfLSTrees==1*/)
                 if(appset.system.equals("windows")){ 
                    dsc.modifyDatasetS(startIndexRR,endTmp,ruleReaders.get(view2)/*rr*/,appset.outFolderPath+"\\Jinputnew.arff",fid,appset);
                 }
                 else{
                     dsc.modifyDatasetS(startIndexRR,endTmp,ruleReaders.get(view2)/*rr*/,appset.outFolderPath+"/Jinputnew.arff",fid,appset); 
                 }
             else if(appset.treeTypes.get(0)==0/*appset.typeOfLSTrees==0*/)
                  if(appset.system.equals("windows")){ 
                    dsc.modifyDatasetCat(startIndexRR,endTmp,ruleReaders.get(view2)/*rr*/,appset.outFolderPath+"\\Jinputnew.arff",fid,appset);
                  }
                  else{
                     dsc.modifyDatasetCat(startIndexRR,endTmp,ruleReaders.get(view2)/*rr*/,appset.outFolderPath+"/Jinputnew.arff",fid,appset); 
                  }
        }
        catch(IOException e){
            e.printStackTrace();
        }  
       }

       //modify dataset for cicle 2
       if(leftSide1==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
           int startIndexRR1=oldIndexRR1+z*appset.numTargets;
           int endTmp=0;
             if((z+1)*appset.numTargets>(endIndexRR1-oldIndexRR1))
                 endTmp=endIndexRR1;
             else endTmp=(z+1)*appset.numTargets+oldIndexRR1;
             
        try{
            if(appset.treeTypes.get(1)==1)
                if(appset.system.equals("windows")){ 
                    dsc1.modifyDatasetS(startIndexRR1,endTmp, ruleReaders.get(view1)/*rr1*/,appset.outFolderPath+"\\Jinputnew1.arff",fid,appset);
                }
                else{
                   dsc1.modifyDatasetS(startIndexRR1,endTmp, ruleReaders.get(view1)/*rr1*/,appset.outFolderPath+"/Jinputnew1.arff",fid,appset); 
                }
            else if(appset.treeTypes.get(1)==0/*appset.typeOfRSTrees==0*/)
                if(appset.treeTypes.get(1)==1)
                     dsc1.modifyDatasetCat(startIndexRR1,endTmp, ruleReaders.get(view1)/*rr1*/,appset.outFolderPath+"\\Jinputnew1.arff",fid,appset);
                else
                     dsc1.modifyDatasetCat(startIndexRR1,endTmp, ruleReaders.get(view1)/*rr1*/,appset.outFolderPath+"/Jinputnew1.arff",fid,appset);
        }
        catch(IOException e){
            e.printStackTrace();
        }
       }
       else if(rightSide1==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
           int endTmp=0;
             if((z+1)*appset.numTargets>(endIndexRR-oldIndexRR))
                 endTmp=endIndexRR;
             else endTmp=(z+1)*appset.numTargets+oldIndexRR;
              
             int startIndexRR=oldIndexRR+z*appset.numTargets;
             
         try{
             if(appset.treeTypes.get(0)==1/*appset.typeOfLSTrees==1*/)
                 if(appset.system.equals("windows"))
                    dsc1.modifyDatasetS(startIndexRR,endTmp,ruleReaders.get(view2)/*rr*/,appset.outFolderPath+"\\Jinputnew1.arff",fid,appset);
                 else
                    dsc1.modifyDatasetS(startIndexRR,endTmp,ruleReaders.get(view2)/*rr*/,appset.outFolderPath+"/Jinputnew1.arff",fid,appset); 
             else if(appset.treeTypes.get(0)==0/*appset.typeOfRSTrees==0*/)
                 if(appset.system.equals("windows"))
                 dsc1.modifyDatasetCat(startIndexRR,endTmp,ruleReaders.get(view2)/*rr*/,appset.outFolderPath+"\\Jinputnew1.arff",fid,appset);
             else
                   dsc1.modifyDatasetCat(startIndexRR,endTmp,ruleReaders.get(view2)/*rr*/,appset.outFolderPath+"/Jinputnew1.arff",fid,appset);   
        }
        catch(IOException e){
            e.printStackTrace();
        }
       }
       
       dsc=null;
       dsc1=null;
      
       if((appset.useNC.get(0)==true && rightSide==1 && appset.networkInit==false && appset.useNetworkAsBackground==false) || (appset.useNC.get(1)==true && leftSide==1 && appset.networkInit==false && appset.useNetworkAsBackground==false)){ 
           if(appset.system.equals("windows")){  
             nclMat.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
             nclMat.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
            }
           else{
              nclMat.resetFile(new File(appset.outFolderPath+"/distances.csv"));
             nclMat.writeToFile(new File(appset.outFolderPath+"/distances.csv"), fid,appset); 
           }
       }
       
        //run the second proces on new data
        // iterate until convergence (no new rules, or very small amount obtained)
         if(leftSide==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath, "view"+(view2+1)+"tmp.s"/*"wbtmp.s"*/, 0,appset.clusteringMemory);//was 1 for rules before
             if(appset.numSupplementTrees>0)
                exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath, "view"+(view2+1)+"tmpF.s"/*"wbtmp.s"*/, 0,appset.clusteringMemory);//was 1 for rules before
             System.out.println("Process 2 side 1 finished!");
         }
         else if(rightSide==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath, "view"+(view1+1)+"tmp.s"/*"unctadtmp.s"*/, 0,appset.clusteringMemory);
             if(appset.numSupplementTrees>0)
                 exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath, "view"+(view1+1)+"tmpF.s"/*"unctadtmp.s"*/, 0,appset.clusteringMemory);
             System.out.println("Process 1 side 1 finished!");
         }

        if((appset.useNC.get(0)==true && rightSide1==1 && appset.networkInit==false && appset.useNetworkAsBackground==false) || (appset.useNC.get(1)==true && leftSide1==1 && appset.networkInit==false && appset.useNetworkAsBackground==false)){ 
              if(appset.system.equals("windows")){ 
                 nclMat1.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
                 nclMat1.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
              }
              else{
                 nclMat1.resetFile(new File(appset.outFolderPath+"/distances.csv"));
                 nclMat1.writeToFile(new File(appset.outFolderPath+"/distances.csv"), fid,appset);
              }
        }
         
         //run the second proces for cicle 2 on new data
        // iterate until convergence (no new rules, or very small amount obtained)
         if(leftSide1==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath,"view"+(view2+1)+"tmp1.s" /*"wbtmpS1.s"*/, 0,appset.clusteringMemory);
             if(appset.numSupplementTrees>0)
                exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath,"view"+(view2+1)+"tmpF1.s" /*"wbtmpS1.s"*/, 0,appset.clusteringMemory);
             System.out.println("Process 2 side 2 finished!");
         }
         else if(rightSide1==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath,"view"+(view1+1)+"tmp1.s"/*"unctadtmpS1.s"*/ , 0,appset.clusteringMemory);
             if(appset.numSupplementTrees>0)
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath,"view"+(view1+1)+"tmpF1.s"/*"unctadtmpS1.s"*/ , 0,appset.clusteringMemory);
             System.out.println("Process 1 side 2 finished!");
         }
//put into function
         
//put outside function
       //extract rules for cicle 1
        String input="", inputF="";
        if(leftSide==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
            if(appset.system.equals("windows")){ 
                 input=appset.outFolderPath+"\\view"+(view2+1)+"tmp.out";
                 inputF=appset.outFolderPath+"\\view"+(view2+1)+"tmpF.out";
            }
            else{
               input=appset.outFolderPath+"/view"+(view2+1)+"tmp.out";
                 inputF=appset.outFolderPath+"/view"+(view2+1)+"tmpF.out"; 
            }
        }
        else if(rightSide==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
            if(appset.system.equals("windows")){ 
                 input=appset.outFolderPath+"\\view"+(view1+1)+"tmp.out";
                    if(appset.numSupplementTrees>0) 
                        inputF=appset.outFolderPath+"\\view"+(view1+1)+"tmpF.out";
            }
            else{
                input=appset.outFolderPath+"/view"+(view1+1)+"tmp.out";
                    if(appset.numSupplementTrees>0) 
                        inputF=appset.outFolderPath+"/view"+(view1+1)+"tmpF.out";
            }
        }
       int newRules=0;
       if((leftSide==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets) || (rightSide==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets)){
           if(appset.generatingModelType<=1)
             ItRules.extractRules(input,fid,datJ,appset,0);//perhaps filter here
           else ItRules.extractRules(input,fid,datJ,appset,1);
            ItRules.setSize();
            if(appset.numSupplementTrees>0){
                if(appset.SupplementPredictiveTreeType == 0)
                     ItRulesF.extractRules(inputF,fid,datJ,appset,0);
                else 
                     ItRulesF.extractRules(inputF,fid,datJ,appset,1);
                ItRulesF.setSize();
            }
       }
        if(leftSide==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
            if(z==0)
                newRules=ruleReaders.get(view2).addnewRulesC(ItRules, appset.numnewRAttr,1);/*rr.addnewRulesC(ItRules, appset.numnewRAttr,1);*/
            else
                newRules=ruleReaders.get(view2).addnewRulesC(ItRules, appset.numnewRAttr,0);//rr.addnewRulesC(ItRules, appset.numnewRAttr,0);
            if(appset.numSupplementTrees>0)
                ruleReaders.get(view2).addnewRulesCF(ItRulesF, appset.numnewRAttr); 
        }
        else if(rightSide==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
            if(z==0)
                newRules=ruleReaders.get(view1).addnewRulesC(ItRules, appset.numnewRAttr,1);
            else
                newRules=ruleReaders.get(view1).addnewRulesC(ItRules, appset.numnewRAttr, 0);
            if(appset.numSupplementTrees>0)
                ruleReaders.get(view1).addnewRulesCF(ItRulesF, appset.numnewRAttr);
        }     
         //extract rules for cicle 2
        input=""; inputF="";
        if(leftSide1==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
            if(appset.system.equals("windows")){ 
                    input=appset.outFolderPath+"\\view"+(view2+1)+"tmp1.out";
                    inputF=appset.outFolderPath+"\\view"+(view2+1)+"tmpF1.out";
            }
            else{
                    input=appset.outFolderPath+"/view"+(view2+1)+"tmp1.out";
                    inputF=appset.outFolderPath+"/view"+(view2+1)+"tmpF1.out"; 
            }
        }
        else if(rightSide1==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
            if(appset.system.equals("windows")){ 
                    input=appset.outFolderPath+"\\view"+(view1+1)+"tmp1.out";
                    inputF=appset.outFolderPath+"\\view"+(view1+1)+"tmpF1.out";
            }
            else{
                input=appset.outFolderPath+"/view"+(view1+1)+"tmp1.out";
                inputF=appset.outFolderPath+"/view"+(view1+1)+"tmpF1.out";
            }
        }
       int newRules1=0;
       if((leftSide1==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets) || (rightSide1==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets)){
           if(appset.generatingModelType<=1)
                ItRules1.extractRules(input,fid,datJ,appset,0);
           else  ItRules1.extractRules(input,fid,datJ,appset,1);
        ItRules1.setSize();
        if(appset.numSupplementTrees>0){
            if(appset.SupplementPredictiveTreeType == 0)
                ItRulesF1.extractRules(inputF,fid,datJ,appset,0);
            else
                 ItRulesF1.extractRules(inputF,fid,datJ,appset,1);
             ItRulesF1.setSize();
        }
       }
        if(leftSide1==1 && (endIndexRR1-oldIndexRR1)>z*appset.numTargets){
            if(z==0)
                    newRules1=ruleReaders.get(view2).addnewRulesC(ItRules1, appset.numnewRAttr,1);//rr.addnewRulesC(ItRules1, appset.numnewRAttr,1);
            else
                    newRules1=ruleReaders.get(view2).addnewRulesC(ItRules1, appset.numnewRAttr, 0);//rr.addnewRulesC(ItRules1, appset.numnewRAttr, 0);
            if(appset.numSupplementTrees>0)
                ruleReaders.get(view2).addnewRulesCF(ItRulesF1, appset.numnewRAttr);
        }
        else if(rightSide1==1 && (endIndexRR-oldIndexRR)>z*appset.numTargets){
            if(z==0)
                newRules1=ruleReaders.get(view1).addnewRulesC(ItRules1, appset.numnewRAttr,1);
            else
                    newRules1=ruleReaders.get(view1).addnewRulesC(ItRules1, appset.numnewRAttr,0);
            if(appset.numSupplementTrees>0)
             ruleReaders.get(view1).addnewRulesCF(ItRulesF1, appset.numnewRAttr);
        }

        System.out.println("New rules cicle 1: "+newRules);
        System.out.println("New rules cicle 2: "+newRules1);
       }

       if(appset.optimizationType==0){ 
        if(appset.useJoin){
            ruleReaders.get(view1).removeDuplicates(ruleReaders.get(view1).newRuleIndex);
            ruleReaders.get(view2).removeDuplicates(ruleReaders.get(view2).newRuleIndex);
            newRedescriptions=rs.createGuidedJoinBasic(ruleReaders.get(view1)/*rr1*/, ruleReaders.get(view2)/*rr*/, jsN, appset, oldIndexRR1, oldIndexRR, RunInd,oom,fid,datJ, elemFreq, attrFreq, redScores,redScoresAtt,redDistCoverage,redDistCoverageAt, redDistNetwork, targetAtScore, Statistics, maxDiffScoreDistribution,nclMatInit,0);
            //add filtering here
              if(appset.numSupplementTrees>0){
                ruleReaders.get(view2).removeRulesCF();
                ruleReaders.get(view1).removeRulesCF();
            }
            
            if(appset.generatingModelType>0){
                 ruleReaders.get(view2).filterRules(ruleReaders.get(view2).newRuleIndex);
                 ruleReaders.get(view1).filterRules(ruleReaders.get(view1).newRuleIndex);
            }
            ruleReaders.get(view2).removeElements(ruleReaders.get(view2).newRuleIndex);
            ruleReaders.get(view1).removeElements(ruleReaders.get(view1).newRuleIndex);
        }
        else if(!appset.useJoin){
            newRedescriptions=rs.createGuidedNoJoinBasic(ruleReaders.get(view1)/*rr1*/, ruleReaders.get(view2)/*rr*/, jsN, appset, oldIndexRR1, oldIndexRR, RunInd,oom,fid,datJ, elemFreq, attrFreq, redScores,redScoresAtt,redDistCoverage,redDistCoverageAt, redDistNetwork, targetAtScore, Statistics, maxDiffScoreDistribution,nclMatInit,0);
             //add filtering here
            if(appset.numSupplementTrees>0){
                ruleReaders.get(view2).removeRulesCF();
                ruleReaders.get(view1).removeRulesCF();
            }
            
            if(appset.generatingModelType>0){
                 ruleReaders.get(view2).filterRules(ruleReaders.get(view2).newRuleIndex);
                 ruleReaders.get(view1).filterRules(ruleReaders.get(view1).newRuleIndex);
            }
             ruleReaders.get(view2).removeElements(ruleReaders.get(view2).newRuleIndex);
             ruleReaders.get(view1).removeElements(ruleReaders.get(view1).newRuleIndex);
        }
       }
       else{
           if(appset.useJoin){
            newRedescriptions=rs.createGuidedJoinExt(ruleReaders.get(view1)/*rr1*/, ruleReaders.get(view2)/*rr*/, jsN, appset, oldIndexRR1, oldIndexRR, RunInd, oom,fid,datJ,view1,view2, appset.maxRSSize);//sqitch sides of rules
            //add filtering here
            
            if(appset.numSupplementTrees>0){
                ruleReaders.get(view2).removeRulesCF();
                ruleReaders.get(view1).removeRulesCF();
            }
            
            if(appset.generatingModelType>0){
                ruleReaders.get(view2).filterRules(ruleReaders.get(view2).newRuleIndex);
                ruleReaders.get(view1).filterRules(ruleReaders.get(view1).newRuleIndex);
            }
            ruleReaders.get(view2).removeElements(ruleReaders.get(view2).newRuleIndex);
            ruleReaders.get(view1).removeElements(ruleReaders.get(view1).newRuleIndex);
        }
        else if(!appset.useJoin){
            newRedescriptions=rs.createGuidedNoJoinExt(ruleReaders.get(view1)/*rr1*/, ruleReaders.get(view2)/*rr*/, jsN, appset, oldIndexRR1, oldIndexRR, RunInd,oom,fid,datJ,view1,view2);
            //add filtering here
            if(appset.numSupplementTrees>0){
                ruleReaders.get(view2).removeRulesCF();
                ruleReaders.get(view1).removeRulesCF();
            }
            
            if(appset.generatingModelType>0){
                ruleReaders.get(view2).filterRules(ruleReaders.get(view2).newRuleIndex);
                ruleReaders.get(view1).filterRules(ruleReaders.get(view1).newRuleIndex);
            }
            ruleReaders.get(view2).removeElements(ruleReaders.get(view2).newRuleIndex);
            ruleReaders.get(view1).removeElements(ruleReaders.get(view1).newRuleIndex);
        }
       }
       
         it++;
        
        System.out.println("New redescriptions: "+newRedescriptions);
        System.out.println("Number of viewes: "+(datJ.W2indexs.size()+1));
        
        for(int rsti = 0; rsti<rs.redescriptions.size();rsti++)
            if(rs.redescriptions.get(rsti).ruleStrings.size()<rs.redescriptions.get(rsti).viewElementsLists.size())
                rs.redescriptions.get(rsti).createRuleString(fid);
        
        for(int nws=0;nws<datJ.W2indexs.size()+1;nws++){
            
            if(nws==view1 || nws == view2)
                    continue;
                
            int oldIndW=ruleReaders.get(nws).newRuleIndex, endIndW=0;
            
            SettingsReader setMW=new SettingsReader();//(appset.outFolderPath+"\\view3tmp.s",appset.outFolderPath+"\\view2.s");
            SettingsReader setMWF = new SettingsReader();
            if(appset.system.equals("windows")){ 
            setMW.setPath(appset.outFolderPath+"\\view"+(nws+1)+"tmp.s");
            setMW.setStaticFilePath=appset.outFolderPath+"\\view"+(nws+1)+"tmp.s";
            setMW.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff");
           }
           else{
              setMW.setPath(appset.outFolderPath+"/view"+(nws+1)+"tmp.s");
              setMW.setStaticFilePath=appset.outFolderPath+"/view"+(nws+1)+"tmp.s";
              setMW.setDataFilePath(appset.outFolderPath+"/Jinputnew.arff"); 
           }
           
            if(appset.numSupplementTrees>0){
                 if(appset.system.equals("windows")){
                     setMWF.setPath(appset.outFolderPath+"\\view"+(nws+1)+"tmpF.s");
                     setMWF.setStaticFilePath=appset.outFolderPath+"\\view"+(nws+1)+"tmp.s";
                     setMWF.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff");
                 }
                 else{
                     setMWF.setPath(appset.outFolderPath+"/view"+(nws+1)+"tmpF.s");
                     setMWF.setStaticFilePath=appset.outFolderPath+"/view"+(nws+1)+"tmp.s";
                     setMWF.setDataFilePath(appset.outFolderPath+"/Jinputnew.arff");
                 }
             }
           
           System.out.println("W2 indexes: "+datJ.W2indexs.size());
           for(int kind=0;kind<datJ.W2indexs.size();kind++)
               System.out.print(datJ.W2indexs.get(kind)+" ");
           System.out.println();
           System.out.println("nws: "+nws);
          // System.out.println("nws special: "+(datJ.W2indexs.get(nws-1)+1));
           
            if(((nws)<(datJ.W2indexs.size())) && (nws-1)>=0)//add case for 0-view, something wrong when view 1 and 3 used....
                setMW.createInitialSettingsGen(nws, datJ.W2indexs.get(nws-1)+1 ,datJ.W2indexs.get(nws),datJ.schema.getNbAttributes() , appset,0);
            else if((nws)==(datJ.W2indexs.size()))
                setMW.createInitialSettingsGen(nws, datJ.W2indexs.get(nws-1)+1 ,datJ.schema.getNbAttributes()+1,datJ.schema.getNbAttributes() , appset,0);
            else if(nws==0){
                setMW.createInitialSettingsGen(nws, 3 ,datJ.W2indexs.get(nws),datJ.schema.getNbAttributes() , appset,0);
            }
            
              numBins=0;
        Size=rs.redescriptions.size();
        if(Size%appset.numTargets==0)
            numBins=Size/appset.numTargets;
        else numBins=Size/appset.numTargets+1;
            
        System.out.println("Num bins: "+numBins);
        for(int z=0;z<numBins/*percentage.length-1*/;z++){
               
            if(z==0){//should create network from redescriptions!
               if(appset.useNC.size()>nws && appset.networkInit==false){ 
                if(appset.useNC.get(nws)==true && ruleReaders.get(nws).rules.size()>0){
                    nclMat.reset(appset);
                    if(appset.distanceFilePaths.size()>=nws && appset.networkInit==false && appset.useNetworkAsBackground==false){
                             nclMat.loadDistance(new File(appset.distanceFilePaths.get(nws)), fid);
                              if(appset.system.equals("windows")){ 
                                     nclMat.writeToFile(new File(appset.outFolderPath+"\\distance.csv"), fid,appset);
                              }
                              else{
                                  nclMat.writeToFile(new File(appset.outFolderPath+"/distance.csv"), fid,appset);
                              }
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat.computeDistanceMatrix(rs.redescriptions, fid, appset.maxDistance, datJ.numExamples,oldRIndex);
                             if(appset.system.equals("windows")){ 
                                    nclMat.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
                                    nclMat.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
                             }
                             else{
                                 nclMat.resetFile(new File(appset.outFolderPath+"/distances.csv"));
                                 nclMat.writeToFile(new File(appset.outFolderPath+"/distances.csv"), fid,appset);
                             }
                     }
                   }
               }
                endIndW=ruleReaders.get(nws).rules.size();
            }
            
            nARules=0; nARules1=0;
            double startPerc=0;//percentage[z];
            double endPerc=0;//percentage[z+1];
            int minCovElements[]=new int[]{0};
            int maxCovElements[]=new int[]{0};
            int cuttof=0;
            
           if(appset.system.equals("windows")) 
                dsc=new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
           else
               dsc=new DataSetCreator(appset.outFolderPath+"/Jinput.arff");
            
             try{
        dsc.readDataset();
        }
        catch(IOException e){
            e.printStackTrace();
        }
            
            System.out.println("startPerc: "+startPerc);
            System.out.println("endPerc: "+endPerc);
            
            int endTmp=0;
             if((z+1)*appset.numTargets>rs.redescriptions.size())
                 endTmp=rs.redescriptions.size();
             else endTmp=(z+1)*appset.numTargets;
              //add conditions in the this part of the code...!!!
             int startIndexRR=z*appset.numTargets;//oldRIndex[0]+z*appset.numTargets;
             System.out.println("Start index RR"+startIndexRR);
             System.out.println("oldRIndex: "+oldRIndex[0]);
             System.out.println("end index RR: "+endTmp);
             
             if((endTmp-startIndexRR)<=0)
                 continue;
            
            for(int i=startIndexRR;i<endTmp;i++)//oldRIndex[0];i<rs.redescriptions.size();i++) //do on the fly when reading rules
                 if(rs.redescriptions.get(i).elements.size()>=appset.minSupport){        
                        nARules++;
                 }
            if(appset.generatingModelType==0)
                setMW.ModifySettings(nARules,dsc.schema.getNbAttributes());
            else setMW.ModifySettingsGF(nARules, dsc.schema.getNbAttributes(), appset);
             
              if(appset.numSupplementTrees>0)
                setMWF.ModifySettingsF(nARules,dsc.schema.getNbAttributes(),appset);
              
             try{
                 if(appset.treeTypes.get(nws)==1/*appset.typeOfRSTrees==1*/){ 
                     if(appset.system.equals("windows")) 
                         dsc.modifyDatasetS(startIndexRR,endTmp, rs.redescriptions,appset.outFolderPath+"\\Jinputnew.arff",fid,appset);
                     else 
                         dsc.modifyDatasetS(startIndexRR,endTmp, rs.redescriptions,appset.outFolderPath+"/Jinputnew.arff",fid,appset);
                 }
         else if(appset.treeTypes.get(nws)==0/*appset.typeOfRSTrees==0*/){
             if(appset.system.equals("windows")) 
                dsc.modifyDatasetCat(startIndexRR,endTmp, rs.redescriptions,appset.outFolderPath+"\\Jinputnew.arff",fid,appset);
             else
                dsc.modifyDatasetCat(startIndexRR,endTmp, rs.redescriptions,appset.outFolderPath+"/Jinputnew.arff",fid,appset); 
         }
        }
        catch(IOException e){
            e.printStackTrace();
        }
             
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath, "view"+(nws+1)+"tmp.s"/*"wbtmp.s"*/, 0,appset.clusteringMemory);//was 1 for rules before
             
             if(appset.numSupplementTrees>0)
                exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath,"view"+(nws+1)+"tmpF.s", 0,appset.clusteringMemory);
             
             System.out.println("Process 1 side "+nws+" finished!");
             
             String input, inputF="";
             if(appset.system.equals("windows")){ 
              input=appset.outFolderPath+"\\view"+(nws+1)+"tmp.out";
               inputF=appset.outFolderPath+"\\view"+(nws+1)+"tmpF.out";
             }
             else{
                 input=appset.outFolderPath+"/view"+(nws+1)+"tmp.out";
                 inputF=appset.outFolderPath+"/view"+(nws+1)+"tmpF.out";
             }
             
              int newRules=0;
              RuleReader ItRules=new RuleReader();
              RuleReader ItRulesF=new RuleReader();
              
              if(appset.generatingModelType<=1)
                    ItRules.extractRules(input,fid,datJ,appset,0);
              else   ItRules.extractRules(input,fid,datJ,appset,1);
        ItRules.setSize();
        
         if(appset.numSupplementTrees>0){
             if(appset.SupplementPredictiveTreeType == 0)
                ItRulesF.extractRules(inputF,fid,datJ,appset,0);
             else 
                  ItRulesF.extractRules(inputF,fid,datJ,appset,1);
                ItRulesF.setSize();
            }
        
            if(z==0)
                newRules=ruleReaders.get(nws).addnewRulesC(ItRules, appset.numnewRAttr,1);
            else
                newRules=ruleReaders.get(nws).addnewRulesC(ItRules, appset.numnewRAttr,0);
            
             if(appset.numSupplementTrees>0)
                ruleReaders.get(nws).addnewRulesCF(ItRulesF, appset.numnewRAttr); 
            
           }
        
        if(numBins == 0)
            continue;

        if(appset.useJoin){//do redescription construction
            ruleReaders.get(nws).removeDuplicates(ruleReaders.get(nws).newRuleIndex);
            rs.combineViewRulesJoin(ruleReaders.get(nws), jsN, appset, oldIndW, RunInd, oom, fid, datJ, oldRIndex ,nws, appset.maxRSSize); 
            
            if(appset.numSupplementTrees>0){
                ruleReaders.get(nws).removeRulesCF();
            }
        
        }
        else{//(rr, rr1, jsN, appset, oldIndexRR, oldIndexRR1, RunInd, oom,fid,datJ);
            rs.combineViewRules(ruleReaders.get(nws), jsN, appset, oldIndW, RunInd, oom, fid, datJ, oldRIndex ,nws);
            
            if(appset.numSupplementTrees>0){
                ruleReaders.get(nws).removeRulesCF();
            }
        }
           
        //rs.combineViewRules(readers.get(nws-2), jsN, appset, oldIndW, RunInd, oom, fid, datJ, oldRIndex ,nws);
        }
        
        if(leftSide==1){
            leftSide=0;
            rightSide=1;
        }
        else if(rightSide==1){
            rightSide=0;
            leftSide=1;
        }
        if(leftSide1==1){
            leftSide1=0;
            rightSide1=1;
        }
        else if(rightSide1==1){
            rightSide1=0;
            leftSide1=1;
        }
        RunInd++;
        System.out.println("Running index: "+RunInd);
        
        
         if(rs.redescriptions.size()>appset.workRSSize){
               
                System.out.println("Rs size after reduction WS1: "+rs.redescriptions.size());
               
               if(rs.redescriptions.size()>((appset.workRSSize+appset.maxRSSize)/2)){
                   
                                tmpPr=rs.redescriptions.size();
           // rs.removeIncomplete();
              
               int numW = datJ.W2indexs.size()+1;
                                
               for(int nwkk=1;nwkk<numW;nwkk++){          
                  rs.removeIncomplete(nwkk);
                  if(rs.redescriptions.size()<=((appset.workRSSize+appset.maxRSSize)/2))
                         break;
                  
           }                  
                                
              tmpPr1=rs.redescriptions.size();
               countNumProduced+=tmpPr-tmpPr1;
               
                System.out.println("Rs size after reduction WS: "+rs.redescriptions.size());

               if(rs.redescriptions.size()>appset.workRSSize){
                   rs.computePVal(datJ,fid);
                   rs.removePVal(appset);
               }
               
                if(rs.redescriptions.size()<=((appset.workRSSize+appset.maxRSSize)/2))
                    continue;
                   
                    RedescriptionSet ResultsAll = new RedescriptionSet();
                 CoocurenceMatrix coc=null;
         
         if(datJ.numExamples<10000 && datJ.schema.getNbAttributes()-1<10000){
                coc=new CoocurenceMatrix(datJ.numExamples,datJ.schema.getNbAttributes()-1);
                coc.computeMatrix(rs, datJ); 
         }
         
               HashSet<Redescription> t = new HashSet<>();
           
        if(appset.parameters.size()==0){
            ArrayList<Double> par=new ArrayList<>();
          par.add(appset.minJS); par.add((double)appset.minSupport);  par.add((double)appset.missingValueJSType);
          System.out.println("Configuring the default parameters...");
          appset.parameters.add(par);
        }       
               
       for(int z=0;z<appset.parameters.size();z++){  
                 RedescriptionSet Result=new RedescriptionSet();

      ArrayList<RedescriptionSet> resSets=null; 

      if(datJ.numExamples<10000 && datJ.schema.getNbAttributes()-1<10000)
            resSets=Result.createRedescriptionSetsCoocGen(rs,appset.preferences,appset.parameters.get(z).get(2).intValue(), appset,datJ,fid,coc);//adds the most specific redescription first
      else
          resSets=Result.createRedescriptionSetsRandGen(rs,appset.preferences,appset.parameters.get(z).get(2).intValue(), appset,datJ,fid,coc);//should add one highly accurate redescription at random
      
          for(int k=0;k<resSets.size();k++){
              t.addAll(resSets.get(k).redescriptions);
          }
       }
       
       ResultsAll.redescriptions.addAll(t);
      rs.redescriptions.clear();
      System.out.println("t: "+t.size());
      System.out.println("ResultsAll: "+ResultsAll.redescriptions.size());
      rs.redescriptions.addAll(t);
               }
               System.out.println("Rs size after reduction1 WS: "+rs.redescriptions.size());
        }
           
     }

        int numFull = 0;
        
          for(int kk=0;kk<rs.redescriptions.size();kk++)
                    if(rs.redescriptions.get(kk).viewsUsed().size() == (datJ.W2indexs.size()+1))
                        numFull++;
                
                System.out.println("Num full reds: "+numFull);
        
        if(rs.redescriptions.size()>appset.workRSSize){
             tmpPr=rs.redescriptions.size();
           // rs.removeIncomplete();
             
             int numW = datJ.W2indexs.size()+1;
             
              for(int nwkk=1;nwkk<numW;nwkk++){          
                  rs.removeIncomplete(nwkk);
                  if(rs.redescriptions.size()<=((appset.workRSSize+appset.maxRSSize)/2))
                   break;
                  
           }
             
              tmpPr1=rs.redescriptions.size();
               countNumProduced+=tmpPr-tmpPr1;
               
                System.out.println("Rs size after reduction WS: "+rs.redescriptions.size());

               if(rs.redescriptions.size()>appset.workRSSize){
                   rs.computePVal(datJ,fid);
                   rs.removePVal(appset);
               }
               
                System.out.println("Rs size after reduction WS1: "+rs.redescriptions.size());
               
               if(rs.redescriptions.size()>((appset.workRSSize+appset.maxRSSize)/2)){
                    RedescriptionSet ResultsAll = new RedescriptionSet();
                 CoocurenceMatrix coc=null;
         
         if(datJ.numExamples<10000 && datJ.schema.getNbAttributes()-1<10000){
                coc=new CoocurenceMatrix(datJ.numExamples,datJ.schema.getNbAttributes()-1);
                coc.computeMatrix(rs, datJ); 
         }
         
               HashSet<Redescription> t = new HashSet<>();
           
        if(appset.parameters.size()==0){
            ArrayList<Double> par=new ArrayList<>();
          par.add(appset.minJS); par.add((double)appset.minSupport);  par.add((double)appset.missingValueJSType);
          System.out.println("Configuring the default parameters...");
          appset.parameters.add(par);
        }       
               
       for(int z=0;z<appset.parameters.size();z++){  
                 RedescriptionSet Result=new RedescriptionSet();

      ArrayList<RedescriptionSet> resSets=null; 

      if(datJ.numExamples<10000 && datJ.schema.getNbAttributes()-1<10000)
            resSets=Result.createRedescriptionSetsCoocGen(rs,appset.preferences,appset.parameters.get(z).get(2).intValue(), appset,datJ,fid,coc);//adds the most specific redescription first
      else
          resSets=Result.createRedescriptionSetsRandGen(rs,appset.preferences,appset.parameters.get(z).get(2).intValue(), appset,datJ,fid,coc);//should add one highly accurate redescription at random
      
          for(int k=0;k<resSets.size();k++){
              t.addAll(resSets.get(k).redescriptions);
          }
       }
       
       ResultsAll.redescriptions.addAll(t);
      rs.redescriptions.clear();
      System.out.println("t: "+t.size());
      System.out.println("ResultsAll: "+ResultsAll.redescriptions.size());
      rs.redescriptions.addAll(t);
               }
               System.out.println("Rs size after reduction1 WS: "+rs.redescriptions.size());
        }
         }//view1
       }//view2
      }//end functions
       
//        rs.writeToFile(appset.outFolderPath+"\\"+appset.outputName+(1)+"intermid"+".rr", datJ, fid, startTime,0,appset, 0,new double[1],oom);
        
        System.out.println("Redescription size main: "+rs.redescriptions.size());
        
        //removing all redescriptions with inadequate minSupport and minJS
        rs.remove(appset);
         
        System.out.println("Redescription size main after remove: "+rs.redescriptions.size());
        
        //filtering
      // rs.filter(appset, rr, rr1,fid,datJ); // think about what we want and if we need it
        
        System.out.println("Redescription size main after filter: "+rs.redescriptions.size());
       
      int numFullRed=0;
        //computing pVal...
     
      rs.adaptSet(datJ, fid, 0);
        numFullRed=rs.computePVal(datJ,fid);
        rs.removePVal(appset);
       
         System.out.println("Redescription size main after Pvalremove: "+rs.redescriptions.size());
        
        System.out.println("Found "+numFullRed+" redescriptions with JS=1.0 and minsupport>"+appset.minSupport);
        System.out.println("Found "+rs.redescriptions.size()+" redescriptions with JS>"+appset.minJS);
        
        rs.removeIncomplete();
        
        System.out.println("Number of full redescriptions: "+rs.redescriptions.size());
        System.out.println("Number of discarded incomplete redescriptions: "+countNumProduced);
        
         int minimize=0;
         if(appset.minimizeRules==true)
             minimize=1;
        
         rs.adaptSet(datJ, fid,0);
          System.out.println(rs.redescriptions.size()+" redescriptions with JS>"+appset.minJS+" left after elimination of long rules");
        //further reduce before computing optimized set
        
        //sorting redescriptions
        System.out.println("Sorting rules!");
        rs.sortRedescriptions();

         for(int i=0;i<rs.redescriptions.size();i++){
             rs.redescriptions.get(i).validate(datJ, fid);
         }
         
          if(appset.useSplitTesting==true)
              for(int i=0;i<rs.redescriptions.size();i++){
                     rs.redescriptions.get(i).ComputeValidationStatistics(datJ,datJFull, fid);
                     rs.redescriptions.get(i).ComputeTestStatistics(datJ,datJTest, fid);
              }
         
         
         for(int i=0;i<rs.redescriptions.size();i++)
             rs.redescriptions.get(i).clearRuleMaps();
         
        if(appset.attributeImportance==0) 
         rs.adaptSet(datJ, fid,minimize);
        else
            rs.adaptSet(datJ, fid, 0);   

         rs.writeToFileTmp("AllContained.rr", datJ, fid, appset);
       System.out.println("Computing rule score and sorting!");
      RedescriptionSet Result=rs;//new RedescriptionSet();
      
      if(appset.optimizationType==0){
      
      double sumN=0.0;
      
      double heuristicWeights[]=appset.preferences.get(0);
      double coverage[]=new double[2];
      double ResultsScore=Result.computeRedescriptionSetScore(heuristicWeights,coverage,datJ,fid);
      System.out.println("Results score: "+ResultsScore);
      
     if(appset.system.equals("windows")){ 
      Result.writeToFile(appset.outFolderPath+"\\"+appset.outputName+(1)+".rr", datJ, fid, startTime,numFullRed,appset, ResultsScore, coverage,oom);
      Result.writePlots(appset.outFolderPath+"\\"+"RuleData"+(1)+".csv", appset,datJ,fid);
     }
     else{
       Result.writeToFile(appset.outFolderPath+"/"+appset.outputName+(1)+".rr", datJ, fid, startTime,numFullRed,appset, ResultsScore, coverage,oom); 
        Result.writePlots(appset.outFolderPath+"/"+"RuleData"+(1)+".csv", appset,datJ,fid);
     }
      }
      else{
          double coverage[];
           rs.computeAllMeasureFS(datJ, appset, fid);
         
      double ResultsScore=0.0;
      
         CoocurenceMatrix coc=null;
         
         if(datJ.numExamples<10000 && datJ.schema.getNbAttributes()-1<10000){
                coc=new CoocurenceMatrix(datJ.numExamples,datJ.schema.getNbAttributes()-1);
                coc.computeMatrix(rs, datJ); 
         }
         
       Result=new RedescriptionSet();
      
      double sumN=0.0;

      if(appset.parameters.size()==0 && appset.exhaustiveTesting==0){
          ArrayList<Double> par=new ArrayList<>();
          par.add(appset.minJS); par.add((double)appset.minSupport);  par.add((double)appset.missingValueJSType);
          System.out.println("Configuring the default parameters...");
          appset.parameters.add(par);
      }
     
      if(appset.exhaustiveTesting==0){
      for(int i=0;i<appset.parameters.size();i++){
          appset.minJS=appset.parameters.get(i).get(0);
          appset.minSupport= appset.parameters.get(i).get(1).intValue();
           Result=new RedescriptionSet();

      ArrayList<RedescriptionSet> resSets=null; 
      if(datJ.numExamples<10000 && datJ.schema.getNbAttributes()-1<10000)
            resSets=Result.createRedescriptionSetsCoocGen(rs,appset.preferences,appset.parameters.get(i).get(2).intValue(), appset,datJ,fid,coc);//adds the most specific redescription first
      else
          resSets=Result.createRedescriptionSetsRandGen(rs,appset.preferences,appset.parameters.get(i).get(2).intValue(), appset,datJ,fid,coc);//should add one highly accurate redescription at random
      
      if(resSets==null){//perhaps create a null file
          break;
      }

      for(int rset=0;rset<resSets.size();rset++)
            resSets.get(rset).computeLift(datJ, fid);
  
      System.out.println("exhaustiveTesting = 0");
      System.out.println("RS size: "+resSets.size());
      
     for(int fit=0;fit<resSets.size();fit++){
       coverage=new double[2];

      ResultsScore=resSets.get(fit).computeRedescriptionSetScoreGen(appset.preferences.get(fit),appset.parameters.get(i).get(2).intValue(),coverage,datJ,appset,fid);
      System.out.println("Results score: "+ResultsScore);
      numFullRed=resSets.get(fit).computePVal(datJ,fid);

      if(appset.system.equals("windows"))
         resSets.get(fit).writeToFile(appset.outFolderPath+"\\"+appset.outputName+"StLev_"+fit+" minjs "+appset.minJS+" JSType "+appset.parameters.get(i).get(2).intValue()+".rr", datJ, fid, startTime,numFullRed,appset, ResultsScore, coverage,oom);
      else
        resSets.get(fit).writeToFile(appset.outFolderPath+"/"+appset.outputName+"StLev_"+fit+" minjs "+appset.minJS+" JSType "+appset.parameters.get(i).get(2).intValue()+".rr", datJ, fid, startTime,numFullRed,appset, ResultsScore, coverage,oom);  
      
      if(appset.system.equals("windows"))
            resSets.get(fit).writePlots(appset.outFolderPath+"\\"+"RuleData"+"StLev_"+fit+" minjs "+appset.minJS+"JSType "+appset.parameters.get(i).get(2).intValue()+".csv", appset,datJ,fid);
      else  resSets.get(fit).writePlots(appset.outFolderPath+"/"+"RuleData"+"StLev_"+fit+" minjs "+appset.minJS+"JSType "+appset.parameters.get(i).get(2).intValue()+".csv", appset,datJ,fid);
     }
      }
   }
      else if(appset.exhaustiveTesting==1){
          System.out.println("type of experimentation: "+appset.exhaustiveTesting);
          for(int type=appset.parameters.get(2).get(0).intValue();type<=appset.parameters.get(2).get(1).intValue();type++){
              for(double minjs=appset.parameters.get(0).get(0);minjs<=appset.parameters.get(0).get(1);minjs+=appset.parameters.get(0).get(2)){
                  for(int minSupp=appset.parameters.get(1).get(0).intValue();minSupp<=appset.parameters.get(1).get(1).intValue();minSupp+=appset.parameters.get(1).get(2).intValue()){
                       appset.minJS=minjs;
          appset.minSupport= minSupp;
           Result=new RedescriptionSet();
           
           ArrayList<RedescriptionSet> resSets = null;
           if(datJ.numExamples<10000 && datJ.schema.getNbAttributes()-1<10000)
            resSets=Result.createRedescriptionSetsCoocGen(rs,appset.preferences,type, appset,datJ,fid,coc);//adds the most specific redescription first
      else
          resSets=Result.createRedescriptionSetsRandGen(rs,appset.preferences,type, appset,datJ,fid,coc);//should add one highly accurate redescription at random
      

      for(int rset=0;rset<resSets.size();rset++)
            resSets.get(rset).computeLift(datJ, fid);
  
     for(int fit=0;fit<resSets.size();fit++){
       coverage=new double[2];

      ResultsScore=resSets.get(fit).computeRedescriptionSetScoreGen(appset.preferences.get(fit),type,coverage,datJ,appset,fid);
      numFullRed=resSets.get(fit).computePVal(datJ,fid);
      System.out.println("Results score: "+ResultsScore);

      if(appset.system.equals("windows"))
        resSets.get(fit).writeToFile(appset.outFolderPath+"\\"+appset.outputName+"StLev_"+fit+" minjs "+appset.minJS+" JSType "+type+"minSupp "+appset.minSupport+".rr", datJ, fid, startTime,numFullRed,appset, ResultsScore, coverage,oom);
      else
          resSets.get(fit).writeToFile(appset.outFolderPath+"/"+appset.outputName+"StLev_"+fit+" minjs "+appset.minJS+" JSType "+type+"minSupp "+appset.minSupport+".rr", datJ, fid, startTime,numFullRed,appset, ResultsScore, coverage,oom);
     }
                  }
              }
          }
          
      }
     
      }
      
      Result.redescriptions.clear();
      rs.redescriptions.clear();
      
      for(int i=0;i<ruleReaders.size();i++)
          ruleReaders.get(i).rules.clear();

      FileDeleter del=new FileDeleter();
     if(appset.system.equals("windows")){  
      del.setPath(appset.outFolderPath+"\\Jinputnew.arff");
      del.delete();
      del.setPath(appset.outFolderPath+"\\Jinputnew1.arff");
      del.delete();
      del.setPath(appset.outFolderPath+"\\Jinput.arff");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmp.s");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmp.s");
      del.delete();
       del.setPath(appset.outFolderPath+"\\view3tmp.s");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmp1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmp1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmp.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmp.model");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmp.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmp.model");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmp1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmp1.model");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmp1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmp1.model");
      del.delete();
      ///
      del.setPath(appset.outFolderPath+"\\view1tmpF1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmpF1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmpF.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmpF.model");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmpF.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmpF.model");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmpF1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1tmpF1.model");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmpF1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2tmpF1.model");
      del.delete();
      ///
      del.setPath(appset.outFolderPath+"\\view3tmp.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view3tmp.model");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view1.model");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2.s");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2.out");
      del.delete();
      del.setPath(appset.outFolderPath+"\\view2.model");
      del.delete();
     }
     else{
       del.setPath(appset.outFolderPath+"/Jinputnew.arff");
      del.delete();
      del.setPath(appset.outFolderPath+"/Jinputnew1.arff");
      del.delete();
      del.setPath(appset.outFolderPath+"/Jinput.arff");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmp.s");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmp.s");
      del.delete();
       del.setPath(appset.outFolderPath+"/view3tmp.s");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmp1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmp1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmp.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmp.model");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmp.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmp.model");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmp1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmp1.model");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmp1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmp1.model");
      del.delete();
      ///
      del.setPath(appset.outFolderPath+"/view1tmpF1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmpF1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmpF.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmpF.model");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmpF.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmpF.model");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmpF1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1tmpF1.model");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmpF1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2tmpF1.model");
      del.delete();
      ///
      del.setPath(appset.outFolderPath+"/view3tmp.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view3tmp.model");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1.s");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view1.model");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2.s");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2.out");
      del.delete();
      del.setPath(appset.outFolderPath+"/view2.model");
      del.delete();  
      
      for(int i=2;i<=(datJ.W2indexs.size()+1);i++){
          del.setPath(appset.outFolderPath+"/view"+(i+1)+".model");
          del.delete();
           del.setPath(appset.outFolderPath+"/view"+(i+1)+".out");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+".s");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmp.model");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmp.out");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmp.s");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmp1.model");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmp1.out");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmp1.s");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmpF1.model");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmpF1.out");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmpF1.s");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmpF.model");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmpF.out");
          del.delete();
          del.setPath(appset.outFolderPath+"/view"+(i+1)+"tmpF.s");
          del.delete();
      }
      
       FileDeleter delTmp=new FileDeleter();
           if(appset.system.equals("windows"))
                delTmp.setPath(appset.outFolderPath+"\\JinputInitial.arff");
           else
               delTmp.setPath(appset.outFolderPath+"/JinputInitial.arff");
           delTmp.delete();
     }
    
 }
}