package gui;

import engine.Axis;
import engine.Camera;
import engine.CoordSystem;
import engine.Instance;
import engine.Mesh;
import engine.Operation;
import engine.Shape;
import engine.Transformation;
import engine.Triangle;
import engine.Vector;
import engine.Vertex;
import engine.World;
import engine.Camera.CameraType;
import engine.display.Display;
import engine.matrix.Matrix;
import engine.matrix.RotationMatrix;
import engine.matrix.ScaleMatrix;
import engine.matrix.TranslationMatrix;
import experiments.Experiment;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

class WorldTreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 1258762041434725355L;
	
	public ArrayList<JCheckBox> cameraNodeSwitchAxisButton;
	public WorldTreeModel(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}

	public WorldTreeModel(TreeNode root) {
		super(root);
	}

	@Override
	public void nodeChanged(TreeNode treeNode) {
		WorldTreeNode node = ((WorldTreeNode) treeNode);
		if (node.getChildCount() == 0) {
			WorldTreeNode parent = (WorldTreeNode) node.getParent();
			int index = parent.getIndex(node);
			Object reference = parent.getUserObject();

			WorldTreeNode root = (WorldTreeNode) getRoot();
			Enumeration<?> e = root.breadthFirstEnumeration();

			while (e.hasMoreElements()) {
				WorldTreeNode n = (WorldTreeNode) e.nextElement();
				if (n.getUserObject() == reference) {
					try {
						WorldTreeNode child = (WorldTreeNode) n.getChildAt(index);
						child.updateComponents(node.getUserObject());
						super.nodeChanged(child);
					} catch (ArrayIndexOutOfBoundsException ae) {
						System.out.println(n);
					}
				}
			}

			if (parent != null)
				nodeChanged(parent);
		} else {
			node.updateComponents();
			super.nodeChanged(treeNode);
		}
	}
}

public class WorldTree extends JTree {
	private static final long serialVersionUID = 880662534781181817L;

	private World world;
	private WorldTreeNode root;
	private ButtonGroup cameraGroup;

	private void createNodes(World world) {
		root = new WorldTreeNode("World");
		setModel(new WorldTreeModel(root));
		for (Instance si : world.instances) {
			createNodes(root, si);
		}
		cameraGroup = new ButtonGroup();
		root.add(new WorldTreeNode(true, "Global Coordinate System", cameraGroup, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton) e.getSource()).isSelected()) {
					Experiment.experiment.world.display.addTransformation(null, null);
				}
			}
		}));
		for (Camera c : world.cameras) {
			createNodes(root, c, Experiment.cameraViews.get(c));
		}
	}

	/*
	 * Create nodes for Cameras
	 */
	private void createNodes(WorldTreeNode rootNode, final Camera c, final CameraView cv) {
		WorldTreeNode cameraNode = new WorldTreeNode(c);
		cameraNode.add(new WorldTreeNode(c.type, "Type", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.type = (CameraType) ((JComboBox) e.getSource()).getSelectedItem();
			}
		}));
		cameraNode.add(new WorldTreeNode(c.n, "Near: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.n = ((NumberEditableLabel) e.getSource()).getValue();
			}
		}));
		cameraNode.add(new WorldTreeNode(c.f, "Far: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.f = ((NumberEditableLabel) e.getSource()).getValue();
			}
		}));
		cameraNode.add(new WorldTreeNode(c.b, "Bottom: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.b = ((NumberEditableLabel) e.getSource()).getValue();
			}
		}));
		cameraNode.add(new WorldTreeNode(c.t, "Top: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.t = ((NumberEditableLabel) e.getSource()).getValue();
			}
		}));
		cameraNode.add(new WorldTreeNode(c.l, "Left: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.l = ((NumberEditableLabel) e.getSource()).getValue();
			}
		}));
		cameraNode.add(new WorldTreeNode(c.r, "Right: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.r = ((NumberEditableLabel) e.getSource()).getValue();
			}
		}));
		createNodes(cameraNode, c.transformation);
		createNodes(cameraNode, c.coordSystem);
		cameraNode.add(new WorldTreeNode(true, "Show View", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cv.frame.setVisible(((JCheckBox) e.getSource()).isSelected());
				c.view = ((JCheckBox) e.getSource()).isSelected();
			}
		}));
		cameraNode.add(new WorldTreeNode(false, "Switch Axis to "+c.toString(), cameraGroup, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton) e.getSource()).isSelected()) {
					Experiment.experiment.world.display.addTransformation(c.coordSystem, c.transformation);
				}
