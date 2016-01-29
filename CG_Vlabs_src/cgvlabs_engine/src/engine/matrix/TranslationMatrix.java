package engine.matrix;

import engine.Vector;

public class TranslationMatrix extends Matrix {
	public Vector translation;

	public TranslationMatrix(double x, double y, double z) {
		translation = new Vector(x, y, z);
	}

	@Override
	public Vector[] apply(Vector[] points, double t) {
		Vector d = translation.multiply(t);
		Vector[] result = new Vector[points.length];

		for (int i = 0; i < points.length; i++)
			result[i] = points[i].add(d);
		return result;
	}

	@Override
	public double[][] toArray(double t) {
		double[][] m = new double[4][4];

		for(int i = 0; i < 4; i++)
			m[i][i] = 1;

		m[0][3] = translation.x*t;
		m[1][3] = translation.y*t;
		m[2][3] = translation.z*t;

		return m;
	}

	@Override
	public String toString() {
		return "Translation by " + translation.toCartesianString();
	}

}
