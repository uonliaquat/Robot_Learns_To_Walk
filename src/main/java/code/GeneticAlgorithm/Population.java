package code.GeneticAlgorithm;


public class Population {

    private Chromosome[] chromosomes;

    public Population(int populationSize){
        chromosomes = new Chromosome[populationSize];
        for(int i = 0; i < populationSize; i++){
            chromosomes[i] = new Chromosome();
        }
    }

    public Population(Chromosome[] chromosomes){
        this.chromosomes = new Chromosome[chromosomes.length];
        for(int i = 0; i < chromosomes.length; i++){
            this.chromosomes[i] = new Chromosome(chromosomes[i].getChromosome(), 0);
        }
    }

    public Chromosome[] getChromosomes() {
        return chromosomes;
    }
}
