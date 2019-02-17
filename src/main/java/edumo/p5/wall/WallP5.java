package edumo.p5.wall;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

import com.thomasdiewald.pixelflow.java.DwPixelFlow;
import com.thomasdiewald.pixelflow.java.imageprocessing.filter.DwFilter;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphics2D;
import spout.Spout;
import TUIO.TuioCursor;
import TUIO.TuioProcessing;
import edumo.p5.wall.gui.ControllerSprite;
import edumo.p5.wall.pencils.Pencil;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlGroup;
import controlP5.ControlP5;
import deadpixel.keystone.CornerPinSurface;
import deadpixel.keystone.Keystone;

public class WallP5 extends PApplet {

	ControlP5 controlP5;

	Pencil[] currentPencil = null;

	int guiX, guiY = 0;

	ControlGroup colorsGroup = null;

	PGraphics paintBuffer = null;

	ColorManager colorManager = null;

	PencilManager pencilManager = null;

	TuioProcessing tuio;

	int tuioX, tuioY = 0;
	boolean tuioPressed = false;

	Map<Integer, TuioCursor> tuios = new HashMap<Integer, TuioCursor>();

	Spout spout;
	PGraphics videoSpout = null;

	Keystone ks;
	CornerPinSurface surface;
	
	boolean calibrating = false;
	
	DwPixelFlow context;

	DwFilter filter;

	PGraphics2D pg_src_A;

	PGraphics2D pg_src_B;
	PGraphics2D pg_src_C; // just another buffer for temporary results
	
	OscP5 oscP5;
	
	boolean draw = false;
	
	int xRight = 0;
	int yRight = 0;
	
	int xLeft = 0;
	int yLeft = 0;

