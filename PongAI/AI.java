import java.util.Arrays;
import java.util.Random;

// trying out q table even though it might be over qualified for this
public class AI {
	static final int HEIGHT = 512;
	static final int WIDTH = 1024;
	static final int DIAMETER = 12;
	static final int PADDLE_WIDTH = 112;
	
	// state parameters: x, vx, y, vy, paddle1_y, paddle2_y
	// actions: move up, move down, dont move
	float x, y, vx, vy, paddle1, paddle2;
	float[][] weights1, weights2;
	float[] input;
	float[] layer1;
	float[] output;
	int fitness;
	int generation;
	int score;
	
	public AI(int generation) {
		weights1 = new float[6][5];
		weights2 = new float[5][3];
		input = new float[6];
		output = new float[3];
		layer1 = new float[5];
		initialize_genes();
		fitness = 0;
		this.generation = generation;
	}
	
	
	
	
	// WE NEED GENES
	// using Xavier Initialization
	public void initialize_genes() {
		for(int i = 0; i < weights1.length; i++) {
			for(int j = 0; j < weights1[0].length; j++) {
				double std = Math.sqrt(2.0/(6 + 5)); // 7 is input size, 3 is output size
				Random random = new Random();
				weights1[i][j] = (float) (random.nextGaussian() * std);
			}
		}
		for(int i = 0; i < weights2.length; i++) {
			for(int j = 0; j < weights2[0].length; j++) {
				double std = Math.sqrt(2.0/(5 + 3)); // 7 is input size, 3 is output size
				Random random = new Random();
				weights2[i][j] = (float) (random.nextGaussian() * std);
			}
		}
	}
	
	public void set_genes(float[][] genes1, float[][] genes2) {
		for(int i = 0; i < weights1.length; i++) {
			for(int j = 0; j < weights1[0].length; j++) {
				weights1[i][j] = genes1[i][j];
			}
		}
		for(int i = 0; i < weights2.length; i++) {
			for(int j = 0; j < weights2[0].length; j++) {
				weights2[i][j] = genes2[i][j];
			}
		}
	}
	
	
	// RETURNS -1 for MOVE DOWN, 1 FOR MOVE UP, 0 FOR DO NOTHING
	public int evaluate(float x, float y, float vx, float vy, float paddle1, float paddle2, boolean is_left) {
		float max = -10000;
		int max_index = 1;
		// reset output
		Arrays.fill(output, 0);
		Arrays.fill(layer1, 0);
		// normalize to the best of your ability
		if(is_left) {
		input[0] = x / WIDTH;
		input[1] = y / HEIGHT;
		input[2] = vx/ 10;
		input[3] = vy / 10;
		input[4] = paddle1 / (HEIGHT - PADDLE_WIDTH);
		input[5] = paddle2 / (HEIGHT - PADDLE_WIDTH);
		} else {
			input[0] = 1 - x / WIDTH;
			input[1] = y / HEIGHT;
			input[2] = - vx/ 10;
			input[3] = vy / 10;
			input[4] = paddle1 / (HEIGHT - PADDLE_WIDTH);
			input[5] = paddle2 / (HEIGHT - PADDLE_WIDTH);
		}
		
		for(int i = 0; i < input.length; i++) {
			for(int j = 0; j < weights1[0].length; j++) {
				layer1[j] += input[i] * weights1[i][j];
			}
		}
		for(int i = 0; i < weights2.length; i++) {
			for(int j = 0; j < weights2[0].length; j++) {
				output[j] += layer1[i] * weights2[i][j];
			}
		}
		
		for(int i = 0; i < output.length; i++) {
			if(output[i] > max) {
				max = output[i];
				max_index = i;
			}
		}
		
		return max_index - 1;
	}
	
	public void print_weights() {
		for(int i = 0; i < weights1.length; i++) {
			for(int j = 0; j < weights1[0].length; j++) {
				System.out.print(weights1[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("inner layer");
		for(int i = 0; i < weights2.length; i++) {
			for(int j = 0; j < weights2[0].length; j++) {
				System.out.print(weights2[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	// getters
	public int get_score() {
		return score;
	}
}