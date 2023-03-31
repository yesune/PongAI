
public class PongMatch {
	static final int SCORE_TO_WIN = 3;
	
	
	private AI left, right;
	private PongBall ball;
	private PongPaddle left_paddle, right_paddle;
	private int gametime;
	private boolean game_over;
	
	PongMatch(AI left, AI right, PongBall ball, PongPaddle left_paddle, PongPaddle right_paddle) {
		this.left = left;
		this.right = right;
		this.ball = ball;
		this.left_paddle = left_paddle;
		this.right_paddle = right_paddle;
		gametime = 0;
		game_over = false;
	}
	
	public AI get_left() {
		return left;
	}
	
	public AI get_right() {
		return right;
	}
	
	public PongBall get_ball() {
		return ball;
	}
	
	public PongPaddle get_left_paddle() {
		return left_paddle;
	}
	
	public PongPaddle get_right_paddle() {
		return right_paddle;
	}
	
	public void set_ball(PongBall ball) {
		this.ball = ball;
	}
	
	public void is_over() {
		if(left.score >= SCORE_TO_WIN || right.score >= SCORE_TO_WIN)
			game_over = true;
	}
	
	public void moveUp(int is_left) { // 1 for left, -1 for right
		if(is_left == 1) {
			left_paddle.moveUp();
		} else {
			right_paddle.moveUp();
		}
	}
	
	public void moveDown(int is_left) { // 1 for left, -1 for right
		if(is_left == 1) {
			left_paddle.moveDown();
		} else {
			right_paddle.moveDown();
		}
	}
	
	public void stop_moving(int is_left) {
		if(is_left == 1) {
			left_paddle.vy = 0;
		} else {
			right_paddle.vy = 0;
		}
	}
	
	public int get_gametime() {
		return gametime;
	}
	
	public void increase_time() { 
		gametime++;
	}
	
	public boolean get_game_over() {
		return game_over;
	}
	
	public void set_game_over(boolean b) {
		game_over = b;
	}

	
	
}
