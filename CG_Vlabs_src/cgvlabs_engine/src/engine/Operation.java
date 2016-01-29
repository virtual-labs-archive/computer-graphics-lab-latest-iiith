package engine;

import engine.display.Display;
import engine.matrix.Matrix;

public class Operation {
	public Matrix matrix, inverse;
	public int duration;
	// Caches the t value calculated in apply
	public double _t;

	public Operation(Matrix m, Matrix i, int d) {
		matrix = m;
		inverse = i;
		duration = d;
	}

	double toTime(int frame) {
		if(duration == 0) {
			return (frame >= 0) ? 1 : 0;
		}

		double t = (frame / (double)duration);
		return ((t < 0) ? 0 : ((t > 1) ? 1 : t));
	}

	double[][] toArray(int frame) {
		_t = toTime(frame);
		return matrix.toArray(_t);
	}

	Vector[] apply(Vector[] vertices, int frame) {
		_t = toTime(frame);
		if(_t > 0)
			return matrix.apply(vertices, _t);
		else
			return vertices;
	}

	CoordSystem apply(CoordSystem coordSystem, int frame, Display display) {
		_t = toTime(frame);
		if(_t > 0)
			return matrix.apply(coordSystem, _t, display);
		else
			return coordSystem;
	}

	Vector[] applyInverse(Vector[] vertices, int frame) {
		_t = toTime(frame);
		if(_t > 0)
			return inverse.apply(vertices, _t);
		else
			return vertices;
	}

	CoordSystem applyInverse(CoordSystem coordSystem, int frame, Display display) {
		_t = toTime(frame);
		if(_t > 0)
			return inverse.apply(coordSystem, _t, display);
		else
			return coordSystem;
	}

	@Override
	public String toString() {
		return matrix.toString() + " in " + duration + " frames";
	}
}
