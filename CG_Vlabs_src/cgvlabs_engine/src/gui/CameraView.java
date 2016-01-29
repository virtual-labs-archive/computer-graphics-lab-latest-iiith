package gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import engine.Camera;
import engine.Camera.CameraType;
import engine.matrix.Matrix;
import engine.matrix.SingularMatrixException;
import experiments.Experiment;

public class CameraView implements GLEventListener {
	public int width, height;
	public Camera camera;
	public TextRenderer textRenderer;
	public FPSAnimator animator;
	public JFrame frame;

	public CameraView(Camera camera) {
		this.camera = camera;
		width = 300;
		height = 300;

		frame = new JFrame(camera.toString());
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);

		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);

		/* Instantiate an object to create the world */
		canvas.addGLEventListener(this);

		/* The panel contains complete GUI */
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(canvas, BorderLayout.CENTER);
		frame.add(mainPanel);

		textRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
		frame.setVisible(true);
		animator = new FPSAnimator(canvas, 100);
		animator.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		updateMatrices(gl);

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);

		//gl.glLoadIdentity();
		Experiment.experiment.world.draw(gl, textRenderer, true);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.setSwapInterval(1);

		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.2f, 0.0f);

		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);

		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glEnable(GL2.GL_ALPHA);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnable(GL2.GL_LIGHTING);
		float pos[] = { 15, 15, 15, 0 };
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_LIGHT0);
		pos[0] = -pos[0];
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_LIGHT1);

		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);

		width = drawable.getWidth();
		height = drawable.getHeight();
	}

	private void updateMatrices(GL2 gl) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if(camera.type == CameraType.ORTHOGRAPHIC) {
			gl.glOrtho(camera.l, camera.r, camera.b, camera.t, camera.n, camera.f);
		} else {
			gl.glFrustum(camera.l, camera.r, camera.b, camera.t, camera.n, camera.f);
		}

		try {
			double[][] Mi = camera.transformation.toInverseArray(Experiment.experiment.world.display);
			Mi = Matrix.multiply(Mi, camera.coordSystem.mi);
			double[] m = new double[16];
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					m[i+j*4] = Mi[i][j];
	
			gl.glMultMatrixd(m, 0);
		} catch (SingularMatrixException e) {
		}

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		if (height == 0)
			height = 1;
		gl.glViewport(x, y, width, height);

		updateMatrices(gl);

		this.width = width;
		this.height = height;
	}
}
