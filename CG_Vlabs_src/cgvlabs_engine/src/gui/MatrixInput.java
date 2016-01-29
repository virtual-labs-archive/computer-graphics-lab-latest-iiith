package gui;

import engine.matrix.Matrix;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MatrixInput extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1545024661184308864L;
	protected NumberEditableLabel[][] mlabels = new NumberEditableLabel[4][4];
	protected double[][] matrix;
	protected ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	public MatrixInput(Matrix m) {
		matrix = m.toArray(1);

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				mlabels[i][j] = new NumberEditableLabel("", matrix[i][j], false);

		setLayout(new GridLayout(4, 4));

		ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object source = e.getSource();
				for(int i = 0; i < 4; i++)
					for(int j = 0; j < 4; j++)
						if (source == mlabels[i][j]) {
							matrix[i][j] = mlabels[i][j].getValue();
						}
				revalidate();
				fireChangeListeners();
			}
		};

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++) {
				mlabels[i][j].addChangeListener(cl);
				add(mlabels[i][j]);
			}
	}

	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
		updateFields();
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public void updateFields() {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				mlabels[i][j].setValue(matrix[i][j]);
	}

	public boolean isEditing() {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (mlabels[i][j].isEditing())
					return true;
		return false;
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

	public String toString() {
		return "Matrix";
	}
}
