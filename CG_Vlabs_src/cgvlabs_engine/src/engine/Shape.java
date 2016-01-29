package engine;

import engine.display.Display;

public abstract class Shape {
	/* Functions for selecting a single point from the shape */
//	public abstract void deselect();
	public abstract void select(Vertex vertex);
	public abstract boolean isSelected(Vertex vertex);
//	public abstract void selectNext();
//	public abstract void selectPrevious();
	public abstract Vector getSelected(Display display);
	public abstract void setSelected(Vector coords, Display display);

	/* Functions to manipulate tracking of specific points */
//	public abstract boolean isTracking(int index);
//	public abstract void startTracking(int index);
//	public abstract void stopTracking(int index);

	/* Draw this shape in the given coordSystem after transforming with given transformation*/
	public abstract void draw(CoordSystem coordSystem, CoordSystem original, Transformation transformation, Display display, boolean isCurrent, boolean isActive);
}
