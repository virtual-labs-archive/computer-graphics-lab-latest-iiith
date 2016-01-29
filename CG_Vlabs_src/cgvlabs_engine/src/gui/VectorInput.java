package gui;

import engine.Vector;

import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VectorInput extends JPanel {
	private static final long serialVersionUID = 1L;
	protected NumberEditableLabel nelX, nelY, nelZ, nelW;
	protected JLabel label;
	protected Vector vector;
	protected boolean isCartesian;
	protected ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	public void setVector(Vector v) {
		this.vector = v;
		updateFields();
	}

	public Vector getVector() {
		return vector;
	}

	public String toString() {
		if(isCartesian)
			return vector.toCartesianString();
		else
			return vector.toString(); 
	}

	public VectorInput(String caption, Vector v, boolean isCartesian) {
		this.vector = v;
		this.isCartesian = isCartesian;

		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		nelX = new NumberEditableLabel("", v.x);
		nelY = new NumberEditableLabel("", v.y);
		nelZ = new NumberEditableLabel("", v.z);
		nelW = new NumberEditableLabel("", v.w);

		ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object source = e.getSource();
				if (source == nelX) {
					vector.x = nelX.getValue();
				} else if (source == nelY) {
					vector.y = nelY.getValue();
				} else if (source == nelZ) {
					vector.z = nelZ.getValue();
				} else if (source == nelW) {
					vector.w = nelW.getValue();
				}
				revalidate();
				fireChangeListeners();
			}
		};

		nelX.addChangeListener(cl);
		nelY.addChangeListener(cl);
		nelZ.addChangeListener(cl);

		if(!isCartesian)
			nelW.addChangeListener(cl);

		add(new JLabel(caption));
		add(new JLabel(isCartesian ? "<" : "["));
		add(nelX);
		add(new JLabel(","));
		add(nelY);
		add(new JLabel(","));
		add(nelZ);
		if(!isCartesian) {
			add(new JLabel(","));
			add(nelW);
		}
		add(new JLabel(isCartesian ? ">" : "]"));

		setOpaque(false);
	}

	public VectorInput(String caption, Vector v) {
		this(caption, v, false);
	}

	public VectorInput(Vector v) {
		this("", v);
	}

	public void updateFields() {
		nelX.setValue(vector.x);
		nelY.setValue(vector.y);
		nelZ.setValue(vector.z);
		nelW.setValue(vector.w);
	}

	public boolean isEditing() {
		return (nelX.isEditing() || nelY.isEditing() || nelZ.isEditing() || nelW.isEditing());
	}

	public void addChangeListener(ChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		changeListeners.remove(changeListener);
	}

	protected void fireChangeListeners() {
		ChangeEvent e = new ChangeEvent(this);
		for (int i = changeListeners.size() - 1; i >= 0; i--)
			changeListeners.get(i).stateChanged(e);
	}
}
