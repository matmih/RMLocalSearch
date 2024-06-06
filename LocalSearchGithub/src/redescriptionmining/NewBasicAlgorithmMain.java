/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redescriptionmining;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static redescriptionmining.DataSetCreator.writeArff;

/**
 *
 * @author matej
 */
public class NewBasicAlgorithmMain {
   


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

        
        if(appset.attributeImportance!=0){
            System.out.println("Information about targeted attributes: ");
            System.out.println(appset.attributeImportance);
            for(int i=0;i<appset.importantAttributes.size();i++)
                System.out.print(appset.importantAttributes.get(i)+" ");
            System.out.println();
        }
        
        Mappings fid=new Mappings();
        Mappings fidFull=new Mappings();
       /* ArrayList<Integer> splitIndex=new ArrayList<>();
        splitIndex.add(312);
        dat.splitDataset(splitIndex, outFolderPath);

          int testEnd=1;
        if(testEnd==1)
            return;*/

       /* int testEnd=1;

        long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
        .getOperatingSystemMXBean()).getTotalPhysicalMemorySize()/(1024*1024);
        long maxMemory = Runtime.getRuntime().maxMemory()/(1024*1024);
        long freemem=Runtime.getRuntime().freeMemory()/(1024*1024);
        long totalMemory = Runtime.getRuntime().totalMemory()/(1024*1024);
        System.out.println("Memory available to JVM: "+maxMemory);
        System.out.println("RAM memory size: "+memorySize);
        System.out.println("Free memory: "+freemem);
        System.out.println("Total memory: "+totalMemory);
        if(testEnd==1)
            return;*/

        //create a static dataset
        DataSetCreator datJ=new DataSetCreator(appset.viewInputPaths, appset.outFolderPath,appset);
        DataSetCreator datJFull=null;//new DataSetCreator(appset.viewInputPaths, appset.outFolderPath,appset);
        
        //DataSetCreator datJ=new DataSetCreator(appset.viewInputPaths.get(0)/*appset.outFolderPath+"\\input1.arff"*/,appset.viewInputPaths.get(1)/*appset.outFolderPath+"\\input2.arff"*/ , appset.outFolderPath);
        
            fid.createIndex(appset.outFolderPath+"\\Jinput.arff");
            
        if(appset.useSplitTesting==true)
            fidFull.createIndex(appset.outFolderPath+"\\Jinput.arff");
        //fid.printMapping();
        NHMCDistanceMatrix nclMatInit=null;
        if(appset.distanceFilePaths.size()>0){
            nclMatInit=new NHMCDistanceMatrix(datJ.numExamples,appset);
            nclMatInit.loadDistance(new File(appset.distanceFilePaths.get(0)), fid);
             nclMatInit.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
             nclMatInit.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
             nclMatInit=null;
        }
        //fid.printMapping();
        //make initial cluster analysis
        
                
        if(appset.useSplitTesting==true){
            ArrayList<DataSetCreator> rDat = new ArrayList<>();
            datJFull=datJ;
            rDat=datJ.createSplit(appset.percentageForTrain);
            datJ = rDat.get(0);
        
        
         try{
                 writeArff(appset.outFolderPath+"\\JinputTrain.arff", datJ.data);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }
        
         fid.clearMaps();
         fid.createIndex(appset.outFolderPath+"\\JinputTrain.arff");
          System.out.println("Full: "+datJFull.numExamples);
        System.out.println("Part: "+datJ.numExamples);
        }
        //datJ.
        
       
        
        /*int test=1;
        if(test==1)
            return;*/
        
        System.out.println("WIndexes size: "+datJ.W2indexs.size());
        
        DataSetCreator datJInit=null;
        
        if(!appset.useSplitTesting==true)
                datJInit=new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
        else
            datJInit=new DataSetCreator(appset.outFolderPath+"\\JinputTrain.arff");
        
