package engine;

import java.util.ArrayList;

import engine.display.Display;
import engine.matrix.CustomMatrix;
import engine.matrix.Matrix;
import engine.matrix.RotationMatrix;
import engine.matrix.ScaleMatrix;
import engine.matrix.SingularMatrixException;
import engine.matrix.SkewMatrix;
import engine.matrix.TranslationMatrix;

public class Transformation {
	private static int count = 0;
	private int id = ++count;

	public ArrayList<Operation> operations;
	public int firstFrame;

	private void addOperation(Matrix matrix, Matrix inverse, int duration) {
		operations.add(0, new Operation(matrix, inverse, duration));
	}

	public Transformation(int firstFrame) {
		operations = new ArrayList<Operation>();
		this.firstFrame = firstFrame;
	}

	public Transformation(int firstFrame, ArrayList<Operation> operations) {
		this(firstFrame);
		this.operations = operations;
	}

	public void identity() {
		operations.clear();
		firstFrame = 0;
	}
	
	public int getLastFrame() {
		int frame = firstFrame;
		for(Operation o : operations)
			frame += o.duration;
		return frame;
	}

	public void rotateX(double degree, int frames) {
		addOperation(new RotationMatrix(Axis.X, degree), new RotationMatrix(
				Axis.X, -degree), frames);
	}

	public void rotateY(double degree, int frames) {
		addOperation(new RotationMatrix(Axis.Y, degree), new RotationMatrix(
				Axis.Y, -degree), frames);
	}

	public void rotateZ(double degree, int frames) {
		addOperation(new RotationMatrix(Axis.Z, degree), new RotationMatrix(
				Axis.Z, -degree), frames);
	}

	public void rotate(double degree, Vector axis, int frames) {
		addOperation(new RotationMatrix(axis, degree), new RotationMatrix(axis,
				-degree), frames);
	}

	public void translate(double tx, double ty, double tz, int frames) {
		addOperation(new TranslationMatrix(tx, ty, tz), new TranslationMatrix(
				-tx, -ty, -tz), frames);
	}

	public void scale(double sx, double sy, double sz, int frames) {
		addOperation(new ScaleMatrix(sx, sy, sz), new ScaleMatrix(1 / sx,
				1 / sy, 1 / sz), frames);
	}

	public void skew(double xy, double xz, double yx, double yz, double zx,
			double zy, int frames) throws SingularMatrixException {
		double m[][] = 
		{		{ 1 , xy, xz, 0 }, 
				{ yx, 1 , yz, 0 }, 
				{ zx, zy, 1 , 0 },
				{ 0 , 0 , 0 , 1 } };
		double mi[][] = new double[4][4];
		Matrix.inverse(m, mi);

		addOperation(new SkewMatrix(xy, xz, yx, yz, zx, zy), new CustomMatrix(
				mi), frames);
	}

	public void custom(double m[][], int frames) throws SingularMatrixException {
		double mi[][] = new double[4][4];
		Matrix.inverse(m, mi);

		addOperation(new CustomMatrix(m), new CustomMatrix(mi), frames);
	}

	Vector[] apply(Vector vertices[], Display display) {
		if (display.showAnimation) {
			int frame = display.currentFrame - firstFrame;
			for (Operation o : operations) {
				vertices = o.apply(vertices, frame);
				frame -= o.duration;
			}
		} else {
			for (Operation o : operations)
				vertices = o.apply(vertices, o.duration);
		}
		return vertices;
	}

	Vector[] applyInverse(Vector vertices[], Display display) {
		if (display.showAnimation) {
			int frame = display.currentFrame - getLastFrame();
			for (int i = operations.size() - 1; i >= 0; i--) {
				Operation o = operations.get(i);
				frame += o.duration;
				vertices = o.applyInverse(vertices, frame);
			}
		} else {
			for (int i = operations.size() - 1; i >= 0; i--) {
				Operation o = operations.get(i);
				vertices = o.applyInverse(vertices, o.duration);
			}
		}
		return vertices;
	}

//	CoordSystem apply(CoordSystem coordSystem, Display display) {
//		if (display.showAnimation) {
//			int frame = display.currentFrame - getLastFrame();
//			for (int i = operations.size()-1; i >= 0; i--) {
//				Operation o = operations.get(i);
//				frame += o.duration;
//				coordSystem = o.apply(coordSystem, frame, display);
//			}
//		} else {
//			for (int i = operations.size()-1; i >= 0; i--) {
//				Operation o = operations.get(i);
//				coordSystem = o.apply(coordSystem, o.duration, display);
//			}
//		}
//		return coordSystem;
//	}

	/*
	 * This method applies the transformation so that the co-ordinates of the
	 * associated shape change in the same way as application of the
	 * transformation to the shape would.
	 */
	CoordSystem applyInverse(CoordSystem coordSystem, Display display) {
		if (display.showAnimation && display.currentFrame < getLastFrame()) {
			int frame = display.currentFrame - firstFrame;
			for (Operation o : operations) {
				coordSystem = o.applyInverse(coordSystem, frame, display);
				frame -= o.duration;
			}
		} else {
			for (Operation o : operations)
				coordSystem = o.applyInverse(coordSystem, o.duration, display);
		}
		return coordSystem;
	}

	public double[][] toArray(Display display) {
		double[][] m = new double[4][4];
		for(int i = 0; i < 4; i++)
			m[i][i] = 1;
		if (display.showAnimation) {
			int frame = display.currentFrame - firstFrame;
			for (Operation o : operations) {
				m = Matrix.multiply(o.toArray(frame), m);
				frame -= o.duration;
			}
		} else {
			for (Operation o : operations)
				m = Matrix.multiply(o.toArray(o.duration), m);
		}
		return m;
	}

	public double[][] toInverseArray(Display display) throws SingularMatrixException {
		double[][] m = toArray(display);
		double[][] mi = new double[4][4];
		Matrix.inverse(m, mi);
		return mi;
	}

	@Override
	public String toString() {
		return "Transformation " + id;
	}
}