/*				else {
					Experiment.experiment.world.display.addTransformation(null, null);
				}
				//cv.frame.setVisible(((JCheckBox) e.getSource()).isSelected());
*/			}
		}));
		
		rootNode.add(cameraNode);
	}

	/*
	 * Create nodes for Meshes
	 */
	private void createNodes(WorldTreeNode rootNode, Mesh mesh) {
		// Add points
		WorldTreeNode pointNode = new WorldTreeNode(mesh.vertices, new JLabel("Vertices"));
		for (Vertex v : mesh.vertices) {
			pointNode.add(new WorldTreeNode(v));
		}
		rootNode.add(pointNode);

		// Add triangles
		if (mesh.triangles != null) {
			WorldTreeNode triangleNode = new WorldTreeNode("Triangles");
			for (Triangle triangle : mesh.triangles) {
				triangleNode.add(new WorldTreeNode(triangle, mesh));
			}
			rootNode.add(triangleNode);
		}
	}

	/*
	 * Create nodes for Co-ordinate Systems
	 */
	private void createNodes(WorldTreeNode rootNode, final CoordSystem coordSystem) {
		WorldTreeNode node = new WorldTreeNode(coordSystem);
		Vector[] v = coordSystem.getVectors();

		// X
		node.add(new WorldTreeNode(v[0], true, "X-axis: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				coordSystem.setX(((VectorInput) e.getSource()).getVector());
			}
		}));

		// Y
		node.add(new WorldTreeNode(v[1], true, "Y-axis: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				coordSystem.setY(((VectorInput) e.getSource()).getVector());
			}
		}));

		// Z
		node.add(new WorldTreeNode(v[2], true, "Z-axis: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				coordSystem.setZ(((VectorInput) e.getSource()).getVector());
			}
		}));

		// O
		node.add(new WorldTreeNode(v[3], false, "Origin: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				coordSystem.setO(((VectorInput) e.getSource()).getVector());
			}
		}));

		// axesSize
		node.add(new WorldTreeNode(coordSystem.coordAxesSize, 1, 1000, "Axes size: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				coordSystem.coordAxesSize = ((IntegerEditableLabel) e.getSource()).getValue();
			}
		}));

		// showUnits
		node.add(new WorldTreeNode(coordSystem.showUnits, "Show units", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				coordSystem.showUnits = ((JCheckBox) e.getSource()).isSelected();
			}
		}));
		rootNode.add(node);

		// isDrawn
		node.add(new WorldTreeNode(coordSystem.isDrawn, "Draw", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				coordSystem.isDrawn = ((JCheckBox) e.getSource()).isSelected();
			}
		}));
		rootNode.add(node);
	}

	/*
	 * Create nodes for Transformations
	 */
	private void createNodes(WorldTreeNode rootNode, Transformation transformation) {
		WorldTreeNode childNode = new WorldTreeNode(transformation);
		for (Operation o : transformation.operations) {
			WorldTreeNode operationNode = new WorldTreeNode(o);
			createNodes(operationNode, o);
			childNode.add(operationNode);
		}
		rootNode.add(childNode);
	}

	/*
	 * Create nodes for an Operation (matrix, duration)
	 */
	private void createNodes(final WorldTreeNode rootNode, final Operation operation) {
		// Create matrix display node
		final MatrixPanel mp = new MatrixPanel(operation);
		final WorldTreeNode matrixNode = new WorldTreeNode(operation.matrix, mp);

		// Matrix specific nodes
		if (operation.matrix instanceof TranslationMatrix) {
			TranslationMatrix t = (TranslationMatrix) operation.matrix;
			rootNode.add(new WorldTreeNode(t.translation, true, "Translation: ", new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					TranslationMatrix ti = (TranslationMatrix) operation.inverse;
					ti.translation = ((VectorInput) e.getSource()).getVector().negate();
					mp.updateCoeffs();
					((WorldTreeModel) treeModel).nodeChanged(matrixNode);
				}
			}));
		} else if (operation.matrix instanceof ScaleMatrix) {
			ScaleMatrix s = (ScaleMatrix) operation.matrix;
			rootNode.add(new WorldTreeNode(s.scale, true, "Scale: ", new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					ScaleMatrix s = (ScaleMatrix) operation.inverse;
					Vector scale = ((VectorInput) e.getSource()).getVector();
					s.scale.x = 1 / scale.x;
					s.scale.y = 1 / scale.y;
					s.scale.z = 1 / scale.z;
					mp.updateCoeffs();
					((WorldTreeModel) treeModel).nodeChanged(matrixNode);
				}
			}));
		} else if (operation.matrix instanceof RotationMatrix) {
			final RotationMatrix r = (RotationMatrix) operation.matrix;
			final RotationMatrix ri = (RotationMatrix) operation.inverse;

			rootNode.add(new WorldTreeNode(r.theta, "Theta: ", new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					r.theta = ((NumberEditableLabel) e.getSource()).getValue();
					ri.theta = -r.theta;
					mp.updateCoeffs();
					((WorldTreeModel) treeModel).nodeChanged(matrixNode);
				}
			}));

			final WorldTreeNode axisTypeNode, axisVectorNode;

			axisTypeNode = new WorldTreeNode(r.axis, "About: ", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					r.axis = (Axis) ((JComboBox) e.getSource()).getSelectedItem();
					ri.axis = r.axis;
					mp.updateCoeffs();
					((WorldTreeModel) treeModel).nodeChanged(matrixNode);
				}
			});

			axisVectorNode = new WorldTreeNode(r.axisVector, true, "Axis vector: ", new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					ri.axisVector = r.axisVector;
					axisTypeNode.comboBox.setSelectedItem(Axis.OTHER);
					((WorldTreeModel) getModel()).nodeChanged(axisTypeNode);
					mp.updateCoeffs();
					((WorldTreeModel) treeModel).nodeChanged(matrixNode);
				}
			});

			rootNode.add(axisTypeNode);
			rootNode.add(axisVectorNode);
		}
		/*
		 * TODO: Add editable nodes for Skew and Custom Matrices
		 */

		// Duration
		rootNode.add(new WorldTreeNode(operation.duration, 0, Integer.MAX_VALUE, "Duration: ", new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				operation.duration = ((IntegerEditableLabel) e.getSource()).getValue();
				mp.updateCoeffs();
				((WorldTreeModel) treeModel).nodeChanged(matrixNode);
			}
		}));

		// Add the Matrix display
		rootNode.add(matrixNode);
	}

	/*
	 * Create nodes for Shape Instances
	 */
	private void createNodes(WorldTreeNode rootNode, final Instance si) {
		WorldTreeNode node = new WorldTreeNode(si);

		// Add coordSystem
		createNodes(node, si.coordSystem);

		WorldTreeNode childNode;

		// Add shapes
		for (Shape shape : si.shapes) {
			if (shape instanceof Mesh) {
				childNode = new WorldTreeNode(shape);
				createNodes(childNode, (Mesh) shape);
				node.add(childNode);
			} else if (shape instanceof Instance) {
				createNodes(node, (Instance) shape);
			}
		}

		// Add transformation
		createNodes(node, si.transformation);

		// Active
		node.add(new WorldTreeNode(si.isActive, "Active", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				si.isActive = ((JCheckBox) e.getSource()).isSelected();
			}
		}));

		// Draw
		node.add(new WorldTreeNode(si.isDrawn, "Draw", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				si.isDrawn = ((JCheckBox) e.getSource()).isSelected();
			}
		}));

		rootNode.add(node);
	}

	public WorldTree(World w) {
		super();
		world = w;
		createNodes(world);
		expandPath(new TreePath(root.getPath()));

		setInvokesStopCellEditing(true);

		WorldTreeRenderer renderer = new WorldTreeRenderer();
		setCellRenderer(renderer);
		setCellEditor(new WorldTreeEditor(this));
		setEditable(true);

		addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath tp = e.getPath();
				Vertex curVertex = null;

				while (tp.getPathCount() > 1) {
					WorldTreeNode node = (WorldTreeNode) tp.getLastPathComponent();
					Object object = node.getUserObject();
					if (object instanceof Shape && curVertex != null) {
						((Shape) object).select(curVertex);
					}
					if (object instanceof Instance) {
						world.setCurrent((Instance) node.getUserObject());
					} else if (object instanceof Vertex) {
						curVertex = (Vertex) object;
					}
					tp = tp.getParentPath();
				}
			}
		});
	}

	public void expandNode(Object object) {
		Enumeration<?> e = root.breadthFirstEnumeration();

		while (e.hasMoreElements()) {
			WorldTreeNode n = (WorldTreeNode) e.nextElement();
			if (n.getUserObject() == object) {
				expandPath(new TreePath(n.getPath()));
			}
		}
	}

	public ArrayList<WorldTreeNode> findMatchingNodes(Object object) {
		ArrayList<WorldTreeNode> nodes = new ArrayList<WorldTreeNode>();
		Enumeration<?> e = root.breadthFirstEnumeration();

		while (e.hasMoreElements()) {
			WorldTreeNode n = (WorldTreeNode) e.nextElement();
			if (n.getUserObject() == object)
				nodes.add(n);
		}
		return nodes;
	}

	public void updateVertices(Shape shape) {
		if (shape instanceof Instance) {
			for (Shape s : ((Instance) shape).shapes)
				updateVertices(s);
		} else if (shape instanceof Mesh) {
			Mesh mesh = (Mesh) shape;
			ArrayList<WorldTreeNode> nodes = findMatchingNodes(shape);
			WorldTreeModel model = (WorldTreeModel) getModel();
			for (WorldTreeNode node : nodes) {
				WorldTreeNode points = (WorldTreeNode) node.getChildAt(0);
				for (int i = points.getChildCount() - 1; i >= 0; i--) {
					WorldTreeNode ptNode = (WorldTreeNode)points.getChildAt(i);
					if(mesh.isSelected((Vertex)(ptNode.getUserObject())))
						model.nodeChanged(ptNode);
				}
			}
		}
	}

	public void addInstance(Instance instance) {
		createNodes(root, instance);
		WorldTreeModel model = (WorldTreeModel) getModel();
		model.nodeStructureChanged(root);
	}

	public void addCamera(Camera camera, CameraView view) {
		createNodes(root, camera, view);
		WorldTreeModel model = (WorldTreeModel) getModel();
		model.nodeStructureChanged(root);
	}

	public void updateInstance(Instance instance) {
		ArrayList<WorldTreeNode> nodes = findMatchingNodes(instance);
		WorldTreeModel model = (WorldTreeModel) getModel();

		for (WorldTreeNode node : nodes) {
			int index = 0;
			WorldTreeNode parent = (WorldTreeNode) node.getParent();
			while (index < parent.getChildCount()) {
				if (parent.getChildAt(index) == node)
					break;
				index++;
			}
			parent.remove(index);
			WorldTreeNode dummy = new WorldTreeNode("dummy");
			createNodes(dummy, instance);
			parent.insert((WorldTreeNode) dummy.getFirstChild(), index);
			model.nodeStructureChanged(node);
		}
	}

	public void deleteInstance(Instance instance) {
		ArrayList<WorldTreeNode> nodes = findMatchingNodes(instance);
		WorldTreeModel model = (WorldTreeModel) getModel();

		ArrayList<Instance> parents = new ArrayList<Instance>();
		for (WorldTreeNode node : nodes) {
			WorldTreeNode parent = (WorldTreeNode) node.getParent();

			if (parent.getUserObject() instanceof Instance) {
				parents.add((Instance) parent.getUserObject());
			} else {
				parent.remove(node);
				model.nodeStructureChanged(parent);
			}
		}

		for (Instance parent : parents)
			deleteInstance(parent);
	}

	public void updateTransformation(Transformation transformation) {
		ArrayList<WorldTreeNode> nodes = findMatchingNodes(transformation);
		WorldTreeModel model = (WorldTreeModel) getModel();

		for (WorldTreeNode node : nodes) {
			int index = 0;
			WorldTreeNode parent = (WorldTreeNode) node.getParent();
			while (index < parent.getChildCount()) {
				if (parent.getChildAt(index) == node)
					break;
				index++;
			}
			parent.remove(index);
			WorldTreeNode dummy = new WorldTreeNode("dummy");
			createNodes(dummy, transformation);
			parent.insert((WorldTreeNode) dummy.getFirstChild(), index);
			model.nodeStructureChanged(node);
		}
	}

	public void deleteShape(Shape shape) {
		ArrayList<WorldTreeNode> nodes = findMatchingNodes(shape);
		for (WorldTreeNode node : nodes) {
			WorldTreeNode parent = (WorldTreeNode) node.getParent();
			deleteInstance((Instance) parent.getUserObject());
		}
	}

	public void deleteCoordSystem(CoordSystem coordSystem) {
		ArrayList<WorldTreeNode> nodes = findMatchingNodes(coordSystem);
		for (WorldTreeNode node : nodes) {
			WorldTreeNode parent = (WorldTreeNode) node.getParent();
			deleteInstance((Instance) parent.getUserObject());
		}
	}

	public void deleteTransformation(Transformation transformation) {
		ArrayList<WorldTreeNode> nodes = findMatchingNodes(transformation);
		for (WorldTreeNode node : nodes) {
			WorldTreeNode parent = (WorldTreeNode) node.getParent();
			deleteInstance((Instance) parent.getUserObject());
		}
	}

	public void updateCamera(Camera camera, Display display) {
		WorldTreeModel model = (WorldTreeModel) getModel();

		int i = root.getChildCount() - 1;
		while (i >= 0) {
			if (camera == ((WorldTreeNode) root.getChildAt(i)).getUserObject())
				break;
			i--;
		}
		root.remove(i);
		WorldTreeNode dummy = new WorldTreeNode("dummy");
		createNodes(dummy, camera, Experiment.cameraViews.get(camera));
		root.insert((WorldTreeNode) dummy.getFirstChild(), i);
		model.nodeStructureChanged(root);
	}

	public void deleteCamera(Camera camera) {
		for (int i = root.getChildCount() - 1; i >= 0; i--) {
			if (camera == ((WorldTreeNode) root.getChildAt(i)).getUserObject()) {
				root.remove(i);
				((WorldTreeModel) getModel()).nodeStructureChanged(root);
				break;
			}
		}
	}

	public void updateMatrices() {
		WorldTreeModel model = (WorldTreeModel) getModel();
		Enumeration<?> e = root.breadthFirstEnumeration();

		while (e.hasMoreElements()) {
			WorldTreeNode n = (WorldTreeNode) e.nextElement();
			if (n.getUserObject() instanceof Matrix) {
				((MatrixPanel) n.renderer).updateCoeffs();
				model.nodeChanged(n);
			}
		}
	}
}

