package engine.matrix;

import engine.Axis;
import engine.Vector;

public class RotationMatrix extends Matrix {
	public Axis axis;
	public Double theta;
	public Vector axisVector = new Vector(1, 0, 0);

	public RotationMatrix(Axis a, double t) {
		axis = a;
		theta = t;
	}

	public RotationMatrix(Vector a, double t) {
		this(Axis.OTHER, t);
		axisVector = a;
	}

	@Override
	public Vector[] apply(Vector[] points, double t) {
		double tt = Math.toRadians(t * theta);
		double sint = Math.sin(tt);
		double cost = Math.cos(tt);
		double x, y, z;

		Vector result[] = new Vector[points.length];

		if (axis == Axis.X) {
			for (int i = 0; i < points.length; i++) {
				Vector p = points[i];
				y = p.y * cost - p.z * sint;
				z = p.y * sint + p.z * cost;
				result[i] = new Vector(p.x, y, z, p.w);
			}
		} else if (axis == Axis.Y) {
			for (int i = 0; i < points.length; i++) {
				Vector p = points[i];
				x = p.x * cost + p.z * sint;
				z = -p.x * sint + p.z * cost;
				result[i] = new Vector(x, p.y, z, p.w);
			}
		} else if (axis == Axis.Z) {
			for (int i = 0; i < points.length; i++) {
				Vector p = points[i];
				x = p.x * cost - p.y * sint;
				y = p.x * sint + p.y * cost;
				result[i] = new Vector(x, y, p.z, p.w);
			}
		} else {
			Vector av = axisVector.normal();
			double s = 1 - cost, X = av.x, Y = av.y, Z = av.z;
			double m[][] = { 
					{X * X + (Y * Y + Z * Z) * cost, X * Y * s - Z * sint, X * Z * s + Y * sint},
					{X * Y * s + Z * sint, Y * Y + (X * X + Z * Z) * cost, Y * Z * s - Y * sint},
					{X * Z * s - Y * sint, Y * Z * s + X * sint, Z * Z + (X * X + Y * Y) * cost}
			};

			for (int i = 0; i < points.length; i++) {
				Vector p = points[i];
				x = p.x * m[0][0] + p.y * m[0][1] + p.z * m[0][2];
				y = p.x * m[1][0] + p.y * m[1][1] + p.z * m[1][2];
				z = p.x * m[2][0] + p.y * m[2][1] + p.z * m[2][2];
				result[i] = new Vector(x, y, z, p.w);
			}
		}
		return result;
	}

	@Override
	public double[][] toArray(double t) {
		double tt = Math.toRadians(t * theta);
		double sint = Math.sin(tt);
		double cost = Math.cos(tt);

		double[][] m = new double[4][4];
		for (int i = 0; i < 4; i++)
			m[i][i] = 1;

		if (axis == Axis.X) {
			m[1][1] = cost;
			m[1][2] = -sint;
			m[2][1] = sint;
			m[2][2] = cost;
		} else if (axis == Axis.Y) {
			m[0][0] = cost;
			m[0][2] = sint;
			m[2][0] = -sint;
			m[2][2] = cost;
		} else if (axis == Axis.Z) {
			m[0][0] = cost;
			m[0][1] = -sint;
			m[1][0] = sint;
			m[1][1] = cost;
		} else {
			Vector av = axisVector.normal();
			double s = 1 - cost, X = av.x, Y = av.y, Z = av.z;
			m[0][0] = s * X * X + cost;
			m[0][1] = s * X * Y + sint * Z;
			m[0][2] = s * X * Z - sint * Y;
			m[1][0] = s * X * Y - sint * Z;
			m[1][1] = s * Y * Y + cost;
			m[1][2] = s * Y * Z + sint * X;
			m[2][0] = s * X * Y + sint * Y;
			m[2][1] = s * Y * Z - sint * X;
			m[2][2] = s * Z * Z + cost;
		}
		return m;
	}

	@Override
	public String toString() {
		return "Rotation by " + theta + " degrees about " + (axis == Axis.OTHER ? axisVector.toCartesianString() : axis.toString());
	}
}
