package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import engine.Camera;
import engine.Transformation;
import engine.World;
import engine.Camera.CameraType;
import experiments.Experiment;

public class CameraDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3371678694283216084L;
	public JTextField txtNear, txtFar, txtBottom, txtTop, txtLeft, txtRight;
	public JTextField txtEx, txtEy, txtEz, txtLx, txtLy, txtLz, txtUx, txtUy, txtUz;
	public JComboBox cmbType, cmbCoordSystems, cmbTransformations;
	public JButton btnNewCoordSystem, btnNewTransformation, btnOK, btnCancel;
	protected Camera camera = null;

	public CameraDialog(JDialog owner, String title) {
		super(owner, title);

		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();
		JPanel panel = new JPanel(gbl);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets.top = 5;
		gbc.insets.left = 5;

		// Clipping parameters:
		JLabel label;
		label = new JLabel("Clipping");
		gbc.gridy = 0;
		gbc.gridheight = 3;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		label = new JLabel("Near:");
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtNear = new JTextField(5);
		gbl.setConstraints(txtNear, gbc);
		panel.add(txtNear);

		label = new JLabel("Far:");
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtFar = new JTextField(5);
		gbl.setConstraints(txtFar, gbc);
		panel.add(txtFar);

		gbc.gridy++;

		label = new JLabel("Bottom:");
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtBottom = new JTextField(5);
		gbl.setConstraints(txtBottom, gbc);
		panel.add(txtBottom);

		label = new JLabel("Top:");
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtTop = new JTextField(5);
		gbl.setConstraints(txtTop, gbc);
		panel.add(txtTop);

		gbc.gridy++;

		label = new JLabel("Left:");
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtLeft = new JTextField(5);
		gbl.setConstraints(txtLeft, gbc);
		panel.add(txtLeft);

		label = new JLabel("Right:");
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtRight = new JTextField(5);
		gbl.setConstraints(txtRight, gbc);
		panel.add(txtRight);

		/* New Data */
		// Eye Position
		label = new JLabel("Camera position:");
		gbc.gridy++;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtEx = new JTextField(5);
		gbl.setConstraints(txtEx, gbc);
		panel.add(txtEx);

		txtEy = new JTextField(5);
		gbl.setConstraints(txtEy, gbc);
		panel.add(txtEy);

		txtEz = new JTextField(5);
		gbl.setConstraints(txtEz, gbc);
		panel.add(txtEz);

		// Look At
		label = new JLabel("Target position:");
		gbc.gridy++;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtLx = new JTextField(5);
		gbl.setConstraints(txtLx, gbc);
		panel.add(txtLx);

		txtLy = new JTextField(5);
		gbl.setConstraints(txtLy, gbc);
		panel.add(txtLy);

		txtLz = new JTextField(5);
		gbl.setConstraints(txtLz, gbc);
		panel.add(txtLz);

		// Up Vector
		label = new JLabel("Up Vector:");
		gbc.gridy++;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtUx = new JTextField(5);
		gbl.setConstraints(txtUx, gbc);
		panel.add(txtUx);

		txtUy = new JTextField(5);
		gbl.setConstraints(txtUy, gbc);
		panel.add(txtUy);

		txtUz = new JTextField(5);
		gbl.setConstraints(txtUz, gbc);
		panel.add(txtUz);

		// Type:
		label = new JLabel("Type:");
		gbc.gridy++;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		cmbType = new JComboBox(CameraType.values());
		gbc.gridwidth = 4;
		gbl.setConstraints(cmbType, gbc);
		panel.add(cmbType);

		// Co-ordinate systems list
		final World world = Experiment.experiment.world;
/*
		label = new JLabel("Co-ordinate system:");
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		cmbCoordSystems = new JComboBox(world.coordSystems.toArray());
		gbc.gridwidth = 4;
		gbl.setConstraints(cmbCoordSystems, gbc);
		panel.add(cmbCoordSystems);

		btnNewCoordSystem = new JButton("New...");
		gbc.gridwidth = 1;
		gbl.setConstraints(btnNewCoordSystem, gbc);
		panel.add(btnNewCoordSystem);
*/
		// Transformations list
		label = new JLabel("Transformation:");
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		cmbTransformations = new JComboBox(world.transformations.toArray());
		gbc.gridwidth = 4;
		gbl.setConstraints(cmbTransformations, gbc);
		panel.add(cmbTransformations);

		btnNewTransformation = new JButton("New...");
		gbc.gridwidth = 1;
		gbl.setConstraints(btnNewTransformation, gbc);
		panel.add(btnNewTransformation);

		// OK, Cancel
		btnOK = new JButton("OK");
		gbc.gridx = 1;
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(btnOK, gbc);
		panel.add(btnOK);

		btnCancel = new JButton("Cancel");
		gbc.gridx = 2;
		gbl.setConstraints(btnCancel, gbc);
		panel.add(btnCancel);

		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(panel);
		pack();
		setLocationRelativeTo(owner);

		final JDialog instanceDialog = this;

/*		btnNewCoordSystem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final CoordSystemDialog csd = new CoordSystemDialog(instanceDialog, "New Co-ordinate System");
				csd.btnOK.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						CoordSystem coordSystem = world.addCoordSystem(csd.getCoordSystem());
						cmbCoordSystems.addItem(coordSystem);
						cmbCoordSystems.setSelectedItem(coordSystem);
						csd.setVisible(false);
					}
				});
				csd.setVisible(true);
			}
		});
*/
		btnNewTransformation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final TransformationDialog td = new TransformationDialog(instanceDialog, "New Transformation");
				td.btnDone.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Transformation t = td.getTransformation();
						world.addTransformation(t);
						cmbTransformations.addItem(t);
						cmbTransformations.setSelectedItem(t);
						td.setVisible(false);
					}
				});
				td.setVisible(true);
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	public CameraDialog(JDialog owner, String title, Camera camera) {
		this(owner, title);
		txtNear.setText(String.valueOf(camera.n));
		txtFar.setText(String.valueOf(camera.f));
		txtLeft.setText(String.valueOf(camera.l));
		txtRight.setText(String.valueOf(camera.r));
		txtBottom.setText(String.valueOf(camera.b));
		txtTop.setText(String.valueOf(camera.t));
		cmbType.setSelectedItem(camera.type);
		txtEx.setText(String.valueOf(camera.ex));
		txtEy.setText(String.valueOf(camera.ey));
		txtEz.setText(String.valueOf(camera.ez));
		txtLx.setText(String.valueOf(camera.lx));
		txtLy.setText(String.valueOf(camera.ly));
		txtLz.setText(String.valueOf(camera.lz));
		txtUx.setText(String.valueOf(camera.ux));
		txtUy.setText(String.valueOf(camera.uy));
		txtUz.setText(String.valueOf(camera.uz));
		// cmbCoordSystems.setSelectedItem(camera.coordSystem);
		cmbTransformations.setSelectedItem(camera.transformation);
		this.camera = camera;
	}

	public Camera getCamera() {
		try {
			double n = Double.parseDouble(txtNear.getText());
			double f = Double.parseDouble(txtFar.getText());
			double b = Double.parseDouble(txtBottom.getText());
			double t = Double.parseDouble(txtTop.getText());
			double l = Double.parseDouble(txtLeft.getText());
			double r = Double.parseDouble(txtRight.getText());
			double ex = Double.parseDouble(txtEx.getText());
			double ey = Double.parseDouble(txtEy.getText());
			double ez = Double.parseDouble(txtEz.getText());
			double lx = Double.parseDouble(txtLx.getText());
			double ly = Double.parseDouble(txtLy.getText());
			double lz = Double.parseDouble(txtLz.getText());
			double ux = Double.parseDouble(txtUx.getText());
			double uy = Double.parseDouble(txtUy.getText());
			double uz = Double.parseDouble(txtUz.getText());
			return new Camera(n, f, b, t, l, r, ex, ey, ez, lx, ly, lz, ux, uy, uz, 
					(CameraType) cmbType.getSelectedItem(), 
					(Transformation) cmbTransformations.getSelectedItem());
/*			return new Camera(n, f, b, t, l, r, 
					(CameraType) cmbType.getSelectedItem(), 
					(CoordSystem) cmbCoordSystems.getSelectedItem(), 
					(Transformation) cmbTransformations.getSelectedItem()); */
		} catch (NumberFormatException ne) {
			JOptionPane.showMessageDialog(null, "One or more values specified are not valid.", "Validation error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public void updateCamera() {
		if (camera != null) {
			camera.n = Double.parseDouble(txtNear.getText());
			camera.f = Double.parseDouble(txtFar.getText());
			camera.b = Double.parseDouble(txtBottom.getText());
			camera.t = Double.parseDouble(txtTop.getText());
			camera.l = Double.parseDouble(txtLeft.getText());
			camera.r = Double.parseDouble(txtRight.getText());
			camera.type = (CameraType) cmbType.getSelectedItem();
			camera.ex = Double.parseDouble(txtEx.getText());
			camera.ey = Double.parseDouble(txtEy.getText());
			camera.ez = Double.parseDouble(txtEz.getText());
			camera.lx = Double.parseDouble(txtLx.getText());
			camera.ly = Double.parseDouble(txtLy.getText());
			camera.lz = Double.parseDouble(txtLz.getText());
			camera.ux = Double.parseDouble(txtUx.getText());
			camera.uy = Double.parseDouble(txtUy.getText());
			camera.uz = Double.parseDouble(txtUz.getText());
			camera.updateCoordSystem();
			// camera.coordSystem = (CoordSystem) cmbCoordSystems.getSelectedItem();
			camera.transformation = (Transformation) cmbTransformations.getSelectedItem();
		}
	}
}
