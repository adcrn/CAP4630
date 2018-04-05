// University of Central Florida
// CAP 4630 - Spring 2018
// Authors: Nhi Nguyen and Alexander Decurnou

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Instances;


public class MulticlassPerceptron implements MulticlassWeka
{
    public static buildClassifer(Instances instances)
    {
        // blah
    }

    public static predict(Instance instance)
    {
        return class_value;
    }

    public Capabilities getCapabilities()
    {
        return null;
    }

    public double classifyInstance(Instance instance)
    {
        return 0;
    }

    @Override
    public double[] distributionForInstance(Instance instance)
    {
        double[] result = new double[data.numClasses()];
        result[predict(instance)] = 1;
        return result;
    }

    public String toString()
    {
        System.out.format("Source file: %s", args[0]);
        System.out.format("Training epochs: %i", args[1]);
        System.out.format("Total # weight updates = ", num_weight_updates);
        System.out.println("\nFinal weights:");
        
        System.out.format("Class 0 weights: %f %f %f", c0w1, c0w2, c0w3);
        System.out.format("Class 1 weights: %f %f %f", c1w1, c1w2, c1w3);
    }

    public static void main(String[] args throws Exception
    {
        System.out.println("University of Central Florida");
        System.out.println("CAP4630 Artificial Intelligence - Spring 2018");
        System.out.println("Multi-Class Perceptron Classifier by Nhi Nguyen and Alexander Decurnou");

        // print intermediate epoch results

        System.out.println("Results:")

        // print weka output

        // print accuracy from MulticlassWeka

        // print toString output
    }
}
