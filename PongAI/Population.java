import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import processing.core.PApplet;


// TO DO 
//
//
// if you have nothing else to do, then change the color of the paddles and the ball depending on the ai




// Population of AI
public class Population{
	static final int SIZE = 500; // MUST BE MULTIPLE OF FOUR
	static final int HEIGHT = 512;
	static final int WIDTH = 1024;
	static final int SCORE_TO_WIN = 3;
	static final int RADIUS = 6;
	static final int DIAMETER = RADIUS * 2;
	static final int PADDLE_WIDTH = 112;
	static final int CROSS_OVER_AMOUNT = 3 * Population.SIZE / 4;
	static final double MUTATION_RATE = 0.05;

	PongAI p;


	AI[] population;
	private PongMatch[] matches;
	
	
	
	public Population(int generation, PongAI p) {
		population = new AI[SIZE];
		for(int i = 0; i < SIZE; i++) {
			population[i] = new AI(generation);
		}
		generate_matches();
		this.p = p;
	}
	
	public void generate_matches() {
		matches = new PongMatch[SIZE/2];
		int[] rand = random_numbers(SIZE);
		for(int i = 0; i < SIZE; i+=2) {
			float[] initial_v = randomize_vel();
			PongPaddle Player1 = new PongPaddle((HEIGHT - 112)/2, 0, 0);
			PongPaddle Player2 = new PongPaddle((HEIGHT - 112)/2, 0, 0);
			PongBall ball = new PongBall((WIDTH - RADIUS) / 2, (HEIGHT - RADIUS) / 2, initial_v[0], initial_v[1], DIAMETER);
			matches[i/2] = new PongMatch(population[rand[i]], population[rand[i+1]], ball, Player1, Player2);
		}
	}
	
	public PongMatch[] get_matches() {
		return matches;
	}
	
	// CROSSOVER METHOD
	public AI[] crossover(AI parent1, AI parent2) {
		float[][] child1_1 = new float[6][5];
		float[][] child2_1 = new float[6][5];
		AI[] temp;
		
		int crossover_point = (int) (Math.random() * (parent1.weights1.length - 1) + 1); // 6 is number of inputs
		
		for(int i = 0; i < parent1.weights1.length; i++) {
			for(int j = 0; j < parent1.weights1[0].length; j++) {
				if(i < crossover_point) {
					child1_1[i][j] = parent1.weights1[i][j];
					child2_1[i][j] = parent2.weights1[i][j];
				} else {
					child2_1[i][j] = parent1.weights1[i][j];
					child1_1[i][j] = parent2.weights1[i][j];
				}
			}
		}
		
		float[][] child1_2 = new float[5][3];
		float[][] child2_2 = new float[5][3];
		
		crossover_point = (int) (Math.random() * (parent1.weights2.length - 1) + 1); // 6 is number of inputs
		
		for(int i = 0; i < parent1.weights2.length; i++) {
			for(int j = 0; j < parent1.weights2[0].length; j++) {
				if(i < crossover_point) {
					child1_2[i][j] = parent1.weights2[i][j];
					child2_2[i][j] = parent2.weights2[i][j];
				} else {
					child2_2[i][j] = parent1.weights2[i][j];
					child1_2[i][j] = parent2.weights2[i][j];
				}
			}
		}
		temp = new AI[2];
		temp[0] = new AI(Math.max(parent1.generation, parent2.generation) + 1);
		temp[1] = new AI(Math.max(parent1.generation, parent2.generation) + 1);
		//System.out.println("1 gen: " + parent1.generation);
		//System.out.println("2 gen: " + parent2.generation);

		//System.out.println("max gen: " + Math.max(parent1.generation, parent2.generation) + 1);
		temp[0].set_genes(child1_1, child1_2);
		temp[1].set_genes(child2_1, child2_2);
		return temp;
	}
	
