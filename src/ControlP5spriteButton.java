import processing.core.*;
import processing.xml.*;

import TUIO.TuioCursor;
import TUIO.TuioProcessing;
import art.dudito.ColorManager;
import art.dudito.Const;
import art.dudito.PencilManager;
import art.dudito.pencils.RealisticSpray;
import art.dudito.pencils.Pencil;
import art.dudito.pencils.SimpleLine;
import art.dudito.pencils.SimplePoint;
import art.dudito.pencils.Spray;
import art.dudito.pencils.Techi;
import controlP5.*;

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

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphicsOffScreen;

public class ControlP5spriteButton extends PApplet {

	/**
	 * ControlP5 SpriteButton
	 * 
	 * IMPORTANT !! ControlerSprite is yet experimental and will undergo changes
	 * and modifications. required png images are included in the data folder of
	 * thius sketch.
	 * 
	 * by andreas schlegel, 2009
	 */

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

	public void setup() {
		size(1024, 768,OPENGL);
		// size(1024, 768, GLConstants.GLGRAPHICS);
		hint(ENABLE_OPENGL_4X_SMOOTH);
		hint(ENABLE_ACCURATE_TEXTURES);
		hint(ENABLE_NATIVE_FONTS);
		smooth();
		frameRate(60);

		// paintBuffer = createGraphics(width, height, P2D);
		// paintBuffer = new GLGraphicsOffScreen(this, width, height, true, 4);
		// paintBuffer.hint(ENABLE_OPENGL_4X_SMOOTH);
		// paintBuffer.hint(ENABLE_ACCURATE_TEXTURES);
		// paintBuffer.hint(ENABLE_NATIVE_FONTS);

		paintBuffer = g;
		guiY = height - 100;

		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(true);

		ControllerSprite sprite = new ControllerSprite(controlP5,
				loadImage("buttonSprite.png"), 75, 75);
		sprite.setMask(loadImage("buttonSpriteMask.png"));
		sprite.enableMask();

		Button b = controlP5.addButton("play", Const.NEW_COLORS, guiX + 80,
				guiY, 50, 220);
		b.setSprite(sprite);

		b = controlP5.addButton("play", 102, guiX + 140, guiY, 50, 220);
		b.setSprite(sprite);

		colorsGroup = controlP5.addGroup("Color-Group", width - 150, 20);

		colorManager = new ColorManager(this, controlP5, colorsGroup);

		pencilManager = new PencilManager(this, paintBuffer, controlP5);
		currentPencil = pencilManager.initPencils();

		background(0);

		tuio = new TuioProcessing(this);
	}

	// called when a cursor is added to the scene
	public void addTuioCursor(TuioCursor tcur) {
		if (tcur.getX() != 0) {
			println("add cursor " + tcur.getCursorID() + " ("
					+ tcur.getSessionID() + ") " + tcur.getX() + " "
					+ tcur.getY());
			tuios.put(tcur.getCursorID(), tcur);
			tuioPressed = true;
		}
	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {
		if (tcur.getX() != 0)
			println("update cursor " + tcur.getCursorID() + " ("
					+ tcur.getSessionID() + ") " + tcur.getX() + " "
					+ tcur.getY() + " " + tcur.getMotionSpeed() + " "
					+ tcur.getMotionAccel());
		
		tuios.put(tcur.getCursorID(), tcur);
	}

	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) {
		if (tcur.getX() != 0)
			println("remove cursor " + tcur.getCursorID() + " ("
					+ tcur.getSessionID() + ")");
		tuios.remove(tcur.getCursorID());
	}

	public void draw() {
		// background(0);

		// paintBuffer.beginDraw();

		hint(ENABLE_DEPTH_TEST);
		pushMatrix();

		paintBuffer.stroke(255);
		paintBuffer.strokeWeight(10);
 
		if (mousePressed) {
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

		if (!tuios.isEmpty()) {
			for (int i = 0; i < 12; i++) {
				TuioCursor cursor = tuios.get(i);
				if (cursor == null) {
					currentPencil[i].stop();
					
				} else {
					currentPencil[i].setColor(colorManager.color.toARGB());
					currentPencil[i].draw((int) cursor.getScreenX(1024),
							(int) cursor.getScreenY(768));
				}
			}
		}

		pencilManager.backGroundDraw();

		pushStyle();
		strokeWeight(1);
		fill(0);
		rect(0, 0, 50, 20);
		fill(255);
		text(frameRate, 10, 10);
		popStyle();

		if (tuioPressed) {

			int[][] coords = { { tuioX, tuioY, MouseEvent.MOUSE_PRESSED } };
			controlP5.controlWindow.multitouch(coords);
			int[][] coords2 = { { tuioX, tuioY, MouseEvent.MOUSE_RELEASED } };
			controlP5.controlWindow.multitouch(coords2);
		}
		
		if(mousePressed){
			int[][] coords = { { mouseX, mouseY, MouseEvent.MOUSE_PRESSED } };
			controlP5.controlWindow.multitouch(coords);
			int[][] coords2 = { { mouseX, mouseY, MouseEvent.MOUSE_RELEASED } };
			controlP5.controlWindow.multitouch(coords2);
		}

		// pushStyle();
		// controlP5.draw();
		// popStyle();

		popMatrix();
		hint(DISABLE_DEPTH_TEST);

	}

	private void newColors() {

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
			println("got an event from group " + theEvent.group().name()
					+ ", isOpen? " + theEvent.group().isOpen());
		} else if (theEvent.isController()) {
			println("got something from a controller "
					+ theEvent.controller().name());

			String name = theEvent.controller().name();

			Pencil[] tempPencil = pencilManager.selectPencil((Button) theEvent
					.controller());
			if (tempPencil != null)
				currentPencil = tempPencil;

			if (name.startsWith(ColorManager.PREFIX)) {

				colorManager.selectColor(name);

			} else if (name.startsWith(PencilManager.PREFIX)) {

			} else {

			}
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--present", "--bgcolor=#F0F0F0",
				"ControlP5spriteButton" });
	}
}
