package sad;

import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.estimate.BMAEstimator;
import weka.classifiers.bayes.net.estimate.BayesNetEstimator;
import weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator;
import weka.classifiers.bayes.net.estimate.SimpleEstimator;
import weka.core.Instances;


public class Predicciones {
	
	private static Predicciones miPredicciones = new Predicciones();

	private Predicciones() {


	}

	public static Predicciones getMiPredicciones() {
		return miPredicciones;
	}
	
	
	public void predecir(Instances train, Instances dev, Instances test) throws Exception {
		
		
		BayesNet estimador = entrenar(train, dev);
		
		Instances total= new Instances(train);
		
		for (int i = 0; i < dev.numInstances(); i++) {
			
			total.add(dev.instance(i));
			
		}
		
		Evaluation evaluator = new Evaluation(total);
		double predictions[] = new double[test.numInstances()+1];
		//Guardamos por cada instancia del test la predicción en un array
		for (int i = 0; i < test.numInstances(); i++) {
			predictions[i] = evaluator.evaluateModelOnceAndRecordPrediction((Classifier)estimador, test.instance(i));
			
		}
		String data="";
		//Imprimimos por pantalla
		for (int i=0; i < test.numInstances(); i++) {
			
			data += ("Instancia:  "+i+"  Clasificada como:  "+predictions[i]+"\n");
		}
		ManejoDatos.getCargaDatos().guardarEnFichero2(data);
	}
	
	public BayesNet entrenar(Instances train, Instances dev) throws Exception
	{
		BayesNet classifier;
		BayesNet mejorClassifier = null;
		Evaluation evaluator;
		double mejorFM = 0.0;
		
		ArrayList<BayesNetEstimator> estimadores = new ArrayList<>();
		
		//estimadores.add(new SimpleEstimator());
		//estimadores.add(new BMAEstimator());
		MultiNomialBMAEstimator bma = new MultiNomialBMAEstimator();
		bma.setUseK2Prior(false);
		estimadores.add(bma);
		//Al usar este me sale un error de incorrect estimator use subclass
		//estimadores.add(new BayesNetEstimator());
		
		ArrayList<String> lista = new ArrayList<>();
		//lista.add("Simple Estimator");
		lista.add("MultiNomialBMA Estimator");
		/*lista.add("BMA Estimator");
		lista.add("MultiNomialBMA Estimator");
		lista.add("BayesNetEstimator");*/
		

		//Por cara estimador que vayamos a usar
		for(int i = 0; i < estimadores.size(); i++)
		{
			classifier = new BayesNet();
			evaluator = new Evaluation(train);
			classifier.setEstimator(estimadores.get(i));
			
			classifier.buildClassifier(train);
			evaluator.evaluateModel(classifier, dev);			
			
			double j = evaluator.weightedFMeasure();
			if(j > mejorFM)
			{
				mejorFM = j;
				mejorClassifier = classifier;
			}
		}
		
		return mejorClassifier;
	}

}