                try{
        datJInit.readDataset();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        datJInit.W2indexs.addAll(datJ.W2indexs);
        datJInit.initialClusteringGen(appset.outFolderPath,appset, datJ.schema.getNbDescriptiveAttributes());
        //datJInit.initialClustering(appset.outFolderPath,appset);
        //datJInit.initialClusteringCategorical(appset.outFolderPath);
        
        SettingsReader initSettings=new SettingsReader();
        initSettings.setDataFilePath(appset.outFolderPath+"\\JinputInitial.arff");
        initSettings.setPath(appset.outFolderPath+"\\view1.s");
        //initSettings.createInitialSettings1(1, datJ.W2indexs.get(0), datJInit.schema.getNbAttributes(), appset);
        System.out.println("distance file size: "+appset.distanceFilePaths.size()+"");
        if(appset.useNC.size()==0)
             initSettings.createInitialSettingsGen(0, 3, datJ.W2indexs.get(0), datJ.schema.getNbAttributes(), appset,1);
        else
             initSettings.createInitialSettingsGen(0, 4, datJ.W2indexs.get(0), datJ.schema.getNbAttributes(), appset,1);
            
        ClusProcessExecutor exec=new ClusProcessExecutor();

        //RunInitW1S1
        exec.run(appset.javaPath,appset.clusPath ,appset.outFolderPath,"view1.s",0, appset.clusteringMemory);//was 1 before for rules
        System.out.println("Process 1 side 1 finished!");
         
        //read the rules obtained from first attribute set
         // String input1="C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctad.out";
          String input1=appset.outFolderPath+"\\view1.out";
           RuleReader rr1=new RuleReader();
           rr1.extractRules(input1,fid,datJInit,appset,0);
           
         //reading arff file
         
           if(appset.distanceFilePaths.size()>1){
            nclMatInit=new NHMCDistanceMatrix(datJ.numExamples,appset);
            nclMatInit.loadDistance(new File(appset.distanceFilePaths.get(1)), fid);
             nclMatInit.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
             nclMatInit.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
             nclMatInit=null;
        }
       
        SettingsReader set=null;
        SettingsReader set1=null;
       
        //RunInitW1S2
        initSettings.setPath(appset.outFolderPath+"\\view2.s");
        //initSettings.createInitialSettings1(2, datJ.W2indexs.get(0), datJInit.schema.getNbAttributes(), appset);
        if(datJ.W2indexs.size()>1)
            initSettings.createInitialSettingsGen(1, datJ.W2indexs.get(0)+1, datJ.W2indexs.get(1), datJ.schema.getNbAttributes(), appset,1);
        else
            initSettings.createInitialSettingsGen(1, datJ.W2indexs.get(0)+1, datJInit.schema.getNbAttributes(), datJ.schema.getNbAttributes(), appset,1);
        
        exec.run(appset.javaPath,appset.clusPath, appset.outFolderPath, "view2.s", 0,appset.clusteringMemory);//was 1 before
        System.out.println("Process 1 side 2 finished!");

        //read the rules obtained from first attribute set
        RuleReader rr=new RuleReader();
        input1=appset.outFolderPath+"\\view2.out";
        rr.extractRules(input1,fid,datJInit,appset,0);
           
           FileDeleter delTmp=new FileDeleter();
           delTmp.setPath(appset.outFolderPath+"\\JinputInitial.arff");
           delTmp.delete();
        
           initSettings.setPath(appset.outFolderPath+"\\view1.s");
          // initSettings.createInitialSettings(1, datJ.W2indexs.get(0), datJ.schema.getNbAttributes(), appset);
           //initSettings.createInitialSettingsGen(0, 3, datJ.W2indexs.get(0), datJ.schema.getNbAttributes(), appset);
           initSettings.setPath(appset.outFolderPath+"\\view2.s");
          // initSettings.createInitialSettings(2, datJ.W2indexs.get(0), datJ.schema.getNbAttributes(), appset);
          // initSettings.createInitialSettingsGen(0, datJ.W2indexs.get(0)+1, datJ.W2indexs.get(1), datJ.schema.getNbAttributes(), appset);
         
           datJInit=null;        
           
        int leftSide=1, rightSide=0;//set left to 1 when computing lf, otherwise right
        int leftSide1=0, rightSide1=1; //left, right side for Side 2
        int it=0;
        Jacard js=new Jacard();
        Jacard jsN[]=new Jacard[3];
        
        for(int i=0;i<jsN.length;i++)
            jsN[i]=new Jacard();
            
        //ArrayList<Redescription> redescriptions=new ArrayList<Redescription>();
        RedescriptionSet rs=new RedescriptionSet();
        int newRedescriptions=1;
        int numIter=0;
        int RunInd=0;
        boolean oom[]= new boolean[1];
        int naex=datJ.numExamples;
        
        //add arrayList of view rules
        ArrayList<RuleReader> readers=new ArrayList<>();
        int oldRIndex[]={0};
        
        NHMCDistanceMatrix nclMat=null;
        if((appset.distanceFilePaths.size()>0 || appset.useNC.get(0)==true) && appset.networkInit==false)
            nclMat=new NHMCDistanceMatrix(datJ.numExamples,appset);
        NHMCDistanceMatrix nclMat1=null;
        if((appset.distanceFilePaths.size()>1 || appset.useNC.get(1)==true) && appset.networkInit==false)
            nclMat1=new NHMCDistanceMatrix(datJ.numExamples,appset);
       
        if(appset.useNetworkAsBackground==true)
            appset.networkInit=false;
        
        if(appset.useNC.size()>=2){
         initSettings.setPath(appset.outFolderPath+"\\view2.s");
         if(appset.useNC.size()>2)
            initSettings.createInitialSettingsGenN(1, datJ.W2indexs.get(0)+1, datJ.W2indexs.get(1), datJ.schema.getNbAttributes(), appset);
         else
            initSettings.createInitialSettingsGenN(1, datJ.W2indexs.get(0)+1, datJ.schema.getNbAttributes()+1, datJ.schema.getNbAttributes(), appset); 
        }
        if(appset.useNC.size()>1){
         initSettings.setPath(appset.outFolderPath+"\\view1.s");
         initSettings.createInitialSettingsGenN(1, 4, datJ.W2indexs.get(0), datJ.schema.getNbAttributes(), appset);
        }
        
        /*if(appset.networkInit==true){
            appset.useNC.set(0, false);
            datJ=new DataSetCreator(appset.viewInputPaths, appset.outFolderPath,appset);
            appset.useNC.set(0, true);
        }*/
        
        int elemFreq[]=new int[datJ.numExamples];
        int attrFreq[]=new int[datJ.schema.getNbAttributes()];
        ArrayList<Double> redScores=new ArrayList<>(appset.numRetRed);
        ArrayList<Double> redScoresAtt=new ArrayList<>(appset.numRetRed);
        ArrayList<Double> targetAtScore=null;
        ArrayList<Double> redDistCoverage=null;
        ArrayList<Double> redDistCoverageAt=null;
        ArrayList<Double> redDistNetwork=null;
         double Statistics[]={0.0,0.0,0.0};//previousMedian - 0, numberIterationsStable - 1, minDifference - 2
         ArrayList<Double> maxDiffScoreDistribution = null;
                
             if(appset.attributeImportance!=0)
                   targetAtScore = new ArrayList<>(appset.numRetRed);
        
        for(int z=0;z<appset.numRetRed;z++){
            redScores.add(Double.NaN);
            redScoresAtt.add(Double.NaN);
        }
        
        while(newRedescriptions!=0 && RunInd<appset.numIterations){
            
       DataSetCreator dsc=null;//new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
       DataSetCreator dsc1=null;//new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
       
       rr.setSize();
       rr1.setSize();
       
       //read dataset for cicle 1
        /*try{
        dsc.readDataset();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        //read dataset for cicle 2
        try{
        dsc1.readDataset();
        }
        catch(IOException e){
            e.printStackTrace();
        }*/

       //dsc.data.getNbRows();
       int nARules=0, nARules1=0;
       int oldIndexRR=rr.newRuleIndex;
       int oldIndexRR1=rr1.newRuleIndex;
       int endIndexRR=0, endIndexRR1=0;
             newRedescriptions=0;
            System.out.println("Iteration: "+(++numIter));

             //do rule creation with various generality levels
        double percentage[]=new double[]{0,0.2,0.4,0.6,0.8,1.0};

        for(int z=0;z<1;z++){//percentage.length-1;z++){

            nARules=0; nARules1=0;
            double startPerc=0;//percentage[z];
            double endPerc=1;//percentage[z+1];
            int minCovElements[]=new int[]{0}, minCovElements1[]=new int[]{0};
            int maxCovElements[]=new int[]{0}, maxCovElements1[]=new int[]{0};

            System.out.println("startPerc: "+startPerc);
            System.out.println("endPerc: "+endPerc);

            int cuttof=0,cuttof1=0;

            if(z==0){
                endIndexRR=rr.rules.size();
                endIndexRR1=rr1.rules.size();
               
                //compute network things only here
                if(appset.useNC.get(1)==true && appset.networkInit==false && appset.useNetworkAsBackground==false){
                    nclMat.reset(appset);
                    nclMat1.reset(appset);
                if(leftSide==1){
                    if(appset.distanceFilePaths.size()>=1){
                             nclMat.loadDistance(new File(appset.distanceFilePaths.get(1)), fid);
                             //nclMat.writeToFile(new File(appset.outFolderPath+"\\distance.csv"), fid);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat.computeDistanceMatrix(rr1, fid, appset.maxDistance, datJ.numExamples);
                     }
                   }
                }
                 if(appset.useNC.get(0)==true && appset.networkInit==false && appset.useNetworkAsBackground==false){
                 if(rightSide==1){
                    if(appset.distanceFilePaths.size()>=2){
                             nclMat.loadDistance(new File(appset.distanceFilePaths.get(0)), fid);
                            // nclMat.writeToFile(new File(appset.outFolderPath+"\\distance.csv"), fid);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat.computeDistanceMatrix(rr, fid, appset.maxDistance, datJ.numExamples);
                     }
                   }
                 }
                  if(appset.useNC.get(1)==true && appset.networkInit==false && appset.useNetworkAsBackground==false){
                if(leftSide1==1){
                    if(appset.distanceFilePaths.size()>=1){
                             nclMat1.loadDistance(new File(appset.distanceFilePaths.get(1)), fid);
                            // nclMat1.writeToFile(new File(appset.outFolderPath+"\\distance.csv"), fid);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat1.computeDistanceMatrix(rr1, fid, appset.maxDistance, datJ.numExamples);
                     }
                }
                  }
                   if(appset.useNC.get(0)==true && appset.networkInit==false && appset.useNetworkAsBackground==false){
                    if(rightSide1==1){
                    if(appset.distanceFilePaths.size()>=2){
                             nclMat1.loadDistance(new File(appset.distanceFilePaths.get(0)), fid);
                            // nclMat1.writeToFile(new File(appset.outFolderPath+"\\distance.csv"), fid);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat1.computeDistanceMatrix(rr, fid, appset.maxDistance, datJ.numExamples);
                     }
                  }
                }
            }
            
            System.out.println("Computing cuttof");
           // cuttof1=rr1.findCutoff(naex, startPerc, endPerc, oldIndexRR1, endIndexRR1, minCovElements1,maxCovElements1, appset.minSupport,appset.maxSupport,appset.numTargets);
            //cuttof=rr.findCutoff(naex, startPerc, endPerc, oldIndexRR, endIndexRR, minCovElements,maxCovElements, appset.minSupport,appset.maxSupport,appset.numTargets);
   
           // System.out.println("Cuttof: "+minCovElements[0]);
           // System.out.println("Cuttof1: "+minCovElements1[0]);

            if(cuttof==-1 || cuttof1==-1){
                //System.out.println("Rule count: 0");
               // return;
                continue;
            }
            
          if(!appset.useSplitTesting==true){ 
             dsc=new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
             dsc1=new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
          }
          else{
             dsc=new DataSetCreator(appset.outFolderPath+"\\JinputTrain.arff");
             dsc1=new DataSetCreator(appset.outFolderPath+"\\JinputTrain.arff");
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
          if(leftSide==1){
              //createSettings
             set=new SettingsReader(appset.outFolderPath+"\\view2tmp.s"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\wbtmp.s"*/,appset.outFolderPath+"\\view2.s"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\wbtmpS.s"*/);
             set.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew.arff"*/);
             /*if(z!=0)
                 set.changeSeed();*/
       //modify settings
             for(int i=oldIndexRR1;i<endIndexRR1;i++) //do on the fly when reading rules
                   // if(rr1.rules.get(i).elements.size()<=naex*endPerc && rr1.rules.get(i).elements.size()>=naex*startPerc && rr1.rules.get(i).elements.size()>=minCovElements1[0] && rr1.rules.get(i).elements.size()<=maxCovElements1[0]) //do parameters analysis in this step
                        nARules++;
             set.ModifySettings(nARules,dsc.schema.getNbAttributes());
          }
          else if(rightSide==1){
                 set=new SettingsReader(appset.outFolderPath+"\\view1tmp.s"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctadtmp.s"*/,appset.outFolderPath+"\\view1.s"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctadtmpS.s"*/);
                 set.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew.arff"*/);
                /* if(z!=0)
                     set.changeSeed();*/

                 for(int i=oldIndexRR;i<endIndexRR;i++) //do on the fly when reading rules
                       // if(rr.rules.get(i).elements.size()<=naex*endPerc && rr.rules.get(i).elements.size()>=naex*startPerc && rr.rules.get(i).elements.size()>=minCovElements[0] && rr.rules.get(i).elements.size()<=maxCovElements[0])
                            nARules++;
                set.ModifySettings(nARules,dsc1.schema.getNbAttributes());
          }

            //create and modify settings for cicle 2
        if(leftSide1==1){
             set1=new SettingsReader(appset.outFolderPath+"\\view2tmp1.s"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\wbtmpS1.s"*/,appset.outFolderPath+"\\view2.s"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\wbtmpS.s"*/);
             set1.setDataFilePath(appset.outFolderPath+"\\Jinputnew1.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew1.arff"*/);
            /* if(z!=0)
                 set1.changeSeed();*/

             for(int i=oldIndexRR1;i<endIndexRR1;i++)
                 // if(rr1.rules.get(i).elements.size()<=naex*endPerc && rr1.rules.get(i).elements.size()>=naex*startPerc && rr1.rules.get(i).elements.size()>=minCovElements1[0] && rr1.rules.get(i).elements.size()<=maxCovElements1[0])
                       nARules1++;
             set1.ModifySettings(nARules1,dsc.schema.getNbAttributes());
          }
          else if(rightSide1==1){

              set1=new SettingsReader(appset.outFolderPath+"\\view1tmp1.s"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctadtmpS1.s"*/,appset.outFolderPath+"\\view1.s"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctadtmpS.s"*/);
              set1.setDataFilePath(appset.outFolderPath+"\\Jinputnew1.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew1.arff"*/);
                /*if(z!=0)
                    set1.changeSeed();*/

              for(int i=oldIndexRR;i<endIndexRR;i++)
                 // if(rr.rules.get(i).elements.size()<=naex*endPerc && rr.rules.get(i).elements.size()>=naex*startPerc && rr.rules.get(i).elements.size()>=minCovElements[0] && rr.rules.get(i).elements.size()<=maxCovElements[0])
                        nARules1++;
              set1.ModifySettings(nARules1,dsc1.schema.getNbAttributes());
          }

        RuleReader ItRules=new RuleReader();
        RuleReader ItRules1=new RuleReader();

       //modify dataset for cicle 1
       if(leftSide==1){
        try{
         if(appset.treeTypes.get(1)==1/*appset.typeOfRSTrees==1*/)   
                dsc.modifyDatasetS(nARules, startPerc, endPerc, oldIndexRR1, endIndexRR1, minCovElements1[0],maxCovElements1[0], rr1,appset.outFolderPath+"\\Jinputnew.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew.arff"*/,fid);
         else if(appset.treeTypes.get(1)==0/*appset.typeOfRSTrees==0*/)
                 dsc.modifyDatasetCat(nARules, startPerc, endPerc, oldIndexRR1, endIndexRR1, minCovElements1[0],maxCovElements1[0], rr1,appset.outFolderPath+"\\Jinputnew.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew.arff"*/,fid);
        }
        catch(IOException e){
            e.printStackTrace();
        }
       }
       else if(rightSide==1){
         try{
             if(appset.treeTypes.get(0)==1/*appset.typeOfLSTrees==1*/)
                dsc.modifyDatasetS(nARules, startPerc, endPerc, oldIndexRR, endIndexRR, minCovElements[0],maxCovElements[0], rr,appset.outFolderPath+"\\Jinputnew.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew.arff"*/,fid);
             else if(appset.treeTypes.get(0)==0/*appset.typeOfLSTrees==0*/)
                dsc.modifyDatasetCat(nARules, startPerc, endPerc, oldIndexRR, endIndexRR, minCovElements[0],maxCovElements[0], rr,appset.outFolderPath+"\\Jinputnew.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew.arff"*/,fid);
        }
        catch(IOException e){
            e.printStackTrace();
        }  
       }

       //modify dataset for cicle 2
       if(leftSide1==1){
        try{
            if(appset.treeTypes.get(1)==1/*appset.typeOfRSTrees==1*/)
                dsc1.modifyDatasetS(nARules1, startPerc, endPerc, oldIndexRR1, endIndexRR1, minCovElements1[0],maxCovElements1[0], rr1,appset.outFolderPath+"\\Jinputnew1.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew1.arff"*/,fid);
            else if(appset.treeTypes.get(1)==0/*appset.typeOfRSTrees==0*/)
                dsc1.modifyDatasetCat(nARules1, startPerc, endPerc, oldIndexRR1, endIndexRR1, minCovElements1[0],maxCovElements1[0], rr1,appset.outFolderPath+"\\Jinputnew1.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew1.arff"*/,fid);
        }
        catch(IOException e){
            e.printStackTrace();
        }
       }
       else if(rightSide1==1){
         try{
             if(appset.treeTypes.get(0)==1/*appset.typeOfLSTrees==1*/)
                dsc1.modifyDatasetS(nARules1, startPerc, endPerc, oldIndexRR, endIndexRR, minCovElements[0],maxCovElements[0] ,rr,appset.outFolderPath+"\\Jinputnew1.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew1.arff"*/,fid);
             else if(appset.treeTypes.get(0)==0/*appset.typeOfRSTrees==0*/)
                dsc1.modifyDatasetCat(nARules1, startPerc, endPerc, oldIndexRR, endIndexRR, minCovElements[0],maxCovElements[0] ,rr,appset.outFolderPath+"\\Jinputnew1.arff"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\UNCTADall3testnew1.arff"*/,fid);
        }
        catch(IOException e){
            e.printStackTrace();
        }
       }
       
       dsc=null;
       dsc1=null;
      
       if((appset.useNC.get(0)==true && rightSide==1 && appset.networkInit==false && appset.useNetworkAsBackground==false) || (appset.useNC.get(1)==true && leftSide==1 && appset.networkInit==false && appset.useNetworkAsBackground==false)){ 
             nclMat.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
             nclMat.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
       }
       
        //run the second proces on new data
        // iterate until convergence (no new rules, or very small amount obtained)
         if(leftSide==1){
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath, "view2tmp.s"/*"wbtmp.s"*/, 0,appset.clusteringMemory);//was 1 for rules before
             System.out.println("Process 2 side 1 finished!");
         }
         else if(rightSide==1){
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath, "view1tmp.s"/*"unctadtmp.s"*/, 0,appset.clusteringMemory);
             System.out.println("Process 1 side 1 finished!");
         }

        if((appset.useNC.get(0)==true && rightSide1==1 && appset.networkInit==false && appset.useNetworkAsBackground==false) || (appset.useNC.get(1)==true && leftSide1==1 && appset.networkInit==false && appset.useNetworkAsBackground==false)){ 
              nclMat1.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
              nclMat1.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
        }
         
         //run the second proces for cicle 2 on new data
        // iterate until convergence (no new rules, or very small amount obtained)
         if(leftSide1==1){
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath,"view2tmp1.s" /*"wbtmpS1.s"*/, 0,appset.clusteringMemory);
             System.out.println("Process 2 side 2 finished!");
         }
         else if(rightSide1==1){
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath,"view1tmp1.s"/*"unctadtmpS1.s"*/ , 0,appset.clusteringMemory);
             System.out.println("Process 1 side 2 finished!");
         }

       //extract rules for cicle 1
        String input="";
        if(leftSide==1)
         input=appset.outFolderPath+"\\view2tmp.out"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\wbtmp.out"*/;
        else if(rightSide==1)
            input=appset.outFolderPath+"\\view1tmp.out"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctadtmp.out"*/;
        //String input1="C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctad.out";
       int newRules=0;
        ItRules.extractRules(input,fid,datJ,appset,0);
        if(appset.attributeImportance==2){
            if((leftSide==1 && appset.containsView>=2) || (rightSide==1 && appset.containsView!=2))
                 ItRules.reduceRuleSet(appset.importantAttributes, fid, appset);
        }
        ItRules.setSize();
        if(leftSide==1){
            if(z==0)
                newRules=rr.addnewRulesC(ItRules, appset.numnewRAttr,1);
            else
                newRules=rr.addnewRulesC(ItRules, appset.numnewRAttr,0);
       // rr.extractRules(input);
        }
        else if(rightSide==1){
            if(z==0)
                newRules=rr1.addnewRulesC(ItRules, appset.numnewRAttr,1);
            else
                newRules=rr1.addnewRulesC(ItRules, appset.numnewRAttr, 0);
            //rr1.extractRules(input);
        }     
         //extract rules for cicle 2
        input="";
        if(leftSide1==1)
         input=appset.outFolderPath+"\\view2tmp1.out"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\wbtmpS1.out"*/;
        else if(rightSide1==1)
            input=appset.outFolderPath+"\\view1tmp1.out"/*"C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctadtmpS1.out"*/;
        //String input1="C:\\Users\\matej\\Documents\\Redescription mining with CLUS\\unctad.out";
       int newRules1=0;
        ItRules1.extractRules(input,fid,datJ,appset,0);
        if(appset.attributeImportance==2){
            if((leftSide1==1 && appset.containsView>=2) || (rightSide1==1 && appset.containsView!=2))
                ItRules1.reduceRuleSet(appset.importantAttributes, fid, appset);
        }
        ItRules1.setSize();
        if(leftSide1==1){
            if(z==0)
                    newRules1=rr.addnewRulesC(ItRules1, appset.numnewRAttr,1);
            else
                    newRules1=rr.addnewRulesC(ItRules1, appset.numnewRAttr, 0);
       // rr.extractRules(input);
        }
        else if(rightSide1==1){
            if(z==0)
                    newRules1=rr1.addnewRulesC(ItRules1, appset.numnewRAttr,1);
            else
                    newRules1=rr1.addnewRulesC(ItRules1, appset.numnewRAttr,0);
            //rr1.extractRules(input);
        }

        System.out.println("New rules cicle 1: "+newRules);
        System.out.println("New rules cicle 2: "+newRules1);
       }
        //add the redescription creaton code
        
