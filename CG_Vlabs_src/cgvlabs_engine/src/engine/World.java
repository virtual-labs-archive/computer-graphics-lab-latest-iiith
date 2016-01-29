package engine;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextRenderer;

import engine.Camera.CameraType;
import engine.display.Display;

public class World {
	public ArrayList<Shape> shapes;
	public ArrayList<Transformation> transformations;
	public ArrayList<CoordSystem> coordSystems;
	public ArrayList<Instance> instances;
	public ArrayList<Camera> cameras;

	public int coordSystemCount = 1;
	public final CoordSystem absCoordSystem = new CoordSystem(Vector.X, Vector.Y, Vector.Z, Vector.O, 1, false, false,
			0);

	public Display display;

	public int curInstance;

	public World() {
		shapes = new ArrayList<Shape>();
		transformations = new ArrayList<Transformation>();
		coordSystems = new ArrayList<CoordSystem>();
		instances = new ArrayList<Instance>();
		cameras = new ArrayList<Camera>();
		display = new Display();

		absCoordSystem.coordAxesSize = 1;
		reset();
	}

	public void reset() {
		shapes.clear();
		transformations.clear();
		coordSystems.clear();

		curInstance = -1;
	}

	/*
	 * Shape Methods
	 */
	public Mesh addPoint(Vector point) {
		Vector[] p = new Vector[1];
		p[0] = point;
		Mesh m = new Mesh(p);
		shapes.add(m);
		return m;
	}

	public Mesh addMesh(Vector[] points, Triangle[] triangles) {
		Mesh m = new Mesh(points, triangles);
		shapes.add(m);
		return m;
	}

	public Mesh addMesh(String filename) throws FileNotFoundException {
		Mesh m = new Mesh(filename);
		shapes.add(m);
		return m;
	}

	public void deleteShape(Shape s) {
		ArrayList<Instance> parents = new ArrayList<Instance>();
		for (Instance instance : instances) {
			if (instance.shapes.contains(s))
				parents.add(instance);
		}
		for (Instance parent : parents) {
			deleteInstance(parent);
		}
		shapes.remove(s);
	}

	/*
	 * Coordinate System methods:
	 */
	public CoordSystem addCoordSystem(Vector x, Vector y, Vector o) {
		CoordSystem c = new CoordSystem(x, y, o, coordSystemCount++);
		coordSystems.add(c);
		return c;
	}

	public CoordSystem addCoordSystem(Vector x, Vector y, Vector z, Vector o) {
		CoordSystem c = new CoordSystem(x, y, z, o, coordSystemCount++);
		coordSystems.add(c);
		return c;
	}

	public CoordSystem addCoordSystem(Vector x, Vector y, Vector z, Vector o, int axesSize, boolean showUnits,
			boolean isDrawn) {
		CoordSystem c = new CoordSystem(x, y, z, o, axesSize, showUnits, isDrawn, coordSystemCount++);
		coordSystems.add(c);
		return c;
	}

	public CoordSystem addCoordSystem(CoordSystem coordSystem) {
		coordSystems.add(coordSystem);
		coordSystem.id = coordSystemCount++;
		return coordSystem;
	}

	public void deleteCoordSystem(CoordSystem c) {
		ArrayList<Instance> parents = new ArrayList<Instance>();
		for (Instance instance : instances) {
			if (instance.coordSystem == c)
				parents.add(instance);
		}
		for (Instance parent : parents) {
			deleteInstance(parent);
		}
		coordSystems.remove(c);
	}

	/*
	 * Transformation methods:
	 */
	public Transformation addTransformation(int firstFrame) {
		Transformation t = new Transformation(firstFrame);
		transformations.add(t);
		return t;
	}

	public Transformation addTransformation(Transformation transformation) {
		transformations.add(transformation);
		return transformation;
	}

	public void deleteTransformation(Transformation t) {
		ArrayList<Instance> parents = new ArrayList<Instance>();
		for (Instance instance : instances) {
			if (instance.transformation == t)
				parents.add(instance);
		}
		for (Instance parent : parents) {
			deleteInstance(parent);
		}
		transformations.remove(t);
	}

	/*
	 * Methods to create relationships between shapes, transformations and
	 * coordsystems
	 */
	public Instance associate(Shape s, CoordSystem c, Transformation t, boolean active, boolean draw) {
		Instance si = new Instance(s, c, t, active, draw);
		instances.add(si);
		curInstance = instances.size() - 1;
		return si;
	}

	public Instance associate(Shape s, CoordSystem c, Transformation t, boolean active, boolean draw, String label) {
		Instance si = new Instance(s, c, t, active, draw, label);
		instances.add(si);
		curInstance = instances.size() - 1;
		return si;
	}

	public Instance addInstance(Instance instance) {
		instances.add(instance);
		curInstance = instances.size() - 1;
		return instance;
	}

	public void selectNext() {
		if (instances.isEmpty()) {
			curInstance = -1;
		} else {
			curInstance = (curInstance + 1) % instances.size();
		}
	}

	public void selectPrev() {
		if (instances.isEmpty()) {
			curInstance = -1;
		} else {
			curInstance--;
			if (curInstance < 0)
				curInstance = instances.size() - 1;
		}
	}

	public Instance getCurrent() {
		if (curInstance >= 0)
			return instances.get(curInstance);
		else
			return null;
	}

	public void setCurrent(Instance si) {
		curInstance = instances.indexOf(si);
	}

	public void deleteInstance(Instance i) {
		// This instance may be nested, so remove the ancestors first:
		ArrayList<Instance> parents = new ArrayList<Instance>();
		for (Instance instance : instances) {
			if (instance.shapes.contains(i))
				parents.add(instance);
		}
		for (Instance parent : parents) {
			deleteInstance(parent);
		}
		instances.remove(i);
		curInstance = instances.size() - 1;
	}

	/*
	 * Methods for creating/modifying Cameras
	 */
	public Camera addOrthographicCamera(int n, int f, int b, int t, int l, int r, CoordSystem coordSystem,
			Transformation transformation) {
		Camera camera = new Camera(n, f, b, t, l, r, CameraType.ORTHOGRAPHIC, coordSystem, transformation);
		cameras.add(camera);

		return camera;
	}

	public Camera addPerspectiveCamera(int n, int f, int b, int t, int l, int r, CoordSystem coordSystem,
			Transformation transformation) {
		Camera camera = new Camera(n, f, b, t, l, r, CameraType.PERSPECTIVE, coordSystem, transformation);
		cameras.add(camera);

		return camera;
	}

	public Camera addCamera(Camera camera) {
		cameras.add(camera);
		return camera;
	}

	public void deleteCamera(Camera camera) {
		cameras.remove(camera);
	}

	public void draw(GL2 gl, TextRenderer textRenderer, boolean isCameraView) {
		display.init(gl, textRenderer, isCameraView);

		display.drawCoordSystem(absCoordSystem, false, false);

		Instance csi = getCurrent();

		for (Instance si : instances)
			if (si.isDrawn)
				si.draw(display, (si == csi));

		if (!isCameraView) {
			for (Camera c : cameras)
				c.draw(display);
		}

		display.finish();
	}
}
