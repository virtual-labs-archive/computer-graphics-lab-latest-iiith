package gui;

import engine.Vertex;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class VertexInput extends VectorInput {
	private static final long serialVersionUID = 1L;
	protected JCheckBox cbTracking = new JCheckBox("Tracking");
	protected Vertex vertex;

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex v) {
		setVector(v.vector);
		vertex = v;
		cbTracking.setSelected(vertex.isTracking);
	}

	public String toString() {
		return super.toString() + (cbTracking.isSelected() ? " Tracking" : "");
	}
	public VertexInput(Vertex v) {
		super(v.vector);
		vertex = v;

		add(cbTracking);
		cbTracking.setOpaque(false);
		cbTracking.setSelected(vertex.isTracking);
		cbTracking.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vertex.isTracking = cbTracking.isSelected();
				fireChangeListeners();
			}
		});
	}
}
