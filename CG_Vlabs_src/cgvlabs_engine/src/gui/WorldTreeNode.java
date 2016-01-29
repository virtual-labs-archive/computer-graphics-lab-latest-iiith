package gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import engine.Axis;
import engine.CoordSystem;
import engine.Mesh;
import engine.Operation;
import engine.Triangle;
import engine.Vector;
import engine.Vertex;
import engine.Camera.CameraType;

public class WorldTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 345919311025053963L;
	protected Component renderer = null, editor = null;
	protected JComboBox comboBox = null;
	protected String caption = "";

	protected enum NodeType {
		DEFAULT, VECTOR, VERTEX, TRIANGLE, INTEGER, DOUBLE, BOOLEAN, ENUM, OPERATION, RADIO
	}

	protected NodeType type;

	public WorldTreeNode(Object userObject) {
		super(userObject);
		renderer = new JLabel(userObject.toString());
		((JLabel) renderer).setOpaque(true);
		type = NodeType.DEFAULT;
	}

	public WorldTreeNode(Object userObject, Component renderer) {
		super(userObject);
		this.renderer = renderer;
		if(renderer instanceof JLabel)
			((JLabel) renderer).setOpaque(true);
		type = NodeType.DEFAULT;
	}

	public WorldTreeNode(Object userObject, Component renderer, Component editor) {
		this(userObject, renderer);
		this.editor = editor;
	}

	/*
	 * Vector node
	 */
	public WorldTreeNode(Vector vector, boolean isCartesian, String c, ChangeListener changeListener) {
		super(vector);
		caption = c;
		final VectorInput vi = new VectorInput(caption, vector, isCartesian);
		renderer = new JLabel(caption + vi.toString());
		((JLabel)renderer).setOpaque(true);

		vi.addChangeListener(changeListener);
		vi.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				((JLabel) renderer).setText(caption + vi.toString());
			}
		});
		editor = vi;
		type = NodeType.VECTOR;
	}

	/*
	 * Vertex node
	 */
	public WorldTreeNode(Vertex vertex) {
		super(vertex);
		renderer = new JLabel(vertex.toString());
		final VertexInput vi = new VertexInput(vertex);
		vi.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				((JLabel) renderer).setText(vi.getVertex().toString());
			}
		});
		editor = vi;
		type = NodeType.VERTEX;
	}

	/*
	 * Triangle node
	 */
	public WorldTreeNode(Triangle triangle, Mesh mesh) {
		super(triangle);
		renderer = new JLabel(triangle.toString());
		final TriangleInput ti = new TriangleInput(triangle, mesh);
		ti.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				((JLabel) renderer).setText(ti.getTriangle().toString());
			}
		});
		editor = ti;
		type = NodeType.TRIANGLE;
	}

	/*
	 * Integer node
	 */
	public WorldTreeNode(int integer, int min, int max, String c, ChangeListener changeListener) {
		super(integer);
		caption = c;
		renderer = new JLabel(caption + String.valueOf(integer));
		final IntegerEditableLabel iel = new IntegerEditableLabel(caption, integer, min, max);
		iel.addChangeListener(changeListener);
		iel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				((JLabel) renderer).setText(caption + iel.getValue());
				setUserObject(iel.getValue());
			}
		});
		editor = iel;
		type = NodeType.INTEGER;
	}

	/*
	 * Number node
	 */
	public WorldTreeNode(double d, String c, ChangeListener changeListener) {
		super(d);
		caption = c;
		renderer = new JLabel(caption + String.valueOf(d));
		final NumberEditableLabel nel = new NumberEditableLabel(caption, d);
		nel.addChangeListener(changeListener);
		nel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				((JLabel) renderer).setText(caption + nel.getValue());
				setUserObject(nel.getValue());
			}
		});
		editor = nel;
		type = NodeType.DOUBLE;
	}

	/*
	 * Boolean node
	 */
	public WorldTreeNode(boolean b, String c, ActionListener actionListener) {
		super(b);
		caption = c;
		final JCheckBox cb = new JCheckBox(caption, b);
		cb.addActionListener(actionListener);
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUserObject(cb.isSelected());
			}
		});
		cb.setOpaque(false);
		renderer = cb;
		editor = cb;
		type = NodeType.BOOLEAN;
	}
	
	/*
	 * Radio Button node
	 */
	public WorldTreeNode(boolean b, String c, ButtonGroup group, ActionListener actionListener) {
		super(b);
		caption = c;
		final JRadioButton rb = new JRadioButton(caption, b);
		//rb.setActionCommand(c);
		rb.addActionListener(actionListener);
		rb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUserObject(rb.isSelected());
			}
		});
		rb.setOpaque(false);
		group.add(rb);
		renderer = rb;
		editor = rb;
		type = NodeType.RADIO;
	}

	/*
	 * Axis enumeration node
	 */
	public WorldTreeNode(Axis axis, String c, ActionListener actionListener) {
		super(axis);
		caption = c;
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(new JLabel(caption));

		comboBox = new JComboBox(Axis.values());
		comboBox.setSelectedItem(axis);
		panel.add(comboBox);

		comboBox.addActionListener(actionListener);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUserObject(comboBox.getSelectedItem());
			}
		});

		renderer = panel;
		editor = panel;
		type = NodeType.ENUM;
	}

	/*
	 * Camera Type node
	 */
	public WorldTreeNode(CameraType camType, String c, ActionListener actionListener) {
		super(camType);
		caption = c;
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(new JLabel(caption));

		comboBox = new JComboBox(CameraType.values());
		comboBox.setSelectedItem(camType);
		panel.add(comboBox);

		comboBox.addActionListener(actionListener);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUserObject(comboBox.getSelectedItem());
			}
		});

		renderer = panel;
		editor = panel;
		type = NodeType.ENUM;
	}

	/*
	 * Operation node
	 */
	public WorldTreeNode(Operation operation) {
		super(operation);
		renderer = new JLabel(operation.toString());
		((JLabel) renderer).setOpaque(true);
		type = NodeType.OPERATION;
	}

	public Component getRenderer() {
		return renderer;
	}

	public Component getEditor() {
		return editor;
	}

	public void updateComponents(Object newValue) {
		setUserObject(newValue);
		switch (type) {
		case VECTOR:
			// Normalize co-ordinate system vectors
			if (((WorldTreeNode) getParent()).userObject instanceof CoordSystem) {
//				if(this != getParent().getChildAt(3))
//					newValue = ((Vector) newValue).normal();
				setUserObject(newValue);
			}
			((VectorInput) editor).setVector((Vector) newValue);
			((JLabel) renderer).setText(caption + editor.toString());
			break;
		case VERTEX:
			((VertexInput) editor).setVertex((Vertex) newValue);
			((JLabel) renderer).setText(caption + editor.toString());
			break;
		case TRIANGLE:
			((TriangleInput) editor).setTriangle((Triangle) newValue);
			((JLabel) renderer).setText(newValue.toString());
			break;
		case INTEGER:
			((IntegerEditableLabel) editor).setValue((Integer) newValue);
			((JLabel) renderer).setText(caption + newValue.toString());
			break;
		case DOUBLE:
			((NumberEditableLabel) editor).setValue((Double) newValue);
			((JLabel) renderer).setText(caption + newValue.toString());
			break;
		case BOOLEAN:
			((JCheckBox) renderer).setSelected((Boolean) newValue);
			break;
		case RADIO:
			((JRadioButton) renderer).setSelected((Boolean) newValue);
			break;
		case ENUM:
			comboBox.setSelectedItem(newValue);
			break;
		case OPERATION:
			((JLabel)renderer).setText(newValue.toString());
			break;
		}
		if (renderer != null) {
			renderer.invalidate();
			renderer.validate();
		}
		if (editor != null) {
			editor.validate();
			editor.invalidate();
		}
	}

	public void updateComponents() {
		updateComponents(getUserObject());
	}

	public boolean isEditable() {
		return (editor != null);
	}
}
