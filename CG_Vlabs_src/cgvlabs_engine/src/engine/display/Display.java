package engine.display;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextRenderer;

import engine.CoordSystem;
import engine.Transformation;
import engine.Vector;
import engine.matrix.Matrix;
import engine.matrix.SingularMatrixException;

public class Display {
	public boolean is3D = false;
	public boolean showAnimation = true;
	public boolean transformCoordSystems = false;
	public float fontScale = 0.01f;
	public int currentFrame = 0;

	public DecimalFormat decimalFormat = new DecimalFormat();

	public Theme theme = new Theme();

	public GL2 gl;
	public TextRenderer textRenderer;
	
	public double[] trans = { 1, 0, 0, 0,  0, 1, 0, 0,  0, 0, 1, 0,  0, 0, 0, 1 };
	public CoordSystem coordSystem;
	public Transformation transformation;

	private class TransparentTriangle {
		public Vector[] vertices = new Vector[3];
		public Vector[] normals = new Vector[3];
		public float[] color;

		TransparentTriangle(Vector a, Vector b, Vector c, Vector na, Vector nb, Vector nc, float[] color) {
			vertices[0] = a;
			vertices[1] = b;
			vertices[2] = c;
			normals[0] = na;
			normals[1] = nb;
			normals[2] = nc;
			this.color = color;
		}
	}
	private ArrayList<TransparentTriangle> transparentTriangles = new ArrayList<TransparentTriangle>();

	public Display() {
		decimalFormat.setMaximumFractionDigits(2);
	}

	private void drawGrid() {
		double mx = 20, my = 20;
		gl.glBegin(GL2.GL_LINES);
		for (double x = -mx; x < mx; x++) {
			gl.glVertex2d(x, -my);
			gl.glVertex2d(x,  my);
		}
		for (double y = -my; y <= my; y++) {
			gl.glVertex2d(-mx, y);
			gl.glVertex2d( mx, y);
		}
		gl.glEnd();
	}

	public void init(GL2 gl, TextRenderer textRenderer, boolean isCameraView) {
		this.gl = gl;
		this.textRenderer = textRenderer;

		theme.reset();

		float[] bg = theme.backgroundColor.getComponents(null);
		gl.glClearColor(bg[0], bg[1], bg[2], bg[3]);
		
/*		gl.glColor3f(0, 0, 1);
		gl.glPointSize(20);
		gl.glBegin(GL2.GL_POINTS);
		gl.glVertex3d(0, 0, 0);
		gl.glEnd();
		gl.glPointSize(1);
*/
		if (!isCameraView) {
			updateTransformation();
			gl.glMultMatrixd(trans, 0);			
		}

		/*
		 * Draw the grid
		 */
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glColor3d(0.3, 0.3, 0.3);
		gl.glLineWidth(1);
		gl.glLineStipple(1, (short) 0xAAAA);

		if(theme.drawXYGrid) {
			setColor(theme.gridXYColor);
			drawGrid();
		}
		if(theme.drawYZGrid) {
			setColor(theme.gridYZColor);
			gl.glRotated(90, 0, 1, 0);
			drawGrid();
			gl.glRotated(-90, 0, 1, 0);
		}
		if(theme.drawZXGrid) {
			setColor(theme.gridZXColor);
			gl.glRotated(90, 1, 0, 0);
			drawGrid();
			gl.glRotated(-90, 1, 0, 0);
		}
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glEnable(GL2.GL_LIGHTING);
	}

	/*
	 * These methods will send data to the front-end
	 */
	private void _draw(Vector p) {
		if (is3D)
			gl.glVertex3d(p.x, p.y, p.z);
		else
			gl.glVertex2d(p.x, p.y);
	}

	public String makeLabel(double d) {
		return decimalFormat.format(d);
	}

	public String makeLabel(Vector v) {
		if(is3D)
			return "(" + makeLabel(v.x) + ", " + makeLabel(v.y) + ", " + makeLabel(v.z) + ")";
		else
			return "(" + makeLabel(v.x) + ", " + makeLabel(v.y) + ")";
	}

	/*
	 */
	void drawString(String str, Vector o, Vector d) {
		o = o.toCartesian();
//		d = d.normal();

//		if (d.x < 0)
//			d = d.negate();

//		double rz = Math.toDegrees(Math.atan2(d.y, Math.sqrt(1 - d.y * d.y)));
//		double ry = -Math.toDegrees(Math.atan2(d.z, d.x));

		gl.glPushMatrix();
		gl.glTranslated(o.x, o.y, (is3D ? o.z : 0));
//		gl.glRotated(ry, 0, 1, 0);
//		gl.glRotated(rz, 0, 0, 1);
		gl.glDisable(GL2.GL_LIGHTING);

		textRenderer.begin3DRendering();
		textRenderer.draw3D(str, 0, 0, 0, fontScale);
		textRenderer.end3DRendering();

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glPopMatrix();
	}

