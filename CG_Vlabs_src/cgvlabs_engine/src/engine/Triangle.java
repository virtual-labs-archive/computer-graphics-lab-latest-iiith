package engine;

public class Triangle {
	public int a, b, c;
	public Vector na, nb, nc;
	public boolean hasNormals;

	public Triangle() {
		a = b = c = -1;
		hasNormals = false;
	}

	public Triangle(Triangle t) {
		a = t.a;
		b = t.b;
		c = t.c;
		na = t.na;
		nb = t.nb;
		nc = t.nc;
		hasNormals = t.hasNormals;
	}

	public Triangle(int _a, int _b, int _c) {
		a = _a;
		b = _b;
		c = _c;
		hasNormals = false;
	}

	public Triangle(int _a, int _b, int _c, Vector _na, Vector _nb, Vector _nc) {
		a = _a;
		b = _b;
		c = _c;
		na = _na;
		nb = _nb;
		nc = _nc;
		hasNormals = true;
	}

	@Override
	public String toString() {
		return "<" + a + "," + b + "," + c + ">";
	}
}