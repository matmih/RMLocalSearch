/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redescriptionmining;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import static la.io.IO.saveDenseMatrix;
import la.matrix.Matrix;
import ml.clustering.Clustering;
import ml.clustering.KMeans;
import ml.optimization.NMF;
import ml.options.KMeansOptions;
import ml.options.L1NMFOptions;
import ml.utils.Matlab;
import static ml.utils.Matlab.full;
import static ml.utils.Matlab.mldivide;
import static ml.utils.Time.toc;

/**
 *
 * @author mmihelci
 */
public class DescribableNMF {
    public static void main(String [] args){
        
        RedescriptionSet result = null;
        
        CLUSRMExecutor exec = new CLUSRMExecutor();
        
        result = exec.executeCLUS_RM(args);  
        
        Matrix Pmat = null, Amat = null;
       
         Matrix Xmat=null;
         Xmat = exec.loadDataIntoMatrix(exec.datJNMF);
         System.out.println(Xmat.getRowDimension()+" "+Xmat.getColumnDimension());

         Pmat = exec.loadDataIntoPMatrix(exec.datJNMF.numExamples, result);    
        // printMatrix(Pmat);
         
         System.out.println("Num reds: "+result.redescriptions.size());
         System.out.println(Pmat.getRowDimension()+" "+Pmat.getColumnDimension());
         
         HashMap<Integer,HashSet<Redescription>> factorRedescriptionMap = new HashMap<>();
         HashMap<Integer,HashSet<Integer>> factorIndexMap = new HashMap<>();
         ArrayList<HashSet<Integer>> factorEntity = new ArrayList<>();
         ArrayList<HashSet<Integer>> factorEntityRedInduced = new ArrayList<>();
         ArrayList<HashSet<Integer>> factorResidualEntity = new ArrayList<>();
            
         int K = exec.appset.numNMFFactors;//add into ApplicationSettings
         
         if(Pmat.getColumnDimension()<K)
             K=Pmat.getColumnDimension();
         
         int maxIter = exec.appset.KMeansIterations;
         boolean verbose = true;
	 KMeansOptions options = new KMeansOptions(K, maxIter, verbose);
	 Clustering KMeans = new KMeans(options);
         KMeans.feedData(Pmat.transpose());
         KMeans.clustering();
         Matrix indicator = full(KMeans.getIndicatorMatrix());
        
         for(int j=0;j<indicator.getColumnDimension();j++){
             for(int i=0;i<indicator.getRowDimension();i++){
                 if(indicator.getEntry(i, j) == 1){
                     if(!factorRedescriptionMap.containsKey(j)){
                         factorRedescriptionMap.put(j, new HashSet<Redescription>());
                         factorIndexMap.put(j,new HashSet<Integer>());
                     }
                     factorRedescriptionMap.get(j).add(result.redescriptions.get(i));
                     factorIndexMap.get(j).add(i);
                 }
             }
         }
         
          Matrix finalClust = exec.createFinalClusterMatrix(factorRedescriptionMap,K);
            File output = new File("factorDescription.txt"); 
                  saveDenseMatrix("TargetF.txt", finalClust);
                       
        /* printMatrix(Xmat);
         int test = 1;
         if(test == 1)
             return;*/
         System.out.println(exec.appset.numNMFFactors);
         System.out.println(exec.appset.NMFTolerance);
         System.out.println(exec.appset.numNMFIterations);
         System.out.println(exec.appset.KMeansIterations);
         

         Matrix Fmat = Matlab.abs(Matlab.randP(exec.datJNMF.numExamples, K));/*finalClust.copy();*/
         Matrix Gmat = Matlab.abs(Matlab.randP(exec.datJNMF.schema.getNbAttributes()-2,K)); /*Matlab.abs(mldivide(Fmat.transpose().mtimes(Fmat), Fmat.transpose().mtimes(Xmat)).transpose());*/
        //replace zeroes with e.g 0.1
         
         
         System.out.println("X dim: "+(Xmat.getRowDimension()+" "+Xmat.getColumnDimension()));
         System.out.println("F dim: "+(Fmat.getRowDimension()+" "+Fmat.getColumnDimension()));
         System.out.println("G dim: "+(Gmat.getRowDimension()+" "+Gmat.getColumnDimension()));
         
           NMF instance = new NMF();
         Amat = instance.constructA(indicator, Pmat, Amat);
         System.out.println("indicator dim: "+indicator.getRowDimension()+" "+indicator.getColumnDimension());
         System.out.println("A dim: "+Amat.getRowDimension()+" "+Amat.getColumnDimension());
         //printMatrix(Amat);
        
          Matrix XmatT = Xmat.copy();
         
         Matrix FmatT = Fmat.copy();
         Matrix GmatT = Gmat.copy();
         Matrix PmatT = Pmat.copy();
         Matrix AmatT = Amat.copy();
         
         double errorAcc = Matlab.norm(Xmat.minus(Fmat.mtimes(Gmat.transpose())),"fro");
         double errorDesc = Matlab.norm(Amat.minus(Fmat.transpose().mtimes(Pmat)),"fro");
         System.out.println("Initial errors: ");
         System.out.println("Accuracy: "+errorAcc);   
         System.out.println("Descriptive: "+errorDesc);
         double normX = Matlab.norm(Xmat,"fro"), normA = Matlab.norm(Amat,"fro");//works well with Lagrange multiplier
         double quoc = normX/normA;
         double maxX =  Matlab.max(Matlab.max(Xmat)[0])[0];
         double maxA =  Matlab.max(Matlab.max(Amat)[0])[0];
         //Fmat = Fmat.times(maxA);//works better with regulariser, remove when regulariser is not used
         double quocV = maxX/maxA; //does not work well with regulariser
        
         //instance.iterate(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 0.0,normX,0);//original - Lagrangian approx (hard constraint can not be satsified)
         //instance.iterateRegularizer1Norm(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 0.0,0.1,0);// constrained NMF, constraint as regularizer, normalized objective function
       
         instance.iterateRegularizer1(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance,/*1000*/200*normX/normA/*100000*/,0,"");// constrained NMF, constraint as regularizer
      // Fmat=instance.iterateRegularizer2(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 0.0,/*1000*/2000*normX/normA/*100000*/,0);// constrained NMF, constraint as regularizer, multiplicative updates, normalized F
        
         //all methods need to return F...
         //instance.iterateRegularizerALS1(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 0.0,1,0);// constrained NMF, constraint as regularizer, the ALS approach
        
        //comparisson grad desc 
       // Fmat = instance.iterateRegularizerGD(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 1/(10000*normX), 1/(normX),10000*normX/(normA),0);// constrained NMF, constraint as regularizer, the ALS-GD approach
       //  FmatT = instance.iterateRegularizerGDDB(Xmat, FmatT, GmatT, AmatT, PmatT, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 1/(10000*normX), 1/(normX),10000*normX/(normA),1.1,0.89,0);// constrained NMF, constraint as regularizer, the ALS-GD approach with Bold driver heuristics
         
       //  Fmat = instance.iterateRegularizerOblique(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance,3000*normX/(normA),0);// constrained NMF, constraint as regularizer, the oblique projected Landweber GD
       
           
       //  Fmat = instance.iterateRegularizerLPG(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance,10,0/*3000*normX/(normA)*/,0);// constrained NMF, constraint as regularizer, the Lin's projected gradient, not working!
       
         
          //Fmat = instance.iterateRegularizerHALS(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance, normX*10 /*normX/(normA*10)*//*10000*normX/(normA)*/);// constrained NMF, constraint as regularizer, the HALS approach
          
        //Fmat = instance.iterateRegularizerCombined(Xmat, Fmat, Gmat, Amat, Pmat, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 1/(100000*normX), 10000/(normX),normX*10, 100000*normX/(normA)/*normX/(normA*10)*//*10000*normX/(normA)*/);// constrained NMF, constraint as regularizer, the HALS approach - not working
         
         
         saveDenseMatrix("FmatTestCheck.txt", Fmat);
         Matrix IndicatorO = null;
          IndicatorO = exec.createIndicatorMatrixNMF(Fmat);
         ArrayList<Double> factorAccuracy = new ArrayList();
         exec.computeFactorAccurracy(factorAccuracy, IndicatorO, finalClust);
         saveDenseMatrix("IndicatorDes.txt", IndicatorO);
          exec.writeFactorRedInfoIntoFile(factorAccuracy,factorRedescriptionMap, output, exec.fid);
         
          factorAccuracy.clear();
          
          IndicatorO = exec.createIndicatorMatrixNMF(FmatT);
           exec.computeFactorAccurracy(factorAccuracy, IndicatorO, finalClust);
           //exec.writeFactorRedInfoIntoFile(factorAccuracy,factorRedescriptionMap, new File("factorDescriptionBD.txt"), exec.fid);//uncoment to compare with some other methodology
          //make HALS approach
          
          int test =1;
          if(test ==1)
              return;
          
         errorAcc = Matlab.norm(Xmat.minus(FmatT.mtimes(GmatT.transpose())),"fro");
         errorDesc = Matlab.norm(Amat.minus(FmatT.transpose().mtimes(Pmat)),"fro");
         System.out.println("Initial errors: ");
         System.out.println("Accuracy: "+errorAcc);   
         System.out.println("Descriptive: "+errorDesc);  
          
         //instance.iterate(Xmat, FmatT, GmatT, AmatT, PmatT, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 0.0,normX,1);
         instance.iterateRegularizerALS(Xmat, FmatT, GmatT, AmatT, PmatT, exec.appset.numNMFIterations, exec.appset.NMFTolerance, 0.0,normX,1);
         saveDenseMatrix("FComp.txt", FmatT);
         
          //create factor/entity HashMap
           factorEntity = exec.createMappings(IndicatorO);
            //get the reds induced clustering from here
            //take as input real factor entity mapping, remove reds that contain more entities, compute residual
              factorEntityRedInduced = exec.createRIMappings(factorRedescriptionMap, factorEntity); //exec.createMappings(finalClust);
         
          IndicatorO = exec.createIndicatorMatrixNMF(FmatT);
         ArrayList<Double> factorAccuracy1 = new ArrayList();
         exec.computeFactorAccurracy(factorAccuracy1, IndicatorO, finalClust);
           saveDenseMatrix("IndicatorRegular.txt", IndicatorO);
         
         System.out.println("Factor accuracy not optimised.");
         for(int i=0;i<factorAccuracy1.size();i++){
             System.out.print(factorAccuracy1.get(i)+" ");
         }
         System.out.println();
         
          KMeansOptions kMeansOptions = new KMeansOptions();
		kMeansOptions.nClus = K;
		kMeansOptions.maxIter = exec.appset.KMeansIterations;
		kMeansOptions.verbose = true;
		
	      KMeans = new KMeans(kMeansOptions);
		KMeans.feedData(XmatT);
		KMeans.initialize(null);
		KMeans.clustering();
		
		Matrix G0 = KMeans.getIndicatorMatrix();
                //printMatrix(G0);
                //Fmat = full(G0.copy());
         
         L1NMFOptions NMFOptions = new L1NMFOptions();
		NMFOptions.maxIter = exec.appset.numNMFIterations;
		NMFOptions.verbose = true;
		NMFOptions.calc_OV = true;
		NMFOptions.epsilon = exec.appset.NMFTolerance;
                
                errorAcc = Matlab.norm(XmatT.minus((G0.mtimes(mldivide(G0.transpose().mtimes(G0), G0.transpose().mtimes(XmatT))))),"fro");
                errorDesc = Matlab.norm(Amat.minus(G0.transpose().mtimes(Pmat)),"fro");
                System.out.println("Initial errors: ");
                System.out.println("Accuracy: "+errorAcc);   
                System.out.println("Descriptive: "+errorDesc);
                
		//Clustering NMF1 = new ml.clustering.L1NMF(NMFOptions);
                Clustering NMF1 = new ml.clustering.L1NMFDescriptive(NMFOptions,100000);
		NMF1.feedData(XmatT);
		//NMF1.clustering(G0);
             ///  clusteringInit(Matrix G0, Matrix F0, Matrix A, Matrix P)
               NMF1.clusteringInit(Fmat, Gmat.transpose(), Amat, Pmat,0);
		
                System.out.println("NMF1: "+NMF1.nExample);
                System.out.println("NMF1: "+NMF1.nFeature);
                System.out.println("NMF1: "+NMF1.nClus);
		System.out.format("Elapsed time: %.3f seconds\n", toc());  
              //create the cluster matrix
              // factorRedescriptionMap
   
              //implement the P update
              
              //remove redundant reds from factors
              //compute factor and corresponding reds supports
              //perform RM that finds better reds in appropriate manner
              //inputs: JS array, target entity sets, output: a set of redescription sets corresonding to factors
              //update P
              //update NMF iterations
              //until convergance
              
              //compute the residual sets
              factorResidualEntity = exec.computeResiduals(factorEntity, factorEntityRedInduced);
              
              //call targeted RM to get new redescriptions
              ArrayList<RedescriptionSet> redsUpdate = null;
              
    }
}
