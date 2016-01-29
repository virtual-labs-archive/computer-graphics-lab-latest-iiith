package engine.matrix;

import engine.Vector;

public class SkewMatrix extends Matrix {
	public Vector xs, ys, zs;

	public SkewMatrix(double xy, double xz, double yx, double yz, double zx, double zy) {
		xs = new Vector(1, xy, xz);
		ys = new Vector(yx, 1, yz);
		zs = new Vector(zx, zy, 1);
	}

	@Override
	public Vector[] apply(Vector[] points, double t) {
		Vector result[] = new Vector[points.length];

		Vector txs = new Vector(1, xs.y * t, xs.z * t);
		Vector tys = new Vector(ys.x * t, 1, ys.z * t);
		Vector tzs = new Vector(zs.x * t, zs.y * t, 1);

		for (int i = 0; i < points.length; i++) {
			Vector v = points[i];
			double x, y, z;
			x = Vector.DotProduct(v, txs);
			y = Vector.DotProduct(v, tys);
			z = Vector.DotProduct(v, tzs);
			result[i] = new Vector(x, y, z);
		}
		return result;
	}

	@Override
	public double[][] toArray(double t) {
		double[][] m = new double[4][4];

		for (int i = 0; i < 4; i++)
			m[i][i] = 1;

		m[0][1] = xs.y * t;
		m[0][2] = xs.z * t;
		m[1][0] = ys.x * t;
		m[1][2] = ys.z * t;
		m[2][0] = zs.x * t;
		m[2][1] = zs.y * t;

		return m;
	}

	@Override
	public String toString() {
		return "Skew (Xy=" + xs.y + ", Xz=" + xs.z + ", Yx=" + ys.x + ", Yz=" + ys.z + ", Zx=" + zs.x + ", Zy=" + zs.y
				+ ") ";
	}
}
