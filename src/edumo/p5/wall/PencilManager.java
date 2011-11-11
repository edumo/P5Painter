package edumo.p5.wall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controlP5.Button;
import controlP5.ControlGroup;
import controlP5.ControlP5;

import processing.core.PApplet;
import processing.core.PGraphics;

import edumo.p5.wall.gui.MyControllerSprite;
import edumo.p5.wall.pencils.Pencil;
import edumo.p5.wall.pencils.RealisticSpray;
import edumo.p5.wall.pencils.Spray;
import edumo.p5.wall.pencils.Techi;

public class PencilManager {

	public static String PREFIX = "color";

	Map<Button, Pencil[]> pencils = new HashMap<Button, Pencil[]>();

	List<Pencil[]> pencils2 = new ArrayList<Pencil[]>();

	PGraphics paintBuffer;
	PApplet applet;
	ControlP5 controlP5;

	String[] pngs = { "spray1.png", "spray2.png", "box-particle.png",
			"spray1.png", "spray1.png", "spray1.png", "spray1.png" };

	public ControlGroup pencilsGroup = null;

	public PencilManager(PApplet applet, PGraphics paintBuffer,
			ControlP5 controlP5) {
		super();
		this.paintBuffer = paintBuffer;
		this.applet = applet;
		this.controlP5 = controlP5;
	}

	int i = 0;
	int xPos = 100;
	int yPos = 200;

	private void add(Pencil[] pencil) {

		int margin = 10;
		i++;

		Button b = controlP5.addButton(pencil.getClass().getSimpleName(),
				Const.PENCIL, xPos, yPos, Const.BUTTON_SIZE, Const.BUTTON_SIZE);

		b.setGroup(pencilsGroup);
		yPos += Const.BUTTON_SIZE + margin;

		MyControllerSprite sprite = new MyControllerSprite(controlP5,
				applet.loadImage("buttonSprite.png"), Const.BUTTON_SIZE,
				Const.BUTTON_SIZE);

		sprite.setImage(applet.loadImage(pngs[i % 7]));

		sprite.tint = false;
		sprite.button = b;
		b.setSprite(sprite);
		b.setGroup(pencilsGroup);

		pencils.put(b, pencil);
		pencils2.add(pencil);

	}

	public Pencil[] initPencils() {

		pencilsGroup = controlP5.addGroup("Pencil-Group", -100, 20);

		Pencil[] pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new Spray(applet, paintBuffer, "trazo.png");
		}
		add(pencils);

		pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new Spray(applet, paintBuffer, "spray3.png");
		}
		add(pencils);

		pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new RealisticSpray(applet, paintBuffer, "trazo2.png");
		}
		add(pencils);

		pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new Spray(applet, paintBuffer, "spray1.png");
		}
		add(pencils);

		pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new Techi(applet, paintBuffer);
		}
		add(pencils);

		return pencils;
	}

	public Pencil[] selectPencil(Button b) {

		return pencils.get(b);
	}

	public void backGroundDraw() {
		for (int i = 0; i < pencils2.size(); i++) {
			Pencil[] pencil = pencils2.get(i);
			pencil[0].backGroundDraw();
		}
	}

}