	public AI mutation(AI original, double rate, double range) {
		float[][] weights1 = new float[6][5];
		AI temp;
		Random rand = new Random();
		
		for(int i = 0; i < original.weights1.length; i++) {
			for(int j = 0; j < original.weights1[0].length; j++) {
				if(Math.random() < rate) {
					weights1[i][j] = (float) (original.weights1[i][j] + (float) rand.nextGaussian() * range);
				} else
					weights1[i][j] = original.weights1[i][j];
			}
		}
		
		float[][] weights2 = new float[5][3];
		
		for(int i = 0; i < original.weights2.length; i++) {
			for(int j = 0; j < original.weights2[0].length; j++) {
				if(Math.random() < rate) {
					weights2[i][j] = (float) (original.weights2[i][j] + (float) rand.nextGaussian() * range);
				} else
					weights2[i][j] = original.weights2[i][j];
			}
		}
		temp = new AI(original.generation);
		temp.set_genes(weights1, weights2);
		return temp;
	}
	
	public void update() {
		for(int i = 0; i < matches.length; i ++) {
			if(!matches[i].get_game_over()) {
				update_match(matches[i]);
				show(matches[i]);
				matches[i].increase_time();
				if(matches[i].get_gametime()== 10000) {
					matches[i].set_game_over(true);
				}
			}
		}
	}
	
	public void show(PongMatch match) {
		p.fill(0, 0, 0, 128);
		p.stroke(255);
		p.rect(40, match.get_left_paddle().y, 8, 112);
    	p.rect(WIDTH-48, match.get_right_paddle().y, 8, 112);
		p.ellipse(match.get_ball().x, match.get_ball().y, 12, 12);
	}
	
	public void update_match(PongMatch match) {
		int action_left, action_right;
		
		// updating player paddles
		action_left = match.get_left().evaluate(match.get_ball().x, match.get_ball().y, match.get_ball().vx,
					  match.get_ball().vy, match.get_left_paddle().y, match.get_right_paddle().y, true);
		action_right = match.get_right().evaluate(match.get_ball().x, match.get_ball().y, match.get_ball().vx,
				  match.get_ball().vy, match.get_right_paddle().y, match.get_left_paddle().y, false);
		if(action_left == 1) {
			match.moveUp(1);
		} else if(action_left == -1) {
			match.moveDown(1);
		} else {
			match.stop_moving(1);
		}
		if(action_right == 1) {
			match.moveUp(-1);
		} else if(action_right == -1) {
			match.moveDown(-1);
		} else {
			match.stop_moving(-1);
		}
		match.get_left_paddle().update(HEIGHT, PADDLE_WIDTH);
		match.get_right_paddle().update(HEIGHT, PADDLE_WIDTH);

		
		// updating the ball
		
		int score = match.get_ball().update(WIDTH, HEIGHT, match.get_left_paddle().y, match.get_right_paddle().y);
    	if(score != 0) {	// IF SCORE IS NOT 0, THEN SOMEONE HAS SCORED, RESET THE FIELD
    		if(score == 1)
    			match.get_right().score++;
    		else
    			match.get_left().score++;
    		match.is_over();
    		float[] initial_v = randomize_vel();
    		PongBall temp_ball = new PongBall((WIDTH - RADIUS) / 2, (HEIGHT - RADIUS) / 2, initial_v[0], initial_v[1], DIAMETER);
    		match.set_ball(temp_ball);
    		match.get_left_paddle().y = (HEIGHT - 112)/2;
    		match.get_right_paddle().y = (HEIGHT - 112)/2;
    	}
		
	}
	
