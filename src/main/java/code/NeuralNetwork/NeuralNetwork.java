package code.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class NeuralNetwork {

    private List<Layer> layers;

    public NeuralNetwork(final Vector<Integer> topology) {
        layers = new ArrayList<>();
        for (int i = 0; i < topology.size(); i++) {
            int noOfWeights = i == 0 ? 0 : layers.get(layers.size() - 1).getNoOfNeurons() + 1;
            layers.add(new Layer(topology.get(i), noOfWeights));
        }
    }

    public NeuralNetwork(final List<Layer> layers){
        this.layers = new ArrayList<>();
        for(int i = 0; i < layers.size(); i++){
            this.layers.add(new Layer(layers.get(i)));
        }
    }

    //here useless is passed to neural network just to differentiate it from other constructor
    public NeuralNetwork(final List<List<Vector<Double>>> weights, int useless){
        this.layers = new ArrayList<>();
        for(int i = 0; i < weights.size(); i++){
            this.layers.add(new Layer(weights.get(i)));
        }
    }

    public void feedForward(final Vector<Double> input){
        if(input.size() != layers.get(0).getNoOfNeurons()) return;
        Vector<Double> output;
        layers.get(0).setOutput(input);
        for(int i = 1; i < layers.size(); i++) {
            output = new Vector<>();
            for (int j = 0; j < layers.get(i).getNoOfNeurons(); j++) {
                layers.get(i).getNeurons().get(j).setSum(layers.get(i - 1).getOutput());
                layers.get(i).getNeurons().get(j).setOutput();
                output.add(layers.get(i).getNeurons().get(j).getOutput());
            }
            layers.get(i).setOutput(output);
        }
    }

    public Vector<Double> getOutput() {
        return layers.get(layers.size() - 1).getOutput();
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public List<List<Vector<Double>>> getWeights(){
        List<List<Vector<Double>>> weights = new ArrayList<>();
        for(int i = 0; i < layers.size(); i++){
            weights.add(layers.get(i).getWeights());
        }
        return weights;
    }
}


