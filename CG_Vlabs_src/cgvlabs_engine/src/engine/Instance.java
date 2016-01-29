package engine;

import java.util.ArrayList;

import engine.display.Display;

public class Instance extends Shape {
	public static int count = 0;
	private int id = ++count;
	public String label = "Instance " + id;

	public ArrayList<Shape> shapes = new ArrayList<Shape>();
	public CoordSystem coordSystem;
	public Transformation transformation;

	/*
	 * isActive: Multiple instances can be active. Active instances are drawn
	 * with higher contrast.
	 * 
	 * isCurrent: This is used to indicate an instance that is currently
	 * selected. Any changes to the shape/coordsystem occurs on the current
	 * instance. The current shape/coordsystem should be indicated specially
	 */
	public boolean isActive = true, isDrawn = true;

	public Instance(Shape shape, CoordSystem coordSystem, Transformation transformation, boolean isActive,
			boolean isDrawn) {
		shapes.add(shape);
		this.coordSystem = coordSystem;
		this.transformation = transformation;
		this.isActive = isActive;
		this.isDrawn = isDrawn;
	}

	public Instance(Shape shape, CoordSystem coordSystem, Transformation transformation, boolean isActive,
			boolean isDrawn, String label) {
		this(shape, coordSystem, transformation, isActive, isDrawn);
		this.label = label;
	}

	public void addShape(Shape shape) {
		shapes.add(shape);
	}

	public void removeShape(Shape shape) {
		shapes.remove(shape);
	}

	public void removeAllShapes() {
		shapes.clear();
	}

	/*
	 * This draw is called from the World object
	 */
	void draw(Display display, boolean isCurrent) {
		CoordSystem c = coordSystem;
		if (display.transformCoordSystems) {
			c = transformation.applyInverse(c, display);
		}

		c.setParent(null);

		// Draw the coordinate system:
		c.draw(display, isCurrent, isActive);

		// Draw the shape:
		for (Shape shape : shapes)
			shape.draw(c, coordSystem, transformation, display, isCurrent, isActive);
	}

	@Override
	public void select(Vertex vertex) {
		for (Shape shape : shapes)
			shape.select(vertex);
	}

	// @Override
	// public void deselect() {
	// for(Shape shape : shapes)
	// shape.deselect();
	// }

	/*
	 * Treat this ShapeInstance as a Shape transform the child coordSystem and
	 * shape, use the given coordSystem as parent and draw both
	 */
	@Override
	public void draw(CoordSystem csParent, CoordSystem original, Transformation tParent, Display display,
			boolean isCurrent, boolean isActive) {
		CoordSystem c = coordSystem;
		if (!display.transformCoordSystems) {
			Vector[] coords = c.getAbsoluteVectors();
			coords = tParent.apply(coords, display);
			c = new CoordSystem(coords[0], coords[1], coords[2], coords[3], c.coordAxesSize, c.showUnits, c.isDrawn, 0);
		} else {
			c = transformation.applyInverse(c, display);
		}

		c.setParent(csParent);

		// Draw the shape:
		for (Shape shape : shapes)
			shape.draw(c, coordSystem, transformation, display, isCurrent, isActive);

		// Draw the coordinate system:
		c.draw(display, isCurrent, isActive);
	}

	@Override
	public boolean isSelected(Vertex vertex) {
		boolean b = false;
		for (Shape shape : shapes)
			b = b || shape.isSelected(vertex);
		return b;
	}

	@Override
	public Vector getSelected(Display display) {
		Vector[] v = new Vector[1];
		for (Shape shape : shapes) {
			v[0] = shape.getSelected(display);
			if (v[0] != null)
				break;
		}

		if(v[0] == null)
			return null;

		if (!display.transformCoordSystems) {
			v = transformation.apply(v, display);
		}
		v[0] = coordSystem.toAbsolute(v[0]);
		return v[0];
	}

	@Override
	public void setSelected(Vector coords, Display display) {
		Shape selectedShape = null;
		for (Shape shape : shapes) {
			if(shape.getSelected(display) != null) {
				selectedShape = shape;
				break;
			}
		}

		Vector[] v = new Vector[1];
		v[0] = coords;
		v[0] = coordSystem.toRelative(v[0]);
		if (!display.transformCoordSystems) {
			v = transformation.applyInverse(v, display);
		}
		selectedShape.setSelected(v[0], display);
	}

	// @Override
	// public void startTracking(int index) {
	// shape.startTracking(index);
	// }
	//
	// @Override
	// public void stopTracking(int index) {
	// shape.stopTracking(index);
	// }
	//
	// @Override
	// public boolean isTracking(int index) {
	// return shape.isTracking(index);
	// }

	@Override
	public String toString() {
		return label;
	}
}
