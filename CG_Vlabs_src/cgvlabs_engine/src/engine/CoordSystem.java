package engine;

import engine.display.Display;
import engine.matrix.Matrix;
import engine.matrix.SingularMatrixException;

public class CoordSystem {
	public int id;

	private CoordSystem parent;
	public double[][] m, mi;

	public Integer coordAxesSize = 10;
	public Boolean showUnits = true, isDrawn = true;

	public CoordSystem(Vector x, Vector y, Vector z, Vector o, int id) {
		m = new double[4][4];
		mi = new double[4][4];

		setVectors(x, y, z, o);
		this.id = id;
	}

	public CoordSystem(Vector x, Vector y, Vector o, int id) {
		m = new double[4][4];
		mi = new double[4][4];

		setVectors(x, y, o);
		this.id = id;
	}

	public CoordSystem(Vector x, Vector y, Vector z, Vector o, Integer coordAxesSize, boolean showUnits, boolean isDrawn, int id) {
		this(x, y, z, o, id);
		this.coordAxesSize = coordAxesSize;
		this.showUnits = showUnits;
		this.isDrawn = isDrawn;
	}

	public void setParent(CoordSystem parent) {
		this.parent = parent;
	}

	public CoordSystem getParent() {
		return parent;
	}

	public void setVectors(Vector x, Vector y, Vector z, Vector o) {
		m[0][0] = x.x;
		m[1][0] = x.y;
		m[2][0] = x.z;
		m[3][0] = 0;
		m[0][1] = y.x;
		m[1][1] = y.y;
		m[2][1] = y.z;
		m[3][1] = 0;
		m[0][2] = z.x;
		m[1][2] = z.y;
		m[2][2] = z.z;
		m[3][2] = 0;
		m[0][3] = o.x;
		m[1][3] = o.y;
		m[2][3] = o.z;
		m[3][3] = o.w;

		try {
			Matrix.inverse(m, mi);
		} catch (SingularMatrixException sme) {
			System.err.println("Singular matrix in CoordSystem");
			sme.printStackTrace();
		}
	}

	public void setVectors(Vector x, Vector y, Vector o) {
		setVectors(x, y, Vector.CrossProduct(x, y), o);
	}

	public Vector getX() {
		return new Vector(m[0][0], m[1][0], m[2][0], m[3][0]);
	}

	public void setX(Vector x) {
		m[0][0] = x.x;
		m[1][0] = x.y;
		m[2][0] = x.z;
		m[3][0] = 0;
		try {
			Matrix.inverse(m, mi);
		} catch (SingularMatrixException sme) {
			System.err.println("Singular matrix in CoordSystem");
			sme.printStackTrace();
		}
	}

	public Vector getY() {
		return new Vector(m[0][1], m[1][1], m[2][1], m[3][1]);
	}

	public void setY(Vector y) {
		m[0][1] = y.x;
		m[1][1] = y.y;
		m[2][1] = y.z;
		m[3][1] = 0;
		try {
			Matrix.inverse(m, mi);
		} catch (SingularMatrixException sme) {
			System.err.println("Singular matrix in CoordSystem");
			sme.printStackTrace();
		}
	}

	public Vector getZ() {
		return new Vector(m[0][2], m[1][2], m[2][2], m[3][2]);
	}

	public void setZ(Vector z) {
		m[0][2] = z.x;
		m[1][2] = z.y;
		m[2][2] = z.z;
		m[3][2] = 0;
		try {
			Matrix.inverse(m, mi);
		} catch (SingularMatrixException sme) {
			System.err.println("Singular matrix in CoordSystem");
			sme.printStackTrace();
		}
	}

	public Vector getO() {
		return new Vector(m[0][3], m[1][3], m[2][3], m[3][3]);
	}

	public void setO(Vector o) {
		m[0][3] = o.x;
		m[1][3] = o.y;
		m[2][3] = o.z;
		m[3][3] = o.w;
		try {
			Matrix.inverse(m, mi);
		} catch (SingularMatrixException sme) {
			System.err.println("Singular matrix in CoordSystem");
			sme.printStackTrace();
		}
	}

	public Vector[] getVectors() {
		Vector[] v = new Vector[4];
		for (int i = 0; i < 4; i++)
			v[i] = new Vector(m[0][i], m[1][i], m[2][i], m[3][i]);
		return v;
	}

	public Vector[] getAbsoluteVectors() {
		Vector[] v = getVectors();
		if (parent != null) {
			for (int i = 0; i < 4; i++)
				v[i] = parent.toAbsolute(v[i]);
		}
		return v;
	}
	
	public double[][] getAbsoluteMatrix(double[][] ma) {
		ma = Matrix.multiply(m, ma);
		if (parent != null)
			ma = parent.getAbsoluteMatrix(ma);
		return ma;
	}

	/*
	 * Converts the vector from this coordinate system to the global coordinate
	 * system
	 */
	public Vector toAbsolute(Vector p) {
		Vector pa = toParent(p);
		if (parent != null)
			pa = parent.toAbsolute(pa);
		return pa;
	}

	/*
	 * Converts the vector from this coordinate system to parent's coordinate
	 * system
	 */
	public Vector toParent(Vector p) {
		return Matrix.multiply(m, p);
	}

	/*
	 * Returns a vector in this coordinate system represented by p in the global
	 * coordinate system
	 */
	public Vector toRelative(Vector p) {
		Vector pr = p;
		if(parent != null)
			pr = parent.toRelative(p);
		return Matrix.multiply(mi, pr);
	}

	public void draw(Display display, boolean isCurrent, boolean isActive) {
		if(isDrawn)
			display.drawCoordSystem(this, isCurrent, isActive);
	}

	public String toString() {
		return "Co-ordinate System " + id;
	}
}
