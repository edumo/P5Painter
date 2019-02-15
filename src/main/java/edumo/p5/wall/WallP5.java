package edumo.p5.wall;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;
import TUIO.TuioCursor;
import TUIO.TuioProcessing;
import edumo.p5.wall.gui.ControllerSprite;
import edumo.p5.wall.pencils.Pencil;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlGroup;
import controlP5.ControlP5;

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

	@Override
	public void settings() {
		size(1024, 768, P3D);
	}
	
	public void setup() {
		
//		hint(ENABLE_OPENGL_4X_SMOOTH);
//		hint(ENABLE_ACCURATE_TEXTURES);
//		hint(ENABLE_NATIVE_FONTS);
		smooth();
		frameRate(60);

		// paintBuffer = createGraphics(width, height, P2D);
		// paintBuffer = new GLGraphicsOffScreen(this, width, height, true, 4);
		// paintBuffer.hint(ENABLE_OPENGL_4X_SMOOTH);
		// paintBuffer.hint(ENABLE_ACCURATE_TEXTURES);
		// paintBuffer.hint(ENABLE_NATIVE_FONTS);

		//paintBuffer = g;
		paintBuffer = createGraphics(width, height, P3D);
		guiY = height - 100;

		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(true);

		ControllerSprite sprite = new ControllerSprite(controlP5,
				loadImage("buttonSprite.png"), 75, 75);
		sprite.setMask(loadImage("buttonSpriteMask.png"));
		sprite.enableMask();

		Button b = controlP5.addButton("play", Const.NEW_COLORS, guiX + 80,
				guiY, 50, 220);
//		b.setSprite(sprite);

		b = controlP5.addButton("stop", 102, guiX + 140, guiY, 50, 220);
//		b.setSprite(sprite);

		colorsGroup = controlP5.addGroup("Color-Group", width - 150, 20);

		colorManager = new ColorManager(this, controlP5, colorsGroup);

		pencilManager = new PencilManager(this, paintBuffer);
		currentPencil = pencilManager.initPencils();
		
		currentPencil = pencilManager.selectPencil("Techi");
		

		background(0);

		tuio = new TuioProcessing(this, 3333);
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
		 background(0);

		 paintBuffer.beginDraw();

		// hint(ENABLE_DEPTH_TEST);
		 paintBuffer.pushMatrix();

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
			for (int i = 0; i < tuios.size(); i++) {
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

		paintBuffer.pushStyle();
		paintBuffer.strokeWeight(1);
		paintBuffer.fill(0);
		paintBuffer.rect(0, 0, 50, 20);
		paintBuffer.fill(255);
		paintBuffer.text(frameRate, 10, 10);
		paintBuffer.popStyle();

		if (tuioPressed) {

			int[][] coords = { { tuioX, tuioY, MouseEvent.MOUSE_PRESSED } };
			//controlP5.controlWindow.multitouch(coords);
			int[][] coords2 = { { tuioX, tuioY, MouseEvent.MOUSE_RELEASED } };
			//controlP5.controlWindow.multitouch(coords2);
		}

		if (mousePressed) {
			int[][] coords = { { mouseX, mouseY, MouseEvent.MOUSE_PRESSED } };
			//controlP5.controlWindow.multitouch(coords);
			int[][] coords2 = { { mouseX, mouseY, MouseEvent.MOUSE_RELEASED } };
			//controlP5.controlWindow.multitouch(coords2);
		}

		// pushStyle();
		// controlP5.draw();
		// popStyle();

		paintBuffer.popMatrix();
		//hint(DISABLE_DEPTH_TEST);
		paintBuffer.endDraw();
		
		image(paintBuffer,0,0);

	}

	private void newColors() {

	}
	
	@Override
	public void keyPressed() {
		currentPencil = pencilManager.nextPencil();
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
			println("got an event from group " + theEvent.group().getName()
					+ ", isOpen? " + theEvent.group().isOpen());
		} else if (theEvent.isController()) {
			println("got something from a controller "
					+ theEvent.controller().getName());

			String name = theEvent.controller().getName();

			//Pencil[] tempPencil = pencilManager.selectPencil((Button) theEvent
		//			.controller());
//			if (tempPencil != null)
//				currentPencil = tempPencil;

			if (name.startsWith(ColorManager.PREFIX)) {

				colorManager.selectColor(name);

			} else if (name.startsWith(PencilManager.PREFIX)) {

			} else {

			}
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--present", "--bgcolor=#F0F0F0",
				"edumo.p5.wall.WallP5" });
	}
}