	public float[] randomize_vel() {
    	float temp[] = new float[2];
    	int neg;
    	for(int i = 0; i < 2; i++) {
    		if(Math.random() >= 0.5) {
    			neg = 1;
    		} else
    			neg = -1;
    		temp[i] = (float) (neg * (2 + 2 * Math.random()));
    	}
    	return temp;
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
	
	public boolean done() {
		int counter = 0;
		boolean result = true;
		for(int i = 0; i < matches.length; i++) {
			if(!matches[i].get_game_over()) {
				counter++;
				result = false;
			}
		}
		//System.out.println(counter);
		return result;
	}
	
	public PongMatch find_best_game() {
		int max = 0, max_index = 0;
		for(int i = 0; i < matches.length; i++) {
			if(matches[i].get_gametime() > max) {
				max_index = i;
				max = matches[i].get_gametime();
			}
		}
		return matches[max_index];
	}
	
	public void generate_population(Population pop, PongAI p) {
		Population new_pop = new Population(0, p);
		// two random numbers
		int counter = 0;
		AI[] parents, children;
		calculate_fitness();
		double[] cdf = generate_cdf(pop);
		for(int i = 0; i < CROSS_OVER_AMOUNT; i++) {
			boolean success = false;
			parents = select_parents(pop, cdf);
			children = pop.crossover(parents[0], parents[1]);
			//children[0].print_weights();
			children[0] = pop.mutation(children[0], MUTATION_RATE, 0.25);
			new_pop.population[counter++] = children[0];
			//System.out.println("score " + children[0].score);
		}
		// preserve winning individuals
		for(int i = 0; i < Population.SIZE; i++) {
			parents = select_parents(pop, cdf);
			parents[0].score = 0;
			new_pop.population[counter++] = parents[0];
			if(counter == SIZE)
				break;
		}
		
		for(int i = 0; i < SIZE; i++) {
			population[i] = new_pop.population[i];
		}
	}
	
	public double[] generate_cdf(Population pop) {
		double[] probabilities = new double[SIZE];
		double[] cdf = new double[pop.SIZE];
		double totalFitness = 0;
		
		// finding total fitness
		for (int i = 0; i < SIZE; i++) {
            totalFitness += pop.population[i].fitness;
        }
		
		// finding fitness probabilities
		for (int i = 0; i < SIZE; i++) {
            probabilities[i] = pop.population[i].fitness / totalFitness;
        }
		
		// finding cumulative probabilities
		cdf[0] = probabilities[0];
		for(int i = 1; i < SIZE; i++) {
			cdf[i] = cdf[i-1] + probabilities[i];
		}
		
		return cdf;
	}
	
	// returns two parents
	public AI[] select_parents(Population pop, double[] cdf) {
		AI[] select = new AI[2];
		double ran;
		int first = 0;
		for(int i = 0; i < 2; i++) { // selecting two parents
			ran = Math.random();
			for(int j = 0; j < SIZE; j++) { // search through population
				if(ran <= cdf[j]) {				// cdf things
					if(i == 1 && j == first) {	// on second iteration, it can match, just bump it up by one or something
						if(j+1 >= SIZE)
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
		
	public void calculate_fitness() {
		for(int i = 0; i < matches.length; i++) {
			matches[i].get_left().fitness = matches[i].get_left().score - matches[i].get_right().score + 3;
			matches[i].get_right().fitness = matches[i].get_right().score - matches[i].get_left().score + 3;
		}
	}
	
	public void getStats() {
		int left_win = 0, right_win = 0, tie_game = 0;
		double average_left = 0, average_right = 0;
		double average_time = 0;
		for(int i = 0; i < matches.length; i++) {
			if(matches[i].get_left().score == SCORE_TO_WIN)
				left_win++;
			else if(matches[i].get_right().score == SCORE_TO_WIN)
				right_win++;
			else
				tie_game++;
			average_left += matches[i].get_left().score;
			average_right += matches[i].get_right().score;
			average_time += matches[i].get_gametime();
		}
		System.out.println("left wins: " + left_win);
		System.out.println("right wins: " + right_win);
		System.out.println("right wins: " + right_win);
		System.out.println("left average: " + (double)(average_left / (SIZE/2)));
		System.out.println("right average: " + (double)(average_right / (SIZE/2)));
		System.out.println("time average: " + (double)(average_time / (SIZE/2)));

	}
		
}