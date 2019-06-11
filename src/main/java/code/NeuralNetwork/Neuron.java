package code.NeuralNetwork;

import java.util.Vector;

public class Neuron {
    private Vector<Double> weights;
    private double output, sum;
    private static int min = -1, max = 1, precision = 2;

    public Neuron(final int noOfWeights){
        weights = new Vector<>();
        for(int i = 0; i < noOfWeights; i++){
            weights.add(getRandomNo());
        }
    }

    public Neuron(final Vector<Double> weights){
        this.weights = new Vector<>();
        for(int i = 0; i < weights.size(); i++){
            this.weights.add(weights.get(i));
        }
    }

    public void setSum(final Vector<Double> input){
        sum = 0;
        for(int i  = 0; i < input.size(); i++){
            sum = sum + (input.get(i) * weights.get(i));
        }
        sum = sum + weights.get(weights.size() - 1);
    }

    public void setOutput(){
        output = sigmoid(sum);
    }

    public double getSum() {
        return sum;
    }

    public double getOutput() {
        return output;
    }

    private static double sigmoid(final double x){
        return  1/(1+Math.exp(-x));
    }

    public static double getRandomNo(){
        double pre = 1;
        for(int i = 0; i < precision; i++){
            pre = pre * 10;
        }
        double rand = min + Math.random() * (max - (min));
        rand = (double)Math.round(rand * pre) / pre;
        return rand;
    }

    public Vector<Double> getWeights() {
        return weights;
    }
}
