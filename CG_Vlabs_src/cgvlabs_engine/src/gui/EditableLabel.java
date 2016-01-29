package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EditableLabel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
	protected ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	protected boolean isEditable = true;

	public static Color onFocusColor = new Color(0, 0.5f, 0.9f);

	public JLabel label, caption;
	public JTextField textField;

	public EditableLabel(String captionText, JTextField customTextField, boolean editable) {
		caption = new JLabel(captionText);
		isEditable = editable;

		label = new JLabel(customTextField.getText());
		label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		label.setOpaque(true);

		textField = customTextField;
		textField.setVisible(false);
		textField.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		setOpaque(false);

		setLayout(new BorderLayout(0, 0));
		add(caption, BorderLayout.WEST);
		add(label, BorderLayout.CENTER);
		add(textField, BorderLayout.EAST);

		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				startEditing();
			}
		});
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				stopEditing();
			}
		});

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				int c = arg0.getKeyCode();
				switch (c) {
				case KeyEvent.VK_ENTER:
					stopEditing();
					break;
				case KeyEvent.VK_ESCAPE:
					cancelEditing();
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				fireChangeListeners();
			}
		});

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if(isEditable)
					label.setForeground(onFocusColor);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if(isEditable)
					label.setForeground(Color.BLACK);
			}
		});
	}

	public EditableLabel(String captionText, String text, boolean isEditable) {
		this(captionText, new JTextField(text), isEditable);
	}

	public EditableLabel(String captionText, String text) {
		this(captionText, new JTextField(text), true);
	}

	public void startEditing() {
		if(isEditable) {
			label.setVisible(false);
			textField.setVisible(true);
			textField.setCaretPosition(0);
			textField.requestFocusInWindow();
			validate();
		}
	}

	public boolean isTextValid() {
		return true;
	}

	public void stopEditing() {
		if (!isTextValid()) {
			cancelEditing();
			return;
		}
		label.setText(textField.getText());
		label.setVisible(true);
		textField.setVisible(false);
		validate();
		fireChangeListeners();
		fireActionListeners();
	}

	public void cancelEditing() {
		label.setVisible(true);
		textField.setText(label.getText());
		textField.setVisible(false);
		validate();
	}

	public void setEditable(boolean editable) {
		isEditable = editable;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setText(String text) {
		label.setText(text);
		textField.setText(text);
	}

	public String getText() {
		return label.getText();
	}

	public boolean isEditing() {
		return textField.isVisible();
	}

	@Override
	public String toString() {
		return getText();
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

	public void addActionListener(ActionListener a) {
		actionListeners.add(a);
	}

	public void removeActionListener(ActionListener a) {
		actionListeners.remove(a);
	}

	private void fireActionListeners() {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getText());
		for (int i = actionListeners.size() - 1; i >= 0; i--)
			actionListeners.get(i).actionPerformed(e);
	}
}
