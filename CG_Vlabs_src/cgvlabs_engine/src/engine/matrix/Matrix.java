package engine.matrix;

import engine.CoordSystem;
import engine.Vector;
import engine.display.Display;

public abstract class Matrix {
	public abstract Vector[] apply(Vector[] points, double time);
	public abstract double[][] toArray(double t);

	public static void inverse(double m[][], double mi[][]) throws SingularMatrixException {
	   double det =
		   m[0][3] * m[1][2] * m[2][1] * m[3][0]-m[0][2] * m[1][3] * m[2][1] * m[3][0]-m[0][3] * m[1][1] * m[2][2] * m[3][0]+m[0][1] * m[1][3]    * m[2][2] * m[3][0]+
		   m[0][2] * m[1][1] * m[2][3] * m[3][0]-m[0][1] * m[1][2] * m[2][3] * m[3][0]-m[0][3] * m[1][2] * m[2][0] * m[3][1]+m[0][2] * m[1][3]    * m[2][0] * m[3][1]+
		   m[0][3] * m[1][0] * m[2][2] * m[3][1]-m[0][0] * m[1][3] * m[2][2] * m[3][1]-m[0][2] * m[1][0] * m[2][3] * m[3][1]+m[0][0] * m[1][2]    * m[2][3] * m[3][1]+
		   m[0][3] * m[1][1] * m[2][0] * m[3][2]-m[0][1] * m[1][3] * m[2][0] * m[3][2]-m[0][3] * m[1][0] * m[2][1] * m[3][2]+m[0][0] * m[1][3]    * m[2][1] * m[3][2]+
		   m[0][1] * m[1][0] * m[2][3] * m[3][2]-m[0][0] * m[1][1] * m[2][3] * m[3][2]-m[0][2] * m[1][1] * m[2][0] * m[3][3]+m[0][1] * m[1][2]    * m[2][0] * m[3][3]+
		   m[0][2] * m[1][0] * m[2][1] * m[3][3]-m[0][0] * m[1][2] * m[2][1] * m[3][3]-m[0][1] * m[1][0] * m[2][2] * m[3][3]+m[0][0] * m[1][1]    * m[2][2] * m[3][3];

	   if(det == 0) {
		   throw new SingularMatrixException("Singular matrix encountered in CustomMatrix.Inverse");
	   }

	   mi[0][0] = (m[1][2]*m[2][3]*m[3][1] - m[1][3]*m[2][2]*m[3][1] + m[1][3]*m[2][1]*m[3][2] - m[1][1]*m[2][3]*m[3][2] - m[1][2]*m[2][1]*m[3][3] + m[1][1]*m[2][2]*m[3][3]) / det;
	   mi[0][1] = (m[0][3]*m[2][2]*m[3][1] - m[0][2]*m[2][3]*m[3][1] - m[0][3]*m[2][1]*m[3][2] + m[0][1]*m[2][3]*m[3][2] + m[0][2]*m[2][1]*m[3][3] - m[0][1]*m[2][2]*m[3][3]) / det;
	   mi[0][2] = (m[0][2]*m[1][3]*m[3][1] - m[0][3]*m[1][2]*m[3][1] + m[0][3]*m[1][1]*m[3][2] - m[0][1]*m[1][3]*m[3][2] - m[0][2]*m[1][1]*m[3][3] + m[0][1]*m[1][2]*m[3][3]) / det;
	   mi[0][3] = (m[0][3]*m[1][2]*m[2][1] - m[0][2]*m[1][3]*m[2][1] - m[0][3]*m[1][1]*m[2][2] + m[0][1]*m[1][3]*m[2][2] + m[0][2]*m[1][1]*m[2][3] - m[0][1]*m[1][2]*m[2][3]) / det;
	   mi[1][0] = (m[1][3]*m[2][2]*m[3][0] - m[1][2]*m[2][3]*m[3][0] - m[1][3]*m[2][0]*m[3][2] + m[1][0]*m[2][3]*m[3][2] + m[1][2]*m[2][0]*m[3][3] - m[1][0]*m[2][2]*m[3][3]) / det;
	   mi[1][1] = (m[0][2]*m[2][3]*m[3][0] - m[0][3]*m[2][2]*m[3][0] + m[0][3]*m[2][0]*m[3][2] - m[0][0]*m[2][3]*m[3][2] - m[0][2]*m[2][0]*m[3][3] + m[0][0]*m[2][2]*m[3][3]) / det;
	   mi[1][2] = (m[0][3]*m[1][2]*m[3][0] - m[0][2]*m[1][3]*m[3][0] - m[0][3]*m[1][0]*m[3][2] + m[0][0]*m[1][3]*m[3][2] + m[0][2]*m[1][0]*m[3][3] - m[0][0]*m[1][2]*m[3][3]) / det;
	   mi[1][3] = (m[0][2]*m[1][3]*m[2][0] - m[0][3]*m[1][2]*m[2][0] + m[0][3]*m[1][0]*m[2][2] - m[0][0]*m[1][3]*m[2][2] - m[0][2]*m[1][0]*m[2][3] + m[0][0]*m[1][2]*m[2][3]) / det;
	   mi[2][0] = (m[1][1]*m[2][3]*m[3][0] - m[1][3]*m[2][1]*m[3][0] + m[1][3]*m[2][0]*m[3][1] - m[1][0]*m[2][3]*m[3][1] - m[1][1]*m[2][0]*m[3][3] + m[1][0]*m[2][1]*m[3][3]) / det;
	   mi[2][1] = (m[0][3]*m[2][1]*m[3][0] - m[0][1]*m[2][3]*m[3][0] - m[0][3]*m[2][0]*m[3][1] + m[0][0]*m[2][3]*m[3][1] + m[0][1]*m[2][0]*m[3][3] - m[0][0]*m[2][1]*m[3][3]) / det;
	   mi[2][2] = (m[0][1]*m[1][3]*m[3][0] - m[0][3]*m[1][1]*m[3][0] + m[0][3]*m[1][0]*m[3][1] - m[0][0]*m[1][3]*m[3][1] - m[0][1]*m[1][0]*m[3][3] + m[0][0]*m[1][1]*m[3][3]) / det;
	   mi[2][3] = (m[0][3]*m[1][1]*m[2][0] - m[0][1]*m[1][3]*m[2][0] - m[0][3]*m[1][0]*m[2][1] + m[0][0]*m[1][3]*m[2][1] + m[0][1]*m[1][0]*m[2][3] - m[0][0]*m[1][1]*m[2][3]) / det;
	   mi[3][0] = (m[1][2]*m[2][1]*m[3][0] - m[1][1]*m[2][2]*m[3][0] - m[1][2]*m[2][0]*m[3][1] + m[1][0]*m[2][2]*m[3][1] + m[1][1]*m[2][0]*m[3][2] - m[1][0]*m[2][1]*m[3][2]) / det;
	   mi[3][1] = (m[0][1]*m[2][2]*m[3][0] - m[0][2]*m[2][1]*m[3][0] + m[0][2]*m[2][0]*m[3][1] - m[0][0]*m[2][2]*m[3][1] - m[0][1]*m[2][0]*m[3][2] + m[0][0]*m[2][1]*m[3][2]) / det;
	   mi[3][2] = (m[0][2]*m[1][1]*m[3][0] - m[0][1]*m[1][2]*m[3][0] - m[0][2]*m[1][0]*m[3][1] + m[0][0]*m[1][2]*m[3][1] + m[0][1]*m[1][0]*m[3][2] - m[0][0]*m[1][1]*m[3][2]) / det;
	   mi[3][3] = (m[0][1]*m[1][2]*m[2][0] - m[0][2]*m[1][1]*m[2][0] + m[0][2]*m[1][0]*m[2][1] - m[0][0]*m[1][2]*m[2][1] - m[0][1]*m[1][0]*m[2][2] + m[0][0]*m[1][1]*m[2][2]) / det;
	}

