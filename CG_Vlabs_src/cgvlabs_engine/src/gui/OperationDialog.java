package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;

import engine.Axis;
import engine.Operation;
import engine.Vector;
import engine.matrix.CustomMatrix;
import engine.matrix.Matrix;
import engine.matrix.RotationMatrix;
import engine.matrix.ScaleMatrix;
import engine.matrix.SingularMatrixException;
import engine.matrix.SkewMatrix;
import engine.matrix.TranslationMatrix;

public class OperationDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9121018171175344437L;
	private String choices[] = { "Translation", "Rotation", "Scale", "Skew", "Custom" };
	private JPanel panels[] = new JPanel[choices.length];
	private JTabbedPane pane = new JTabbedPane();
	public JSpinner spnDuration = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
	public JButton btnOK = new JButton("OK");

	/* Translation Matrix */
	private JTextField txtTranslationX = new JTextField(5);
	private JTextField txtTranslationY = new JTextField(5);
	private JTextField txtTranslationZ = new JTextField(5);

	/* Rotation Matrix */
	private JTextField txtRotationTheta = new JTextField(5);
	private JComboBox cmbRotationAxis = new JComboBox(Axis.values());
	private JTextField txtRotationAxisX = new JTextField(5);
	private JTextField txtRotationAxisY = new JTextField(5);
	private JTextField txtRotationAxisZ = new JTextField(5);

	/* Scale Matrix */
	private JTextField txtScaleX = new JTextField(5);
	private JTextField txtScaleY = new JTextField(5);
	private JTextField txtScaleZ = new JTextField(5);

	/* Skew Matrix */
	private JTextField txtSkewXy = new JTextField(5);
	private JTextField txtSkewXz = new JTextField(5);
	private JTextField txtSkewYx = new JTextField(5);
	private JTextField txtSkewYz = new JTextField(5);
	private JTextField txtSkewZx = new JTextField(5);
	private JTextField txtSkewZy = new JTextField(5);

	/* Custom Matrix */
	private JTextField txtCustomM[][] = new JTextField[4][4];

	public OperationDialog(Dialog owner, String title) {
		super(owner, title);
		setLayout(new BorderLayout());

		JPanel panel = new JPanel(new BorderLayout());

		for (int i = 0; i < choices.length; i++) {
			panels[i] = makePanel(choices[i]);
			pane.addTab(choices[i], panels[i]);
		}

		panel.add(pane, BorderLayout.CENTER);

		JPanel northPanel = new JPanel();
		northPanel.add(new JLabel("Duration: "));
		((NumberEditor) spnDuration.getEditor()).getTextField().setColumns(3);
		northPanel.add(spnDuration);
		panel.add(northPanel, BorderLayout.NORTH);

		JPanel southPanel = new JPanel();
		southPanel.add(btnOK);
		panel.add(southPanel, BorderLayout.SOUTH);

		add(panel, BorderLayout.CENTER);
		setSize(400, 250);
		setLocationRelativeTo(owner);
	}

	/*
	 * Populate values from the given operation
	 */
	public OperationDialog(Dialog owner, String title, Operation operation) {
		this(owner, title);

		spnDuration.setValue(operation.duration);
		Matrix m = operation.matrix;
		if (m instanceof TranslationMatrix) {
			pane.setSelectedIndex(0);
			Vector t = ((TranslationMatrix) m).translation;
			txtTranslationX.setText(String.valueOf(t.x));
			txtTranslationY.setText(String.valueOf(t.y));
			txtTranslationZ.setText(String.valueOf(t.z));
		} else if (m instanceof RotationMatrix) {
			pane.setSelectedIndex(1);
			txtRotationTheta.setText(String.valueOf(((RotationMatrix) m).theta));
			Axis axis = ((RotationMatrix) m).axis;
			cmbRotationAxis.setSelectedItem(axis);
			if (axis == Axis.OTHER) {
				Vector r = ((RotationMatrix) m).axisVector;
				txtRotationAxisX.setText(String.valueOf(r.x));
				txtRotationAxisY.setText(String.valueOf(r.y));
				txtRotationAxisZ.setText(String.valueOf(r.z));
			}
		} else if (m instanceof ScaleMatrix) {
			pane.setSelectedIndex(2);
			Vector s = ((ScaleMatrix) m).scale;
			txtScaleX.setText(String.valueOf(s.x));
			txtScaleY.setText(String.valueOf(s.y));
			txtScaleZ.setText(String.valueOf(s.z));
		} else if (m instanceof SkewMatrix) {
			pane.setSelectedIndex(3);
			SkewMatrix s = (SkewMatrix) m;
			txtSkewXy.setText(String.valueOf(s.xs.y));
			txtSkewXz.setText(String.valueOf(s.xs.z));
			txtSkewYx.setText(String.valueOf(s.ys.x));
			txtSkewYz.setText(String.valueOf(s.ys.z));
			txtSkewZx.setText(String.valueOf(s.zs.x));
			txtSkewZy.setText(String.valueOf(s.zs.y));
		} else if (m instanceof CustomMatrix) {
			pane.setSelectedIndex(4);
			double d[][] = ((CustomMatrix) m).m;
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++)
					txtCustomM[i][j].setText(String.valueOf(d[i][j]));
		}
	}

	Operation getOperation() {
		return makeOperation(choices[pane.getSelectedIndex()]);
	}

	private JPanel makePanel(String name) {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		if ("Translation".equals(name)) {
			JPanel xPanel = new JPanel();
			xPanel.add(new JLabel("Tx:"));
			xPanel.add(txtTranslationX);
			panel.add(xPanel);

			JPanel yPanel = new JPanel();
			yPanel.add(new JLabel("Ty:"));
			yPanel.add(txtTranslationY);
			panel.add(yPanel);

			JPanel zPanel = new JPanel();
			zPanel.add(new JLabel("Tz:"));
			zPanel.add(txtTranslationZ);
			panel.add(zPanel);
		} else if ("Rotation".equals(name)) {
			JPanel thetaPanel = new JPanel();
			thetaPanel.add(new JLabel("Theta (in degrees):"));
			thetaPanel.add(txtRotationTheta);
			panel.add(thetaPanel);

			JPanel axisPanel = new JPanel();
			axisPanel.add(new JLabel("Axis of rotation:"));
			axisPanel.add(cmbRotationAxis);
			panel.add(axisPanel);

			final JPanel axisVectorPanel = new JPanel();
			axisVectorPanel.add(new JLabel("X:"));
			axisVectorPanel.add(txtRotationAxisX);
			axisVectorPanel.add(new JLabel("Y:"));
			axisVectorPanel.add(txtRotationAxisY);
			axisVectorPanel.add(new JLabel("Z:"));
			axisVectorPanel.add(txtRotationAxisZ);
			// panel.add(axisVectorPanel);

			cmbRotationAxis.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (Axis.OTHER == ((JComboBox) e.getSource()).getSelectedItem()) {
						panel.add(axisVectorPanel);
					} else {
						panel.remove(axisVectorPanel);
					}
				}
			});
		} else if ("Scale".equals(name)) {
			JPanel xPanel = new JPanel();
			xPanel.add(new JLabel("Sx:"));
			xPanel.add(txtScaleX);
			panel.add(xPanel);

			JPanel yPanel = new JPanel();
			yPanel.add(new JLabel("Sy:"));
			yPanel.add(txtScaleY);
			panel.add(yPanel);

			JPanel zPanel = new JPanel();
			zPanel.add(new JLabel("Sz:"));
			zPanel.add(txtScaleZ);
			panel.add(zPanel);
		} else if ("Skew".equals(name)) {
			JPanel xPanel = new JPanel();
			xPanel.add(new JLabel("Xy:"));
			xPanel.add(txtSkewXy);
			xPanel.add(new JLabel("Xz:"));
			xPanel.add(txtSkewXz);
			panel.add(xPanel);

			JPanel yPanel = new JPanel();
			yPanel.add(new JLabel("Yx:"));
			yPanel.add(txtSkewYx);
			yPanel.add(new JLabel("Yz:"));
			yPanel.add(txtSkewYz);
			panel.add(yPanel);

			JPanel zPanel = new JPanel();
			zPanel.add(new JLabel("Zx:"));
			zPanel.add(txtSkewZx);
			zPanel.add(new JLabel("Zy:"));
			zPanel.add(txtSkewZy);
			panel.add(zPanel);
		} else if ("Custom".equals(name)) {
			for (int i = 0; i < 4; i++) {
				JPanel rowPanel = new JPanel();
				for (int j = 0; j < 4; j++) {
					JTextField tf = new JTextField(i == j ? "     1" : "     0");
					txtCustomM[i][j] = tf;
					rowPanel.add(txtCustomM[i][j]);
				}
				panel.add(rowPanel);
			}
		}
		return panel;
	}

	private Operation makeOperation(String name) {
		Matrix m = null, mi = null;
		int duration;
		try {
			duration = (Integer) spnDuration.getValue();
			if ("Translation".equals(name)) {
				double tx = Double.parseDouble(txtTranslationX.getText());
				double ty = Double.parseDouble(txtTranslationY.getText());
				double tz = Double.parseDouble(txtTranslationZ.getText());
				m = new TranslationMatrix(tx, ty, tz);
				mi = new TranslationMatrix(-tx, -ty, -tz);
			} else if ("Rotation".equals(name)) {
				double theta = Double.parseDouble(txtRotationTheta.getText());
				Axis axis = (Axis) cmbRotationAxis.getSelectedItem();
				if (axis == Axis.OTHER) {
					Vector v = new Vector(Double.parseDouble(txtRotationAxisX.getText()), Double
							.parseDouble(txtRotationAxisY.getText()), Double.parseDouble(txtRotationAxisZ.getText()));
					m = new RotationMatrix(v, theta);
					mi = new RotationMatrix(v, -theta);
				} else {
					m = new RotationMatrix(axis, theta);
					mi = new RotationMatrix(axis, -theta);
				}
			} else if ("Scale".equals(name)) {
				double sx = Double.parseDouble(txtScaleX.getText());
				double sy = Double.parseDouble(txtScaleY.getText());
				double sz = Double.parseDouble(txtScaleZ.getText());
				m = new ScaleMatrix(sx, sy, sz);
				mi = new ScaleMatrix(1 / sx, 1 / sy, 1 / sz);
			} else if ("Skew".equals(name)) {
				m = new SkewMatrix(Double.parseDouble(txtSkewXy.getText()), Double.parseDouble(txtSkewXz.getText()),
						Double.parseDouble(txtSkewYx.getText()), Double.parseDouble(txtSkewYz.getText()), Double
								.parseDouble(txtSkewZx.getText()), Double.parseDouble(txtSkewZy.getText()));
				double inv[][] = new double[4][4];
				Matrix.inverse(m.toArray(1), inv);
				mi = new CustomMatrix(inv);
			} else if ("Custom".equals(name)) {
				double d[][] = new double[4][4], di[][] = new double[4][4];
				for (int i = 0; i < 4; i++)
					for (int j = 0; j < 4; j++)
						d[i][j] = Double.parseDouble(txtCustomM[i][j].getText());
				Matrix.inverse(d, di);
				m = new CustomMatrix(d);
				mi = new CustomMatrix(di);
			}
		} catch (NumberFormatException ne) {
			JOptionPane.showMessageDialog(null, "One or more values specified are not valid.", "Validation error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (SingularMatrixException se) {
			JOptionPane.showMessageDialog(this, "This matrix is singular. Please check the values", "Singular Matrix",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return new Operation(m, mi, duration);
	}
}
