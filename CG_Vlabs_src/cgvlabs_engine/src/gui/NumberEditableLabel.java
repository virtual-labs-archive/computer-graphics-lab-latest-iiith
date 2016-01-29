package gui;

import engine.Vector;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class NumberEditableLabel extends EditableLabel {
	private static final long serialVersionUID = 1L;

	public NumberEditableLabel(String caption, double value, boolean isEditable) {
		super(caption, Vector.formatter.format(value), isEditable);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!(c == '.' || c == '-' || c == '+' || (c >= '0' && c <= '9')))
					e.consume();
			}
		});
	}

	public NumberEditableLabel(String caption, double value) {
		this(caption, value, true);
	}

	public boolean isTextValid() {
		try {
			Double.parseDouble(textField.getText());
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	};

	Double getValue() {
		if (isTextValid()) {
			return Double.parseDouble(textField.getText());
		} else {
			return Double.parseDouble(label.getText());
		}
	}

	void setValue(Double v) {
		label.setText(Vector.formatter.format(v));
		textField.setText(label.getText());
	}
}
