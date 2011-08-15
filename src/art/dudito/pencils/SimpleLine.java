package art.dudito.pencils;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class SimpleLine implements Pencil {

	PApplet applet;
	PVector last = new PVector(-1, -1);
	PGraphics graphics;
	int color = 255;

	public SimpleLine(PApplet applet, PGraphics graphics) {
		this.graphics = graphics;
		this.applet = applet;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see art.dudito.pencils.Pencil#draw(int, int)
	 */
	@Override
	public void draw(int x, int y) {

		if (last.x > 0) {
			graphics.line(last.x, last.y, x, y);
		}
		last.x = x;
		last.y = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see art.dudito.pencils.Pencil#stop()
	 */
	@Override
	public void stop() {
		last.x = -1;
		last.y = -1;
	}

	@Override
	public void backGroundDraw() {
		// TODO Auto-generated method stub
		
	}

}
