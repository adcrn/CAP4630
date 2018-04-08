// University of Central Florida
// CAP 4630 - Spring 2018
// Authors: Nhi Nguyen and Alexander Decurnou

import weka.classifiers.Classifier;
import java.text.DecimalFormat;
import weka.core.*;

import java.lang.Math;

public class MulticlassPerceptron implements weka.classifiers.Classifier
{
    int bias = 0;
    String fileName;
    int epoch = 0;
    int weightUpdates = 0;
    double[][] weights;
        
    public MulticlassPerceptron(String[] options)
    {
        // print out required header
        System.out.println("\nUniversity of Central Florida");
        System.out.println("CAP4630 Artificial Intelligence - Spring 2018");
        System.out.println("Multi-Class Perceptron Classifier by Nhi Nguyen and Alexander Decurnou");
        
        // initialize bias and parse input commands
        this.bias = 1;
        this.fileName = options[0];
        this.epoch = Integer.parseInt(options[1]);
    }
    
    public void buildClassifier(Instances instances)
    {
        // get number or classes and attributes
        int numClasses = instances.numClasses();
        int numAttributes = instances.numAttributes();

        // initialize size of weight vector
        this.weights = new double[numClasses][numAttributes];
        double[] features = new double[numAttributes];

        // use a weight vector for each class
        for(int i = 0; i < numClasses; i++)
        {
            // initial weight values for all inputs including bias is 0.0
            // assign last weight in weight vector for the bias value
            for(int j = 0; j < numAttributes; j++)
                this.weights[i][j] = 0.0;
        }

        // run all data over n amount of epochs
        for(int i = 0; i < this.epoch; i++)
        {
            System.out.print("\nEpoch\t" + i + ": ");

            // get number of instances
            int numInstances = instances.numInstances();

            for(int m = 0; m < numInstances; m++)
            {
                // determine correct classification
                double correctClass = instances.instance(m).value(numAttributes - 1);

                // instantiate feature vector
                for(int j = 0; j < numAttributes; j++)
                {
                    if(j == numAttributes - 1)
                        features[j] = this.bias;
                    else
                        features[j] = instances.instance(m).value(j);
                }

                // predict with current weights
                int prediction = predict(instances.instance(m));

                // incorrect
                if(prediction != correctClass)
                {
                    this.weightUpdates++;

                    System.out.print(0);

                    // lower score of wrong answer, and raise score of correct
                    // answer
                    for(int j = 0; j < numClasses; j++)
                    {
                        if(j == correctClass)
                        {
                            for(int k = 0; k < this.weights[j].length; k++)
                                weights[j][k] += features[k];
                        }

                        else
                        {
                            for(int k = 0; k < this.weights[j].length; k++)
                                weights[j][k] -= features[k];   
                        }
                    }
                }

                // correct
                else
                    System.out.print(1);
            }

        }

        System.out.println();
    }

    public int predict(Instance instance)
    {
        // get number of classes and attributes
        int numClasses = instance.numClasses();
        int numAttributes = instance.numAttributes();

        // instantiate arrays
        double[] activation = new double[numClasses];
        double[] features = new double[numAttributes];

        // instantiate feature vector
        for(int i = 0; i < numAttributes; i++)
        {
            if(i == numAttributes - 1)
                features[i] = this.bias;
            else
                features[i] = instance.value(i);
        }

        // compute activation for each class
        for(int i = 0; i < numClasses; i++)
        {
            activation[i] = 0.0;

            for(int j = 0; j < numAttributes; j++)
            {
                // dot product of w(y) and f(x)
                activation[i] += this.weights[i][j] * features[j];
            }
        }

        double argmax = -1.0;
        int index = -1;
        // highest activation value wins
        for(int i = 0; i < activation.length; i++)
        {   
            if(activation[i] > argmax)
            {
                argmax = activation[i];
                index = i;
            }
        }

        // return class of highest activation
        return index;
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
        double[] result = new double[instance.numClasses()];
        result[predict(instance)] = 1;
        return result;
    }

    public String toString()
    {
        DecimalFormat decFormat = new DecimalFormat("#0.000");
    
        System.out.println("Source file: " + this.fileName);
        System.out.println("Training epochs: " + this.epoch);

        System.out.println("Total # weight updates = " + this.weightUpdates);
        System.out.println("\nFinal weights: \n");

        for(int i = 0; i < weights.length; i++)
        {
            System.out.print("Class " + i + " weights:  ");

            for(int j = 0; j < weights[0].length; j++)
            {
                if(weights[i][j] >= 0)
                    System.out.print(" ");

                System.out.print(decFormat.format(weights[i][j]) + " ");
            }

            System.out.println();
        }


        return "";
    }
}