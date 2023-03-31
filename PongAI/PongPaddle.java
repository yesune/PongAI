
public class PongPaddle {
	
	
	int y;
	int vy;
	int playerID;
	int moving = 0; // 0 for none, 1 for up, -1 for down
	
	public PongPaddle(int y, int vy, int playerID) {
		this.y = y;
		this.vy = vy;
		this.playerID = playerID;
	}
	
	public void update(int max_y, int paddle_width) {
		if(y + vy > max_y - paddle_width) {
			y = max_y - paddle_width;
		} else if(y + vy < 0) {
			y = 0;
		} else {
			y += vy;
		}
		
		vy = 0;
	}
	
	public void moveUp() {
		vy = -8;
		moving = 2;
	}
	
	public void moveDown() {
		vy = 8;
		moving = -2;
	}
	
	
	
}