class WorldTreeRenderer implements TreeCellRenderer {
	private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

	Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;

	public WorldTreeRenderer() {
		selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
		selectionForeground = UIManager.getColor("Tree.selectionForeground");
		selectionBackground = UIManager.getColor("Tree.selectionBackground");
		textForeground = UIManager.getColor("Tree.textForeground");
		textBackground = UIManager.getColor("Tree.textBackground");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		if ((value != null) && (value instanceof WorldTreeNode)) {
			WorldTreeNode node = ((WorldTreeNode) value);
			Component renderer = node.getRenderer();
			renderer.setEnabled(tree.isEnabled());

			if (selected) {
				renderer.setForeground(selectionForeground);
				renderer.setBackground(selectionBackground);
			} else {
				renderer.setForeground(textForeground);
				renderer.setBackground(textBackground);
			}

			return renderer;
		}
		return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}
}

class WorldTreeEditor extends AbstractCellEditor implements TreeCellEditor {
	private static final long serialVersionUID = 8643854601273385990L;

	JTree tree;
	WorldTreeNode currentNode = null;

	public WorldTreeEditor(JTree tree) {
		this.tree = tree;
	}

	public Object getCellEditorValue() {
		return currentNode.getUserObject();
	}

	public boolean isCellEditable(EventObject event) {
		boolean returnValue = false;
		if (event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			if (path != null) {
				Object node = path.getLastPathComponent();
				return (node != null) && (node instanceof WorldTreeNode) && ((WorldTreeNode) node).isEditable();
			}
		}
		return returnValue;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row) {

		if (leaf && value instanceof WorldTreeNode) {
			WorldTreeNode node = (WorldTreeNode) value;
			currentNode = node;
			return node.getEditor();
		}
		return new JLabel(value.toString());
	}
}
