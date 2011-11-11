package edumo.p5.wall.pencils;

import processing.core.PApplet;
import processing.core.PGraphics;

public class ColorDrop {

	float initPosX = 0, initPosY = 0; // initial position of each drop
	float posY = 0; // variable to set and get position of painting drop (if
					// initPosY+posY < initPosY+lengthDrop then move the
					// drop down)
	float lengthDrop;
	float diameterDrop;
	float dropSpeed;
	int colorDrop; // color of the drop

	PApplet applet;
	PGraphics graphics;

	private void init() {
		lengthDrop = applet.random(50, 100); // length of the drop line
		diameterDrop = applet.random(3f, 3.0f); // diameter of the drop
		dropSpeed = applet.random(0.1f, 5);

	}

	public ColorDrop(PApplet applet, PGraphics graphics, float initPosX_,
			float initPosY_, int colorDrop_) { // position
		// & color
		this.graphics = graphics;
		this.applet = applet;
		initPosX = initPosX_;
		initPosY = initPosY_;
		colorDrop = colorDrop_;
		init();
	}

	public ColorDrop(PApplet applet, PGraphics graphics, float initPosX_,
			float initPosY_, int colorDrop_, float lengthDrop_) { // position,
																	// color &
		this.graphics = graphics;
		this.applet = applet;												// length
		initPosX = initPosX_;
		initPosY = initPosY_;
		lengthDrop = lengthDrop_;
		colorDrop = colorDrop_;
		init();
	}

	public ColorDrop(PApplet applet, PGraphics graphics, float initPosX_,
			float initPosY_, int colorDrop_, float lengthDrop_,
			float diameterDrop_) { // position, color, length
		this.graphics = graphics;
		this.applet = applet;							// & width
		initPosX = initPosX_;
		initPosY = initPosY_;
		lengthDrop = lengthDrop_;
		diameterDrop = diameterDrop_;
		colorDrop = colorDrop_;
		init();
	}

	public boolean moveDrop() {
		if (posY <= lengthDrop) {
			posY += applet.noise(1) * dropSpeed;
			return true;
		} else {
			return false;
		}
	}

	public void drawDrop() {
		applet.noStroke();
		applet.fill(colorDrop, 255 - (int) (posY / lengthDrop * 200));
		applet.strokeWeight(1);
		applet.ellipseMode(PApplet.CENTER);
		applet.ellipse(initPosX, initPosY + posY, diameterDrop,
				diameterDrop * 2);
	}
}
