package gui;

import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import engine.CoordSystem;
import engine.Vector;
import experiments.Experiment;

public class CoordSystemDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1239006496851894701L;
	protected Vector x = new Vector(1,0,0,0);
	protected Vector y = new Vector(0,1,0,0);
	protected Vector z = new Vector(0,0,1,0);
	protected Vector o = new Vector(0,0,0,1);
	public JButton btnOK = new JButton("OK");

	public CoordSystemDialog(Dialog owner, String title) {
		super(owner, title);
		
		JPanel panel = new JPanel(new FlowLayout());

		// X
		final VectorInput X = new VectorInput("X axis:", x, true);
		panel.add(X);

		// Y
		final VectorInput Y = new VectorInput("Y axis:", y, true);
		panel.add(Y);

		// Z
		final VectorInput Z = new VectorInput("Z axis:", z, true);
		panel.add(Z);

		// O
		final VectorInput O = new VectorInput("Origin:", o, false);
		panel.add(O);

		panel.add(btnOK);
//		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		add(panel);
		setSize(160, 200);
		setLocationRelativeTo(owner);
	}

	public CoordSystem getCoordSystem() {
		return new CoordSystem(x, y, z, o, Experiment.experiment.world.coordSystemCount);
	}
}
