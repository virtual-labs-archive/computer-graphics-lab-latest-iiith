package engine;

import java.text.DecimalFormat;

/*
 * Class for representing homogeneous 3D vectors
 */
public class Vector {
	/*
	 * formatter is to format text-fields and labels
	 */
	public static DecimalFormat formatter = new DecimalFormat();
	static {
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(1);
	}

	public static final Vector X = new Vector(1,0,0), Y = new Vector(0,1,0), Z = new Vector(0,0,1), O = new Vector(0,0,0);

	public double x, y, z, w;

	public Vector() {
		x = y = z = 0;
		w = 1;
	}

	public Vector(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}

	public Vector(double a, double b, double c, double d) {
		x = a;
		y = b;
		z = c;
		w = d;
	}

	public Vector(double a, double b, double c) {
		this(a, b, c, 1);
	}

	public double magnitude() {
		if (w == 0) {
			// Direction vector
			return Math.sqrt(x * x + y * y + z * z);
		}
		return Math.sqrt(x * x + y * y + z * z) / w;
	}

	/*
	 * Converts the homogeneous co-ordinates to a normalized vector
	 */
	public Vector toCartesian() {
		if (w == 0) {
			return new Vector(x, y, z, 0);
		}
		double invw = 1.f / w;
		return new Vector(x * invw, y * invw, z * invw);
	}

	/*
	 * Returns a unit vector.
	 */
	public Vector normal() {
		double m = this.magnitude();
		Vector n = (m > 0) ? this.divide(m) : this;
		return n.toCartesian();
	}

	public Vector set(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
		return this;
	}

	public Vector negate() {
		return new Vector(-x, -y, -z, w);
	}

	public Vector multiply(double s) {
		return new Vector(x * s, y * s, z * s, w);
	}

	public Vector divide(double f) {
		double invf = 1 / f;
		return new Vector(x, y, z, w).multiply(invf);
	}

	public Vector add(Vector v) {
		return new Vector(x * v.w + w * v.x, y * v.w + w * v.y, z * v.w + w * v.z, w * v.w);
	}

	public Vector subtract(Vector v) {
		return new Vector(x * v.w - w * v.x, y * v.w - w * v.y, z * v.w - w * v.z, w * v.w);
	}

	public static Vector CrossProduct(Vector v1, Vector v2) {
		return new Vector(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x, v1.w * v2.w);
	}

	public static double DotProduct(Vector a, Vector b) {
		if (a.w == 0 || b.w == 0) {
			System.err.println("DotProduct called on direction vector");
			return ((a.x * b.x) + (a.y * b.y) + (a.z * b.z));
		}
		return ((a.x * b.x) + (a.y * b.y) + (a.z * b.z)) / (a.w * b.w);
	}

	public String toString() {
		return "[ " 
		+ formatter.format(x) + " , "
		+ formatter.format(y) + " , "
		+ formatter.format(z) + " , "
		+ formatter.format(w) + " ]";
	}

	public String toCartesianString() {
		if(w != 0)
			return "< " 
			+ formatter.format(x/w) + " , "
			+ formatter.format(y/w) + " , "
			+ formatter.format(z/w) + " >";
		else
			return "< " 
			+ formatter.format(x) + " , "
			+ formatter.format(y) + " , "
			+ formatter.format(z) + " >";
	}
}
