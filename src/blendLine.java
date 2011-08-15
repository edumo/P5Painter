import processing.core.*;
import processing.xml.*;

import processing.opengl.*;
import javax.media.opengl.GL;

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

public class blendLine extends PApplet {

	GL gl;

	float[] x, y, lx, ly, vx, vy;
	int num = 65;

	public void setup() {
		size(1024, 768, OPENGL);
		gl = ((PGraphicsOpenGL) g).gl; // opengl hack
		colorMode(HSB, 1.0f);
		smooth();
		background(0);

		x = new float[num];
		y = new float[num];
		lx = new float[num];
		ly = new float[num];
		vx = new float[num];
		vy = new float[num];
		for (int i = 0; i < num; i++) {
			x[i] = width / 2;
			y[i] = height / 2;
		}
		
		
		frameRate(60);
	}

	public void draw() {
		float f = 0.95f;
		float k1 = 0.01f;
		float k2 = 0.54f;
		stroke(0.73f, 1.0f, 1.0f, 0.06f);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE); // this takes care of the
													// blending
		// blend(0, 0, width, height, 0, 0, width, height, BLEND);
		for (int i = 0; i < num; i++) {
			lx[i] = x[i];
			ly[i] = y[i];
			if (i == 0) {
				vx[i] += k1 * (mouseX - x[i]);
				vy[i] += k1 * (mouseY - y[i]);
				vx[i] *= f;
				vy[i] *= f;
			} else {
				vx[i] = k2 * (x[i - 1] - x[i]);
				vy[i] = k2 * (y[i - 1] - y[i]);
			}
			x[i] += vx[i];
			y[i] += vy[i];
			line(lx[i], ly[i], x[i], y[i]);
		}
		
		text(frameRate,10,10);
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "blendLine" });
	}
}
