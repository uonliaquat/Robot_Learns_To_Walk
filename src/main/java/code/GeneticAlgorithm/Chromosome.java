package code.GeneticAlgorithm;

import code.Constants;
import code.NeuralNetwork.NeuralNetwork;

import java.util.Vector;

public class Chromosome {
    private NeuralNetwork chromosome;
    private int fitness;
    private static Vector<Integer> layers = new Vector<>();
    public Chromosome(){
        if(layers.size() == 0) {
            layers.add(Constants.IL_SIZE);
            layers.add(Constants.HL_SIZE);
            layers.add(Constants.OL_SIZE);
        }
        chromosome = new NeuralNetwork(layers);
    }

    public Chromosome(NeuralNetwork neuralNetwork, int fitness){
        this.fitness = fitness;
        chromosome = new NeuralNetwork(neuralNetwork.getLayers());
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int getFitness() {
        return fitness;
    }

    public NeuralNetwork getChromosome() {
        return chromosome;
    }

    public static void sortChromosomes(Chromosome[] chromosomes){

        for(int i = 0; i < chromosomes.length; i++){
            int max = chromosomes[i].getFitness();
            for(int j = i + 1; j < chromosomes.length; j++){
                if(max < chromosomes[j].getFitness()) {
                    Chromosome temp = new Chromosome(chromosomes[i].getChromosome(), chromosomes[i].getFitness());
                    chromosomes[i] = new Chromosome(chromosomes[j].getChromosome(), chromosomes[j].getFitness());
                    chromosomes[j] = new Chromosome(temp.getChromosome(), temp.fitness);
                }
            }
        }
    }
}
