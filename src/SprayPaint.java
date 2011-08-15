import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class SprayPaint extends PApplet {

	Drop[] drips;
	int numDrips = 0;
	int red = 255;
	int green = 253;
	int blue = 240;

	public void setup() {
		size(1000, 600);
		background(0);
		drips = new Drop[6000];
		smooth();

	}

	void fastBlur(PImage img, int radius) {

		if (radius < 1) {
			return;
		}
		int w = img.width;
		int h = img.height;
		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, fx, fy, i, p, p1, p2, yp, yi, yw;
		int vmin[] = new int[max(w, h)];
		int vmax[] = new int[max(w, h)];
		img.loadPixels();
		int[] pix = img.pixels;
		int dv[] = new int[256 * div];
		for (i = 0; i < 256 * div; i++) {
			dv[i] = (i / div);
		}

		yw = yi = 0;

		for (fy = 0; fy < h; fy++) {
			rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + min(wm, max(i, 0))];
				rsum += (p & 0xff0000) >> 16;
				gsum += (p & 0x00ff00) >> 8;
				bsum += p & 0x0000ff;
			}
			for (fx = 0; fx < w; fx++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				if (fy == 0) {
					vmin[fx] = min(fx + radius + 1, wm);
					vmax[fx] = max(fx - radius, 0);
				}
				p1 = pix[yw + vmin[fx]];
				p2 = pix[yw + vmax[fx]];

				rsum += ((p1 & 0xff0000) - (p2 & 0xff0000)) >> 16;
				gsum += ((p1 & 0x00ff00) - (p2 & 0x00ff00)) >> 8;
				bsum += (p1 & 0x0000ff) - (p2 & 0x0000ff);
				yi++;
			}
			yw += w;
		}

		for (fx = 0; fx < w; fx++) {
			rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = max(0, yp) + fx;
				rsum += r[yi];
				gsum += g[yi];
				bsum += b[yi];
				yp += w;
			}
			yi = fx;
			for (fy = 0; fy < h; fy++) {
				pix[yi] = 0xff000000 | (dv[rsum] << 16) | (dv[gsum] << 8)
						| dv[bsum];
				if (fx == 0) {
					vmin[fy] = min(fy + radius + 1, hm) * w;
					vmax[fy] = max(fy - radius, 0) * w;
				}
				p1 = fx + vmin[fy];
				p2 = fx + vmax[fy];

				rsum += r[p1] - r[p2];
				gsum += g[p1] - g[p2];
				bsum += b[p1] - b[p2];

				yi += w;
			}
		}

	}

	public void draw() {
		for (int i = 0; i < numDrips; i++) {
			drips[i].drip();
			drips[i].show();
			drips[i].stopping();
		}
		if (mousePressed == true && mouseButton == LEFT && mouseY > 20) {
			// for (int i = 0; i < 5; i++) {
			// float theta = random(0, 2 * PI);
			// int radius = (int) random(0, 20);
			// int x = mouseX + (int) (cos(theta) * radius);
			// int y = mouseY + (int) (sin(theta) * radius);
			// stroke(red, green, blue, 100);
			// fill(red, green, blue);
			// int c = color(red, green, blue);
			// ellipse(x, y, 2, 2);
			radialGradient(mouseX, mouseY, 255, 20);

			// }
			// if (numDrips < 999 && random(1) < .2) {
			// drips[numDrips] = new Drop(mouseX, mouseY, red, green, blue);
			// numDrips++;
			// }
		} else if (mousePressed == true && mouseButton == RIGHT) {
			fill(0);
			stroke(0);
			float theta = random(0, 2 * PI);
			int radius = (int) (random(0, 20));
			int x = mouseX + (int) (cos(theta) * radius);
			int y = mouseY + (int) (sin(theta) * radius);
			ellipse(x, y, 20, 20);
		}
		fastBlur(g, 33);
		
	}

	void radialGradient(float x, float y, int c, int size) {
		PGraphics pg = createGraphics(size, size, JAVA2D);
		pg.beginDraw();
		pg.background(30, 0);
		int halfsize = size / 2;

		for (int i = 0; i <= size; i += 1) {
			for (int j = 0; j <= size; j += 1) {
				// calculate distance to center
				// float distance = (float) Math.hypot(i - size / 2, j - size /
				// 2) / (size / 2);
				// float distance = (float) sqrt(sq(i-size/2) + sq(j-size/2)) /
				// (size/2);
				float xDist = i - halfsize;
				float yDist = j - halfsize;
				float distance = (float) Math.sqrt(xDist * xDist + yDist
						* yDist)
						/ halfsize;
				float scale = 1 - distance;
				if (scale < 0) {
					scale = 0;
				}
				float transparency = 255 * (scale * scale);
				int thisColour = color(c, transparency);
				// pg.fill(thisColour);
				// pg.ellipse(i, j, random(5),random(5));
				pg.set(i, j, thisColour);
			}
		}

		pg.endDraw();
		imageMode(CENTER);
		image(pg, x, y);
	}

	// void actionPerformed (GUIEvent e) {
	// if (e.getSource() == b1) {
	// red=32;
	// green=164;
	// blue=29;
	// }
	// else if (e.getSource() == b2) {
	// red=155;
	// green=48;
	// blue=255;
	// }
	// else if (e.getSource() == b3) {
	// red = 255;
	// green = 253;
	// blue = 240;
	// }
	// else if (e.getSource() == b4) {
	// red=int(random(0,255));
	// green = int(random(0,255));
	// blue = int(random(0,255));
	// }
	// }

	class Drop {
		int x, y, size, r, red, green, blue;
		boolean isMoving;

		Drop(int theX, int theY, int theRed, int theGreen, int theBlue) {
			x = theX;
			y = theY;
			red = theRed;
			green = theGreen;
			blue = theBlue;
			r = (int) (random(399, 600));
			size = 5;
			isMoving = true;
		}

		void drip() {
			if (size > 1 && random(1) < .3) {
				size--;
			}
			if (isMoving == true) {
				y++;
			}
		}

		void stopping() {
			if ((int) (random(100)) == 0) {
				isMoving = false;
			}
		}

		void show() {
			fill(red, green, blue, 100);
			stroke(red, green, blue, 100);
			ellipse(x, y, size, size);
			int c = color(red, green, blue);
			radialGradient(x, y, c, size);
		}
	}

}
