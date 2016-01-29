package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import engine.Operation;
import engine.Transformation;

public class TransformationDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3803008213425453660L;
	protected Transformation transformation = null;
	protected OperationDialog operationDialog;

	public JPanel panel = new JPanel(new BorderLayout());
	public DefaultListModel listModel = new DefaultListModel();
	public JList lstOperations = new JList(listModel);
	public JButton btnDone = new JButton("Done");
	public JButton btnAdd = new JButton("Add...");
	public JButton btnEdit = new JButton("Edit...");
	public JButton btnDelete = new JButton("Delete");
	public JButton btnUp = new JButton("\u02C4");
	public JButton btnDown = new JButton("\u02C5");
	public JSpinner spnrStartFrame = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));

	ArrayList<Operation> operations = new ArrayList<Operation>();

	/* Creating Mode */
	public TransformationDialog(Dialog owner, String title) {
		super(owner, title);
		operationDialog = new OperationDialog(this, "New Operation");
		operationDialog.btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Operation o = operationDialog.getOperation();
				if (o != null) {
					insertRow(o);
					operationDialog.setVisible(false);
				}
			}
		});

		((JSpinner.DefaultEditor) spnrStartFrame.getEditor()).getTextField().setColumns(4);
		JPanel sfPanel = new JPanel();
		sfPanel.add(new JLabel("Starting Frame: "));
		sfPanel.add(spnrStartFrame);
		panel.add(sfPanel, BorderLayout.NORTH);

		lstOperations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstOperations.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					int index = lstOperations.getSelectedIndex();
					if (index == -1) {
						btnEdit.setEnabled(false);
						btnDelete.setEnabled(false);
						btnUp.setEnabled(false);
						btnDown.setEnabled(false);
					} else {
						btnUp.setEnabled(index > 0);
						btnDown.setEnabled(index < listModel.getSize() - 1);
						btnEdit.setEnabled(true);
						btnDelete.setEnabled(true);
					}
				}
			}
		});
		panel.add(new JScrollPane(lstOperations), BorderLayout.CENTER);

		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				operationDialog.setVisible(true);
			}
		});

		final TransformationDialog thisDialog = this;
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int index = lstOperations.getSelectedIndex();
				Operation operation = (Operation)listModel.get(index);
				final OperationDialog editDialog = new OperationDialog(thisDialog, "Edit Operation", operation);
				editDialog.btnOK.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Operation o = editDialog.getOperation();
						if(o != null) {
							listModel.remove(index);
							listModel.add(index, o);
							lstOperations.setSelectedIndex(index);
							lstOperations.ensureIndexIsVisible(index);
							editDialog.setVisible(false);
						}
					}
				});
				editDialog.setVisible(true);
			}
		});
		btnEdit.setEnabled(false);

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = lstOperations.getSelectedIndex();
				listModel.remove(index);

				int size = listModel.getSize();

				if (size == 0) {
					btnDelete.setEnabled(false);
				} else { // Select an index.
					if (index == listModel.getSize()) {
						index--;
					}
					lstOperations.setSelectedIndex(index);
					lstOperations.ensureIndexIsVisible(index);
				}
			}
		});
		btnDelete.setEnabled(false);

		btnUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = lstOperations.getSelectedIndex();
				Object o = listModel.get(index);
				listModel.remove(index);
				index--;
				listModel.add(index, o);
				lstOperations.setSelectedIndex(index);
				lstOperations.ensureIndexIsVisible(index);
			}
		});
		btnUp.setEnabled(false);

		btnDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = lstOperations.getSelectedIndex();
				Object o = listModel.get(index);
				listModel.remove(index);
				index++;
				listModel.add(index, o);
				lstOperations.setSelectedIndex(index);
				lstOperations.ensureIndexIsVisible(index);
			}
		});
		btnDown.setEnabled(false);

		JPanel btnPanel = new JPanel();
		btnPanel.add(btnAdd);
		btnPanel.add(btnEdit);
		btnPanel.add(btnDelete);
		btnPanel.add(btnUp);
		btnPanel.add(btnDown);
		btnPanel.add(btnDone);

		panel.add(btnPanel, BorderLayout.SOUTH);

		add(panel);
		setSize(400, 300);
		setLocationRelativeTo(owner);
	}

	/* Editing Mode */
	public TransformationDialog(Dialog owner, String title, Transformation transformation) {
		this(owner, title);
		spnrStartFrame.setValue(transformation.firstFrame);
		for(Operation o : transformation.operations) {
			insertRow(o);
		}
		// Only set when editing
		this.transformation = transformation;
	}

	protected void insertRow(Operation operation) {
		operations.add(operation);
		listModel.addElement(operation);

		int index = listModel.getSize() - 1;
		lstOperations.setSelectedIndex(index);
		lstOperations.ensureIndexIsVisible(index);
		btnUp.setEnabled(index > 0);
		btnDown.setEnabled(index < listModel.getSize() - 1);
	}

	public Transformation getTransformation() {
		ArrayList<Operation> operations = new ArrayList<Operation>();
		for(int i = 0; i < listModel.size(); i++)
			operations.add((Operation)listModel.get(i));
		return new Transformation((Integer)spnrStartFrame.getValue(), operations);
	}

	/* To be used in edit mode */
	public void updateTransformation() {
		if(transformation != null) {
			transformation.operations.clear();
			for(int i = 0; i < listModel.size(); i++)
				transformation.operations.add((Operation)listModel.get(i));
			transformation.firstFrame = (Integer)spnrStartFrame.getValue();			
		}
	}
}
