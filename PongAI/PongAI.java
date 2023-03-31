
//import java.util.concurrent.TimeUnit;

import java.io.IOException;

import processing.core.PApplet;
import processing.core.PFont;

public class PongAI extends PApplet{
	static final int HEIGHT = 512;
	static final int WIDTH = 1024;
	static final int DIAMETER = 12;
	static final int PADDLE_WIDTH = 112;
	
	int player_1, player_2;
	
	int Player_Playing = 0; // 1 means a human is playing, 0 means two AI
	
	boolean game_over = false;
	boolean best_game = false;
	
	//float initial_v[] = randomize_vel();
	
	//PongBall ball = new PongBall((WIDTH - RADIUS) / 2, (HEIGHT - RADIUS) / 2, initial_v[0], initial_v[1], DIAMETER);
	PongPaddle Player1 = new PongPaddle((HEIGHT - 112)/2, 0, 0);
	PongPaddle Player2 = new PongPaddle((HEIGHT - 112)/2, 0, 0);
	
	PongMatch best_match = null;
	
	Population pop;
	
	static AI ai_1, ai_2;
	
	int score, player1_score = 0, player2_score = 0;
	
	boolean windowClosed = false;

	  public void exit() {
	    windowClosed = true;
	    super.exit();
	  }
	
    public static void main(String[] args) {
        PApplet.main("PongAI");
    }

    public void settings() {
        size(WIDTH, HEIGHT);
    }

    public void setup() {
    	textAlign(CENTER);
    	PFont customFont = createFont("PressStart2P-Regular.ttf", 32);
    	textFont(customFont);
        pop = new Population(0, this);
    }

    public void draw() {
    	background(0);
    	if(!pop.done()) {
    		pop.update();
    	} else if (best_game) {
    		best_match = pop.find_best_game();
    		best_match.get_left().score = 0;
    		best_match.get_right().score = 0;
    		pop.update_match(best_match);
			pop.show(best_match);
    	} else if (best_game) { 
    		pop.update_match(best_match);
			pop.show(best_match);
			if (best_match.get_game_over()) {
				best_game = false;
			}
    	} else {
    		// train the model
    		pop.getStats();
    		try {
				FileIO.save_population(pop);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		pop.generate_population(pop, this);
    		pop.generate_matches();
    		System.out.println(pop.done());
    	}

    }
    
    public void keyPressed() {
    	if(key == CODED) {
    		if(keyCode == UP) {
    			Player1.moveUp();
    		} else if(keyCode == DOWN) {
    			Player1.moveDown();
    		}
    	}
    }
    
    public void keyReleased() {
    	if(key == CODED) {
    		if(keyCode == UP) {
    			Player1.vy = 0;
    		} else if(keyCode == DOWN) {
    			Player1.vy = 0;
    		}
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
    
    public void reset() {  
    	player1_score = 0;
    	player2_score = 0;
    	game_over = false;
    }
    
    public void setAI(AI a, AI b) {
    	ai_1 = a;
    	ai_2 = b;
    }
}
