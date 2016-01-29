package engine.matrix;

import engine.Vector;

public class ScaleMatrix extends Matrix {
	public Vector scale;

	public ScaleMatrix(double x, double y, double z) {
		scale = new Vector(x, y, z);
	}

	@Override
	public Vector[] apply(Vector[] points, double t) {
		Vector s = new Vector(1 - t, 1 - t, 1 - t).add(scale.multiply(t));
		Vector result[] = new Vector[points.length];

		for (int i = 0; i < points.length; i++)
			result[i] = new Vector(points[i].x * s.x, points[i].y * s.y,
					points[i].z * s.z, points[i].w);

		return result;
	}

	@Override
	public double[][] toArray(double t) {
		double[][] m = new double[4][4];
		m[0][0] = (1-t) + scale.x * t;
		m[1][1] = (1-t) + scale.y * t;
		m[2][2] = (1-t) + scale.z * t;
		m[3][3] = 1;
		return m;
	}

	@Override
	public String toString() {
		return "Scale by " + scale.toCartesianString();
	}
}