	@Override
	public void settings() {
		size(1920, 1080, P3D);
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
		//myRemoteLocation = new NetAddress("127.0.0.1", 12002);
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
			println(x, y);

			xRight = (int) map(x, -1f, 1f, 0, width);
			yRight = (int) map(y, 1.2f, 0f, 0, height);
			
			xLeft = (int) map(x, -1f, 1f, 0, width);
			yLeft = (int) map(y, 1.2f, 0f, 0, height);
		}
	}


	public void setup() {

		// hint(ENABLE_OPENGL_4X_SMOOTH);
		// hint(ENABLE_ACCURATE_TEXTURES);
		// hint(ENABLE_NATIVE_FONTS);
		smooth();
		frameRate(60);

		// paintBuffer = createGraphics(width, height, P2D);
		// paintBuffer = new GLGraphicsOffScreen(this, width, height, true, 4);
		// paintBuffer.hint(ENABLE_OPENGL_4X_SMOOTH);
		// paintBuffer.hint(ENABLE_ACCURATE_TEXTURES);
		// paintBuffer.hint(ENABLE_NATIVE_FONTS);

		// paintBuffer = g;
		paintBuffer = createGraphics(width, height, P3D);
		guiY = height - 100;

		videoSpout = createGraphics(width, height, P2D);

		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(true);

		ControllerSprite sprite = new ControllerSprite(controlP5, loadImage("buttonSprite.png"), 75, 75);
		sprite.setMask(loadImage("buttonSpriteMask.png"));
		sprite.enableMask();

		Button b = controlP5.addButton("play", Const.NEW_COLORS, guiX + 80, guiY, 50, 220);
		// b.setSprite(sprite);

		b = controlP5.addButton("stop", 102, guiX + 140, guiY, 50, 220);
		// b.setSprite(sprite);

		colorsGroup = controlP5.addGroup("Color-Group", width - 150, 20);

		colorManager = new ColorManager(this, controlP5, colorsGroup);

		pencilManager = new PencilManager(this, paintBuffer);
		currentPencil = pencilManager.initPencils();

		currentPencil = pencilManager.selectPencil("Techi");

		background(0);

		tuio = new TuioProcessing(this, 3333);

		spout = new Spout(this);

		ks = new Keystone(this);
		surface = ks.createCornerPinSurface(width, height, 20);
		
		
		context = new DwPixelFlow(this);
		context.print();
		context.printGL();

		filter = new DwFilter(context);

		pg_src_A = (PGraphics2D) createGraphics(width, height, P2D);
		pg_src_A.smooth(8);


		pg_src_B = (PGraphics2D) createGraphics(width, height, P2D);
		pg_src_B.smooth(8);

		pg_src_C = (PGraphics2D) createGraphics(width, height, P2D);
		pg_src_C.smooth(8);


	}

	// called when a cursor is added to the scene
	public void addTuioCursor(TuioCursor tcur) {
		if (tcur.getX() != 0) {
			println("add cursor " + tcur.getCursorID() + " (" + tcur.getSessionID() + ") " + tcur.getX() + " "
					+ tcur.getY());
			tuios.put(tcur.getCursorID(), tcur);
			tuioPressed = true;
		}
	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {
		if (tcur.getX() != 0)
			println("update cursor " + tcur.getCursorID() + " (" + tcur.getSessionID() + ") " + tcur.getX() + " "
					+ tcur.getY() + " " + tcur.getMotionSpeed() + " " + tcur.getMotionAccel());

		tuios.put(tcur.getCursorID(), tcur);
	}

	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) {
		if (tcur.getX() != 0)
			println("remove cursor " + tcur.getCursorID() + " (" + tcur.getSessionID() + ")");
		tuios.remove(tcur.getCursorID());
	}

	public void draw() {
		background(0);

		spout.receiveTexture(videoSpout);

		image(videoSpout, 0, 0);

		paintBuffer.beginDraw();

		// hint(ENABLE_DEPTH_TEST);
		paintBuffer.pushMatrix();

		paintBuffer.stroke(255);
		paintBuffer.strokeWeight(10);

		if (mousePressed && !calibrating) {
			currentPencil[0].setColor(colorManager.color.toARGB());
			currentPencil[0].draw(mouseX, mouseY);
		} else {
			currentPencil[0].stop();
		}
		// if (tuioPressed) {
		// currentPencil.setColor(colorManager.color.toARGB());
		// currentPencil.draw(tuioX, tuioY);
		// } else {
		// currentPencil.stop();
		// }

		if (draw) {
			currentPencil[0].draw(xRight, yRight);
			currentPencil[0].draw(xRight, yRight);
		}

		pencilManager.backGroundDraw();

		paintBuffer.pushStyle();
		paintBuffer.strokeWeight(1);
		paintBuffer.fill(0);
		paintBuffer.rect(0, 0, 50, 20);
		paintBuffer.fill(255);
		paintBuffer.text(frameRate, 10, 10);
		paintBuffer.popStyle();

		if (tuioPressed) {

			int[][] coords = { { tuioX, tuioY, MouseEvent.MOUSE_PRESSED } };
			// controlP5.controlWindow.multitouch(coords);
			int[][] coords2 = { { tuioX, tuioY, MouseEvent.MOUSE_RELEASED } };
			// controlP5.controlWindow.multitouch(coords2);
		}

		if (mousePressed) {
			int[][] coords = { { mouseX, mouseY, MouseEvent.MOUSE_PRESSED } };
			// controlP5.controlWindow.multitouch(coords);
			int[][] coords2 = { { mouseX, mouseY, MouseEvent.MOUSE_RELEASED } };
			// controlP5.controlWindow.multitouch(coords2);
		}

		// pushStyle();
		// controlP5.draw();
		// popStyle();

		paintBuffer.popMatrix();
		// hint(DISABLE_DEPTH_TEST);
		paintBuffer.endDraw();
		
		pg_src_A.beginDraw();
		pg_src_A.background(0,0);
		pg_src_A.image(paintBuffer,0,0);
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

		
		// image(paintBuffer, 0, 0);
		surface.render(pg_src_A);

	}

	private void newColors() {

	}

	@Override
	public void keyPressed() {
		currentPencil = pencilManager.nextPencil();
		switch (key) {
		case 'c':
			// enter/leave calibration mode, where surfaces can be warped
			// and moved
			ks.toggleCalibration();
			calibrating = !calibrating;
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

	public void play(int theValue) {

		if (theValue == Const.PENCILS) {
			// pencilManager.pencilsGroup.setVisible(!pencilsGroup.isVisible());
		} else if (theValue == Const.NEW_COLORS) {
			colorManager.newRandomColors();
		} else

			background(0);
	}

	public void controlEvent(ControlEvent theEvent) {
		if (theEvent.isGroup()) {
			println("got an event from group " + theEvent.group().getName() + ", isOpen? " + theEvent.group().isOpen());
		} else if (theEvent.isController()) {
			println("got something from a controller " + theEvent.controller().getName());

			String name = theEvent.controller().getName();

			// Pencil[] tempPencil = pencilManager.selectPencil((Button) theEvent
			// .controller());
			// if (tempPencil != null)
			// currentPencil = tempPencil;

			if (name.startsWith(ColorManager.PREFIX)) {

				colorManager.selectColor(name);

			} else if (name.startsWith(PencilManager.PREFIX)) {

			} else {

			}
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--present", "--bgcolor=#F0F0F0", "edumo.p5.wall.WallP5" });
	}
}
