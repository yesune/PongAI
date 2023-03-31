import java.sql.Time;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// TO DO - add weight for x position

import processing.core.PApplet;

public class Pong extends PApplet{
	static final int SIZE = Population.SIZE;
	static final int SCORE_TO_WIN = 3;
	static final int CROSS_OVER_AMOUNT = 3 * Population.SIZE / 4;
	static final double MUTATION_RATE = 0.05;

	
	boolean windowClosed = false;

	 
	
	public static void main(String[] args) {
		int current_gen = 0;
		int counter = 0;
		int[] random_numbers = random_numbers(SIZE);
		Population pop = new Population(current_gen++);
		
		PongAI pong = new PongAI();
		pong.player_1 = random_numbers[counter] + 1;
		pong.player_2 = random_numbers[counter + 1] + 1;
		pong.setAI(pop.population[0], pop.population[1]);
		PApplet.runSketch(new String[]{"PongAI"}, pong);
		while(true) {
			System.out.print(""); // idk why but i need to have some random code for this to work
			if(pong.game_over) {
				if(counter == Population.SIZE) {
					pop = generate_population(pop, current_gen);
					counter = 0;
					random_numbers = random_numbers(SIZE);
				}
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// fitness ranges from 0 to 6
				pop.population[random_numbers[counter]].fitness = pong.player1_score - pong.player2_score + SCORE_TO_WIN; // adding score to win so no negative values
				pop.population[random_numbers[counter + 1]].fitness = pong.player2_score - pong.player1_score + SCORE_TO_WIN;
				
				// reset to set new players and new score
				pong.reset();
				
				pong.player_1 = random_numbers[counter] + 1;
				pong.player_2 = random_numbers[counter + 1] + 1;
				pong.setAI(pop.population[random_numbers[counter++]], pop.population[random_numbers[counter++]]);
				
			}
			
		}
		
    }
	
	public static Population generate_population(Population pop, int generation) {
		Population new_pop = new Population(generation);
		// two random numbers
		int counter = 0;
		AI[] parents, children;
		double[] cdf = generate_cdf(pop);
		for(int i = 0; i < CROSS_OVER_AMOUNT; i++) {
			boolean success = false;
			parents = select_parents(pop, cdf);
			children = pop.crossover(parents[0], parents[1]);
			children[0].print_weights();
			children[0] = pop.mutation(children[0], MUTATION_RATE, 0.25);
			new_pop.population[counter++] = children[0];
		}
		// preserve winning individuals
		for(int i = 0; i < Population.SIZE; i++) {
			parents = select_parents(pop, cdf);
			new_pop.population[counter++] = parents[0];
			if(counter == SIZE)
				break;
		}
		return new_pop;
	}
	
	// generates a cumulative distribution for probability purposes
	public static double[] generate_cdf(Population pop) {
		double[] probabilities = new double[pop.SIZE];
		double[] cdf = new double[pop.SIZE];
		double totalFitness = 0;
		
		// finding total fitness
		for (int i = 0; i < pop.SIZE; i++) {
            totalFitness += pop.population[i].fitness;
        }
		
		// finding fitness probabilities
		for (int i = 0; i < pop.SIZE; i++) {
            probabilities[i] = pop.population[i].fitness / totalFitness;
        }
		
		// finding cumulative probabilities
		cdf[0] = probabilities[0];
		for(int i = 1; i < pop.SIZE; i++) {
			cdf[i] = cdf[i-1] + probabilities[i];
			System.out.println(cdf[i]);
		}
		
		return cdf;
	}
	
	// returns two parents
	public static AI[] select_parents(Population pop, double[] cdf) {
		AI[] select = new AI[2];
		double ran;
		int first = 0;
		for(int i = 0; i < 2; i++) { // selecting two parents
			ran = Math.random();
			for(int j = 0; j < pop.SIZE; j++) { // search through population
				if(ran <= cdf[j]) {				// cdf things
					if(i == 1 && j == first) {	// on second iteration, it can match, just bump it up by one or something
						if(j+1 >= pop.SIZE)
							j--;
						else
							j++;
					}
					first = j;
					select[i] = pop.population[j];
					break;
				}
			}
		}
		return select;
	}
	
	
    public static int[] random_numbers(int count) {
        Random random = new Random();
        Set<Integer> generated = new HashSet<>();
        int[] result = new int[count];
        int i = 0;
        
        while (i < count) {
            int num = random.nextInt(count);
            if (!generated.contains(num)) {
                generated.add(num);
                result[i] = num;
                i++;
            }
        }
        
        return result;
    }


	
}