        if(appset.useJoin){
            newRedescriptions=rs.createGuidedJoinBasic(rr, rr1, jsN, appset, oldIndexRR, oldIndexRR1, RunInd,oom,fid,datJ, elemFreq, attrFreq, redScores,redScoresAtt,redDistCoverage,redDistCoverageAt, redDistNetwork, targetAtScore, Statistics, maxDiffScoreDistribution,nclMatInit,0);
            rr.removeElements(rr.newRuleIndex);
            rr1.removeElements(rr1.newRuleIndex);
        }
        else if(!appset.useJoin){
            newRedescriptions=rs.createGuidedNoJoinBasic(rr, rr1, jsN, appset, oldIndexRR, oldIndexRR1, RunInd,oom,fid,datJ, elemFreq, attrFreq, redScores,redScoresAtt,redDistCoverage,redDistCoverageAt, redDistNetwork, targetAtScore, Statistics, maxDiffScoreDistribution,nclMatInit,0);
            rr.removeElements(rr.newRuleIndex);
            rr1.removeElements(rr1.newRuleIndex);
        }

         it++;
        
        System.out.println("New redescriptions: "+newRedescriptions);
         
        //if more than two viewes get guided search in here for further views...
        //should be modified for redescription addition
        //new join procedures should be created 
        System.out.println("Number of viewes: "+datJ.W2indexs.size());
        for(int nws=2;nws<datJ.W2indexs.size()+1;nws++){
            if(readers.size()<(datJ.W2indexs.size()-2)+1)
            readers.add(new RuleReader());
            int oldIndW=readers.get(nws-2).newRuleIndex, endIndW=0;
            
            SettingsReader setMW=new SettingsReader();//(appset.outFolderPath+"\\view3tmp.s",appset.outFolderPath+"\\view2.s");
            setMW.setPath(appset.outFolderPath+"\\view3tmp.s");
            setMW.setStaticFilePath=appset.outFolderPath+"\\view3tmp.s";
            setMW.setDataFilePath(appset.outFolderPath+"\\Jinputnew.arff");
            if((nws-1)<(datJ.W2indexs.size()-2+1))
                setMW.createInitialSettingsGen(nws, datJ.W2indexs.get(nws-1)+1 ,datJ.W2indexs.get(nws)+1,datJ.schema.getNbAttributes() , appset,0);
            else
                setMW.createInitialSettingsGen(nws, datJ.W2indexs.get(nws-1)+1 ,datJ.schema.getNbAttributes()+1,datJ.schema.getNbAttributes() , appset,0);
            
        for(int z=0;z<1/*percentage.length-1*/;z++){
       
            if(z==0){//should create network from redescriptions!
               if(appset.useNC.size()>nws && appset.networkInit==false){ 
                if(appset.useNC.get(nws)==true && readers.get(nws-2).rules.size()>0){
                    nclMat.reset(appset);
                    if(appset.distanceFilePaths.size()>=nws && appset.networkInit==false && appset.useNetworkAsBackground==false){
                             nclMat.loadDistance(new File(appset.distanceFilePaths.get(nws)), fid);
                             nclMat.writeToFile(new File(appset.outFolderPath+"\\distance.csv"), fid,appset);
                    }
                     else if(appset.computeDMfromRules==true){
                             nclMat.computeDistanceMatrix(rs.redescriptions, fid, appset.maxDistance, datJ.numExamples,oldRIndex);
                             nclMat.resetFile(new File(appset.outFolderPath+"\\distances.csv"));
                             nclMat.writeToFile(new File(appset.outFolderPath+"\\distances.csv"), fid,appset);
                     }
                   }
               }
                endIndW=readers.get(nws-2).rules.size();
            }
            
            nARules=0; nARules1=0;
            double startPerc=0;//percentage[z];
            double endPerc=1;//percentage[z+1];
            int minCovElements[]=new int[]{0};
            int maxCovElements[]=new int[]{0};
            int cuttof=0;
            
            //cuttof=rs.findCutoff(naex, startPerc, endPerc, minCovElements,maxCovElements,oldRIndex, appset.minSupport,appset.maxSupport,appset.numTargets);
             System.out.println("minCovElements: "+minCovElements[0]);
             System.out.println("maxCovElements: "+maxCovElements[0]);
             System.out.println("cuttof: "+cuttof);
             
           if(cuttof==-1)
                continue;
             
            dsc=new DataSetCreator(appset.outFolderPath+"\\Jinput.arff");
            
             try{
        dsc.readDataset();
        }
        catch(IOException e){
            e.printStackTrace();
        }
            
            System.out.println("startPerc: "+startPerc);
            System.out.println("endPerc: "+endPerc);

            for(int i=oldRIndex[0];i<rs.redescriptions.size();i++) //do on the fly when reading rules
                    if(rs.redescriptions.get(i).elements.size()<=naex*endPerc && rs.redescriptions.get(i).elements.size()>=naex*startPerc && rs.redescriptions.get(i).elements.size()>=minCovElements[0] && rs.redescriptions.get(i).elements.size()<=maxCovElements[0]) //do parameters analysis in this step
                        nARules++;
             setMW.ModifySettings(nARules,dsc.schema.getNbAttributes());
             try{
         if(appset.treeTypes.get(nws)==1/*appset.typeOfRSTrees==1*/)   
                dsc.modifyDatasetS(nARules, startPerc, endPerc, oldRIndex[0], rs.redescriptions.size(), minCovElements[0],maxCovElements[0], rs.redescriptions,appset.outFolderPath+"\\Jinputnew.arff",fid);
         else if(appset.treeTypes.get(nws)==0/*appset.typeOfRSTrees==0*/)
                dsc.modifyDatasetCat(nARules, startPerc, endPerc, oldRIndex[0], rs.redescriptions.size(), minCovElements[0],maxCovElements[0], rs.redescriptions,appset.outFolderPath+"\\Jinputnew.arff",fid);
        }
        catch(IOException e){
            e.printStackTrace();
        }
             
             exec.run(appset.javaPath, appset.clusPath, appset.outFolderPath, "view3tmp.s"/*"wbtmp.s"*/, 0,appset.clusteringMemory);//was 1 for rules before
             System.out.println("Process 1 side "+nws+" finished!");
             
             String input;
              input=appset.outFolderPath+"\\view3tmp.out";
              int newRules=0;
              RuleReader ItRules=new RuleReader();
              
             ItRules.extractRules(input,fid,datJ,appset,0);
        ItRules.setSize();
            if(z==0)
                newRules=readers.get(nws-2).addnewRulesC(ItRules, appset.numnewRAttr,1);
            else
                newRules=readers.get(nws-2).addnewRulesC(ItRules, appset.numnewRAttr,0);
               
           }
        
        if(appset.useJoin){//do redescription construction
            rs.combineViewRulesJoin(readers.get(nws-2), jsN, appset, oldIndW, RunInd, oom, fid, datJ, oldRIndex ,nws, appset.maxRSSize); 
        }
        else{//(rr, rr1, jsN, appset, oldIndexRR, oldIndexRR1, RunInd, oom,fid,datJ);
            rs.combineViewRules(readers.get(nws-2), jsN, appset, oldIndW, RunInd, oom, fid, datJ, oldRIndex ,nws);
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
        }
       
        System.out.println("Redescription size main: "+rs.redescriptions.size());
        
        //removing all redescriptions with inadequate minSupport and minJS
        rs.remove(appset);
         
        System.out.println("Redescription size main after remove: "+rs.redescriptions.size());
        
        //filtering
      // rs.filter(appset, rr, rr1,fid,datJ); // think about what we want and if we need it
        
        System.out.println("Redescription size main after filter: "+rs.redescriptions.size());
       
      int numFullRed=0;
        //computing pVal...
        numFullRed=rs.computePVal(datJ,fid);
        rs.removePVal(appset);
        
         System.out.println("Redescription size main after Pvalremove: "+rs.redescriptions.size());
        
        System.out.println("Found "+numFullRed+" redescriptions with JS=1.0 and minsupport>"+appset.minSupport);
        System.out.println("Found "+rs.redescriptions.size()+" redescriptions with JS>"+appset.minJS);
        
         int minimize=0;
         if(appset.minimizeRules==true)
             minimize=1;
        
         rs.adaptSet(datJ, fid,0);
          System.out.println(rs.redescriptions.size()+" redescriptions with JS>"+appset.minJS+" left after elimination of long rules");
        //further reduce before computing optimized set

         /*if(numFullRed>appset.numRetRed){
            appset.minJS=1.0;
            rs.remove(appset);
        }
        else if(numFullRed<appset.numRetRed && rs.redescriptions.size()>appset.numRetRed){
            while(true){
                int num=rs.countNumber(appset.minJS+0.1);
                if((rs.redescriptions.size()-num)>appset.numRetRed){
                    appset.minJS=appset.minJS+0.1;
                    rs.remove(appset);
                }
                else break;
            }
        }*/ //removed for the time being
        
        //sorting redescriptions
        System.out.println("Sorting rules!");
        rs.sortRedescriptions();
        
        System.out.println("Validation in main");
         //this.adaptSet(dat, map);
          for(int i=0;i<rs.redescriptions.size();i++)//uncomment
            rs.redescriptions.get(i).removeRedundant();
  
         for(int i=0;i<rs.redescriptions.size();i++){
             //rs.redescriptions.get(i).closeInterval(datJ, fid);
             rs.redescriptions.get(i).validate(datJ, fid);
         }
         
         for(int i=0;i<rs.redescriptions.size();i++)
             rs.redescriptions.get(i).clearRuleMaps();
         
         if(appset.useSplitTesting==true)
              for(int i=0;i<rs.redescriptions.size();i++)
                     rs.redescriptions.get(i).ComputeValidationStatistics(datJ,datJFull, fid);
         
         rs.adaptSet(datJ, fid,minimize);
         
         CoocurenceMatrix coc=new CoocurenceMatrix(datJ.numExamples,datJ.schema.getNbAttributes()-1);
         coc.computeMatrix(rs, datJ);
         File out=new File(appset.outFolderPath+"\\Elements.txt");
         coc.writeToFileElements(out, datJ.numExamples);
         out=new File(appset.outFolderPath+"\\Attributes.txt");
         coc.writeToFileAttributes(out,datJ.schema.getNbAttributes()-1);
         
       System.out.println("Computing rule score and sorting!");
      RedescriptionSet Result=new RedescriptionSet();
      
      double sumN=0.0;
      /*if(appset.JSImpWeight+appset.PValImpWeight+appset.AttDivImpWeight+appset.ElemDivImpWeight+appset.RuleSizeImpWeight>1.0){
            sumN=appset.JSImpWeight+appset.PValImpWeight+appset.AttDivImpWeight+appset.ElemDivImpWeight+appset.RuleSizeImpWeight;
            appset.JSImpWeight/=sumN; appset.PValImpWeight/=sumN; appset.AttDivImpWeight/=sumN; appset.ElemDivImpWeight/=sumN; appset.RuleSizeImpWeight/=sumN;
      }*/
      
      double heuristicWeights[]=appset.preferences.get(0);
      //double heuristicWeights[]=new double[]{appset.JSImpWeight,appset.PValImpWeight,appset.ElemDivImpWeight,appset.AttDivImpWeight,appset.RuleSizeImpWeight};
      Result.createRedescriptionSetCooc(rs, heuristicWeights, appset,datJ,fid,coc);
      //Result.createRedescriptionSet(rs, heuristicWeights, appset,datJ,fid);
      double coverage[]=new double[2];
      double ResultsScore=Result.computeRedescriptionSetScore(heuristicWeights,coverage,datJ,fid);
      System.out.println("Results score: "+ResultsScore);
        //writing redescriptions to file
      //rs.writeToFile(appset.outFolderPath+"\\"+appset.outputName, datJ, fid, rr, rr1, startTime,numFullRed,appset);//fix output file name
      
      Result.writeToFile(appset.outFolderPath+"\\"+appset.outputName, datJ, fid, startTime,numFullRed,appset, ResultsScore, coverage,oom);
      Result.writePlots(appset.outFolderPath+"\\"+"RuleData.csv", appset,datJ,fid);
      
      coc.init(datJ.numExamples, datJ.schema.getNbAttributes()-1);
      coc.computeMatrix(Result, datJ);
      out=new File(appset.outFolderPath+"\\AttributesOpt.txt");
      coc.writeToFileAttributes(out, datJ.schema.getNbAttributes()-1);
      out=new File(appset.outFolderPath+"\\ElementsOpt.txt");
      coc.writeToFileElements(out, datJ.numExamples);
      
      Result.redescriptions.clear();
      rs.redescriptions.clear();
      rr.rules.clear();
      rr1.rules.clear();
      
      FileDeleter del=new FileDeleter();
      del.setPath(appset.outFolderPath+"\\Jinputnew.arff");
      del.delete();
      del.setPath(appset.outFolderPath+"\\Jinputnew1.arff");
      del.delete();
      del.setPath(appset.outFolderPath+"\\Jinput.arff");
      del.delete();
      if(appset.useSplitTesting==true){
            del.setPath(appset.outFolderPath+"\\JinputTrain.arff");
            del.delete();
      }
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
}