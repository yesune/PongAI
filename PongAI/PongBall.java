import java.lang.Math;

public class PongBall {
	static final int PADDLE_WIDTH = 112;
	
	float x, y;
	float vx, vy;
	int radius;
	
	
	public PongBall(float x, float y, float vx, float vy, int diameter) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.radius = diameter / 2;
	}
	
	int update(int max_x, int max_y, int Player1_y, int Player2_y) {
		boolean hit = false;
		
		// To do, change angle based on movement of paddle
		// Also fix closest_x if you want, idcs
		float closest_y, closest_x;
		int[] players = new int[]{Player1_y, Player2_y};
		
		// temporary x and y, we need to simulate one move in the future
		float temp_x = x + vx;
		float temp_y = y + vy;
		
		
		for(int i = 0; i < players.length; i++) {
			if(temp_y < players[i])
				closest_y = players[i];
			else if(temp_y > players[i] + PADDLE_WIDTH)
				closest_y = players[i] + PADDLE_WIDTH;
			else
				closest_y = temp_y;
			if(i == 0)
				closest_x = 48;
			else
				closest_x = max_x - 48;
			float distance_x = temp_x - closest_x;
			float distance_y = temp_y - closest_y;
			float distance = (float) Math.sqrt(distance_x * distance_x + distance_y * distance_y);
			//System.out.println(distance);
			if(distance <= radius) {
				if(i == 0)
					x = closest_x + radius;
				else
					x = closest_x - radius;
				vx = -vx;
				hit = true;
				
				// time to change the vy depending on how its hit
				float position = (players[i] + PADDLE_WIDTH / 2 - temp_y) / PADDLE_WIDTH;
				//System.out.println(position);
				vy -= position * 5; // I don't know why its minus instead of plus
						
				break;
			}
		}
		//System.out.println("vx: " + vx + ", x: " + x);
		
		if(!hit) {
			// If player is not intersecting with Paddle
			if(x + vx > max_x - radius) {
				return -1;
				/*x -= (x + vx) % (max_x - radius); //IM SO PROUD OF THIS LINE IM USING THIS FOR EVERY BOUNCE MECHANIC
				vx = -vx;*/
			} else if(x + vx < radius) {
				return 1;
				/*x = -vx + x;
				vx = -vx;*/
			} else {
				x += vx;
				if(vx < 0)
					vx -= 0.001; // I WANT TO SLOWLY INCREASE SPEED OF THE GAME SO IT ENDS YOU KNOW
				else
					vx += 0.001;
			}
		}
		if(y + vy > max_y - radius && vy > 0) {
			y -= (y + vy) % (max_y - radius);
			vy = -vy;
		} else if(y + vy < radius && vy < 0) {
			y = -vy + y;
			vy = -vy;
		} else {
			y += vy;
			/*
			if(vy < 0)
				vy -= 0.001; // I WANT TO SLOWLY INCREASE SPEED OF THE GAME SO IT ENDS YOU KNOW
			else
				vy += 0.001;*/
		}
		// final check to limit velocity
		if(vy > 10)
			vy = 10;
		if(vy < -10)
			vy = -10;
		if(vx > 10)
			vx = 10;
		if(vx < -10)
			vx = -10;
			
		return 0;
	}
}
