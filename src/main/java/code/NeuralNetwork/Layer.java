package code.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Layer {

    private List<Neuron> neurons;
    private Vector<Double> output;
    private int noOfNeurons;

    public Layer(final int noOfNeurons, final int noOFWeights){
        this.noOfNeurons = noOfNeurons;
        neurons = new ArrayList<>();
        for(int i = 0; i < noOfNeurons; i++){
            neurons.add(new Neuron(noOFWeights));
        }
    }

    public Layer(final Layer layer){
        this.noOfNeurons = layer.getNoOfNeurons();
        neurons = new ArrayList<>();
        for(int i = 0; i < noOfNeurons; i++){
            neurons.add(new Neuron(layer.getNeurons().get(i).getWeights()));
        }
    }

    public Layer(final List<Vector<Double>> weights){
        this.noOfNeurons = weights.size();
        neurons = new ArrayList<>();
        for(int i = 0; i < noOfNeurons; i++){
            neurons.add(new Neuron(weights.get(i)));
        }
    }

    public void setOutput(final Vector<Double> output) {
        this.output = new Vector<>();
        for(int i = 0; i < output.size(); i++){
            this.output.add(output.get(i));
        }
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public Vector<Double> getOutput() {
        return output;
    }

    public int getNoOfNeurons() {
        return noOfNeurons;
    }

    public List<Vector<Double>> getWeights(){
        List<Vector<Double>> neuronsWeights = new ArrayList<>();
        for(int i = 0; i < noOfNeurons; i++){
            Vector<Double> weights = neurons.get(i).getWeights();
            neuronsWeights.add(weights);
        }
        return neuronsWeights;
    }
}
