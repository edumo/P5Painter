import processing.core.*;
import processing.xml.*;

import java.applet.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.Image;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;

public class plasma extends PApplet {

	int pixelSize = 2;
	PGraphics pg;

	public void setup() {
		size(640, 360,OPENGL);
		// Create buffered image for plasma effect
		pg = createGraphics(160, 90, P2D);
		colorMode(HSB);
		noSmooth();
	}

	public void draw() {
		float xc = 25;

		// Enable this to control the speed of animation regardless of CPU power
		// int timeDisplacement = millis()/30;

		// This runs plasma as fast as your computer can handle
		int timeDisplacement = frameCount;

		// No need to do this math for every pixel
		float calculation1 = sin(radians(timeDisplacement * 0.61655617f));
		float calculation2 = sin(radians(timeDisplacement * -3.6352262f));

		// Output into a buffered image for reuse
		pg.beginDraw();
		pg.loadPixels();

		// Plasma algorithm
		for (int x = 0; x < pg.width; x++, xc += pixelSize) {
			float yc = 25;
			float s1 = 128 + 128 * sin(radians(xc) * calculation1);

			for (int y = 0; y < pg.height; y++, yc += pixelSize) {
				float s2 = 128 + 128 * sin(radians(yc) * calculation2);
				float s3 = 128 + 128 * sin(radians((xc + yc + timeDisplacement * 5) / 2));
				float s = (s1 + s2 + s3) / 3;
				pg.pixels[x + y * pg.width] = color(s, 255 - s / 2.0f, 255);
			}
		}
		pg.updatePixels();
		pg.endDraw();

		// display the results
//		image(pg, 0, 0, width, height);
		
		
		beginShape();
		
		fill(155);
		texture(pg);
		
		 translate(width / 2, height / 2);
		
		vertex(-100, -100, 0, 0, 0);
		  vertex(100, -40, 0, 400, 120);
		  vertex(0, 100, 0, 200, 400);
		endShape();

	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "plasma" });
	}
}
