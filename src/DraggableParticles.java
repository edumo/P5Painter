import processing.core.*;
import processing.xml.*;

import toxi.geom.*;
import toxi.physics.ParticleString;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;

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

public class DraggableParticles extends PApplet {

	/**
	 * <p>
	 * Draggable particle demo showing how to handle mouse events on particles
	 * and allow them to be dragged around with the mouse.
	 * </p>
	 * 
	 * <p>
	 * See original discussion at:
	 * http://processing.org/discourse/yabb2/YaBB.pl?num=1266335586
	 * </p>
	 */

	/*
	 * Copyright (c) 2010 Karsten Schmidt
	 * 
	 * This demo & library is free software; you can redistribute it and/or
	 * modify it under the terms of the GNU Lesser General Public License as
	 * published by the Free Software Foundation; either version 2.1 of the
	 * License, or (at your option) any later version.
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

	VerletPhysics physics;
	VerletParticle selected = null;

	// squared snap distance for picking particles
	float snapDist = 10 * 10;

	public void setup() {
		size(600, 600,P3D);
		smooth();
		PApplet applet = this;
		physics = new VerletPhysics();
		AABB aabb = new AABB(new Vec3D(applet.width, applet.height, 0),
				new Vec3D(applet.width, applet.height, 0));
		physics.setWorldBounds(aabb);
		// create 10 particle strings of 20 particles each
		for (int i = 0; i < 10; i++) {
			ParticleString s = new ParticleString(physics, new Vec3D(width / 2,
					height / 2, 0), Vec3D.fromXYTheta(i * 0.1f * TWO_PI)
					.scaleSelf(10), 20, 1, 0.5f);
		}
	}

	public void draw() {
		background(255);
		noFill();
		physics.update();
		// draw all springs
		for (Iterator i = physics.springs.iterator(); i.hasNext();) {
			VerletSpring s = (VerletSpring) i.next();
			line(s.a.x, s.a.y, s.b.x, s.b.y);
		}
		// draw all particles
		for (Iterator i = physics.particles.iterator(); i.hasNext();) {
			VerletParticle p = (VerletParticle) i.next();
			// selected particle in cyan, all others in black
			stroke(p == selected ? 0xff00ffff : 0xff000000);
			ellipse(p.x, p.y, 5, 5);
		}
	}

	// check all particles if mouse pos is less than snap distance
	public void mousePressed() {
		selected = null;
		Vec3D mousePos = new Vec3D(mouseX, mouseY, 0);
		for (Iterator i = physics.particles.iterator(); i.hasNext();) {
			VerletParticle p = (VerletParticle) i.next();
			// if mouse is close enough, keep a reference to
			// the selected particle and lock it (becomes unmovable by physics)
			if (p.distanceToSquared(mousePos) < snapDist) {
				selected = p;
				selected.lock();
				break;
			}
		}
	}

	// only react to mouse dragging events if we have a selected particle
	public void mouseDragged() {
		if (selected != null) {
			selected.set(mouseX, mouseY, 0);
		}
	}

	// if we had a selected particle unlock it again and kill reference
	public void mouseReleased() {
		if (selected != null) {
			selected.unlock();
			selected = null;
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "DraggableParticles" });
	}
}