	void drawString(String str, Vector o) {
		drawString(str, o, new Vector(1, 0, 0));
	}

	void drawPoint(Vector p, boolean label) {
		p = p.toCartesian();
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(GL2.GL_POINTS);
		_draw(p);
		gl.glEnd();
		gl.glEnable(GL2.GL_LIGHTING);
		if (label) {
			drawString(makeLabel(p), p);
		}
	}

	void drawPoint(Vector p) {
		drawPoint(p, false);
	}

	void drawLine(Vector from, Vector to) {
		from = from.toCartesian();
		to = to.toCartesian();
		gl.glBegin(GL2.GL_LINES);
		_draw(from);
		_draw(to);
		gl.glEnd();
	}

	private void drawTracker(Vector from, Vector to, double length, boolean showUnits) {
		drawPoint(from);
		drawLine(from, to);

		Vector mid = (from.add(to)).divide(2);

		if (showUnits && Math.abs(length) > 0.1) {
			drawString(makeLabel(length), mid, from.subtract(to));
		}
	}

	void drawCoordTracker(Vector p, Vector pa, Vector px, Vector py, Vector pz, boolean showUnits, boolean isCurrent, boolean isActive) {
		if(isCurrent || isActive) {
			gl.glPointSize(2);
	
			gl.glLineWidth(1);
			gl.glLineStipple(1, (short) 0x8888);
	
			setColor(theme.xColors.getNext(isCurrent, isActive));
			drawTracker(px, pz, p.x, showUnits);
	
			setColor(theme.yColors.getNext(isCurrent, isActive));
			drawTracker(py, pz, p.y, showUnits);
	
			if(is3D) {
				setColor(theme.zColors.getNext(isCurrent, isActive));
				drawTracker(pz, pa, p.z, showUnits);
			}
		}
	}

	public void drawVertex(Vector p, Vector pa, CoordSystem c, boolean isCurrent, boolean isActive, boolean trackCoords, boolean showUnits) {
		gl.glPointSize(3);
		drawPoint(pa);

		CoordSystem parent = c.getParent();
		if (parent != null) {
			Vector pp = parent.toRelative(c.toAbsolute(p));
			drawVertex(pp, pa, parent, isCurrent, isActive, trackCoords, parent.showUnits);
		}

		p = p.toCartesian();
		if (trackCoords && c.isDrawn) {
			Vector px = c.toAbsolute(new Vector(0, p.y, 0)).toCartesian();
			Vector py = c.toAbsolute(new Vector(p.x, 0, 0)).toCartesian();
			Vector pz = c.toAbsolute(new Vector(p.x, p.y, 0)).toCartesian();
			drawCoordTracker(p, pa.toCartesian(), px, py, pz, showUnits, isCurrent, isActive);
		}
	}

	public void drawTriangle(Vector a, Vector b, Vector c, boolean drawEdges, boolean drawFaces) {
		a = a.toCartesian();
		b = b.toCartesian();
		c = c.toCartesian();

		Vector n = Vector.CrossProduct(c.subtract(a), b.subtract(a)).normal();

		if (n.z < 0)
			n.z = -n.z;
		
		drawTriangleN(a, b, c, n, n, n, drawEdges, drawFaces);
	}
	public void drawTriangle(Vector a, Vector b, Vector c) {
		drawTriangle(a, b, c, false, true);
	}

	/* With Normals */
	public void drawTriangleN(Vector a, Vector b, Vector c, Vector na, Vector nb, Vector nc, boolean drawEdges, boolean drawFaces) {
		a = a.toCartesian();
		b = b.toCartesian();
		c = c.toCartesian();

		float[] color = new float[4];
		gl.glGetFloatv(GL2.GL_CURRENT_COLOR, color, 0);
		if(drawFaces) {
			transparentTriangles.add(new TransparentTriangle(a, b, c, na, nb, nc, color));
		}

		if(drawEdges) {
			gl.glColor4f(1, 1, 1, 1);
			gl.glLineWidth(1);
			gl.glLineStipple(1, (short) 0xFFFF);
			gl.glBegin(GL2.GL_LINE_STRIP);
				_draw(a);
				_draw(b);
				_draw(c);
				_draw(a);
			gl.glEnd();
			gl.glColor4f(color[0], color[1], color[2], color[3]);
		}
	}
	public void drawTriangleN(Vector a, Vector b, Vector c, Vector na, Vector nb, Vector nc) {
		drawTriangleN(a, b, c, na, nb, nc, false, true);
	}

