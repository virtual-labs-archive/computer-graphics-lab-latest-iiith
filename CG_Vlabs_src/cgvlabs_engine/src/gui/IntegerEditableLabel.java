package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class IntegerEditableLabel extends EditableLabel {
	private static final long serialVersionUID = 1L;
	public int maximum = Integer.MAX_VALUE, minimum = Integer.MIN_VALUE;

	public IntegerEditableLabel(String caption, int value) {
		super(caption, String.valueOf(value));

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if(!(c == '-' || c == '+' || (c >= '0' && c <= '9')))
					e.consume();
			}
		});
	}

	public IntegerEditableLabel(String caption, int value, int min, int max) {
		this(caption, value);
		maximum = max;
		minimum = min;
	}

	public boolean isTextValid() {
		try {
			int i = Integer.parseInt(textField.getText());
			return (i >= minimum) && (i <= maximum);
		} catch (NumberFormatException e) {
		}
		return false;
	};

	Integer getValue() {
		if(isTextValid())
			return Integer.parseInt(textField.getText());
		else
			return Integer.parseInt(label.getText());
	}

	void setValue(Integer i) {
		label.setText(String.valueOf(i));
		textField.setText(label.getText());
	}
}
