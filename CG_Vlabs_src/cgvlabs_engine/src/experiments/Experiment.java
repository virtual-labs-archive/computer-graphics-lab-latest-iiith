package experiments;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.gl2.GLUgl2;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import engine.Camera;
import engine.CoordSystem;
import engine.Instance;
import engine.Shape;
import engine.Transformation;
import engine.Vector;
import engine.World;
import gui.CameraDialog;
import gui.CameraView;
import gui.InstanceDialog;
import gui.TransformationDialog;
import gui.VectorInput;
import gui.WorldTree;
import gui.WorldTreeNode;

public abstract class Experiment implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener {
	public World world;
	public TextRenderer textRenderer;

	/* GUI components */
	public static GLCanvas canvas;
	public static JPanel mainPanel, sidePanel, pointPanel;
	public static VectorInput currentPoint;
	public static JLabel currentPointCartesian;
	public static JScrollBar frameScrollbar;
	public static JTabbedPane tabbedPane;
	public static JPanel worldTabPanel, displayTabPanel;
	public static JButton btnNew, btnEdit, btnDelete, btnNewCamera;
	public static Object treeSelectedObject;
	public static WorldTree tree;
	public static HashMap<Camera,CameraView> cameraViews;

	/* OpenGL view transformations */
	protected java.awt.Point pPrev;
	protected Vector translation = new Vector(0, 0, 0);
	protected Vector rotation = new Vector(0, 0, 0);
	protected double sx = 10, sy = 10, aspectRatio;
	protected int width, height;

	/* Static data */
	public static Experiment experiment;
	protected static FPSAnimator animator;
	protected static GLU glu;
	protected static GL2 gl;
	protected static MouseEvent mouseEvent;
	protected static int mouseWheelRotation;
	public static boolean lockVertices;

