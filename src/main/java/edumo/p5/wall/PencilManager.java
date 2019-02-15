package edumo.p5.wall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;

import edumo.p5.wall.gui.MyControllerSprite;
import edumo.p5.wall.pencils.Pencil;
import edumo.p5.wall.pencils.RealisticSpray;
import edumo.p5.wall.pencils.Spray;
import edumo.p5.wall.pencils.Techi;

public class PencilManager {

	public static String PREFIX = "color";

	Map<String, Pencil[]> pencils = new HashMap<String, Pencil[]>();

	List<Pencil[]> pencils2 = new ArrayList<Pencil[]>();

	PGraphics paintBuffer;
	PApplet applet;

	String[] pngs = { "spray1.png", "spray2.png", "box-particle.png",
			"spray1.png", "spray1.png", "spray1.png", "spray1.png" };


	public PencilManager(PApplet applet, PGraphics paintBuffer) {
		super();
		this.paintBuffer = paintBuffer;
		this.applet = applet;
	}

	int i = 0;
	int xPos = 100;
	int yPos = 200;

	private void add(String name,Pencil[] pencil) {

		int margin = 10;
		i++;

		pencils.put(name, pencil);
		pencils2.add(pencil);

	}

	public Pencil[] initPencils() {


		Pencil[] pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new Spray(applet, paintBuffer, "trazo.png");
		}
		add("trazo",pencils);

		pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new Spray(applet, paintBuffer, "spray3.png");
		}
		add("spray3",pencils);

		pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new RealisticSpray(applet, paintBuffer, "trazo2.png");
		}
		add("trazo2",pencils);

		pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new Spray(applet, paintBuffer, "spray1.png");
		}
		add("spray1",pencils);

		pencils = new Pencil[12];
		for (int i = 0; i < 12; i++) {
			pencils[i] = new Techi(applet, paintBuffer);
		}
		add("Techi",pencils);

		return pencils;
	}

	public Pencil[] selectPencil(String b) {

		return pencils.get(b);
	}
	
	int index = 0;
	
	public Pencil[] nextPencil() {
		index = ++index % pencils2.size(); 
		System.out.println("pencil +"+index);
		return pencils2.get(index);
	}

	public void backGroundDraw() {
		for (int i = 0; i < pencils2.size(); i++) {
			Pencil[] pencil = pencils2.get(i);
			pencil[0].backGroundDraw();
		}
	}

}
