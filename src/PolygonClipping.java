import processing.core.*;
import processing.xml.*;

import toxi.geom.*;
import toxi.math.waves.*;
import toxi.processing.*;

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

public class PolygonClipping extends PApplet {

	/**
	 * <p>
	 * Demonstration of the Sutherland-Hodgeman 2D polygon clipping algorithm
	 * introduced with toxiclibscore-0019. The screen is divided into a grid of
	 * small rectangles which are used to define virtual viewports and act as
	 * clipping shapes for moving particles (rendered as polygons).
	 * </p>
	 * 
	 * <p>
	 * <strong>Usage</strong>:<br/>
	 * Press any key to turn clipping on/off
	 * </p>
	 */

	/*
	 * Copyright (c) 2010 Karsten Schmidt
	 * 
	 * This library is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU Lesser General Public License as published by
	 * the Free Software Foundation; either version 2.1 of the License, or (at
	 * your option) any later version.
	 * 
	 * http://creativecommons.org/licenses/LGPL/2.1/
	 * 
	 * This library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
	 * General Public License for more details.
	 * 
	 * You should have received a copy of the GNU Lesser General Public License
	 * along with this library; if not, write to the Free Software Foundation,
	 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
	 */

	int COLS = 1;
	int ROWS = 1;

	List cells = new ArrayList();
	ToxiclibsSupport gfx;

	boolean isClipped = true;

	public void setup() {
		size(680, 382);
		smooth();
		gfx = new ToxiclibsSupport(this);
		strokeWeight(2);
		Vec2D cellSize = new Vec2D((float) width / COLS, (float) height / ROWS);
		for (float y = 0; y < height; y += cellSize.y) {
			for (float x = 0; x < width; x += cellSize.x) {
				ClipCell c = new ClipCell(new Rect(x + 2, y + 2,
						cellSize.x - 4, cellSize.y - 4));
				cells.add(c);
			}
		}
	}

	public void draw() {
		background(255, 0, 0);
		noFill();
		stroke(255);
		for (Iterator i = cells.iterator(); i.hasNext();) {
			ClipCell c = (ClipCell) i.next();
			c.update();
			c.draw(gfx, isClipped);
		}
	}

	public void keyPressed() {
		isClipped = !isClipped;
	}

	class Lissajous {

		AbstractWave xmod, ymod;
		float x, y;

		public Lissajous(AbstractWave x, AbstractWave y) {
			xmod = x;
			ymod = y;
		}

		public void update() {
//			x = xmod.update();
//			y = ymod.update();
			x = mouseX;
			y = mouseY;
		}
	}

	class ClipCell {

		Rect bounds;
		Circle dot;
		Lissajous curve;

		public ClipCell(Rect bounds) {
			this.bounds = bounds;
			this.dot = new Circle(bounds.getCentroid(), random(10, 60));
			SineWave wx = new SineWave(0, random(0.01f, 0.08f),
					bounds.width * 0.5f, bounds.getCentroid().x);
			SineWave wy = new SineWave(0, random(0.01f, 0.08f),
					bounds.height * 0.5f, bounds.getCentroid().y);
			this.curve = new Lissajous(wx, wy);
		}

		public void update() {
			curve.update();
			dot.set(curve.x, curve.y);
		}

		public void draw(ToxiclibsSupport gfx, boolean useClipping) {
			Polygon2D poly = dot.toPolygon2D(30);
			if (useClipping) {
				PolygonClipper2D clipper = new SutherlandHodgemanClipper(bounds);
				poly = clipper.clipPolygon(poly);
			}
			gfx.polygon2D(poly);
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "PolygonClipping" });
	}
}