	public static void prepare(Container mainContainer, Experiment exp) {
		// try {
		// for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		// if ("Nimbus".equals(info.getName())) {
		// UIManager.setLookAndFeel(info.getClassName());
		// break;
		// }
		// }
		// } catch (Exception e) {
		// // If Nimbus is not available, you can set the GUI to another look
		// and feel.
		// }
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		canvas = new GLCanvas(caps);

		/* Instantiate an object to create the world */
		experiment = exp;
		canvas.addGLEventListener(experiment);
		canvas.addMouseListener(experiment);
		canvas.addMouseMotionListener(experiment);
		canvas.addMouseWheelListener(experiment);

		/* The panel contains complete GUI */
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(canvas, BorderLayout.CENTER);
		mainContainer.add(mainPanel);

		animator = new FPSAnimator(canvas, 100);

		/* Create Camera Views */
		cameraViews = new HashMap<Camera, CameraView>();
		for(Camera camera : experiment.world.cameras) {
			cameraViews.put(camera, new CameraView(camera));
		}

		/* The side panel */
		sidePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		sidePanel.setPreferredSize(new Dimension(300, 400));
		mainPanel.add(sidePanel, BorderLayout.EAST);

		tabbedPane = new JTabbedPane();
		sidePanel.add(tabbedPane);

		prepareWorldTab();
		tabbedPane.addTab("World", worldTabPanel);

		prepareDisplayTab();
		tabbedPane.addTab("Display", displayTabPanel);

		/* Current frame slider */
		frameScrollbar = new JScrollBar(JScrollBar.HORIZONTAL);
		frameScrollbar.setMinimum(0);
		frameScrollbar.setMaximum(10000);

		frameScrollbar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				experiment.world.display.currentFrame = ae.getValue();
				tree.updateMatrices();
			}
		});

		mainPanel.add(frameScrollbar, BorderLayout.SOUTH);

		glu = new GLUgl2();
	}

	abstract protected URL getInstructionsURL();

	protected static String loadInstructionURL(URL url) throws IOException {
		String newline = System.getProperty("line.separator");
		String inputLine, text = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		while ((inputLine = in.readLine()) != null)
		    text = text + inputLine + newline;
		in.close();
		System.out.println(text);
		return text;
	}

	protected static void prepareWorldTab() {
		worldTabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		worldTabPanel.setPreferredSize(new Dimension(300, 520));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);
		splitPane.setPreferredSize(new Dimension(300, 400));

		/* Add the instructions panel */
		try {
			JEditorPane editorPane = new JEditorPane();
			editorPane.setContentType("text/html");
			editorPane.setPage(experiment.getInstructionsURL());
			editorPane.setEditable(false);

			editorPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						JEditorPane pane = (JEditorPane) e.getSource();
						try {
							pane.setPage(e.getURL());
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			});

			JScrollPane editorScrollPane = new JScrollPane(editorPane);
			editorScrollPane.setPreferredSize(new Dimension(300, 200));
			editorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			splitPane.setTopComponent(editorScrollPane);
		} catch (IOException e) {
			System.err.println("Error loading instructions file");
			e.printStackTrace();
		}

		/* The world tree */
		tree = new WorldTree(experiment.world);

		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		splitPane.setBottomComponent(scrollPane);
		worldTabPanel.add(splitPane);

		/* New and Delete */
		addNewEditDelete();

		/* Point panel */
		pointPanel = new JPanel(new GridLayout(2, 1));
		pointPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"Current Point's Absolute co-ordinates"));

		currentPoint = new VectorInput("Homogeneous: ", new Vector(0, 0, 0), false);
		pointPanel.add(currentPoint);

		currentPointCartesian = new JLabel("Cartesian: " + currentPoint.getVector().toCartesianString());
		pointPanel.add(currentPointCartesian);

		worldTabPanel.add(pointPanel);
	}

	protected static void addNewEditDelete() {
		btnNew = new JButton("New");
		worldTabPanel.add(btnNew);
		btnNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final InstanceDialog id = new InstanceDialog(null, "New Instance");
				id.btnOK.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Instance instance = id.getInstance();
						experiment.world.addInstance(instance);
						tree.addInstance(instance);
						id.setVisible(false);
					}
				});
				id.setVisible(true);
			}
		});

		btnEdit = new JButton("Edit");
		btnEdit.setEnabled(false);
		worldTabPanel.add(btnEdit);

		btnDelete = new JButton("Del");
		btnDelete.setEnabled(false);
		worldTabPanel.add(btnDelete);

		btnNewCamera = new JButton("New Cam");
		worldTabPanel.add(btnNewCamera);
		btnNewCamera.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final CameraDialog cd = new CameraDialog(null, "New Camera");
				cd.btnOK.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Camera camera = cd.getCamera();
						if(camera != null) {
							experiment.world.addCamera(camera);
							CameraView cv = new CameraView(camera);
							cameraViews.put(camera, cv);
							tree.addCamera(camera, cv);
							cd.setVisible(false);
						}
					}
				});
				cd.setVisible(true);
			}
		});


		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				WorldTreeNode node = (WorldTreeNode) e.getPath().getLastPathComponent();
				Object o = node.getUserObject();
				treeSelectedObject = o;
				btnDelete.setEnabled((o instanceof Shape || o instanceof CoordSystem || o instanceof Transformation || o instanceof Camera));
				btnEdit.setEnabled(o instanceof Transformation || o instanceof Instance || o instanceof Camera);
			}
		});

		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object o = treeSelectedObject;
				if (o instanceof Transformation) {
					final Transformation transformation  = (Transformation)o;
					final TransformationDialog td = new TransformationDialog(null, "Editing " + o.toString(), transformation);
					td.btnDone.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							td.updateTransformation();
							tree.updateTransformation(transformation);
							td.setVisible(false);
						}
					});
					td.setVisible(true);
				} else if(o instanceof Instance) {
					final Instance instance = (Instance)o;
					final InstanceDialog id = new InstanceDialog(null, "Editing " + instance.toString(), instance);
					id.btnOK.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							id.updateInstance();
							id.setVisible(false);
							tree.updateInstance(instance);
						}
					});
					id.setVisible(true);
				} else if(o instanceof Camera) {
					final Camera camera = (Camera)o;
					final CameraDialog cd = new CameraDialog(null, "Editing " + camera.toString(), camera);
					cd.btnOK.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							cd.updateCamera();
							cd.setVisible(false);
							tree.updateCamera(camera, experiment.world.display);
						}
					});
					cd.setVisible(true);
				}
			}
		});

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object o = treeSelectedObject;
				if (o instanceof Instance) {
					if (JOptionPane.showConfirmDialog(null,
							"This will delete this instance and all associated instances. Continue?",
							"Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						experiment.world.deleteInstance((Instance) o);
						tree.deleteInstance((Instance)o);
					}
				} else if (o instanceof Shape) {
					if (JOptionPane.showConfirmDialog(null,
							"This will delete this shape and all associated instances. Continue?", "Confirm deletion",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						experiment.world.deleteShape((Shape) o);
						tree.deleteShape((Shape)o);
					}
				} else if (o instanceof CoordSystem) {
					if (JOptionPane.showConfirmDialog(null,
							"This will delete this co-ordinate system and all associated instances. Continue?",
							"Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						experiment.world.deleteCoordSystem((CoordSystem) o);
						tree.deleteCoordSystem((CoordSystem)o);
					}
				} else if (o instanceof Transformation) {
					if (JOptionPane.showConfirmDialog(null,
							"This will delete this transformation and all associated instances. Continue?",
							"Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						experiment.world.deleteTransformation((Transformation) o);
						tree.deleteTransformation((Transformation)o);
					}
				} else if (o instanceof Camera) {
					if (JOptionPane.showConfirmDialog(null,
							"This will delete this Camera and the associated view. Continue?",
							"Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						Camera camera = (Camera)o;
						CameraView cv = cameraViews.get(camera);
						experiment.world.deleteCamera(camera);
						tree.deleteCamera(camera);
						cv.frame.setVisible(false);
					}					
				}
				btnDelete.setEnabled(false);
			}
		});
	}

	protected static void prepareDisplayTab() {
		displayTabPanel = new JPanel(new BorderLayout());

		JPanel flags = new JPanel(new GridLayout(0, 1));

		/* 3D or 2D */
		JCheckBox cb = new JCheckBox("3D", experiment.world.display.is3D);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				experiment.world.display.is3D = (ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		flags.add(cb);

		/* Left click edit Vertex or not */
		cb = new JCheckBox("Lock vertices", lockVertices);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				lockVertices = (ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		flags.add(cb);

		/* Show/Hide animation */
		cb = new JCheckBox("Show animation", experiment.world.display.showAnimation);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				experiment.world.display.showAnimation = (ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		flags.add(cb);

		/* Transform co-ordinate systems or points */
		cb = new JCheckBox("Transform Co-ordinate Systems", experiment.world.display.transformCoordSystems);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				experiment.world.display.transformCoordSystems = (ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		flags.add(cb);

		/* Draw XY grid */
		cb = new JCheckBox("Draw XY grid", experiment.world.display.theme.drawXYGrid);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				experiment.world.display.theme.drawXYGrid = (ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		flags.add(cb);

		/* Draw YZ grid */
		cb = new JCheckBox("Draw YZ grid", experiment.world.display.theme.drawYZGrid);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				experiment.world.display.theme.drawYZGrid = (ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		flags.add(cb);

		/* Draw ZX grid */
		cb = new JCheckBox("Draw ZX grid", experiment.world.display.theme.drawZXGrid);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				experiment.world.display.theme.drawZXGrid = (ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		flags.add(cb);

		displayTabPanel.add(flags, BorderLayout.NORTH);

		/* Reset view button */
		Button resetButton = new Button("Reset View");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				experiment.rotation = new Vector(0, 0, 0);
				experiment.translation = new Vector(0, 0, 0);
			}
		});
		JPanel resetPanel = new JPanel();
		resetPanel.add(resetButton);
		displayTabPanel.add(resetPanel, BorderLayout.CENTER);
	}

	protected static void updatePointPanel() {
		Instance si = experiment.world.getCurrent();
		if (si == null) {
			currentPoint.setEnabled(false);
			return;
		}

		if (currentPoint.isEditing()) {
			si.setSelected(currentPoint.getVector(), experiment.world.display);
			tree.updateVertices(si);
		} else {
			Vector point = si.getSelected(experiment.world.display);
			if (point == null) {
				currentPoint.setEnabled(false);
			} else {
				currentPoint.setEnabled(true);
				currentPoint.setVector(point);
			}
		}
		currentPointCartesian.setText("Cartesian: " + currentPoint.getVector().toCartesianString());
	}

	public static void start() {
		animator.start();
	}

	public static void stop() {
		animator.stop();
	}

	public static void destroy() {
		System.err.println("Destroying...");
		canvas.destroy();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		update();
		render(drawable);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();

		gl.setSwapInterval(1);

		textRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.PLAIN, 50));

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
		/* Light 0 */
		float pos[] = { 0, 0, -1, 1 };
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_LIGHT0);

		/* Light 1 */
		pos[2] = -pos[2];
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_LIGHT1);
		
		/* Light 2 */
		pos[1] = pos[2];
		pos[2] = 0;
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_LIGHT2);

		/* Light 3 */
		pos[1] = -pos[1];
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_LIGHT3);

		/* Light 4 */
		pos[0] = pos[1];
		pos[1] = 0;
		gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_LIGHT4);

		/* Light 5 */
		pos[0] = -pos[0];
		gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_LIGHT5);

		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);

		width = drawable.getWidth();
		height = drawable.getHeight();	
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		gl = drawable.getGL().getGL2();

		if (height == 0)
			height = 1;
		aspectRatio = width / (double) height;
		gl.glViewport(x, y, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-sx * aspectRatio, sx * aspectRatio, -sy, sy, -1000, 1000);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		this.width = width;
		this.height = height;
	}

	protected void update() {
		updatePointPanel();
	}

	protected void render(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
//		gl.glColor3f(1.0f, 0.0f, 0.0f);

		gl.glTranslated(translation.x, translation.y, translation.z);

		if (world.display.is3D) {
			gl.glRotated(rotation.x, 1, 0, 0);
			gl.glRotated(rotation.y, 0, 1, 0);
		}
		gl.glRotated(rotation.z, 0, 0, 1);

		world.draw(gl, textRenderer, false);

		/*
		 * Handle mouse events
		 */
		if (mouseEvent != null && pPrev != null) {
			int modifiers = mouseEvent.getModifiersEx();
			java.awt.Point p = mouseEvent.getPoint();
			double dx = p.getX() - pPrev.getX();
			double dy = p.getY() - pPrev.getY();

			if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) > 0) {
				if(!lockVertices) {
				Instance si = experiment.world.getCurrent();
					if (si != null) {
						Vector v = si.getSelected(experiment.world.display);
						Vector dv = getOpenGLPos(p.x, p.y).subtract(getOpenGLPos(pPrev.x, pPrev.y));
						v = v.add(dv);

						if ((modifiers & MouseEvent.SHIFT_DOWN_MASK) > 0) {
							v.x = Math.rint(v.x / v.w * 10) * 0.1 * v.w;
							v.y = Math.rint(v.y / v.w * 10) * 0.1 * v.w;
							v.z = Math.rint(v.z / v.w * 10) * 0.1 * v.w;
						} else {
							v.x = Math.rint(v.x / v.w * 100) * 0.01 * v.w;
							v.y = Math.rint(v.y / v.w * 100) * 0.01 * v.w;
							v.z = Math.rint(v.z / v.w * 100) * 0.01 * v.w;
						}
						si.setSelected(v, world.display);

						tree.updateVertices(si);
					}
				}
				else {
					translation.x += 20 * dx / (double) height;
					translation.y -= 20 * dy / (double) height;
				}
			} else if ((modifiers & MouseEvent.BUTTON2_DOWN_MASK) > 0) {
				translation.x += 20 * dx / (double) height;
				translation.y -= 20 * dy / (double) height;
			} else if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) > 0) {
				if (world.display.is3D) {
					rotation.y += 360.0 * dx / (double) width;
					rotation.x += 360.0 * dy / (double) height;
				} else {
					double x1 = p.getX() - width / 2, x2 = pPrev.getX() - width / 2;
					double y1 = height / 2 - p.getY(), y2 = height / 2 - pPrev.getY();
					rotation.z += Math.toDegrees(Math.atan2(y1, x1) - Math.atan2(y2, x2));
				}
			}
			pPrev = p;
			mouseEvent = null;
		}

		if (mouseWheelRotation != 0) {
			sx += mouseWheelRotation;
			sy += mouseWheelRotation;
			mouseWheelRotation = 0;

			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(-sx * aspectRatio, sx * aspectRatio, -sy, sy, -1000, 1000);

			gl.glMatrixMode(GL2.GL_MODELVIEW);
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	protected Vector getOpenGLPos(int x, int y) {
		int viewport[] = new int[4];
		double modelview[] = new double[16];
		double projection[] = new double[16];
		double winX, winY, winZ = 0;
		double[] pos = new double[3];

		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);

		winX = x;
		winY = viewport[3] - y - 1;

		// if (world.display.is3D) {
		// FloatBuffer fb = FloatBuffer.allocate(1);
		// gl.glReadPixels(x, (int) winY, 1, 1, GL2.GL_DEPTH_COMPONENT,
		// GL2.GL_FLOAT, fb);
		// winZ = fb.get(0);
		// }

		glu.gluUnProject(winX, winY, winZ, modelview, 0, projection, 0, viewport, 0, pos, 0);

		return new Vector(pos[0], pos[1], pos[2]);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseEvent = e;
		pPrev = e.getPoint();
		int modifiers = mouseEvent.getModifiersEx();
		if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) > 0) {
			canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else if ((modifiers & MouseEvent.BUTTON2_DOWN_MASK) > 0) {
			canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		} else if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) > 0) {
			canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseEvent = e;
		pPrev = e.getPoint();
		canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseEvent = e;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelRotation = e.getWheelRotation();
	}
}