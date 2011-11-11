package edumo.p5.wall.pencils;

public interface Pencil {

	abstract void draw(int x, int y);

	abstract void stop();
	
	void setColor(int color);
	
	void backGroundDraw();

}