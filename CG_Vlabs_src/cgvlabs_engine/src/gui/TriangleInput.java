package gui;

import engine.Mesh;
import engine.Triangle;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TriangleInput extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	protected IntegerEditableLabel ielA, ielB, ielC;
	protected Triangle triangle;
	protected Mesh mesh;
	protected ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	public void setTriangle(Triangle triangle, Mesh mesh) {
		this.triangle = triangle;
		this.mesh = mesh;
		updateFields();
	}

	public void setTriangle(Triangle triangle) {
		this.triangle = triangle;
		updateFields();
	}

	public void updateFields() {
		ielA.setValue(triangle.a);
		ielB.setValue(triangle.b);
		ielC.setValue(triangle.c);
	}

	public Triangle getTriangle() {
		return triangle;
	}

	public TriangleInput(final Triangle triangle, Mesh mesh) {
		this.triangle = triangle;
		this.mesh = mesh;

		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		ielA = new IntegerEditableLabel("", triangle.a, 0, mesh.vertices.length - 1);
		ielB = new IntegerEditableLabel("", triangle.b, 0, mesh.vertices.length - 1);
		ielC = new IntegerEditableLabel("", triangle.c, 0, mesh.vertices.length - 1);

		ielA.textField.addActionListener(this);
		ielB.textField.addActionListener(this);
		ielC.textField.addActionListener(this);

		add(new JLabel("<"));
		add(ielA);
		add(new JLabel(","));
		add(ielB);
		add(new JLabel(","));
		add(ielC);
		add(new JLabel(">"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == ielA.textField) {
			triangle.a = ielA.getValue();
		} else if (source == ielB.textField) {
			triangle.b = ielB.getValue();
		} else if (source == ielC.textField) {
			triangle.c = ielC.getValue();
		}
		fireChangeListeners();
	}

	public void addChangeListener(ChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		changeListeners.remove(changeListener);
	}

	private void fireChangeListeners() {
		ChangeEvent e = new ChangeEvent(this);
		for (int i = changeListeners.size() - 1; i >= 0; i--)
			changeListeners.get(i).stateChanged(e);
	}
}
