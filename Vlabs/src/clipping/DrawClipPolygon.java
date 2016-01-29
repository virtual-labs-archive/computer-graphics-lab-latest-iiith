package clipping;
import java.applet.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;


import library.DrawToolkit;
import library.Point;
import library.Polygon;

public class DrawClipPolygon extends Applet  implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int ObjectLimit_x = 500;
	private static int ObjectLimit_y = 10;
	private static int pointRadii = 4;
	private static int pointer = 0;
	int RenderObject[][] = new int[ObjectLimit_x][ObjectLimit_y];
	private static int state = 0;
	private static int [][]curpoly = new int [ObjectLimit_x][2];
	private static int [][]newline = new int[ObjectLimit_x][2];
	private static int newlinecount = 0;
	private static int polyvertices = 0;
	private static int gotoNextStep = 0;


	int width, height;
	private static DrawToolkit draw;
	private static int xmin,xmax,ymin,ymax;

	private static final int horizontalCoordinateSize = 600;
	private static final int verticalCoordinateSize = 600;

	private static final Color messageDisplayColor = Color.white;
	private static final Color clipWindowSelectColor = Color.yellow;
	private static final Color polygonColor = Color.BLUE;
	private static final Color polygonColorClipLine = Color.yellow;
	private static final Color coordinateColor = Color.red;
	private static final Color acceptPolygon = Color.green;
	private static final Color newClippedPolygon = Color.orange.darker();
	private static final Color clipWindow = Color.white;

	private int pre_draw_state;

	private Button next_button, previous_button, default_params, clip_window, start_end, back_button;

	private static final String NextButtonName = "Next Iteration";
	private static final String PreviousButtonName = "Previous Iteration";
	private static final String DefaultParamsName = "Start Experiment with Default Values";
	private static final String ClippedWindowName = "Enter";
	private static final String StartEndName = "Start Experiment";
	private static final String BackName = "Back";

	private static final int next_button_width = 100;
	private static final int previous_button_width = 150;
	private static final int default_button_width = 250;
	private static final int frame_start_button_width = 250;
	private static final int back_button_width = 100;
	private static final int button_height = 20;

	private static TextField clip_start_x1_text, clip_start_y1_text;
	private static TextField clip_end_x2_text, clip_end_y2_text ;
	private static TextField vertices_text;
	private static final int TEXT_COLUMNS = 350;

	private static final int WINDOW_GAP_1 = 100;
	private static final int WINDOW_GAP_2 = 20;

	private Polygon P, Cp;
	private Point[] PT, PT1;
	private PolygonClipping PC;
	private int numPoly;
	
	private int[] boundary;

	private int transx(int a)
	{
		return a;	
	}
	private int transy(int a)
	{
		return height - a;	
	}

	public void init()
	{
		for (int i=0;i<ObjectLimit_x;i++)
		{
			curpoly[i][0]=curpoly[i][1]=0;
			newline[i][0]=newline[i][1]=0;
		}

		setSize(horizontalCoordinateSize, verticalCoordinateSize);
		width = getSize().width;
		height = getSize().height;
		String str = "", temp = "";
		P = new Polygon();
		Cp = new Polygon();
		String[] str1 = new String[15];
		BufferedReader br = null;

		PT = new Point[4];
		PT1 = new Point[6];

		for (int i = 0; i < 4; i++)
			PT[i] = new Point();
		for (int i = 0;i < 6; i++)
			PT1[i] = new Point();

		PC = new PolygonClipping();
		try
		{
			URL url = new URL(this.getCodeBase(), this.getParameter("file_name"));
			br = new BufferedReader(new InputStreamReader(url.openStream()));

			while ( (temp = br.readLine() ) !=null)
			{
				str=str+" "+temp;
			}
			str1=str.split(" ");

			int x,y;

			for(int i=0;i<8;i+=2)
			{
				x=Integer.parseInt(str1[i+1]);
				y=Integer.parseInt(str1[i+2]);
				
				PT[Math.round(i/2)].setPoint(x,y);
				P.addVertex(PT[(i/2)]);
			}
			
			boundary=new int[4];
			
			//Get end points of the Polygon
			P.getRectangleBoundary(boundary);
			xmin = transx(boundary[0]);
			xmax = transx(boundary[1]);
			ymin = transy(boundary[2]);
			ymax = transy(boundary[3]);
			
			numPoly=Integer.parseInt(str1[9]);

			for(int i=9;i<(2*numPoly)+9;i+=2)
			{
				x=Integer.parseInt(str1[i+1]);
				y=Integer.parseInt(str1[i+2]);

				(PT1[(i-9)/2]).setPoint(x,y);
				Cp.addVertex(PT1[(i-9)/2]);
			}

		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		RenderObject=PC.Clip(Cp, P);
		
		for (int i=0;RenderObject[i][0]!=-2;i++)
		{
			if(RenderObject[i][0]==0)
			{			
				RenderObject[i][2]=transy(RenderObject[i][2]);
			}
			else
			{			
				RenderObject[i][3]=transy(RenderObject[i][3]);
				RenderObject[i][5]=transy(RenderObject[i][5]);
			}
		}
		
		boundary=new int[4];
		//Get end points of the Polygon
		P.getRectangleBoundary(boundary);
		xmin = transx(boundary[0]);
		xmax = transx(boundary[1]);
		ymin = transy(boundary[2]);
		ymax = transy(boundary[3]);
		
		Messages.setStringxSelectLinedisplay(horizontalCoordinateSize-250);
		Messages.setStringySelectLinedisplay(100);
		Messages.setStringxClipRectangleDisplay(horizontalCoordinateSize-250);
		Messages.setStringyClipRectangleDisplay(40);
		Messages.setStringxCodeDisplay(horizontalCoordinateSize-250);
		Messages.setStringyCodeDisplay(110);

		draw = new DrawToolkit();

		// Make the default background color black.
		setBackground( Color.black );
		
		setLayout(null);

		next_button = new Button(NextButtonName);
		previous_button = new Button(PreviousButtonName);
		default_params = new Button(DefaultParamsName);
		clip_window = new Button(ClippedWindowName);
		start_end = new Button(StartEndName);
		back_button = new Button(BackName);

		clip_start_x1_text = new TextField(TEXT_COLUMNS);
		clip_start_y1_text = new TextField(TEXT_COLUMNS);
		clip_end_x2_text = new TextField(TEXT_COLUMNS);
		clip_end_y2_text = new TextField(TEXT_COLUMNS);
		vertices_text = new TextField(TEXT_COLUMNS);

		add(next_button);
		add(previous_button);
		add(default_params);
		add(clip_window);
		add(start_end);
		add(back_button);

		add(clip_start_x1_text);
		add(clip_start_y1_text);
		add(clip_end_x2_text);
		add(clip_end_y2_text);
		add(vertices_text);

		next_button.setBackground(Color.WHITE);
		previous_button.setBackground(Color.WHITE);
		default_params.setBackground(Color.WHITE);
		clip_window.setBackground(Color.WHITE);
		start_end.setBackground(Color.WHITE);
		back_button.setBackground(Color.WHITE);
		clip_start_x1_text.setBackground(Color.WHITE);
		clip_start_y1_text.setBackground(Color.WHITE);
		clip_end_x2_text.setBackground(Color.WHITE);
		clip_end_y2_text.setBackground(Color.WHITE);
		vertices_text.setBackground(Color.WHITE);

		next_button.addActionListener(this);
		previous_button.addActionListener(this);
		default_params.addActionListener(this);
		clip_window.addActionListener(this);
		start_end.addActionListener(this);
		back_button.addActionListener(this);

		clip_start_x1_text.addActionListener(this);
		clip_start_y1_text.addActionListener(this);
		clip_end_x2_text.addActionListener(this);
		clip_end_y2_text.addActionListener(this);
		vertices_text.addActionListener(this);

		next_button.setVisible(false);
		previous_button.setVisible(false);
		default_params.setVisible(true);
		clip_window.setVisible(true);
		start_end.setVisible(false);
		back_button.setVisible(false);

		clip_start_x1_text.setVisible(true);
		clip_start_y1_text.setVisible(true);
		clip_end_x2_text.setVisible(true);
		clip_end_y2_text.setVisible(true);
		vertices_text.setVisible(false);

		next_button.setBounds(horizontalCoordinateSize + WINDOW_GAP_1 + WINDOW_GAP_2 + previous_button_width,
				2*WINDOW_GAP_1, next_button_width, button_height);
		previous_button.setBounds(horizontalCoordinateSize + WINDOW_GAP_1, 2*WINDOW_GAP_1,
				previous_button_width, button_height);
		default_params.setBounds(horizontalCoordinateSize + WINDOW_GAP_1, 2*WINDOW_GAP_1 + 2*WINDOW_GAP_2,
				default_button_width, button_height);
		clip_window.setBounds(horizontalCoordinateSize + WINDOW_GAP_1,
				2*WINDOW_GAP_1, frame_start_button_width, button_height);
		start_end.setBounds(horizontalCoordinateSize + WINDOW_GAP_1, 2*WINDOW_GAP_1,
				frame_start_button_width, button_height);
		back_button.setBounds(horizontalCoordinateSize + WINDOW_GAP_1 + (int)(frame_start_button_width - back_button_width)/2 ,
				2*WINDOW_GAP_1 + (int)(1.5*WINDOW_GAP_2), back_button_width, button_height);

		clip_start_x1_text.setBounds(horizontalCoordinateSize + WINDOW_GAP_1, WINDOW_GAP_1 + WINDOW_GAP_2,
				next_button_width, button_height);
		clip_start_y1_text.setBounds(horizontalCoordinateSize + WINDOW_GAP_1 + next_button_width + WINDOW_GAP_2,
				WINDOW_GAP_1 + WINDOW_GAP_2, next_button_width, button_height);
		clip_end_x2_text.setBounds(horizontalCoordinateSize + WINDOW_GAP_1, WINDOW_GAP_1 + 3*WINDOW_GAP_2,
				next_button_width, button_height);
		clip_end_y2_text.setBounds(horizontalCoordinateSize + WINDOW_GAP_1 + next_button_width + WINDOW_GAP_2,
				WINDOW_GAP_1 + 3*WINDOW_GAP_2, next_button_width, button_height);
		vertices_text.setBounds(horizontalCoordinateSize + WINDOW_GAP_1, WINDOW_GAP_1 + WINDOW_GAP_2,
				TEXT_COLUMNS, button_height);

		clip_start_x1_text.setText(boundary[0]+"");
		clip_start_y1_text.setText(boundary[2]+"");
		clip_end_x2_text.setText(boundary[1]+"");
		clip_end_y2_text.setText(boundary[3]+"");

		str = "";
		for(int i = 0; i < numPoly; i++)
			str += PT1[i].getX() + " " + PT1[i].getY() + ",";
		vertices_text.setText(str);

		pre_draw_state = 0;
		pointer = 0;
		state = 0;

	}
	
	private void RenderClipLine(Graphics g,int choice)
	{
		if (1==choice )
		{
			draw.makeLine(g,xmin, ymin, xmin, ymax,clipWindowSelectColor, false);
			draw.drawString(g,Messages.leftSide, Messages.getStringxCodeDisplay(),
									Messages.getStringyCodeDisplay(),messageDisplayColor);
			draw.writeCoordinates(g,xmin, ymin,xmin, transy(ymin),coordinateColor);
			draw.writeCoordinates(g,xmin, ymax,xmax, transy(ymax),coordinateColor);
		}
		if (2==choice )
		{
			draw.makeLine(g,xmax, ymin, xmax, ymax,clipWindowSelectColor, false);
			draw.drawString(g,Messages.rightSide, Messages.getStringxCodeDisplay(),
									Messages.getStringyCodeDisplay(),messageDisplayColor);
			draw.writeCoordinates(g,xmax, ymin,xmax, transy(ymin),coordinateColor);
			draw.writeCoordinates(g,xmax, ymax,xmax, transy(ymax),coordinateColor);
		}
		if (3==choice )
		{
			draw.makeLine(g,xmin, ymin, xmax, ymin,clipWindowSelectColor, false);
			draw.drawString(g,Messages.bottomSide, Messages.getStringxCodeDisplay(),
									Messages.getStringyCodeDisplay(),messageDisplayColor);
			draw.writeCoordinates(g,xmin, ymin,xmin, transy(ymin),coordinateColor);
			draw.writeCoordinates(g,xmax, ymin,xmax, transy(ymin),coordinateColor);

		}
		if (4==choice )
		{
			draw.makeLine(g,xmax, ymax, xmin, ymax,clipWindowSelectColor, false);
			draw.drawString(g, Messages.topSide, Messages.getStringxCodeDisplay(),
									Messages.getStringyCodeDisplay(), messageDisplayColor );
			draw.writeCoordinates(g,xmax, ymax,xmin, transy(ymax),coordinateColor);
			draw.writeCoordinates(g,xmax, ymax,xmin, transy(ymax),coordinateColor);

		}
	}
	private void makenewLines(Color c1,Color c2,Graphics g)
	{
		int i;
		for(i=0;i<newlinecount;i++)
		{
			draw.makeFilledRoundPoint(g,newline[i][0],newline[i][1],pointRadii,c1);
			draw.makeFilledRoundPoint(g,newline[i+1][0],newline[i+1][1],pointRadii,c1);
			draw.makeLine(g,newline[i][0],newline[i][1],newline[i+1][0],newline[i+1][1],c1,false);
			draw.writeCoordinates(g,newline[i][0],newline[i][1],newline[i][0],transy(newline[i][1]),c2);
			draw.writeCoordinates(g,newline[i+1][0],newline[i+1][1],newline[i+1][0],transy(newline[i+1][1]), c2);

			i++;
		}

	}

	void polyanimate(Graphics g)
	{
		if(state==0)
		{
			int i=0;

			if(RenderObject[pointer][0]==0)
			{
				polyvertices=0;
				newlinecount=0;

				for(i=pointer;RenderObject[i][0]==0;i++)
				{
					polyvertices++;
					curpoly[i-pointer][0]=RenderObject[i][1];
					curpoly[i-pointer][1]=RenderObject[i][2];
				}

				draw.makeCPolygon(g,curpoly,height,polyvertices,pointRadii,polygonColor,coordinateColor);

				pointer=i;

			}
			else if((RenderObject[pointer][1]==2) || (RenderObject[pointer][1]==1))
			{
				draw.makeCPolygon(g,curpoly,height,polyvertices,pointRadii,polygonColor,coordinateColor);
				RenderClipLine(g,RenderObject[pointer][0]); //Selects the clipping line of the window
				makenewLines(newClippedPolygon, coordinateColor ,g); //Draws the new clipped polygon

				//Selects the clip line of the polygon
				draw.makeLine(g,RenderObject[pointer][2], RenderObject[pointer][3],
						RenderObject[pointer][4],RenderObject[pointer][5],polygonColorClipLine,false);

				draw.writeCoordinates(g,RenderObject[pointer][2],RenderObject[pointer][3],
						RenderObject[pointer][2],transy(RenderObject[pointer][3]),coordinateColor);
				draw.writeCoordinates(g,RenderObject[pointer][4],RenderObject[pointer][5],
						RenderObject[pointer][4],transy(RenderObject[pointer][5]),coordinateColor);
				draw.drawString(g,Messages.clipLine + 
						draw.makeCoordinate(RenderObject[pointer][2],transy(RenderObject[pointer][3]))
						+ " and " + 
						draw.makeCoordinate(RenderObject[pointer][4],transy(RenderObject[pointer][5]))
						, Messages.getStringxSelectLinedisplay(),
						Messages.getStringySelectLinedisplay(),messageDisplayColor);

				if(RenderObject[pointer][1]==2)
				{
					// Selects the clip line of the window
					draw.makeLine(g,RenderObject[pointer][2], RenderObject[pointer][3],
							RenderObject[pointer][4],RenderObject[pointer][5],polygonColorClipLine,false);
					draw.writeCoordinates(g,RenderObject[pointer][2],RenderObject[pointer][3],
							RenderObject[pointer][2],transy(RenderObject[pointer][3]), coordinateColor);
					draw.writeCoordinates(g,RenderObject[pointer][4],RenderObject[pointer][5],
							RenderObject[pointer][4],transy(RenderObject[pointer][5]), coordinateColor);

					newline[newlinecount][0]=RenderObject[pointer][2];
					newline[newlinecount][1]=RenderObject[pointer][3];
					newline[newlinecount+1][0]=RenderObject[pointer][4];
					newline[newlinecount+1][1]=RenderObject[pointer][5];
				}
			}

			if((gotoNextStep==1 ))
			{
				pointer++;
				if(RenderObject[pointer-1][1]==2)
					newlinecount+=2;
				if(RenderObject[pointer-1][0]==-2)
				{
					draw.makeCPolygon(g,curpoly,height,polyvertices,pointRadii,acceptPolygon,coordinateColor);
					pointer=0;
					newlinecount=0;
					state=1;
				}
				gotoNextStep=0;
			}
		}
		else
		{
			draw.makeCPolygon(g,curpoly,height,polyvertices,pointRadii,acceptPolygon,coordinateColor);
		}

	}

	private void draw_grid(Graphics g)
	{
		draw.makeNRectangle(g,xmin,xmax,ymin,ymax,Color.white, clipWindow,false,false);
		draw.writeGridCoordinates(g, height, Messages.getStringxClipRectangleDisplay(),
				Messages.getStringyClipRectangleDisplay() + 10, xmin, xmax, ymin, ymax, true, messageDisplayColor);
		draw.drawString(g, Messages.clipRectangle, Messages.getStringxClipRectangleDisplay(),
				Messages.getStringyClipRectangleDisplay(), messageDisplayColor);

		g.setFont(new Font("Helectiva", Font.PLAIN, 14));
		g.setColor(Color.WHITE);
		g.drawString("Enter the lower left coordinates (x,y) of the grid", 
				horizontalCoordinateSize + WINDOW_GAP_1, WINDOW_GAP_1 + (int)(2*WINDOW_GAP_2/3));
		g.drawString("Enter the upper right coordinates (x,y) of the grid", 
				horizontalCoordinateSize + WINDOW_GAP_1, WINDOW_GAP_1 + 2*WINDOW_GAP_2 + (int)(2*WINDOW_GAP_2/3));
	}

	private void draw_grid_polygon_vertices(Graphics g)
	{
		draw.makeNRectangle(g,xmin,xmax,ymin,ymax,Color.white, clipWindow,false,false);
		draw.writeGridCoordinates(g, height, Messages.getStringxClipRectangleDisplay(),
				Messages.getStringyClipRectangleDisplay() + 10, xmin, xmax, ymin, ymax, true, messageDisplayColor);
		draw.drawString(g, Messages.clipRectangle, Messages.getStringxClipRectangleDisplay(),
				Messages.getStringyClipRectangleDisplay(), messageDisplayColor);

		draw.makeCPolygon(g,curpoly,height,polyvertices,pointRadii,polygonColor,coordinateColor);

		g.setFont(new Font("Helectiva", Font.PLAIN, 14));
		g.setColor(Color.WHITE);
		g.drawString("Enter coordinates x1 y1,x2 y2,x3 y3,... of the vertices (max 10 vertices)", 
				horizontalCoordinateSize + WINDOW_GAP_1, WINDOW_GAP_1);
	}

	public void paint (Graphics g)
	{

		if(pre_draw_state == 0)
		{
			draw_grid(g);
		}
		else if(pre_draw_state == 1)
		{
			draw_grid_polygon_vertices(g);
		}
		else
		{
			//draw.drawCoordinate(g,width,height,Color.white);
			draw.makeNRectangle(g,xmin,xmax,ymin,ymax,Color.white, clipWindow,false,false);
			draw.writeGridCoordinates(g, height, Messages.getStringxClipRectangleDisplay(),
					Messages.getStringyClipRectangleDisplay() + 10, xmin, xmax, ymin, ymax, true, messageDisplayColor);
			draw.drawString(g, Messages.clipRectangle, Messages.getStringxClipRectangleDisplay(),
					Messages.getStringyClipRectangleDisplay(), messageDisplayColor);

			polyanimate(g);
		}
	}



	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand() == BackName)
		{
			PC = new PolygonClipping();
			RenderObject=PC.Clip(Cp, P);
			for (int i=0;RenderObject[i][0]!=-2;i++)
			{
				if(RenderObject[i][0]==0)
				{			
					RenderObject[i][2]=transy(RenderObject[i][2]);
				}
				else
				{			
					RenderObject[i][3]=transy(RenderObject[i][3]);
					RenderObject[i][5]=transy(RenderObject[i][5]);
				}
			}
			draw = new DrawToolkit();

			next_button.setVisible(false);
			previous_button.setVisible(false);
			default_params.setVisible(true);
			clip_window.setVisible(true);
			start_end.setVisible(false);
			back_button.setVisible(false);

			clip_start_x1_text.setVisible(true);
			clip_start_y1_text.setVisible(true);
			clip_end_x2_text.setVisible(true);
			clip_end_y2_text.setVisible(true);
			vertices_text.setVisible(false);
			
			pre_draw_state = 0;
			pointer = 0;
			state = 0;
		
			repaint();
			return;
		}
		if(pre_draw_state == 0)
		{
			if(e.getActionCommand() == DefaultParamsName)
			{
				String str = "", temp = "";
				P = new Polygon();
				Cp = new Polygon();
				String[] str1 = new String[15];
				BufferedReader br = null;

				PT = new Point[4];
				PT1 = new Point[6];

				for (int i = 0; i < 4; i++)
					PT[i] = new Point();
				for (int i = 0;i < 6; i++)
					PT1[i] = new Point();

				PC = new PolygonClipping();
				try
				{
					URL url = new URL(this.getCodeBase(), this.getParameter("file_name"));
					br = new BufferedReader(new InputStreamReader(url.openStream()));

					while ( (temp = br.readLine() ) !=null)
						str=str+" "+temp;
					str1=str.split(" ");

					int x,y;

					for(int i=0;i<8;i+=2)
					{
						x=Integer.parseInt(str1[i+1]);
						y=Integer.parseInt(str1[i+2]);
						PT[Math.round(i/2)].setPoint(x,y);
						P.addVertex(PT[(i/2)]);
					}
					numPoly=Integer.parseInt(str1[9]);

					for(int i=9;i<(2*numPoly)+9;i+=2)
					{

						x=Integer.parseInt(str1[i+1]);
						y=Integer.parseInt(str1[i+2]);

						(PT1[(i-9)/2]).setPoint(x,y);
						Cp.addVertex(PT1[(i-9)/2]);
					}

				}
				catch (FileNotFoundException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (IOException e2)
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				RenderObject=PC.Clip(Cp, P);
				for (int i=0;RenderObject[i][0]!=-2;i++)
				{
					if(RenderObject[i][0]==0)
					{			
						RenderObject[i][2]=transy(RenderObject[i][2]);
					}
					else
					{			
						RenderObject[i][3]=transy(RenderObject[i][3]);
						RenderObject[i][5]=transy(RenderObject[i][5]);
					}
				}
				
				boundary=new int[4];
				//Get end points of the Polygon
				P.getRectangleBoundary(boundary);
				xmin=transx(boundary[0]);
				xmax=transx(boundary[1]);
				ymin=transy(boundary[2]);
				ymax=transy(boundary[3]);

				next_button.setVisible(true);
				previous_button.setVisible(true);
				default_params.setVisible(false);
				clip_window.setVisible(false);
				start_end.setVisible(false);
				back_button.setVisible(true);

				clip_start_x1_text.setVisible(false);
				clip_start_y1_text.setVisible(false);
				clip_end_x2_text.setVisible(false);
				clip_end_y2_text.setVisible(false);
				vertices_text.setVisible(false);
				
				pre_draw_state = -1;
				pointer = 0;
				state = 0;
			}
			if(e.getActionCommand() == ClippedWindowName)
			{
				try
				{
					int x1 = Integer.parseInt(clip_start_x1_text.getText());
					int y1 = Integer.parseInt(clip_start_y1_text.getText());
					int x2 = Integer.parseInt(clip_end_x2_text.getText());
					int y2 = Integer.parseInt(clip_end_y2_text.getText());
					
					if((x1 < 0) || (x1 > 300) || (y1 < 0) || (y1 > 300)
							|| (x2 < 100) || (x2 > 500) || (y2 < 100) || (y2 > 500)
							|| (x2 < x1) || (y2 < y1))
					{
						clip_start_x1_text.setText(boundary[0]+"");
						clip_start_y1_text.setText(boundary[2]+"");
						clip_end_x2_text.setText(boundary[1]+"");
						clip_end_y2_text.setText(boundary[3]+"");
					}
					else
					{
						P = new Polygon();
						PT = new Point[4];

						for (int i = 0; i < 4; i++)
							PT[i] = new Point();
						
						PT[0].setPoint(x1,y1);
						PT[1].setPoint(x2,y1);
						PT[2].setPoint(x2,y2);
						PT[3].setPoint(x1,y2);
						
						P.addVertex(PT[0]);
						P.addVertex(PT[1]);
						P.addVertex(PT[2]);
						P.addVertex(PT[3]);

						boundary=new int[4];
						//Get end points of the Polygon
						P.getRectangleBoundary(boundary);
						xmin=transx(boundary[0]);
						xmax=transx(boundary[1]);
						ymin=transy(boundary[2]);
						ymax=transy(boundary[3]);
						
						next_button.setVisible(false);
						previous_button.setVisible(false);
						default_params.setVisible(false);
						clip_window.setVisible(false);
						start_end.setVisible(true);
						back_button.setVisible(true);

						clip_start_x1_text.setVisible(false);
						clip_start_y1_text.setVisible(false);
						clip_end_x2_text.setVisible(false);
						clip_end_y2_text.setVisible(false);
						vertices_text.setVisible(true);
						
						pre_draw_state = 1;
					}
				}
				catch (NumberFormatException error)
				{
					clip_start_x1_text.setText(boundary[0]+"");
					clip_start_y1_text.setText(boundary[2]+"");
					clip_end_x2_text.setText(boundary[1]+"");
					clip_end_y2_text.setText(boundary[3]+"");					
				}
			}
			repaint();
		}
		else if(pre_draw_state == 1)
		{
			if(e.getActionCommand() == StartEndName)
			{
				String[] str = new String[10];
				str = vertices_text.getText().split(",");
				
				numPoly = 0;
				String[] str2 = new String[5];
				int[] coord = new int[2];
				int str2_count, tmp_count;
				boolean correct = true;
				
				Cp = new Polygon();
				PT1 = new Point[str.length + 1];
				
				for (int i = 0;i < 6; i++)
					PT1[i] = new Point();
				
				while(numPoly < str.length)
				{
					str2 = str[numPoly].split("[ \t\f\n\r]");
					str2_count = tmp_count = 0;
					
					while(str2_count < str2.length)
					{
						try
						{
							coord[str2_count] = Integer.parseInt(str2[tmp_count++]);
						}
						catch (NumberFormatException error)
						{
							correct = false;
							break;
						}
						str2_count++;
					}
					if(str2_count == 2 && correct == true)
					{
						if(coord[0] < 0 || coord[0] > 500 | coord[1] < 0 || coord[1] > 500)
						{
							correct = false;
							break;
						}
						else
						{
							PT1[numPoly].setPoint(coord[0], coord[1]);
							Cp.addVertex(PT1[numPoly]);
						}
					}
					else
					{
						correct = false;
						break;
					}
					numPoly++;
				}
				if(correct)
				{
					RenderObject=PC.Clip(Cp, P);
					for (int i=0;RenderObject[i][0]!=-2;i++)
					{
						if(RenderObject[i][0]==0)
						{			
							RenderObject[i][2]=transy(RenderObject[i][2]);
						}
						else
						{			
							RenderObject[i][3]=transy(RenderObject[i][3]);
							RenderObject[i][5]=transy(RenderObject[i][5]);
						}
					}

					next_button.setVisible(true);
					previous_button.setVisible(true);
					default_params.setVisible(false);
					clip_window.setVisible(false);
					start_end.setVisible(false);
					back_button.setVisible(true);

					clip_start_x1_text.setVisible(false);
					clip_start_y1_text.setVisible(false);
					clip_end_x2_text.setVisible(false);
					clip_end_y2_text.setVisible(false);
					vertices_text.setVisible(false);
					
					pre_draw_state = -1;
					pointer = 0;
					state = 0;
				}
			}

			repaint();
		}
		else
		{
			if(e.getActionCommand() == NextButtonName)
			{
				gotoNextStep=1;
			}
			if(e.getActionCommand() == PreviousButtonName)
			{
				pointer=0;
				state=0;
			}
			repaint();
		}
	}

}