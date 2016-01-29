package clipping;
import java.applet.*;

import java.awt.*;
import java.awt.event.*;


import library.DrawToolkit;
import library.Line;
import library.Point;
import library.Polygon;

public class DrawClipLine extends Applet  implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ReturnObject RO;
	int width, height;
	private static DrawToolkit draw;
	private static int xmin,xmax,ymin,ymax;
	private float pointRadii=(float) 4.0;
	private float normalPointRadii = (float)3.0;
	private int pointer;

	// Button information
	private static final String NextButtonName = "Next Iteration";
	private static final String PreviousButtonName = "Previous Iteration";
	private static final String DefaultParamsName = "Start Experiment with Default Values";
	private static final String ClipWindowName = "Enter";
	private static final String StartEndName = "Start Experiment";
	private static final String BackName = "Back";

	//Window size
	private static final int horizontalWindowSize = 600;
	private static final int verticalWindowSize = 600;
	private static final int horizontalCoordinateSize = 500;
	private static final int verticalCoordinateSize = 600;
	private static final int WINDOW_GAP1 = 100;
	private static final int WINDOW_GAP2 = 20;

	//String display information
	private static final int shiftXcoordinate = 2;
	private static final int shiftYcoordinate = 4;

	private Button next, previous, default_params, clip_window, start_end, back_button;

	private static final int nextButtonWidth = 100;
	private static final int previousButtonWidth = 125;
	private static final int default_button_width = 250;
	private static final int frame_start_button_width = 250;
	private static final int back_button_width = 100;
	private static final int buttonHeight = 20;
	// private static final int fixedBottomGap = 20;

	private static TextField grid_start_x_text, grid_start_y_text;
	private static TextField grid_end_x_text, grid_end_y_text;
	private static TextField start_x1_text, start_y1_text, end_x2_text, end_y2_text;
	private static final int TEXT_COLUMNS = 50;

	// Colour information
	private static final Color displayColor = Color.white;
	// private static final Color coordinateColor = Color.white;
	private static final Color clippingWindowColor = Color.white;
	private static final Color gridColor = Color.orange;
	private static final Color clippedLineColor = Color.gray;
	private static final Color clippingLineColor = Color.magenta.brighter();
	private static final Color highlightColor = Color.blue;
	private static final Color highlightPointColor = Color.yellow;
	private static final Color acceptColor = Color.green;
	private static final Color binaryColor = Color.white;
	private static final Color dotCoordinatecolor = Color.RED.brighter();

	private int pre_draw_state;

	private Polygon P;
	private Line L;
	private LineClipping LC;

	private Point rect1, rect2, rect3, rect4;
	private Point p1, p2;

	// It controls when should we move to next step
	private int gotoNextStep;

	private int transx(int a)
	{
		return a;	
	}
	private int transy(int a)
	{
		return height-a;	
	}


	public void init()
	{
		P = new Polygon();
		LC = new LineClipping();

		setSize(horizontalWindowSize, verticalWindowSize);
		Messages.setStringxSelectLinedisplay(horizontalWindowSize-200);
		Messages.setStringySelectLinedisplay(100);
		Messages.setStringxClipRectangleDisplay(horizontalWindowSize-200);
		Messages.setStringyClipRectangleDisplay(40);
		Messages.setStringxCodeDisplay(horizontalWindowSize-200);
		Messages.setStringyCodeDisplay(110);

		width = getSize().width;
		height = getSize().height;
		// System.out.println("Width"+width);
		// System.out.println("Height"+height);

		rect1 = new Point(100, 100);
		rect2 = new Point(300, 100);
		rect3 = new Point(300, 300);
		rect4 = new Point(100, 300);
		p1 = new Point(10, 450);
		p2 = new Point(500, 40);
		P.addVertex(rect1);
		P.addVertex(rect2);
		P.addVertex(rect3);
		P.addVertex(rect4);

		//Clip the line
		L = new Line(p1,p2);
		RO = LC.Clip(L, P);

		int boundary[];
		boundary = new int[4];
		//Get end points of the Polygon
		P.getRectangleBoundary(boundary);
		xmin = transx(boundary[0]);
		xmax = transx(boundary[1]);
		ymin = transy(boundary[2]);
		ymax = transy(boundary[3]);

		//Display the object got after clipping the line
		for(int i = 0; i < RO.count; i++)
		{

			RO.RenderObject[i][1] = RO.RenderObject[i][1];
			RO.RenderObject[i][2] = transy(RO.RenderObject[i][2]);
			RO.RenderObject[i][3] = transx(RO.RenderObject[i][3]);
			RO.RenderObject[i][4] = transy(RO.RenderObject[i][4]);

			/*
			for(int j = 0; j < 7; j++)
				System.out.print(RO.RenderObject[i][j]+" ");

			System.out.println("");
			 */	
		}
		// System.out.println(RO.count);

		//Make a object for Drawing Shapes
		draw = new DrawToolkit();
		//resize(width);

		// Make the default background color black.
		setBackground( Color.black );
		
		setLayout(null);

		//Make next button and previous button
		next = new Button(NextButtonName);
		previous = new Button(PreviousButtonName);
		default_params = new Button(DefaultParamsName);
		clip_window = new Button(ClipWindowName);
		start_end = new Button(StartEndName);
		back_button = new Button(BackName);			

		grid_start_x_text = new TextField(TEXT_COLUMNS);
		grid_start_y_text = new TextField(TEXT_COLUMNS);
		grid_end_x_text = new TextField(TEXT_COLUMNS);
		grid_end_y_text = new TextField(TEXT_COLUMNS);
		start_x1_text = new TextField(TEXT_COLUMNS);
		start_y1_text = new TextField(TEXT_COLUMNS);
		end_x2_text = new TextField(TEXT_COLUMNS);
		end_y2_text = new TextField(TEXT_COLUMNS);

		/*
			next.setBounds((getWidth() - nextButtonWidth)/3, getHeight() - nextButtonHeight - fixedBottomGap,
					nextButtonWidth, nextButtonHeight);
			previous.setBounds((getWidth() - previousButtonWidth)/3, getHeight() - previousButtonHeight - fixedBottomGap,
					previousButtonWidth, previousButtonHeight);
		 */

		add(next);
		add(previous);
		add(default_params);
		add(clip_window);
		add(start_end);
		add(back_button);

		add(grid_start_x_text);
		add(grid_start_y_text);
		add(grid_end_x_text);
		add(grid_end_y_text);
		add(start_x1_text);
		add(start_y1_text);
		add(end_x2_text);
		add(end_y2_text);

		next.setBackground(Color.WHITE);
		previous.setBackground(Color.WHITE);
		default_params.setBackground(Color.WHITE);
		clip_window.setBackground(Color.WHITE);
		start_end.setBackground(Color.WHITE);
		back_button.setBackground(Color.WHITE);

		grid_start_x_text.setBackground(Color.WHITE);
		grid_start_y_text.setBackground(Color.WHITE);
		grid_end_x_text.setBackground(Color.WHITE);
		grid_end_y_text.setBackground(Color.WHITE);
		start_x1_text.setBackground(Color.WHITE);
		start_y1_text.setBackground(Color.WHITE);
		end_x2_text.setBackground(Color.WHITE);
		end_y2_text.setBackground(Color.WHITE);

		next.addActionListener(this);
		previous.addActionListener(this);
		default_params.addActionListener(this);
		clip_window.addActionListener(this);
		start_end.addActionListener(this);
		back_button.addActionListener(this);

		grid_start_x_text.addActionListener(this);
		grid_start_y_text.addActionListener(this);
		grid_end_x_text.addActionListener(this);
		grid_end_y_text.addActionListener(this);
		start_x1_text.addActionListener(this);
		start_y1_text.addActionListener(this);
		end_x2_text.addActionListener(this);
		end_y2_text.addActionListener(this);

		// the following lines need to be changed later
		next.setVisible(false);
		previous.setVisible(false);
		default_params.setVisible(true);
		clip_window.setVisible(true);
		start_end.setVisible(false);
		back_button.setVisible(false);

		grid_start_x_text.setVisible(true);
		grid_start_y_text.setVisible(true);
		grid_end_x_text.setVisible(true);
		grid_end_y_text.setVisible(true);
		start_x1_text.setVisible(false);
		start_y1_text.setVisible(false);
		end_x2_text.setVisible(false);
		end_y2_text.setVisible(false);

		next.setBounds(horizontalWindowSize + WINDOW_GAP1 + previousButtonWidth + WINDOW_GAP2,
							2*WINDOW_GAP1, nextButtonWidth, buttonHeight);
		previous.setBounds(horizontalWindowSize + WINDOW_GAP1, 2*WINDOW_GAP1, previousButtonWidth, buttonHeight);
		default_params.setBounds(horizontalWindowSize + WINDOW_GAP1, 2*WINDOW_GAP1 + 2*WINDOW_GAP2,
							default_button_width, buttonHeight);
		clip_window.setBounds(horizontalWindowSize + WINDOW_GAP1,
							2*WINDOW_GAP1, frame_start_button_width, buttonHeight);
		start_end.setBounds(horizontalWindowSize + WINDOW_GAP1, 2*WINDOW_GAP1,
							frame_start_button_width, buttonHeight);
		back_button.setBounds(horizontalWindowSize + WINDOW_GAP1 + (int)(frame_start_button_width - back_button_width)/2 ,
							2*WINDOW_GAP1 + (int)(1.5*WINDOW_GAP2), back_button_width, buttonHeight);

		grid_start_x_text.setBounds(horizontalWindowSize + WINDOW_GAP1, WINDOW_GAP1 + WINDOW_GAP2,
				TEXT_COLUMNS, buttonHeight);
		grid_start_y_text.setBounds(horizontalWindowSize + WINDOW_GAP1 + 3*WINDOW_GAP2,
				WINDOW_GAP1 + WINDOW_GAP2, TEXT_COLUMNS, buttonHeight);
		grid_end_x_text.setBounds(horizontalWindowSize + WINDOW_GAP1, WINDOW_GAP1 + 3*WINDOW_GAP2,
				TEXT_COLUMNS, buttonHeight);
		grid_end_y_text.setBounds(horizontalWindowSize + WINDOW_GAP1 + 3*WINDOW_GAP2,
				WINDOW_GAP1 + 3*WINDOW_GAP2, TEXT_COLUMNS, buttonHeight);
		start_x1_text.setBounds(horizontalWindowSize + WINDOW_GAP1, WINDOW_GAP1 + WINDOW_GAP2,
				TEXT_COLUMNS, buttonHeight);
		start_y1_text.setBounds(horizontalWindowSize + WINDOW_GAP1 + 3*WINDOW_GAP2,
				WINDOW_GAP1 + WINDOW_GAP2, TEXT_COLUMNS, buttonHeight);
		end_x2_text.setBounds(horizontalWindowSize + WINDOW_GAP1, WINDOW_GAP1 + 3*WINDOW_GAP2,
				TEXT_COLUMNS, buttonHeight);
		end_y2_text.setBounds(horizontalWindowSize + WINDOW_GAP1 + 3*WINDOW_GAP2,
				WINDOW_GAP1 + 3*WINDOW_GAP2, TEXT_COLUMNS, buttonHeight);

		grid_start_x_text.setText(rect1.getX()+"");
		grid_start_y_text.setText(rect1.getY()+"");
		grid_end_x_text.setText(rect3.getX()+"");
		grid_end_y_text.setText(rect3.getY()+"");

		start_x1_text.setText(p1.getX()+"");
		start_y1_text.setText(p1.getY()+"");
		end_x2_text.setText(p2.getX()+"");
		end_y2_text.setText(p2.getY()+"");

		pre_draw_state = 0;
		pointer = 0;
		gotoNextStep = 1;
	}

	void renderclipLine(int side, Graphics g)
	{

		//Draw Line from the current points in given color and highlight the current point

		draw.makeLine(g,RO.RenderObject[pointer][1],RO.RenderObject[pointer][2],
				RO.RenderObject[pointer][3],RO.RenderObject[pointer][4],highlightColor, false);
		draw.writeCoordinates(g,RO.RenderObject[pointer][1]+ shiftXcoordinate,
				RO.RenderObject[pointer][2]- shiftYcoordinate,RO.RenderObject[pointer][1],
				transy(RO.RenderObject[pointer][2])  , dotCoordinatecolor);
		draw.writeCoordinates(g,RO.RenderObject[pointer][3] + shiftXcoordinate,
				RO.RenderObject[pointer][4]- shiftYcoordinate,RO.RenderObject[pointer][3],
				transy(RO.RenderObject[pointer][4])  , dotCoordinatecolor);
		draw.makeFilledRoundPoint(g, RO.RenderObject[pointer][1], RO.RenderObject[pointer][2],
				(float) normalPointRadii, dotCoordinatecolor);
		draw.makeFilledRoundPoint(g, RO.RenderObject[pointer][3],RO.RenderObject[pointer][4],
				(float) normalPointRadii, dotCoordinatecolor);
		draw.makeFilledRoundPoint(g,RO.RenderObject[pointer][1],RO.RenderObject[pointer][2],
				pointRadii,highlightPointColor);
		draw.writeString(g,RO.RenderObject[pointer][1]-1,RO.RenderObject[pointer][2]+10,
				RO.RenderObject[pointer][5],binaryColor);//This writes the code of the point
		draw.writeString(g,RO.RenderObject[pointer][3]-1,RO.RenderObject[pointer][4]+10,
				RO.RenderObject[pointer][6],binaryColor);//This writes the code of the point

		g.setFont(new Font("Helectiva", Font.BOLD, 16));
		//Make the sides of the clip window
		if(side==1)
		{
			g.drawString(Messages.leftSide, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1);
			draw.makeLine(g,xmin, ymin, xmin, ymax,clippingLineColor, false);
			draw.writeCoordinates(g,xmin+ shiftXcoordinate, ymin- shiftYcoordinate,xmin,
					transy(ymin),dotCoordinatecolor);
			draw.writeCoordinates(g,xmin+ shiftXcoordinate, ymax- shiftYcoordinate,xmin,
					transy(ymax),dotCoordinatecolor);
			draw.makeFilledRoundPoint(g,RO.RenderObject[pointer][1],RO.RenderObject[pointer][2],
					pointRadii,highlightPointColor);
			draw.writeCodeString(g, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 + WINDOW_GAP2,
					RO.RenderObject[pointer][5], 1, '&', displayColor);

			draw.makeFilledRoundPoint(g, xmin, ymin, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmin, ymax, (float) normalPointRadii, dotCoordinatecolor);

		}
		else if(side ==2)
		{
			g.drawString(Messages.rightSide, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1);
			draw.makeLine(g,xmax, ymin, xmax, ymax,clippingLineColor, false);
			draw.writeCoordinates(g,xmax+ shiftXcoordinate, ymin- shiftYcoordinate,xmax,
					transy(ymin),dotCoordinatecolor);
			draw.writeCoordinates(g,xmax+ shiftXcoordinate, ymax- shiftYcoordinate,xmax,
					transy(ymax),dotCoordinatecolor);
			draw.writeCodeString(g, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 + WINDOW_GAP2, 
					RO.RenderObject[pointer][5], 2, '&', displayColor);

			draw.makeFilledRoundPoint(g, xmax, ymin, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmax, ymax, (float) normalPointRadii, dotCoordinatecolor);
		}
		else if(side ==3)
		{
			g.drawString(Messages.bottomSide, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1);
			draw.makeLine(g,xmin, ymin, xmax, ymin,clippingLineColor, false);
			draw.writeCoordinates(g,xmin+ shiftXcoordinate, ymin- shiftYcoordinate,xmin,
					transy(ymin),dotCoordinatecolor);
			draw.writeCoordinates(g,xmax+ shiftXcoordinate, ymin- shiftYcoordinate,xmax,
					transy(ymin),dotCoordinatecolor);
			draw.writeCodeString(g, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 + WINDOW_GAP2,
					RO.RenderObject[pointer][5], 3, '&', displayColor);

			draw.makeFilledRoundPoint(g, xmin, ymin, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmax, ymin, (float) normalPointRadii, dotCoordinatecolor);

		}
		else if(side ==4)
		{
			g.drawString(Messages.topSide, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1);
			draw.makeLine(g,xmax, ymax, xmin, ymax,clippingLineColor, false);
			draw.writeCoordinates(g,xmax+ shiftXcoordinate, ymax- shiftYcoordinate,
					xmax, transy(ymax),dotCoordinatecolor);
			draw.writeCoordinates(g,xmin+ shiftXcoordinate, ymax- shiftYcoordinate,
					xmin, transy(ymax),dotCoordinatecolor);
			draw.writeCodeString(g, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 + WINDOW_GAP2,
					RO.RenderObject[pointer][5], 4, '&', displayColor);

			draw.makeFilledRoundPoint(g, xmax, ymax, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmin, ymax, (float) normalPointRadii, dotCoordinatecolor);

		}
		//sleep(anSpeed);
	}

	private void animate(Graphics g)
	{
		if(RO.RenderObject[pointer][0]==-2)
		{
			//This is executed when the line is completely clipped
			if(RO.accept==true)
			{
				draw.makeLine(g,RO.RenderObject[pointer][1],RO.RenderObject[pointer][2],RO.RenderObject[pointer][3],
						RO.RenderObject[pointer][4],acceptColor,false);
				draw.writeCoordinates(g,RO.RenderObject[pointer][1]+ shiftXcoordinate,
						RO.RenderObject[pointer][2]- shiftYcoordinate,RO.RenderObject[pointer][1],
						transy(RO.RenderObject[pointer][2])  ,dotCoordinatecolor);
				draw.writeCoordinates(g,RO.RenderObject[pointer][3]+ shiftXcoordinate,
						RO.RenderObject[pointer][4]- shiftYcoordinate,RO.RenderObject[pointer][3],
						transy(RO.RenderObject[pointer][4])  , dotCoordinatecolor);
				draw.makeFilledRoundPoint(g, RO.RenderObject[pointer][1], RO.RenderObject[pointer][2], 
						(float) normalPointRadii, dotCoordinatecolor);
				draw.makeFilledRoundPoint(g, RO.RenderObject[pointer][3],RO.RenderObject[pointer][4], 
						(float) normalPointRadii, dotCoordinatecolor);
				draw.writeCodeString(g, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 + WINDOW_GAP2,
						RO.RenderObject[pointer][5], RO.RenderObject[pointer][6],
						'|', displayColor);


				g.drawString("Clipped Line", horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, 
						WINDOW_GAP1);
				//This writes the code of the point
				draw.writeString(g,RO.RenderObject[pointer][1]-1,RO.RenderObject[pointer][2]+10,
						RO.RenderObject[pointer][5],binaryColor);
				//This writes the code of the point
				draw.writeString(g,RO.RenderObject[pointer][3]-1,RO.RenderObject[pointer][4]+10,
						RO.RenderObject[pointer][6],binaryColor);

			}

			if(RO.accept==false) {
				draw.writeCodeString(g, horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 + WINDOW_GAP2,
						RO.RenderObject[pointer][5], RO.RenderObject[pointer][6], '&', displayColor);
			}

			draw.makeNRectangle(g,xmin,xmax,ymin,ymax,acceptColor, dotCoordinatecolor,false,false);

			draw.writeCoordinates(g, xmin + shiftXcoordinate, ymin - shiftYcoordinate,
					xmin, transy(ymin), dotCoordinatecolor);
			draw.writeCoordinates(g, xmin + shiftXcoordinate, ymax - shiftYcoordinate,
					xmin, transy(ymax), dotCoordinatecolor);
			draw.writeCoordinates(g, xmax + shiftXcoordinate, ymax - shiftYcoordinate,
					xmax, transy(ymax), dotCoordinatecolor);
			draw.writeCoordinates(g, xmax + shiftXcoordinate, ymin - shiftYcoordinate,
					xmax, transy(ymin), dotCoordinatecolor);

			draw.makeFilledRoundPoint(g, xmin, ymin, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmin, ymax, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmax, ymax, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmax, ymin, (float) normalPointRadii, dotCoordinatecolor);
			//sleep(anSpeed);
		}
		else if(RO.RenderObject[pointer][0]==0)
		{
			// There will be always be two coordinates succeeding zero in the data given by algorithm.
			// We draw a line from one point to another.
			draw.makeLine(g,RO.RenderObject[pointer][1],RO.RenderObject[pointer][2],
					RO.RenderObject[pointer][3],RO.RenderObject[pointer][4],highlightColor,false); 
			draw.writeCoordinates(g,RO.RenderObject[pointer][1]  + shiftXcoordinate, 
					RO.RenderObject[pointer][2] - shiftYcoordinate,
					RO.RenderObject[pointer][1],
					transy(RO.RenderObject[pointer][2]), dotCoordinatecolor);
			draw.writeCoordinates(g,RO.RenderObject[pointer][3] + shiftXcoordinate,
					RO.RenderObject[pointer][4] - shiftYcoordinate,RO.RenderObject[pointer][3],
					transy(RO.RenderObject[pointer][4]), dotCoordinatecolor);

			draw.writeString(g,RO.RenderObject[pointer][1]-1,RO.RenderObject[pointer][2]+10,
					RO.RenderObject[pointer][5],binaryColor);//This writes the code of the point
			draw.writeString(g,RO.RenderObject[pointer][3]-1,RO.RenderObject[pointer][4]+10,
					RO.RenderObject[pointer][6],binaryColor);//This writes the code of the point
			draw.makeFilledRoundPoint(g, RO.RenderObject[pointer][1], RO.RenderObject[pointer][2],
					(float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, RO.RenderObject[pointer][3],RO.RenderObject[pointer][4],
					(float) normalPointRadii, dotCoordinatecolor);


		}
		else if(RO.RenderObject[pointer][0]==1)
		{
			// Highlight current point and the clip boundary it is being checked against in this case
			// which is the left one.
			renderclipLine(1,g);
		}else if(RO.RenderObject[pointer][0]==2)
		{
			renderclipLine(2,g);
		}
		else if(RO.RenderObject[pointer][0]==3)
		{
			renderclipLine(3,g);
		}
		else if(RO.RenderObject[pointer][0]==4)
		{
			renderclipLine(4,g);
		}
		if(gotoNextStep==1)
		{
			pointer+=1;
			//		              speedCounter=Speed;
			gotoNextStep=0;
		}
		if(pointer==RO.count)
			pointer=0;

	}

	private void draw_decide_clip_rectangle(Graphics g)
	{
		draw.makeNRectangle(g,xmin,xmax,ymin,ymax,clippingWindowColor, dotCoordinatecolor, false, false);

		draw.makeFilledRoundPoint(g, xmin, ymin, (float) normalPointRadii, dotCoordinatecolor);
		draw.makeFilledRoundPoint(g, xmin, ymax, (float) normalPointRadii, dotCoordinatecolor);
		draw.makeFilledRoundPoint(g, xmax, ymax, (float) normalPointRadii, dotCoordinatecolor);
		draw.makeFilledRoundPoint(g, xmax, ymin, (float) normalPointRadii, dotCoordinatecolor);

		draw.writeCoordinates(g, xmin + shiftXcoordinate, ymin - shiftYcoordinate,
				xmin, transy(ymin), dotCoordinatecolor);
		draw.writeCoordinates(g, xmin + shiftXcoordinate, ymax - shiftYcoordinate,
				xmin, transy(ymax), dotCoordinatecolor);
		draw.writeCoordinates(g, xmax + shiftXcoordinate, ymax - shiftYcoordinate,
				xmax,transy(ymax), dotCoordinatecolor);
		draw.writeCoordinates(g, xmax + shiftXcoordinate, ymin - shiftYcoordinate,
				xmax, transy(ymin), dotCoordinatecolor);

		draw.drawGrid(g,height, xmin,xmax,ymin,ymax, verticalCoordinateSize,horizontalCoordinateSize,gridColor);

		g.setFont(new Font("Helectiva", Font.PLAIN, 14));
		g.setColor(Color.WHITE);
		g.drawString("Enter the coordinates (x, y) of bottom left corner of the clipping window",
				horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 - (int)(WINDOW_GAP2/3));
		g.drawString("Enter the coordinates (x, y) of upper right corner of the clipping window",
				horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 + (int)(1.67*WINDOW_GAP2));
	}

	private void draw_decide_line(Graphics g)
	{
		draw.makeNRectangle(g,xmin,xmax,ymin,ymax,clippingWindowColor, dotCoordinatecolor, false, false);

		draw.makeFilledRoundPoint(g, xmin, ymin, (float) normalPointRadii, dotCoordinatecolor);
		draw.makeFilledRoundPoint(g, xmin, ymax, (float) normalPointRadii, dotCoordinatecolor);
		draw.makeFilledRoundPoint(g, xmax, ymax, (float) normalPointRadii, dotCoordinatecolor);
		draw.makeFilledRoundPoint(g, xmax, ymin, (float) normalPointRadii, dotCoordinatecolor);

		draw.writeCoordinates(g, xmin + shiftXcoordinate, ymin - shiftYcoordinate,
				xmin, transy(ymin), dotCoordinatecolor);
		draw.writeCoordinates(g, xmin + shiftXcoordinate, ymax - shiftYcoordinate,
				xmin, transy(ymax), dotCoordinatecolor);
		draw.writeCoordinates(g, xmax + shiftXcoordinate, ymax - shiftYcoordinate,
				xmax,transy(ymax), dotCoordinatecolor);
		draw.writeCoordinates(g, xmax + shiftXcoordinate, ymin - shiftYcoordinate,
				xmax, transy(ymin), dotCoordinatecolor);

		draw.drawGrid(g,height, xmin,xmax,ymin,ymax, verticalCoordinateSize,horizontalCoordinateSize,gridColor);

		draw.makeLine(g,RO.RenderObject[0][1],RO.RenderObject[0][2],
				RO.RenderObject[0][3],RO.RenderObject[0][4],clippedLineColor,false);
		draw.writeCoordinates(g,RO.RenderObject[0][1] + shiftXcoordinate,
				RO.RenderObject[0][2] - shiftYcoordinate, RO.RenderObject[0][1],
				transy(RO.RenderObject[0][2])  , clippedLineColor);
		draw.writeCoordinates(g,RO.RenderObject[0][3] + shiftXcoordinate,
				RO.RenderObject[0][4] - shiftYcoordinate, RO.RenderObject[0][3],
				transy(RO.RenderObject[0][4]), clippedLineColor);

		g.setFont(new Font("Helectiva", Font.PLAIN, 14));
		g.setColor(Color.WHITE);
		g.drawString("Enter the coordinates (x, y) of one end point of the line",
				horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 - (int)(WINDOW_GAP2/3));
		g.drawString("Enter the coordinates (x, y) of the other end point of the line",
				horizontalWindowSize + WINDOW_GAP1 - WINDOW_GAP2, WINDOW_GAP1 + (int)(1.67*WINDOW_GAP2));
	}

	public void paint (Graphics g)
	{
		g.translate(20, 20);

		if(pre_draw_state == 0)
			draw_decide_clip_rectangle(g);
		else if (pre_draw_state == 1)
			draw_decide_line(g);
		else
		{
			//draw.drawCoordinate(g,horizontalCoordinateSize,verticalCoordinateSize,coordinateColor);
			draw.makeNRectangle(g,xmin,xmax,ymin,ymax,clippingWindowColor, dotCoordinatecolor, false, false);

			draw.makeFilledRoundPoint(g, xmin, ymin, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmin, ymax, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmax, ymax, (float) normalPointRadii, dotCoordinatecolor);
			draw.makeFilledRoundPoint(g, xmax, ymin, (float) normalPointRadii, dotCoordinatecolor);

			draw.writeCoordinates(g, xmin + shiftXcoordinate, ymin - shiftYcoordinate,
					xmin, transy(ymin), dotCoordinatecolor);
			draw.writeCoordinates(g, xmin + shiftXcoordinate, ymax - shiftYcoordinate,
					xmin, transy(ymax), dotCoordinatecolor);
			draw.writeCoordinates(g, xmax + shiftXcoordinate, ymax - shiftYcoordinate,
					xmax,transy(ymax), dotCoordinatecolor);
			draw.writeCoordinates(g, xmax + shiftXcoordinate, ymin - shiftYcoordinate,
					xmax, transy(ymin), dotCoordinatecolor);

			g.setColor(displayColor);
			/*
			g.drawString(Messages.clipRectangle, Messages.getStringxClipRectangleDisplay(),
					Messages.getStringyClipRectangleDisplay());
			draw.writeGridCoordinates(g, height, Messages.getStringxClipRectangleDisplay(),
					Messages.getStringyClipRectangleDisplay() + 10,
					xmin, xmax, ymin, ymax, true, displayColor);
			 */

			draw.drawGrid(g,height, xmin,xmax,ymin,ymax, verticalCoordinateSize,
					horizontalCoordinateSize,gridColor);
			// Draw Original line helps in seeing what portion has been clipped
			//glLineWidth(1);
			draw.makeLine(g,RO.RenderObject[0][1],RO.RenderObject[0][2],
					RO.RenderObject[0][3],RO.RenderObject[0][4],clippedLineColor,false);
			draw.writeCoordinates(g,RO.RenderObject[0][1] + shiftXcoordinate,
					RO.RenderObject[0][2] - shiftYcoordinate, RO.RenderObject[0][1],
					transy(RO.RenderObject[0][2])  , clippedLineColor);
			draw.writeCoordinates(g,RO.RenderObject[0][3] + shiftXcoordinate,
					RO.RenderObject[0][4] - shiftYcoordinate, RO.RenderObject[0][3],
					transy(RO.RenderObject[0][4]), clippedLineColor);

			draw.writeString(g,RO.RenderObject[0][1] -1,RO.RenderObject[0][2] + 10,
					RO.RenderObject[0][5],clippedLineColor);//This writes the code of the point
			draw.writeString(g,RO.RenderObject[0][3] -1,RO.RenderObject[0][4] + 10,
					RO.RenderObject[0][6],clippedLineColor);//This writes the code of the point

			//glLineWidth(3);
			animate(g);
		}

	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand() == BackName)
		{
			P = new Polygon();
			LC = new LineClipping();
			//Make a object for Drawing Shapes
			draw = new DrawToolkit();

			grid_start_x_text.setText(rect1.getX()+"");
			grid_start_y_text.setText(rect1.getY()+"");
			grid_end_x_text.setText(rect3.getX()+"");
			grid_end_y_text.setText(rect3.getY()+"");

			start_x1_text.setText(p1.getX()+"");
			start_y1_text.setText(p1.getY()+"");
			end_x2_text.setText(p2.getX()+"");
			end_y2_text.setText(p2.getY()+"");

			next.setVisible(false);
			previous.setVisible(false);
			default_params.setVisible(true);
			clip_window.setVisible(true);
			start_end.setVisible(false);
			back_button.setVisible(false);

			grid_start_x_text.setVisible(true);
			grid_start_y_text.setVisible(true);
			grid_end_x_text.setVisible(true);
			grid_end_y_text.setVisible(true);
			start_x1_text.setVisible(false);
			start_y1_text.setVisible(false);
			end_x2_text.setVisible(false);
			end_y2_text.setVisible(false);

			pre_draw_state = 0;
			pointer = 0;
			gotoNextStep = 1;

			repaint();
			return;
		}
		if(pre_draw_state == 0)
		{
			if(e.getActionCommand() == DefaultParamsName)
			{
				rect1 = new Point(100, 100);
				rect2 = new Point(300, 100);
				rect3 = new Point(300, 300);
				rect4 = new Point(100, 300);
				p1 = new Point(10, 450);
				p2 = new Point(500, 40);
				P = new Polygon();
				P.addVertex(rect1);
				P.addVertex(rect2);
				P.addVertex(rect3);
				P.addVertex(rect4);

				//Clip the line
				L = new Line(p1,p2);
				RO = LC.Clip(L, P);

				int boundary[];
				boundary = new int[4];
				//Get end points of the Polygon
				P.getRectangleBoundary(boundary);
				xmin = transx(boundary[0]);
				xmax = transx(boundary[1]);
				ymin = transy(boundary[2]);
				ymax = transy(boundary[3]);

				//Display the object got after clipping the line
				for(int i = 0; i < RO.count; i++)
				{

					RO.RenderObject[i][1] = RO.RenderObject[i][1];
					RO.RenderObject[i][2] = transy(RO.RenderObject[i][2]);
					RO.RenderObject[i][3] = transx(RO.RenderObject[i][3]);
					RO.RenderObject[i][4] = transy(RO.RenderObject[i][4]);

					/*
					for(int j = 0; j < 7; j++)
						System.out.print(RO.RenderObject[i][j]+" ");

					System.out.println("");
					 */
				}

				next.setVisible(true);
				previous.setVisible(true);
				default_params.setVisible(false);
				clip_window.setVisible(false);
				start_end.setVisible(false);
				back_button.setVisible(true);

				grid_start_x_text.setVisible(false);
				grid_start_y_text.setVisible(false);
				grid_end_x_text.setVisible(false);
				grid_end_y_text.setVisible(false);
				start_x1_text.setVisible(false);
				start_y1_text.setVisible(false);
				end_x2_text.setVisible(false);
				end_y2_text.setVisible(false);

				pre_draw_state = -1;
				pointer = 0;
				gotoNextStep = 1;

				repaint();
			}
			else if(e.getActionCommand() == ClipWindowName)
			{
				try
				{
					int x1 = Integer.parseInt(grid_start_x_text.getText());
					int y1 = Integer.parseInt(grid_start_y_text.getText());
					int x2 = Integer.parseInt(grid_end_x_text.getText());
					int y2 = Integer.parseInt(grid_end_y_text.getText());

					if((x1 < 0) || (x1 > 300) || (y1 < 0) || (y1 > 300)
							|| (x2 < 100) || (x2 > 500) || (y2 < 100) || (y2 > 500)
							|| (x2 < x1) || (y2 < y1))
					{
						grid_start_x_text.setText(xmin+"");
						grid_start_y_text.setText(ymin+"");
						grid_end_x_text.setText(xmax+"");
						grid_end_y_text.setText(ymax+"");
					}
					else
					{
						rect1 = new Point(x1, y1);
						rect2 = new Point(x2, y1);
						rect3 = new Point(x2, y2);
						rect4 = new Point(x1, y2);
						P = new Polygon();
						P.addVertex(rect1);
						P.addVertex(rect2);
						P.addVertex(rect3);
						P.addVertex(rect4);

						int boundary[];
						boundary = new int[4];
						//Get end points of the Polygon
						P.getRectangleBoundary(boundary);
						xmin = transx(boundary[0]);
						xmax = transx(boundary[1]);
						ymin = transy(boundary[2]);
						ymax = transy(boundary[3]);

						next.setVisible(false);
						previous.setVisible(false);
						default_params.setVisible(false);
						clip_window.setVisible(false);
						start_end.setVisible(true);
						back_button.setVisible(true);

						grid_start_x_text.setVisible(false);
						grid_start_y_text.setVisible(false);
						grid_end_x_text.setVisible(false);
						grid_end_y_text.setVisible(false);
						start_x1_text.setVisible(true);
						start_y1_text.setVisible(true);
						end_x2_text.setVisible(true);
						end_y2_text.setVisible(true);

						pre_draw_state = 1;
					}					
				}
				catch (NumberFormatException error)
				{
					// TODO: handle exception
					grid_start_x_text.setText(xmin+"");
					grid_start_y_text.setText(ymin+"");
					grid_end_x_text.setText(xmax+"");
					grid_end_y_text.setText(ymax+"");
				}
				repaint();		
			}
		}
		else if(pre_draw_state == 1)
		{
			if(e.getActionCommand() == StartEndName)
			{
				try
				{
					int x1 = Integer.parseInt(start_x1_text.getText());
					int y1 = Integer.parseInt(start_y1_text.getText());
					int x2 = Integer.parseInt(end_x2_text.getText());
					int y2 = Integer.parseInt(end_y2_text.getText());

					if((x1 < 0) || (x1 > 500) || (y1 < 0) || (y1 > 500)
							|| (x2 < 0) || (x2 > 500) || (y2 < 0) || (y2 > 500))
					{
						start_x1_text.setText(p1.getX()+"");
						start_y1_text.setText(p1.getY()+"");
						end_x2_text.setText(p2.getX()+"");
						end_y2_text.setText(p2.getY()+"");
					}
					else
					{

						p1 = new Point(x1, y1);
						p2 = new Point(x2, y2);

						L = new Line(p1,p2);
						RO = LC.Clip(L, P);

						//Display the object got after clipping the line
						for(int i = 0; i < RO.count; i++)
						{
							RO.RenderObject[i][1] = RO.RenderObject[i][1];
							RO.RenderObject[i][2] = transy(RO.RenderObject[i][2]);
							RO.RenderObject[i][3] = transx(RO.RenderObject[i][3]);
							RO.RenderObject[i][4] = transy(RO.RenderObject[i][4]);
						}

						next.setVisible(true);
						previous.setVisible(true);
						default_params.setVisible(false);
						clip_window.setVisible(false);
						start_end.setVisible(false);
						back_button.setVisible(true);

						grid_start_x_text.setVisible(false);
						grid_start_y_text.setVisible(false);
						grid_end_x_text.setVisible(false);
						grid_end_y_text.setVisible(false);
						start_x1_text.setVisible(false);
						start_y1_text.setVisible(false);
						end_x2_text.setVisible(false);
						end_y2_text.setVisible(false);

						pre_draw_state = -1;
						pointer = 0;
						gotoNextStep = 1;
					}
				}
				catch (NumberFormatException error)
				{
					start_x1_text.setText(p1.getX()+"");
					start_y1_text.setText(p1.getY()+"");
					end_x2_text.setText(p2.getX()+"");
					end_y2_text.setText(p2.getY()+"");
				}
				repaint();
			}
		}
		else
		{
			if(pointer>=0)
			{
				if(e.getActionCommand() == NextButtonName)
				{
					gotoNextStep=1;
				}
				else if(e.getActionCommand() == PreviousButtonName)
				{

					if((pointer - 2) >= 0)
						pointer -= 2;
					gotoNextStep = 1;
				}
				//System.out.println(pointer);
			}
		}
		repaint();
	}
}
