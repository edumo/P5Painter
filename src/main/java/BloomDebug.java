import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import oscP5.*;
import netP5.*;
import com.thomasdiewald.pixelflow.java.DwPixelFlow;
import com.thomasdiewald.pixelflow.java.imageprocessing.filter.DwFilter;
import processing.core.PApplet;
import processing.opengl.PGraphics2D;
import KinectPV2.*;
import deadpixel.keystone.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class BloomDebug extends PApplet {

	/**
	 * 
	 * PixelFlow | Copyright (C) 2016 Thomas Diewald - http://thomasdiewald.com
	 * 
	 * A Processing/Java library for high performance GPU-Computing (GLSL). MIT
	 * License: https://opensource.org/licenses/MIT
	 * 
	 */

	Keystone ks;
	CornerPinSurface surfaceK;

	KinectPV2 kinect;

	DwPixelFlow context;

	DwFilter filter;

	PGraphics2D pg_src_A_NOClear;

	PGraphics2D pg_src_A;

	PGraphics2D pg_src_B;
	PGraphics2D pg_src_C; // just another buffer for temporary results

	ArrayList<PVector> left = new ArrayList();
	ArrayList<PVector> right = new ArrayList();

	OscP5 oscP5;
	NetAddress myRemoteLocation;

	boolean draw = false;
	boolean addPoint = false;
	PVector lastPosition = new PVector();
	int xRight = 0;
	int yRight = 0;

	public void settings() {
		size(1920, 1080, P3D);
		smooth(0);
	}

	public void setup() {
		context = new DwPixelFlow(this);
		context.print();
		context.printGL();

		filter = new DwFilter(context);

		pg_src_A = (PGraphics2D) createGraphics(width, height, P2D);
		pg_src_A.smooth(8);

		pg_src_A_NOClear = (PGraphics2D) createGraphics(width, height, P2D);
		pg_src_A_NOClear.smooth(8);

		pg_src_B = (PGraphics2D) createGraphics(width, height, P2D);
		pg_src_B.smooth(8);

		pg_src_C = (PGraphics2D) createGraphics(width, height, P2D);
		pg_src_C.smooth(8);
		// frameRate(60);
		// frameRate(1000);

		kinect = new KinectPV2(this);

		kinect.enableSkeletonColorMap(true);
		kinect.enableColorImg(true);

		kinect.init();

		setupOSC();

		ks = new Keystone(this);
		surfaceK = ks.createCornerPinSurface(width, height, 20);

	}

	float anim_rotate = 0;

	public void draw() {

		pg_src_A_NOClear.beginDraw();
		pg_src_A_NOClear.line(0, 0, mouseX, mouseY);

		pg_src_A_NOClear.endDraw();

		image(kinect.getColorImage(), 0, 0, width, height);

		int mx = mouseX;
		int my = mouseY;
		mx = width / 2;
		my = height / 2;
		pg_src_A.beginDraw();
		{
			pg_src_A.background(0, 0);
			pg_src_A.noFill();

			pg_src_A.stroke(0, 100, 255);
			pg_src_A.strokeWeight(3);

			ArrayList<KSkeleton> skeletonArray = kinect.getSkeletonColorMap();

			// individual JOINTS
			for (int i = 0; i < skeletonArray.size(); i++) {
				KSkeleton skeleton = (KSkeleton) skeletonArray.get(i);
				if (skeleton.isTracked()) {
					KJoint[] joints = skeleton.getJoints();

					int col = skeleton.getIndexColor();
					fill(col);
					stroke(col);
					drawBody(joints);

					int jointType = KinectPV2.JointType_HandTipRight;

					if (right.size() > 500) {
						right.remove(0);
					}
					KJoint joint = joints[KinectPV2.JointType_HandRight];

					// if (joint.getState() == KinectPV2.HandState_Open) {
					if (addPoint) {
						// right.add(new PVector(joints[jointType].getX(),
						// joints[jointType].getY(), joints[jointType].getZ()));
						right.add(new PVector(xRight, yRight));

						lastPosition.x = xRight;
						lastPosition.y = yRight;
						addPoint = false;
					}

					jointType = KinectPV2.JointType_HandTipLeft;

					if (left.size() > 500) {
						left.remove(0);
					}
					joint = joints[KinectPV2.JointType_HandLeft];

					// if (joint.getState() == KinectPV2.HandState_Open) {
					if (addPoint) {
						left.add(new PVector(joints[jointType].getX(),
								joints[jointType].getY(), joints[jointType]
										.getZ()));
					}
					// draw different color for each hand state
					// drawHandState(joints[KinectPV2.JointType_HandRight]);
					// drawHandState(joints[KinectPV2.JointType_HandLeft]);
				}
			}
			pg_src_A.stroke(0, 100, 255);
			for (int i = 1; i < right.size(); i++) {
				PVector pos = right.get(i);
				PVector pos2 = right.get(i - 1);
				if (PVector.dist(pos, pos2) < 50)
					pg_src_A.line(pos.x, pos.y, pos.z, pos2.x, pos2.y, pos2.z);
			}

			pg_src_A.stroke(255, 100, 0);
			for (int i = 1; i < left.size(); i++) {
				PVector pos = left.get(i);
				PVector pos2 = left.get(i - 1);
				if (PVector.dist(pos, pos2) < 50)
					pg_src_A.line(pos.x, pos.y, pos.z, pos2.x, pos2.y, pos2.z);
			}
		}
		pg_src_A.endDraw();

		pg_src_B.beginDraw();
		pg_src_B.clear();
		pg_src_B.endDraw();

		DwFilter filter = DwFilter.get(context);

		filter.bloom.param.mult = map(mouseX, 0, width, 0, 20);
		filter.bloom.param.radius = map(mouseY, 0, height, 0, 1);

		filter.luminance_threshold.param.threshold = 0.3f;
		filter.luminance_threshold.param.exponent = 10;

		// System.out.println("mult/radius: "+filter.bloom.param.mult+"/"+filter.bloom.param.radius);
		if (!keyPressed) {
			filter.bloom.apply(pg_src_A, pg_src_A, null);
		} else {
			filter.luminance_threshold.apply(pg_src_A, pg_src_B);
			filter.bloom.apply(pg_src_B, pg_src_B, pg_src_A);
		}

		// blendMode(REPLACE);
		// background(0);
		// image(pg_src_A, 0, 0);
		surfaceK.render(pg_src_A);
		// info
		String txt_fps = String.format(getClass().getName()
				+ "   [size %d/%d]   [frame %d]   [fps %6.2f]", pg_src_A.width,
				pg_src_A.height, frameCount, frameRate);
		surface.setTitle(txt_fps);

		text("frameRate" + frameRate, 500, 500);

		if (keyPressed && key == ' ') {
			left = new ArrayList();
			right = new ArrayList();

			pg_src_A_NOClear.beginDraw();
			pg_src_A_NOClear.background(0, 0);

			pg_src_A_NOClear.endDraw();
		}
	}

	// DRAW BODY
	public void drawBody(KJoint[] joints) {
		/*
		 * drawBone(joints, KinectPV2.JointType_Head, KinectPV2.JointType_Neck);
		 * drawBone(joints, KinectPV2.JointType_Neck,
		 * KinectPV2.JointType_SpineShoulder); drawBone(joints,
		 * KinectPV2.JointType_SpineShoulder, KinectPV2.JointType_SpineMid);
		 * drawBone(joints, KinectPV2.JointType_SpineMid,
		 * KinectPV2.JointType_SpineBase); drawBone(joints,
		 * KinectPV2.JointType_SpineShoulder,
		 * KinectPV2.JointType_ShoulderRight); drawBone(joints,
		 * KinectPV2.JointType_SpineShoulder, KinectPV2.JointType_ShoulderLeft);
		 * drawBone(joints, KinectPV2.JointType_SpineBase,
		 * KinectPV2.JointType_HipRight); drawBone(joints,
		 * KinectPV2.JointType_SpineBase, KinectPV2.JointType_HipLeft);
		 */
		// Right Arm
		// drawBone(joints, KinectPV2.JointType_ShoulderRight,
		// KinectPV2.JointType_ElbowRight);
		// drawBone(joints, KinectPV2.JointType_ElbowRight,
		// KinectPV2.JointType_WristRight);
		// drawBone(joints, KinectPV2.JointType_WristRight,
		// KinectPV2.JointType_HandRight);
		drawBone(joints, KinectPV2.JointType_HandRight,
				KinectPV2.JointType_HandTipRight);
		drawBone(joints, KinectPV2.JointType_WristRight,
				KinectPV2.JointType_ThumbRight);

		// Left Arm
		/*
		 * drawBone(joints, KinectPV2.JointType_ShoulderLeft,
		 * KinectPV2.JointType_ElbowLeft); drawBone(joints,
		 * KinectPV2.JointType_ElbowLeft, KinectPV2.JointType_WristLeft);
		 * drawBone(joints, KinectPV2.JointType_WristLeft,
		 * KinectPV2.JointType_HandLeft);
		 */

		drawBone(joints, KinectPV2.JointType_HandLeft,
				KinectPV2.JointType_HandTipLeft);
		drawBone(joints, KinectPV2.JointType_WristLeft,
				KinectPV2.JointType_ThumbLeft);

		// Right Leg
		/*
		 * drawBone(joints, KinectPV2.JointType_HipRight,
		 * KinectPV2.JointType_KneeRight); drawBone(joints,
		 * KinectPV2.JointType_KneeRight, KinectPV2.JointType_AnkleRight);
		 * drawBone(joints, KinectPV2.JointType_AnkleRight,
		 * KinectPV2.JointType_FootRight);
		 * 
		 * // Left Leg drawBone(joints, KinectPV2.JointType_HipLeft,
		 * KinectPV2.JointType_KneeLeft); drawBone(joints,
		 * KinectPV2.JointType_KneeLeft, KinectPV2.JointType_AnkleLeft);
		 * drawBone(joints, KinectPV2.JointType_AnkleLeft,
		 * KinectPV2.JointType_FootLeft);
		 * 
		 * drawJoint(joints, KinectPV2.JointType_HandTipLeft); drawJoint(joints,
		 * KinectPV2.JointType_HandTipRight); drawJoint(joints,
		 * KinectPV2.JointType_FootLeft); drawJoint(joints,
		 * KinectPV2.JointType_FootRight);
		 * 
		 * drawJoint(joints, KinectPV2.JointType_ThumbLeft); drawJoint(joints,
		 * KinectPV2.JointType_ThumbRight);
		 * 
		 * drawJoint(joints, KinectPV2.JointType_Head);
		 */
	}

	// draw joint
	public void drawJoint(KJoint[] joints, int jointType) {
		pg_src_A.pushMatrix();
		pg_src_A.translate(joints[jointType].getX(), joints[jointType].getY(),
				joints[jointType].getZ());
		pg_src_A.ellipse(0, 0, 5, 5);
		pg_src_A.popMatrix();
	}

	// draw bone
	public void drawBone(KJoint[] joints, int jointType1, int jointType2) {
		pg_src_A.pushMatrix();
		pg_src_A.translate(joints[jointType1].getX(),
				joints[jointType1].getY(), joints[jointType1].getZ());
		pg_src_A.ellipse(0, 0, 25, 25);
		pg_src_A.popMatrix();
		pg_src_A.line(joints[jointType1].getX(), joints[jointType1].getY(),
				joints[jointType1].getZ(), joints[jointType2].getX(),
				joints[jointType2].getY(), joints[jointType2].getZ());
	}

	// draw hand state
	public void drawHandState(KJoint joint) {
		noStroke();
		handState(joint.getState());
		pushMatrix();
		translate(joint.getX(), joint.getY(), joint.getZ());
		ellipse(0, 0, 70, 70);
		popMatrix();
	}

	/*
	 * Different hand state KinectPV2.HandState_Open KinectPV2.HandState_Closed
	 * KinectPV2.HandState_Lasso KinectPV2.HandState_NotTracked
	 */
	public void handState(int handState) {
		switch (handState) {
		case KinectPV2.HandState_Open:
			fill(0, 255, 0);
			break;
		case KinectPV2.HandState_Closed:
			fill(255, 0, 0);
			break;
		case KinectPV2.HandState_Lasso:
			fill(0, 0, 255);
			break;
		case KinectPV2.HandState_NotTracked:
			fill(255, 255, 255);
			break;
		}
	}

	public void keyPressed() {
		switch (key) {
		case 'c':
			// enter/leave calibration mode, where surfaces can be warped
			// and moved
			ks.toggleCalibration();
			break;

		case 'l':
			// loads the saved layout
			ks.load();
			break;

		case 's':
			// saves the layout
			ks.save();
			break;
		}
	}

	public void setupOSC() {

		/* start oscP5, listening for incoming messages at port 12000 */
		oscP5 = new OscP5(this, 9000);

		/*
		 * myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters, an
		 * ip address and a port number. myRemoteLocation is used as parameter
		 * in oscP5.send() when sending osc packets to another computer, device,
		 * application. usage see below. for testing purposes the listening port
		 * and the port of the remote location address are the same, hence you
		 * will send messages back to this sketch.
		 */
		myRemoteLocation = new NetAddress("127.0.0.1", 12002);
	}

	/* incoming osc message are forwarded to the oscEvent method. */
	public void oscEvent(OscMessage theOscMessage) {
		/* print the address pattern and the typetag of the received OscMessage */
		// print((millis()-time)+"### received an osc message.");
		print(" addrpattern: " + theOscMessage.addrPattern());
		// println(" typetag: "+theOscMessage.typetag());
		// time = millis();
		if (theOscMessage.addrPattern().contains("pressed")) {
			draw = true;
		}
		if (theOscMessage.addrPattern().contains("released")) {
			draw = false;
		}

		if (theOscMessage.addrPattern().contains("position")) {
			float x = theOscMessage.get(0).floatValue();
			float y = theOscMessage.get(1).floatValue();
			// float z = theOscMessage.get(2).floatValue();
			println(x, y);

			xRight = (int) map(x, -1f, 1f, 0, width);
			yRight = (int) map(y, 1.2f, 0f, 0, height);
			addPoint = true;
		}
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "BloomDebug" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}
