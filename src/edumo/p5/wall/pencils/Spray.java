package edumo.p5.wall.pencils;

import java.util.ArrayList;
import java.util.List;

import edumo.p5.wall.Const;


import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import toxi.geom.Spline2D;
import toxi.geom.Vec2D;

public class Spray implements Pencil {

	int size = Const.BRUSH_SIZE;

	List<PVector> points = new ArrayList<PVector>();

	PVector last = new PVector();

	PGraphics graphics;

	PApplet applet;

	int color = 255;

	PImage trazo = null;

	ColorDrop[] drops, delDrops;
	int dropFactor = 0;

	public Spray(PApplet applet, PGraphics graphics, String trazoPath) {
		super();
		this.graphics = graphics;
		this.applet = applet;
		
		trazo = applet.loadImage(trazoPath);
		trazo.resize(size, size);
		
		drops = new ColorDrop[0];
		delDrops = new ColorDrop[0];
	}

	@Override
	public void draw(int mouseX, int mouseY) {
		graphics.stroke(255);
		drawUnit(mouseX, mouseY, 200);
		// radialGradient(mouseX, mouseY, 255, size);
		points.add(new PVector(mouseX, mouseY));
		if (last.x > 0) {
			// graphics.line(last.x, last.y, mouseX, mouseY);

			float d = PApplet.dist(last.x, last.y, mouseX, mouseY);

			if (d > 5000) {

				java.util.List vertices = getVerticesSpline();
				if (vertices != null)
					for (int index = 0; index < vertices.size(); index++) {

						drawLine(d, last, new PVector(mouseX, mouseY));
					}

			} else if (d > 5) {
				drawLine(d, last, new PVector(mouseX, mouseY));
			}

			updateDropFactor(d);

			// vamos a por los drops
			createDropByFactor(mouseX, mouseY);

		}
		last.x = mouseX;
		last.y = mouseY;

	}

	public void createDropByFactor(int mouseX, int mouseY) {
		int randomValueForDrop = applet.floor(applet.random(0, 100));
		if (randomValueForDrop <= dropFactor) {
			ColorDrop setDrop = new ColorDrop(applet, graphics, mouseX, mouseY,
					color);
			drops = (ColorDrop[]) applet.append(drops, setDrop);
			dropFactor = -10;
		}
	}

	public void updateDropFactor(float d) {
		if (d < 2) {
			dropFactor++;
		} else {
			dropFactor--;
			if (dropFactor < -5)
				dropFactor = -5;
		}
	}

	public void backGroundDraw() {
		for (int i = 0; i < drops.length; i++) {
			if (drops[i].moveDrop())
				drops[i].drawDrop();
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		last.x = -1;
		dropFactor = 0;
	}

	public void drawLine(float d, PVector start, PVector end) {

		int steps = (int) d / 2;
		if (steps > 35) {
			steps = 35;
		}

		float incX = d / steps;

		graphics.pushMatrix();
		graphics.translate(start.x, start.y);
		float angle = PApplet.atan2(end.x - start.x, end.y - start.y);
		graphics.rotate(-angle + PApplet.HALF_PI);

		for (int i = 0; i < steps; i++) {
			// radialGradient(i * incX, 0, 255, size);

			drawUnit((int) (i * incX), 0, 150);
		}

		graphics.popMatrix();
	}

	public List getVerticesSpline() {
		java.util.List vertices = null;
		int numP = points.size();

		if (numP > 3) {
			Vec2D[] handles = new Vec2D[numP];
			graphics.pushStyle();
			graphics.fill(255, 0, 0);
			for (int i = 0; i < numP; i++) {
				PVector pv = points.get(i);
				Vec2D v = new Vec2D(pv.x, pv.y);
				handles[i] = v;
				// if (true)
				// ellipse(v.x, v.y, 5, 5);
			}

			Spline2D spline = new Spline2D(handles);
			// sample the curve at a higher resolution
			// so that we get extra 8 points between each original
			// pair
			// of points
			vertices = spline.computeVertices(8);
			// draw the smoothened curve

			graphics.popStyle();
		}

		return vertices;
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

	private void radialGradient(float x, float y, int c, int size) {
		PGraphics pg = applet.createGraphics(size, size, PApplet.JAVA2D);
		pg.beginDraw();
		int halfsize = size / 2;

		for (int i = 0; i <= size; i += 1) {
			for (int j = 0; j <= size; j += 1) {
				// calculate distance to center
				// float distance = (float) Math.hypot(i - size / 2, j - size /
				// 2) / (size / 2);
				// float distance = (float) sqrt(sq(i-size/2) + sq(j-size/2)) /
				// (size/2);
				float xDist = i - halfsize;
				float yDist = j - halfsize;
				float distance = (float) Math.sqrt(xDist * xDist + yDist
						* yDist)
						/ halfsize;
				float scale = 1 - distance;
				if (scale < 0) {
					scale = 0;
				}
				float transparency = 255 * (scale * scale);
				int thisColour = graphics.color(c, (int) transparency);
				pg.set(i, j, thisColour);
			}
		}

		pg.endDraw();
		graphics.pushStyle();
		graphics.imageMode(PApplet.CENTER);
		graphics.tint(color);
		graphics.image(pg, x, y);
		graphics.popStyle();
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

}
