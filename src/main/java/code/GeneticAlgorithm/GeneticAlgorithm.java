package code.GeneticAlgorithm;

import code.Constants;
import code.NeuralNetwork.NeuralNetwork;
import code.NeuralNetwork.Neuron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class GeneticAlgorithm {

    private Population population;

    public GeneticAlgorithm(){
        population = new Population(Constants.POPULATION_SIZE);
    }

    public GeneticAlgorithm(Chromosome[] chromosomes){
        population = new Population(chromosomes);
    }

    public void produceNextGen(){
        Chromosome[] resultChromosomes = new Chromosome[Constants.POPULATION_SIZE];

        //sort chromosomes as per their fitness value
        Chromosome.sortChromosomes(population.getChromosomes());


        int index = selectFittest(Constants.FITNESS_RATE, population.getChromosomes(), resultChromosomes);
        index = crossOver(Constants.CROSSOVER_RATE, population.getChromosomes(), resultChromosomes, index);
        mutation(Constants.MUTATION_RATE, population.getChromosomes(), resultChromosomes, index);

        population = new Population(resultChromosomes);


    }


    private int selectFittest(double ratio, Chromosome[] chromosomes, Chromosome[] resultChromosomes){
        int iterations = (int) (ratio * Constants.POPULATION_SIZE);
        for(int i = 0; i < iterations; i++){
            resultChromosomes[i] = new Chromosome(chromosomes[i].getChromosome(), chromosomes[i].getFitness());
        }
        return iterations;

    }

    private int crossOver(double ratio, Chromosome[] chromosomes, Chromosome[] resultChromosomes, int index){
        int iterations = (int) (ratio * Constants.POPULATION_SIZE);
        for(int i = 1; i <= iterations; i++){
            List<List<Vector<Double>>> c1_oldWeights = chromosomes[i - 1].getChromosome().getWeights();
            List<List<Vector<Double>>> c2_oldWeights = chromosomes[i].getChromosome().getWeights();

            List<Vector<Double>> c1_weightsL1 = c1_oldWeights.get(0);
            List<Vector<Double>> c1_weightsL2 = c1_oldWeights.get(1);
            List<Vector<Double>> c1_weightsL3 = c2_oldWeights.get(2);


            List<List<Vector<Double>>> c1_newWeights = new ArrayList<>();
            c1_newWeights.add(c1_weightsL1);
            c1_newWeights.add(c1_weightsL2);
            c1_newWeights.add(c1_weightsL3);

            NeuralNetwork neuralNetwork = new NeuralNetwork(c1_newWeights, 0);

            resultChromosomes[index] = new Chromosome(neuralNetwork, 0);
            index++;
        }
        return index;
    }

    private void mutation(double ratio, Chromosome[] chromosomes, Chromosome[] resultChromosomes, int index){
        int iterations = (int) (ratio * Constants.POPULATION_SIZE);
        int totalNoOFWeights = (Constants.IL_SIZE * Constants.HL_SIZE) + (Constants.HL_SIZE * Constants.OL_SIZE);
        for(int i = 0; i < iterations; i++){
            List<List<Vector<Double>>> c_weights = chromosomes[i].getChromosome().getWeights();
            int noOfWeightsToChange = getRandomNo(1,totalNoOFWeights);
            for(int j = 0; j < noOfWeightsToChange; j++){
                double rand_weight = Neuron.getRandomNo();
                int lNo = getRandomNo(1,3);
                int neuron_no = getRandomNo(0, chromosomes[0].getChromosome().getLayers().get(lNo).getNoOfNeurons());
                int weight_no = getRandomNo(0, chromosomes[0].getChromosome().getLayers().get(lNo).getNeurons().get(neuron_no).getWeights().size());
                c_weights.get(lNo).get(neuron_no).set(weight_no, rand_weight);
            }

            NeuralNetwork neuralNetwork = new NeuralNetwork(c_weights,0);
            resultChromosomes[index] = new Chromosome(neuralNetwork, 0);
            index++;
        }

    }


    public static int getRandomNo(int min, int max){
        if(min == max){
            return min;
        }
        Random random = new Random();
        int n = random.nextInt(max - min) + min;
        return n;
    }

    public Population getPopulation() {
        return population;
    }
}
