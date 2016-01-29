package gui;

import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import engine.Operation;

public class MatrixPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5787849270548705655L;
	protected JLabel[][] mlabels = new JLabel[4][4];
	protected double[][] coeffs;
	protected Operation operation;

	protected static DecimalFormat formatter = new DecimalFormat();
	static {
		formatter.setMinimumFractionDigits(1);
		formatter.setMaximumFractionDigits(1);
	}

	public MatrixPanel(Operation o) {
		setLayout(new GridLayout(4, 4));

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++) {
				mlabels[i][j] = new JLabel("", SwingConstants.CENTER);
				mlabels[i][j].setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
				add(mlabels[i][j]);
			}

		setOperation(o);
	}

	public void setOperation(Operation o) {
		operation = o;
		updateCoeffs();
	}

	public Operation getOperation() {
		return operation;
	}

	public void updateCoeffs() {
		coeffs = operation.matrix.toArray(operation._t);
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				mlabels[i][j].setText(formatter.format(coeffs[i][j]));
	}

	public String toString() {
		return operation.matrix.toString();
	}
}
