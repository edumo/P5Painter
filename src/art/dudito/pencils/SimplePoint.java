package art.dudito.pencils;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class SimplePoint implements Pencil {

	PApplet applet;
	PVector last = new PVector(-1, -1);
	PGraphics graphics;
	
	int color = 255;

	public SimplePoint(PApplet applet, PGraphics graphics) {
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
			graphics.point(last.x, last.y);
		}
		last.x = x;
		last.y = y;

		//TODO c
		
//		applet.blend(-1, -1,x-10 , y-10, -1, -1, 20, 20, applet.LIGHTEST);
//		if (applet.mousePressed == true) {
//			applet.blend(0, 0, 0, 0, 0, 0, applet.wi, h, applet.SUBTRACT);
//		}
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
