package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import engine.CoordSystem;
import engine.Instance;
import engine.Shape;
import engine.Transformation;
import engine.World;
import experiments.Experiment;

public class InstanceDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7414637035461377372L;
	public JTextField txtInstanceLabel;
	public JComboBox cmbCoordSystems, cmbTransformations;
	public JButton btnAddShape, btnRemoveShape, btnNewCoordSystem, btnNewTransformation, btnOK, btnCancel;
	public DefaultListModel listModel = new DefaultListModel();
	public JList lstShapes = new JList(listModel);
	protected Instance instance = null;

	public InstanceDialog(JDialog owner, String title) {
		super(owner, title);
		setLocationRelativeTo(owner);
		setSize(400, 230);

		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();
		JPanel panel = new JPanel(gbl);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets.top = 5;
		gbc.insets.left = 5;

		// Instance Label
		JLabel label = new JLabel("Instance Name:");
		gbl.setConstraints(label, gbc);
		panel.add(label);

		txtInstanceLabel = new JTextField("Instance " + Instance.count);
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		gbl.setConstraints(txtInstanceLabel, gbc);
		panel.add(txtInstanceLabel);

		// Shapes list
		label = new JLabel("Shapes:");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		final World world = Experiment.experiment.world;

		lstShapes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstShapes.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					btnRemoveShape.setEnabled(lstShapes.getSelectedIndex() != -1);
				}
			}
		});

		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		JScrollPane sclShapes = new JScrollPane(lstShapes);
		gbl.setConstraints(sclShapes, gbc);
		panel.add(sclShapes);

		btnAddShape = new JButton("Add...");
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbl.setConstraints(btnAddShape, gbc);
		panel.add(btnAddShape);

		btnRemoveShape = new JButton("Remove");
		btnRemoveShape.setEnabled(false);
		gbc.gridy = 2;
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		gbl.setConstraints(btnRemoveShape, gbc);
		panel.add(btnRemoveShape);

		// Co-ordinate systems list
		label = new JLabel("Co-ordinate system:");
		gbc.gridx = 0;
		gbc.gridy++;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		cmbCoordSystems = new JComboBox(world.coordSystems.toArray());
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbl.setConstraints(cmbCoordSystems, gbc);
		panel.add(cmbCoordSystems);

		btnNewCoordSystem = new JButton("New...");
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		gbl.setConstraints(btnNewCoordSystem, gbc);
		panel.add(btnNewCoordSystem);

		// Transformations list
		label = new JLabel("Transformation:");
		gbc.gridx = 0;
		gbc.gridy++;
		gbl.setConstraints(label, gbc);
		panel.add(label);

		cmbTransformations = new JComboBox(world.transformations.toArray());
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbl.setConstraints(cmbTransformations, gbc);
		panel.add(cmbTransformations);

		btnNewTransformation = new JButton("New...");
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		gbl.setConstraints(btnNewTransformation, gbc);
		panel.add(btnNewTransformation);

		// OK, Cancel
		btnOK = new JButton("OK");
		btnOK.setEnabled(false);
		gbc.gridx = 1;
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(btnOK, gbc);
		panel.add(btnOK);

		btnCancel = new JButton("Cancel");
		gbc.gridx = 2;
		gbl.setConstraints(btnCancel, gbc);
		panel.add(btnCancel);

		add(panel);

		final JDialog instanceDialog = this;

		btnAddShape.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final ShapeDialog sd = new ShapeDialog(instanceDialog, "Add Shape");
				sd.btnDone.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						listModel.addElement(sd.getShape());
						btnOK.setEnabled(true);
						sd.setVisible(false);
					}
				});
				sd.setVisible(true);
			}
		});

		btnRemoveShape.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.removeElement(lstShapes.getSelectedValue());
				btnOK.setEnabled(listModel.size() > 0);
			}
		});

		btnNewCoordSystem.addActionListener(new ActionListener() {
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

	public InstanceDialog(JDialog owner, String title, Instance instance) {
		this(owner, title);
		for (Shape shape : instance.shapes)
			listModel.addElement(shape);
		cmbCoordSystems.setSelectedItem(instance.coordSystem);
		cmbTransformations.setSelectedItem(instance.transformation);
		txtInstanceLabel.setText(instance.label);
		this.instance = instance;

		btnOK.setEnabled(true);
	}

	public Instance getInstance() {
		if(listModel.size() == 0) return null;
		Instance instance = new Instance((Shape) listModel.get(0), (CoordSystem) cmbCoordSystems
				.getSelectedItem(), (Transformation) cmbTransformations.getSelectedItem(), true, true, txtInstanceLabel.getText());
		for (int i = 1; i < listModel.getSize(); i++)
			instance.addShape((Shape) listModel.get(i));
		return instance;
	}

	public void updateInstance() {
		if(instance != null && listModel.size() > 0) {
			instance.removeAllShapes();
			for (int i = 0; i < listModel.getSize(); i++)
				instance.addShape((Shape) listModel.get(i));
			instance.coordSystem = (CoordSystem)cmbCoordSystems.getSelectedItem();
			instance.transformation = (Transformation)cmbTransformations.getSelectedItem();
			instance.label = txtInstanceLabel.getText();
		}
	}
}