	public static Vector multiply(double[][] m, Vector v) {
		Vector r = new Vector(0,0,0,0);
		r.x = m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z + m[0][3] * v.w;
		r.y = m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z + m[1][3] * v.w;
		r.z = m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z + m[2][3] * v.w;
		r.w = m[3][0] * v.x + m[3][1] * v.y + m[3][2] * v.z + m[3][3] * v.w;
		return r;
	}

	public static double[][] multiply(double[][] a, double[][] b) {
		double[][] c = new double[4][4];

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				for(int k = 0; k < 4; k++)
					c[i][j] += a[i][k] * b[k][j];
		return c;
	}

	public CoordSystem apply(CoordSystem c, double t, Display d) {
		Vector v[] = new Vector[4];
		v[0] = new Vector(1, 0, 0, 0);
		v[1] = new Vector(0, 1, 0, 0);
		v[2] = new Vector(0, 0, 1, 0);
		v[3] = new Vector(0, 0, 0, 1);
		v = apply(v, t);

		Vector O, X, Y, Z;
		X = c.toParent(v[0]);
		Y = c.toParent(v[1]);
		Z = c.toParent(v[2]);
		O = c.toParent(v[3]);
		CoordSystem cn = new CoordSystem(X, Y, Z, O, c.coordAxesSize, c.showUnits, c.isDrawn, 0);
		cn.setParent(c.getParent());
		return cn;

/*		double[][] Ti = this.toArray(t), C = new double[4][4], Ci = new double[4][4], Cp = new double[4][4];

		Vector[] v = c.getAbsoluteVectors();
		for(int i = 0; i < 3; i++) {
			C[0][i] = v[i].x;
			C[1][i] = v[i].y;
			C[2][i] = v[i].z;
		}
		C[3][3] = v[3].w;

		try {
			CustomMatrix.Inverse(C, Ci);
		} catch (SingularMatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				for(int k = 0; k < 4; k++)
					Cp[i][j] += Ci[i][k] * Ti[k][j];

		for(int i = 0; i < 4; i++) {
			v[i].x = Cp[0][i];
			v[i].y = Cp[1][i];
			v[i].z = Cp[2][i];
			v[i].w = 1;
		}

		return new CoordSystem(v[0], v[1], v[2], v[3], c.coordAxesSize, c.showUnits);
*/	}
}
