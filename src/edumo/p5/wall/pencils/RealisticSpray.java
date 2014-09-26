package edumo.p5.wall.pencils;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class RealisticSpray implements Pencil {

	PGraphics graphics;

	PApplet applet;

	int size = 20;

	int stepSize = 8;

	int maxStepSize = 35;

	int color = 255;

	PImage trazo = null;

	PImage current = null;

	PVector last = new PVector(-1, -1);

	public RealisticSpray(PApplet applet, PGraphics graphics, String trazoName) {
		super();
		this.graphics = graphics;
		this.applet = applet;

		trazo = applet.loadImage(trazoName);
		current = trazo.get();
		trazo.resize(size, size);
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		// graphics.ellipse(mouseX, mouseY, size, size);
		// drawUnit(mouseX, mouseY, 175);

		if (last.x > 0) {

			float d = PApplet.dist(last.x, last.y, mouseX, mouseY);

			if (d > 500) {

				// java.util.List vertices = getVerticesSpline();
				// if (vertices != null)
				// for (int index = 0; index < vertices.size(); index++) {
				//
				// drawLine(d, last, new PVector(mouseX, mouseY));
				// }

			} else if (d > stepSize) {
				drawContinum(last, new PVector(mouseX, mouseY), d);
			} else
				drawUnit(mouseX, mouseY, (int) (175 / d));

		} else {
			drawUnit(mouseX, mouseY, 175);
		}

		last.x = mouseX;
		last.y = mouseY;

	}

	public void drawUnit(int mouseX, int mouseY, int alpha) {
		graphics.pushStyle();
		graphics.pushMatrix();
		graphics.translate(mouseX, mouseY);
		graphics.rotate(applet.radians(applet.random(360)));
		graphics.imageMode(PApplet.CENTER);
		graphics.tint(color, alpha);
		graphics.image(trazo, 0, 0);
		graphics.popMatrix();
		graphics.popStyle();
	}

	protected void drawContinum(PVector start, PVector end, float dist) {

		stepSize = 8;

		int steps = (int) (dist / stepSize);

		if (steps > maxStepSize)
			steps = maxStepSize;

		float incX = dist / (float) steps;

		graphics.pushMatrix();
		graphics.translate(start.x, start.y);
		float angle = PApplet.atan2(end.x - start.x, end.y - start.y);
		graphics.rotate(-angle + PApplet.HALF_PI);

		int alpha = 175 / steps;

		for (int i = 0; i < steps; i++) {
			drawUnit((int) (i * incX), 0, alpha);
		}

		graphics.popMatrix();

	}

	@Override
	public void stop() {
		last.x = -1;

		if (applet.mousePressed) {
			applet.println("stopCalled");
		}

	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public void backGroundDraw() {
		// TODO Auto-generated method stub

	}

}
