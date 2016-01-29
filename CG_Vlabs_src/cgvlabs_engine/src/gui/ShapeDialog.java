package gui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import engine.Instance;
import engine.Shape;
import engine.World;
import experiments.Experiment;

public class ShapeDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4394714698437842310L;
	public JComboBox cmbShapes;
	public JButton btnDone, btnCancel;

	public ShapeDialog(JDialog owner, String title) {
		super(owner, title);
		setSize(300, 120);
		setLocationRelativeTo(owner);

		final World world = Experiment.experiment.world;

		JPanel panel = new JPanel(new BorderLayout());

		// Shapes list
		JLabel label = new JLabel("Select Shape:");
		panel.add(label, BorderLayout.NORTH);

		cmbShapes = new JComboBox(world.shapes.toArray());
		for (Instance i : world.instances)
			cmbShapes.addItem(i);
		panel.add(cmbShapes, BorderLayout.CENTER);

		JButton btnNew = new JButton("New...");
		panel.add(btnNew, BorderLayout.EAST);

		JPanel btnPanel = new JPanel();
		btnDone = new JButton("Done");
		btnCancel = new JButton("Cancel");
		btnPanel.add(btnDone);
		btnPanel.add(btnCancel);
		panel.add(btnPanel, BorderLayout.SOUTH);

		add(panel);

		final JDialog shapeDialog = this;
		// Add behaviour:
		btnNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fd = new FileDialog(shapeDialog, "Select a PLY file:", FileDialog.LOAD);
				fd.setVisible(true);
				String fname = fd.getFile();
				if (fname != null) {
					fname = fd.getDirectory() + fname;
					try {
						Shape s = world.addMesh(fname);
						cmbShapes.addItem(s);
						cmbShapes.setSelectedItem(s);
					} catch (FileNotFoundException fe) {
						JOptionPane.showMessageDialog(shapeDialog, "Eror reading from " + fname + ": "
								+ fe.getMessage());
					}
				}
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	public Shape getShape() {
		return (Shape)cmbShapes.getSelectedItem();
	}
}
