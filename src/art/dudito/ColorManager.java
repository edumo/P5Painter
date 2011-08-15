package art.dudito;

import java.util.ArrayList;
import java.util.List;

import art.dudito.gui.MyControllerSprite;

import processing.core.PApplet;

import controlP5.Button;
import controlP5.CColor;
import controlP5.ControlGroup;
import controlP5.ControlP5;
import controlP5.ControllerSprite;

import toxi.color.ColorList;
import toxi.color.ColorRange;
import toxi.color.TColor;
import toxi.color.theory.ColorTheoryRegistry;
import toxi.color.theory.ColorTheoryStrategy;

/**
 * Responsable de manejar los utilcolor de toxi asociado a botones controlp5
 * 
 * @author dudito
 * 
 */

public class ColorManager {

	private static int NUMCOLORS = 6;

	private int size = 40;

	private int margin = 20;

	private int offset = 200;

	public static String PREFIX = "color";

	private ColorList colors;

	public TColor color;

	private List<Button> cButtons = new ArrayList<Button>();

	public ColorManager(PApplet applet, ControlP5 controlP5, ControlGroup g) {

		// MyControllerSprite sprite = new MyControllerSprite(controlP5,
		// applet.loadImage("buttonSprite.png"), 75, 75);
		// sprite.setMask(applet.loadImage("buttonSpriteMask.png"));
		// sprite.enableMask();

		g.setPosition(applet.width, 0);

		for (int index = 0; index < NUMCOLORS; index++) {
			controlP5.Button b = controlP5.addButton(PREFIX + "-" + index, 0,
					-25, offset + index * (size + margin), size, size);
			MyControllerSprite sprite = new MyControllerSprite(controlP5,
					applet.loadImage("buttonSprite.png"), Const.BUTTON_SIZE,
					Const.BUTTON_SIZE);
			
			sprite.setImage(applet.loadImage("trazo.png"));

			sprite.button = b;
			b.setSprite(sprite);
			b.setGroup(g);
			cButtons.add(b);
		}

		newRandomColors();
	}

	public void newRandomColors() {

		ArrayList strategies = ColorTheoryRegistry.getRegisteredStrategies();

		ColorTheoryStrategy s = (ColorTheoryStrategy) strategies.get(strategies
				.size() - 1);

		colors = ColorList.createUsingStrategy(s, TColor.newRandom());
		// swatches(list, 235, yoff);
		// colors = new ColorRange(colors).addBrightnessRange(0, 1).getColors(
		// null, 100, 0.05f);
		// colors.sortByDistance(false);

		color = colors.get(0);

		for (int index = 0; index < NUMCOLORS; index++) {
			Button button = cButtons.get(index);
			TColor color = colors.get(index);
			CColor cColor = new CColor();
			cColor.setBackground(color.toARGB());

			button.setColor(cColor);
		}

	}

	public ColorList getColors() {
		return colors;
	}

	public void selectColor(String name) {

		if (name.startsWith(PREFIX)) {
			String sub = name.substring(PREFIX.length() + 1, name.length());
			int colorNum = Integer.parseInt(sub);
			color = colors.get(colorNum);
		}

	}
}
