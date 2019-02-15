package edumo.p5.wall.pencils;

import processing.core.*;

import toxi.geom.*;
import toxi.physics.ParticleString;
import toxi.physics.VerletMinDistanceSpring;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.ParticleBehavior;

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

public class Techi implements Pencil {

	VerletPhysics physics;
	int SIZE = 10;
	int drawingMethod = 0;

	int color = 255;
	boolean cumulative = true;
	boolean controlIt = true;

	boolean freeze = false;

	PApplet applet;

	PGraphics graphics;

	boolean first = true;

	int firstCont = 0;

	public Techi(PApplet applet, PGraphics graphics) {
		super();
		this.applet = applet;
		this.graphics = graphics;
		applet.ellipseMode(PApplet.CENTER);
		applet.rectMode(PApplet.CENTER);

		initPhysics();

	}

	public void drawParticles() {

		if (cumulative) {
			graphics.stroke(color, 30);
			graphics.strokeWeight(2);
			for (int i = 1; i < physics.particles.size(); i++) {
				VerletParticle p1 = physics.particles.get(i - 1);
				VerletParticle p2 = physics.particles.get(i);
				graphics.line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
			}

			for (int i = 0; i < physics.springs.size(); i++) {
				VerletSpring spring = physics.springs.get(i);
				applet.line(spring.a.x, spring.a.y, spring.a.z, spring.b.x,
						spring.b.y, spring.b.z);
			}
		} else {
			graphics.background(0);
			graphics.fill(200);
			for (int i = 0; i < physics.particles.size(); i++) {
				graphics.ellipse(physics.particles.get(i).x,
						physics.particles.get(i).y, 2, 2);
			}
			applet.strokeWeight(1);
			for (int i = 0; i < physics.springs.size(); i++) {
				VerletSpring spring = physics.springs.get(i);
				applet.line(spring.a.x, spring.a.y, spring.a.z, spring.b.x,
						spring.b.y, spring.b.z);
			}

		}
	}

	public void initPhysics() {

		float restLength = 20;
		float strength = 0.8f;

		float minRestLength = 50;
		float repulsionStrength = 0.01f;

		int nbParticles = 10;

		physics = new VerletPhysics();

		AABB aabb = new AABB(new Vec3D(applet.width, applet.height, 0),
				new Vec3D(applet.width, applet.height, 0));
		physics.setWorldBounds(aabb);

		for (int i = 0; i < nbParticles; i++) {
			VerletParticle particle = new VerletParticle(applet.random(
					applet.width * .3f, applet.width * .33f), applet.random(
					applet.height * .5f, applet.height * .55f), 0);

			physics.addParticle(particle);
		}

		for (int i = 0; i < physics.particles.size(); i++) {
			physics.addSpring(new VerletSpring(physics.particles.get(0),
					physics.particles.get(i), restLength, strength));
		}

		// for (int i = 0; i < 10; i++) {
		// ParticleString s = new ParticleString(physics, new Vec3D(
		// applet.width / 2, applet.height / 2, 0), Vec3D.fromXYTheta(
		// (float) (i * 0.1 * PApplet.TWO_PI)).scaleSelf(10), 20, 1,
		// 0.5f);
		// }

		if (physics.particles.size() >= 3) {
			physics.addSpring(new VerletSpring(physics.particles.get(1),
					physics.particles.get(physics.particles.size() - 1),
					restLength, strength));
		}

		for (int i = 1; i < physics.particles.size(); i++) {
			physics.addSpring(new VerletMinDistanceSpring(physics.particles
					.get(0), physics.particles.get(i), minRestLength,
					repulsionStrength));
		}
	}

	public void draw(int x, int y) {

		physics.particles.get(0).x = x;
		physics.particles.get(0).y = y;

		if (first) {
			for (int i = 0; i < physics.particles.size(); i++) {
				VerletParticle particle = physics.particles.get(i);
				particle.set(x, y, 0);
			}

			for (int j = 0; j < 10; j++) {
				for (int i = 0; i < physics.particles.size(); i++) {
					VerletParticle particle = physics.particles.get(i);
					particle.set(x, y, 0);
				}
				physics.update();
			}

			first = false;
		}

		if (applet.frameCount % 2 == 0)
			physics.update();

		drawParticles();

	}

	public void stop() {
		first = true;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void backGroundDraw() {
		// TODO Auto-generated method stub

	}
}
