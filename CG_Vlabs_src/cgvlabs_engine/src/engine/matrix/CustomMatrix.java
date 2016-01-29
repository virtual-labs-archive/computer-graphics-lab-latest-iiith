package engine.matrix;

import engine.Vector;

public class CustomMatrix extends Matrix {
	public double m[][];

	public CustomMatrix(double _m[][]) {
		m = new double[4][4];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				m[i][j] = _m[i][j];
	}

	@Override
	public Vector[] apply(Vector[] points, double t) {
		double mt[][] = new double[4][4];
		/*
		 * Interpolate between the identity matrix and m
		 */
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				if (i == j)
					mt[i][j] = (1 - t) + m[i][j] * t;
				else
					mt[i][j] = m[i][j] * t;
			}

		Vector result[] = new Vector[points.length];
		/*
		 * Multiply the matrix and Vector
		 */
		for (int i = 0; i < points.length; i++) {
			double x = points[i].x, y = points[i].y, z = points[i].z, w = points[i].w;
			double rx, ry, rz, rw;

			rx = mt[0][0] * x + mt[0][1] * y + mt[0][2] * z + mt[0][3] * w;
			ry = mt[1][0] * x + mt[1][1] * y + mt[1][2] * z + mt[1][3] * w;
			rz = mt[2][0] * x + mt[2][1] * y + mt[2][2] * z + mt[2][3] * w;
			rw = mt[3][0] * x + mt[3][1] * y + mt[3][2] * z + mt[3][3] * w;

			result[i] = new Vector(rx, ry, rz, rw);
		}
		return result;
	}

	@Override
	public double[][] toArray(double t) {
		double[][] mt = new double[4][4];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				if (i == j)
					mt[i][j] = (1 - t) + m[i][j] * t;
				else
					mt[i][j] = m[i][j] * t;
			}
		return mt;
	}

	@Override
	public String toString() {
		return "Custom";
	}
}
