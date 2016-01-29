package engine;

import java.awt.Color;

import engine.display.Display;
import experiments.Experiment;

public class Camera {
	private static int count = 0;
	private int id = ++count;

	/* Type */
	public enum CameraType {
		ORTHOGRAPHIC, PERSPECTIVE;
	}

	public CameraType type;
	/* Clipping Planes */
	public double l = -1, r = 1, t = 1, b = -1, n = -1, f = 1;
	/* Camera position, lookat and up vectors */
	public double ex = 0, ey = 0, ez = 0, lx = 0, ly = 0, lz = -1, ux = 0, uy = 1, uz = 0;
	/* Coordinate System */
	public CoordSystem coordSystem;
	/* Transformation */
	public Transformation transformation;
	public boolean view;

	public Camera(double near, double far, double bottom, double top, double left, double right, CameraType type,
			CoordSystem coordSystem, Transformation transformation) {
		n = near;
		f = far;
		b = bottom;
		t = top;
		l = left;
		r = right;
		this.type = type;
		coordSystem.coordAxesSize = 1;
		this.coordSystem = coordSystem;
		Vector yax = coordSystem.getY();
		Vector zax = coordSystem.getZ();
		Vector ori = coordSystem.getO(); 
		ex = ori.x / ori.w;	ey = ori.y / ori.w;	ez = ori.z / ori.w;
		lx = ex - zax.x;	ly = ey - zax.y;	lz = ez - zax.z;
		ux = yax.x;			uy = yax.y;			uz = yax.z;
		this.transformation = transformation;
		this.view=true;
	}
	
	public Camera(double near, double far, double bottom, double top, double left, double right, 
			double eyex, double eyey, double eyez, 
			double lookx, double looky, double lookz, 
			double upx, double upy, double upz, 
			CameraType type, Transformation transformation) {
		n = near;
		f = far;
		b = bottom;
		t = top;
		l = left;
		r = right;
		this.view=true;
		this.type = type;
		this.transformation = transformation;
		ex = eyex;	ey = eyey;	ez = eyez;
		lx = lookx; ly = looky; lz = lookz;
		ux = upx;	uy = upy;	uz = upz;
		Vector zax = new Vector(ex-lx, ey-ly, ez-lz, 0);
		Vector xax = Vector.CrossProduct(new Vector(ux, uy, uz, 0), zax);
		Vector yax = Vector.CrossProduct(zax, xax);
		this.coordSystem = new CoordSystem(xax, yax, zax, new Vector(ex, ey, ez, 1), Experiment.experiment.world.coordSystemCount);
		this.coordSystem.coordAxesSize = 1;
	}
	
	public void updateCoordSystem() {
		Vector zax = new Vector(ex-lx, ey-ly, ez-lz, 0);
		Vector xax = Vector.CrossProduct(new Vector(ux, uy, uz, 0), zax);
		Vector yax = Vector.CrossProduct(zax, xax);
		this.coordSystem.setVectors(xax, yax, zax, new Vector(ex, ey, ez, 1));
//		this.coordSystem = new CoordSystem(xax, yax, zax, new Vector(ex, ey, ez, 1), Experiment.experiment.world.coordSystemCount);
		this.coordSystem.coordAxesSize = 1;
	}

	void draw(Display display) {
		coordSystem.draw(display, true, true);

		Vector[] c = new Vector[9];
		if (type == CameraType.ORTHOGRAPHIC) {
			c[0] = new Vector(l, t, -n);
			c[1] = new Vector(r, t, -n);
			c[2] = new Vector(r, b, -n);
			c[3] = new Vector(l, b, -n);
			c[4] = new Vector(l, t, -f);
			c[5] = new Vector(r, t, -f);
			c[6] = new Vector(r, b, -f);
			c[7] = new Vector(l, b, -f);
			c[8] = new Vector(0, 0, 0);
		} else if (type == CameraType.PERSPECTIVE) {
			c[0] = new Vector(l, t, -n);
			c[1] = new Vector(r, t, -n);
			c[2] = new Vector(r, b, -n);
			c[3] = new Vector(l, b, -n);

			double s = f / n;
			c[4] = new Vector(l * s, t * s, -f);
			c[5] = new Vector(r * s, t * s, -f);
			c[6] = new Vector(r * s, b * s, -f);
			c[7] = new Vector(l * s, b * s, -f);
			c[8] = new Vector(0, 0, 0);
		}

		c = transformation.apply(c, display);
		for (int i = 0; i < c.length; i++)
			c[i] = coordSystem.toAbsolute(c[i]);

		display.setColor(new Color(0.8f, 0.8f, 0.8f, 0.7f));

		/* Draw the camera trapezoid: */
		display.drawTriangle(c[8], c[1], c[0], true, true);
		display.drawTriangle(c[8], c[2], c[1], true, true);
		display.drawTriangle(c[8], c[3], c[2], true, true);
		display.drawTriangle(c[8], c[0], c[3], true, true);

		/* Draw the frustum */
		if(view) {
		display.setColor(new Color(0.8f, 0.8f, 0.8f, 0.2f));
		display.drawQuad(c[0], c[3], c[2], c[1], true, true);
		display.drawQuad(c[4], c[5], c[6], c[7], true, true);
		display.drawQuad(c[1], c[2], c[6], c[5], true, true);
		display.drawQuad(c[0], c[4], c[7], c[3], true, true);
		display.drawQuad(c[0], c[1], c[5], c[4], true, true);
		display.drawQuad(c[3], c[7], c[6], c[2], true, true);
		}
	}

	@Override
	public String toString() {
		return "Camera " + id;
	}
}
