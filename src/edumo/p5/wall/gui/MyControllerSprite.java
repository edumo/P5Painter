package edumo.p5.wall.gui;

import processing.core.PApplet;
import processing.core.PImage;
import controlP5.Button;
import controlP5.ControlP5;
import controlP5.ControllerSprite;

public class MyControllerSprite extends ControllerSprite {

	public Button button;

	PImage image = null;

	public boolean tint = true;

	public MyControllerSprite(ControlP5 arg0, PImage arg1, int w, int h) {
		super(arg0, arg1, w, h);
	}

	public void setImage(PImage image) {
		this.image = image;
		this.image.resize(width, height);
	}

	@Override
	public void draw(PApplet applet) {
		// super.draw(arg0);
		applet.pushStyle();
		applet.fill(0);
		applet.ellipse(-3, -3, button.getWidth() + 6, button.getHeight() + 6);

		if (tint) {
			// CHAPUZEEEEOOOOO esto es para botones con dise�o
			applet.tint(button.getColor().getBackground(), 255);
			// applet.imageMode(PApplet.CENTER);
			applet.image(image, -3, -3);
			applet.image(image, -3, -3);
			applet.image(image, -3, -3);
			applet.image(image, -3, -3);
		}
		applet.image(image, -3, -3);
		//
		// applet.ellipse(0, 0, button.getWidth(), button.getHeight());

		int newState = getState();

		if (newState != 0) {

			applet.noFill();

			if (newState == 1) {
				applet.strokeWeight(1);
			} else {
				applet.strokeWeight(3);
			}

			applet.stroke(255);
			applet.ellipse(-1, -1, button.getWidth() + 2,
					button.getHeight() + 2);
		}

		applet.popStyle();
	}
}