	public void drawQuad(Vector a, Vector b, Vector c, Vector d, boolean drawEdges, boolean drawFaces) {
		gl.glDisable(GL2.GL_LIGHTING);

		if(drawFaces) {
			drawTriangle(a, b, c);
			drawTriangle(a, c, d);
		}
		float[] color = new float[4];
		gl.glGetFloatv(GL2.GL_CURRENT_COLOR, color, 0);
		if(drawEdges) {
			gl.glColor4f(1,1,1,1);
			gl.glLineWidth(1);
			gl.glLineStipple(1, (short) 0xFFFF);
			gl.glBegin(GL2.GL_LINE_STRIP);
				_draw(a);
				_draw(b);
				_draw(c);
				_draw(d);
				_draw(a);
			gl.glEnd();
			gl.glColor4f(color[0], color[1], color[2], color[3]);
		}
		gl.glEnable(GL2.GL_LIGHTING);
	}

	public void finish() {
		//gl.glDepthMask(false);
		//gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(GL2.GL_TRIANGLES);
		for(TransparentTriangle triangle: transparentTriangles) {
			gl.glColor4f(triangle.color[0], triangle.color[1], triangle.color[2], triangle.color[3]);
			for(int i = 0; i < 3; i++) {
				gl.glNormal3d(triangle.normals[i].x, triangle.normals[i].y, Math.abs(triangle.normals[i].z));
				_draw(triangle.vertices[i]);
			}
		}
		gl.glEnd();
		transparentTriangles.clear();
		//gl.glEnable(GL2.GL_LIGHTING);
		//gl.glDepthMask(true);
	}

	private void drawCoordAxis(Vector o, Vector x, Vector p1, Vector p2, int size) {
		Vector mx = o.subtract(x.multiply(size));
		Vector nx = o.add(x.multiply(size));

		gl.glLineWidth(1);
		gl.glLineStipple(1, (short)0xFFFF);
		gl.glBegin(GL2.GL_LINES);
		_draw(nx);
		_draw(o);
		gl.glEnd();

		gl.glLineWidth(1);
		gl.glLineStipple(1, (short)0x8888);
		gl.glBegin(GL2.GL_LINES);
		_draw(mx);
		_draw(o);
		gl.glEnd();

		gl.glBegin(GL2.GL_POINTS);
		Vector pnx = o.add(x), pmx = o.subtract(x);
		for (double di = 1; di <= size; di++) {
			_draw(pnx);
			_draw(pmx);
			pnx = pnx.add(x);
			pmx = pmx.subtract(x);
		}
		gl.glEnd();

		drawTriangle(nx.add(p1), nx.add(x), nx.subtract(p1));
		drawTriangle(nx.add(p2), nx.add(x), nx.subtract(p2));
		drawTriangle(mx.add(p1), mx.subtract(x), mx.subtract(p1));
		drawTriangle(mx.add(p2), mx.subtract(x), mx.subtract(p2));
	}

	public void drawCoordSystem(CoordSystem c, boolean isCurrent, boolean isActive) {
		int coordAxesSize = c.coordAxesSize;

		Vector[] va = c.getAbsoluteVectors();
		Vector x = va[0], y = va[1], z = va[2], o = va[3];
		x.w = y.w = z.w = 1;

		setColor(theme.originColors.getNext(isCurrent, isActive));
		gl.glPointSize(3);
		drawPoint(o, true);

		gl.glLineStipple(1, (short)0xFFFF);
		gl.glLineWidth(1);
		gl.glDisable(GL2.GL_LIGHTING);

		setColor(theme.xColors.getNext(isCurrent, isActive));
		drawCoordAxis(o, x, y.multiply(.1), z.multiply(.1), coordAxesSize);

		setColor(theme.yColors.getNext(isCurrent, isActive));
		drawCoordAxis(o, y, z.multiply(.1), x.multiply(.1), coordAxesSize);

		if(is3D) {
			setColor(theme.zColors.getNext(isCurrent, isActive));
			drawCoordAxis(o, z, x.multiply(.1), y.multiply(.1), coordAxesSize);
		}

		gl.glEnable(GL2.GL_LIGHTING);
	}

	public void setColor(Color color) {
		float[] c = color.getComponents(null);
		gl.glColor4f(c[0], c[1], c[2], c[3]);
		textRenderer.setColor(c[0], c[1], c[2], c[3]);
	}
	
	public void addTransformation(CoordSystem c, Transformation t) {
		coordSystem = c;
		transformation = t;
	}
	
	public void updateTransformation() {
			double[][] t = { { 1, 0, 0, 0 },
							 { 0, 1, 0, 0 },
							 { 0, 0, 1, 0 },
							 { 0, 0, 0, 1 } };
			if(coordSystem != null) {
				t = coordSystem.getAbsoluteMatrix(t);
				try {
					Matrix.inverse(Matrix.multiply(t, transformation.toArray(this)), t);
				} catch (SingularMatrixException sme) {
					System.err.println("Singular matrix in CoordSystem");
					sme.printStackTrace();
				}
			}
			for (int i=0; i < 4; i++)
				for (int j=0; j < 4; j++)
					trans[i + j*4] = t[i][j];
	}
}
