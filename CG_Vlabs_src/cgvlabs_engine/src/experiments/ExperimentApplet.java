package experiments;

import java.awt.BorderLayout;

import javax.swing.JApplet;

import experiments.experiment1.Experiment1;

public class ExperimentApplet extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3407225304326970491L;

	public void init() {
		setSize(1000,600);
		getContentPane().setLayout(new BorderLayout());
		Experiment1.prepare(this);
	}

	@Override
	public void start() {
		Experiment1.start();
	}

	@Override
	public void stop() {
		Experiment1.stop();
	}

	@Override
	public void destroy() {
		Experiment1.destroy();
		super.destroy();
	}
}
