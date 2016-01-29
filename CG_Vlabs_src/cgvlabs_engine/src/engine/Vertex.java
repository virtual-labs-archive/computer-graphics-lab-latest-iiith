package engine;

import engine.display.Display;

public class Vertex {
	public boolean isTracking = false;
	public Vector vector;

	public Vertex(double x, double y, double z, double w) {
		vector = new Vector(x, y, z, w);
	}

	public Vertex(double x, double y, double z) {
		this(x,y,z,1);
	}

	public Vertex(Vector vertex) {
		this(vertex.x, vertex.y, vertex.z, vertex.w);
	}

	public void draw(CoordSystem coordSystem, Transformation transformation,
			Display display, boolean isCurrent, boolean isActive) {
		Vector[] ap = new Vector[1], rp = new Vector[1];
		ap[0] = vector;
		rp[0] = vector;

		if (!display.transformCoordSystems) {
			rp = transformation.apply(rp, display);
			ap[0] = coordSystem.toAbsolute(rp[0]);
		} else {
			rp[0] = coordSystem.toRelative(ap[0]);
		}

		display.setColor(display.theme.vertexColors
				.getNext(isCurrent, isActive));
		display.drawVertex(rp[0], ap[0], coordSystem, isCurrent, isActive,
				isTracking, coordSystem.showUnits);
	}
	
	@Override
	public String toString() {
		return vector.toString() + (isTracking ? " Tracking" : "");
	}
}
