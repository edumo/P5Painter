import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;
import spout.Spout;
import KinectPV2.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class HDColor extends PApplet {

	/*
	 * Thomas Sanchez Lengeling. http://codigogenerativo.com/
	 * 
	 * KinectPV2, Kinect for Windows v2 library for processing
	 * 
	 * Simple HD Color test
	 */

	KinectPV2 kinect;

	// DECLARE A SPOUT OBJECT
	Spout spout;
	PGraphics canvas; // Graphics for demo

	public void setup() {

		kinect = new KinectPV2(this);
		kinect.enableColorImg(true);

		kinect.init();

		spout = new Spout(this);

		spout.createSender("kinect-hdcolor");

		canvas = createGraphics(width, height, P2D);
	}

	public void draw() {
		background(0);

		// obtain the color image from the kinect v2
		PImage image = kinect.getColorImage();
		canvas.beginDraw();
		canvas.image(image, 0, 0);
		canvas.endDraw();
		spout.sendTexture(canvas);
		image(canvas, 0, 0, 1920, 1080);

		fill(255, 0, 0);
		text(frameRate, 50, 50);
	}

	public void mousePressed() {
		println(frameRate);
		saveFrame();
	}

	public void settings() {
		size(1920, 1080, P3D);
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "HDColor" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}
