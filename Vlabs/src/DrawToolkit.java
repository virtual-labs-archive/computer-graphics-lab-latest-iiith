

import java.awt.*;

// This is a graphics helper library which has a lot of functions inbuilt.
// These can be used very easily by passing appropriate argument.
public class DrawToolkit
{
	// Display Text
	public void displayText(Graphics g, int m1, int m2, Color colour)
	{
		g.setColor(colour);
		g.setFont (new Font ("Helvetica", Font.PLAIN, 10));
		String str = new String(m1 + ", " + m2);
		g.drawString(str, m1 + 1, m2 + 1);
	}
	
	private String byte_to_binary(int x)
	{
		String b = new String();
		int z;
		
		b = "( ";
		for (z = 8; z > 0; z >>= 1)
		{
			b = b + (((x & z) == z) ? "1" : "0");
		}

		b = b + " )";
		return b;
	}
	
	public void writeString(Graphics g, int m1, int m2, int code, Color colour)
	{
		g.setColor(colour);
		g.setFont (new Font ("Helvetica", Font.PLAIN, 10));

		String str = new String(byte_to_binary(code));
		g.drawString(str, m1, m2);
	}
	
	public void writeCoordinates(Graphics g, int m1, int m2, int x, int y, Color colour)
	{
		g.setColor(colour);
		g.setFont (new Font ("Helvetica", Font.PLAIN, 10));

		String str = new String("(" + x + "," + y + ")");
		g.drawString(str, m1, m2);
	}
	
	public void writeCoordinates(Graphics g, int m1, int m2, int coord, Color colour)
	{
		FontMetrics f;
		g.setColor(colour);
		g.setFont (new Font ("Helvetica", Font.PLAIN, 13));
		f = g.getFontMetrics();

		String str = new String(""+coord);
		g.drawString(str, m1 - f.stringWidth(str)/2, m2 + f.getHeight()/2);
	}
	
	//Make a Line
	public void makeLine(Graphics g, int a, int b, int c, int d)
	{
		g.drawLine(a, b, c, d);
	}

	//Make a line with colour
	public void makeLine(Graphics g, int a, int b, int c, int d, Color colour, boolean coordPrint)
	{
		g.setColor(colour);
		g.drawLine(a, b, c, d);
		if(coordPrint)
		{
			displayText(g, a, b, colour);
			displayText(g, c, d, colour);
		}
	}
	
	//Draw coordinate axis
	public void drawCoordinate(Graphics g, int ww, int wh, Color colour)
	{
		g.setColor(colour);
		makeLine(g, 0, ww, ww, ww);
		makeLine(g, 0, wh, 0, 0);
	}
	
	// Show the selected point as you wish to show.
	// Another method could be written to change the appearance of how a highlighted point should look.
	public void makeRoundPoint(Graphics g, int tx, int ty, float radii, Color colour)
	{
		g.setColor(colour);
		g.drawOval((int)(tx - radii), (int)(ty - radii), (int)(radii*2), (int)(radii*2));
	}
	
	public void makeFilledRoundPoint(Graphics g, int tx, int ty, float radii, Color colour)
	{
		g.setColor(colour);
		g.fillOval((int)(tx - radii), (int)(ty - radii), (int)(radii*2), (int)(radii*2));
	}
	
	
	//Draws a grid for Cohen Sutherland for Clipping Lines
	public void drawGrid(Graphics g, int xmin, int xmax, int ymin, int ymax, int wh, int ww, Color colour)
	{
		g.setColor(colour);
		makeLine(g, xmin, 0, xmin, wh);
		makeLine(g, xmax, 0, xmax, wh);
		makeLine(g, 0, ymin, ww, ymin);
		makeLine(g, 0, ymax, ww, ymax);
		displayText(g, xmin, 0, colour);
		displayText(g, xmax, 0, colour);
		displayText(g, 0, ymin, colour);
		displayText(g, 0, ymax, colour);
	}
	
	//Draws a rectangle w/o blinking
	public void makeRectangle(Graphics g, int xmin, int xmax, int ymin, int ymax , Color colour, boolean fill, boolean coordPrint)
	{
		g.setColor(colour);
		if(!fill)
            g.drawRect(xmin, Math.min(Math.abs(ymin), Math.abs(ymax)), Math.abs(xmax - xmin), Math.abs(ymax - ymin));
    else
    {
            g.clearRect(xmin, Math.max(ymin, ymax), xmax - xmin, Math.abs(ymax - ymin));
            g.fillRect(xmin, Math.max(ymin, ymax), xmax - xmin, Math.abs(ymax - ymin));
    }    
		if(coordPrint)
		{
			displayText(g, xmin, ymin, colour);
			displayText(g, xmin, ymax, colour);
			displayText(g, xmax, ymin, colour);
			displayText(g, xmax, ymax, colour);
		}
	}
	
	//Draws a rectangle with blinking
	public void makeRectangle(Graphics g, int xmin, int xmax, int ymin, int ymax , Color colour1, Color colour2,
								boolean fill, int counter, int blinkingAnimationTime, boolean coordPrint)
	{
		if((counter/blinkingAnimationTime) % 2 == 0)
			g.setColor(colour1);
		else
			g.setColor(colour2);
		if(!fill)
			g.drawRect(xmin, ymin, xmax - xmin, ymax - ymin);
		else
			g.fillRect(xmin, ymin, xmax - xmin, ymax - ymin);

		if(coordPrint)
		{
			if((counter/blinkingAnimationTime) % 2 == 0)
			{
				displayText(g, xmin, ymin, colour1);
				displayText(g, xmin, ymax, colour1);
				displayText(g, xmax, ymin, colour1);
				displayText(g, xmax, ymax, colour1);
			}
			else
			{
				displayText(g, xmin, ymin, colour2);
				displayText(g, xmin, ymax, colour2);
				displayText(g, xmax, ymin, colour2);
				displayText(g, xmax, ymax, colour2);
			}
		}
	}

	
    public void makeNRectangle(Graphics g, int xmin, int xmax, int ymin, int ymax , Color colour, boolean fill, boolean coordPrint)
    {
            g.setColor(colour);
            if(!fill)
            	g.drawRect(xmin, Math.min(Math.abs(ymin), Math.abs(ymax)), Math.abs(xmax - xmin), Math.abs(ymax - ymin));
            else
            {
            	g.clearRect(xmin, Math.max(ymin, ymax), xmax - xmin, Math.abs(ymax - ymin));
            	g.fillRect(xmin, Math.max(ymin, ymax), xmax - xmin, Math.abs(ymax - ymin));
            }
            if(coordPrint)
            {
                    displayText(g, xmin, ymin, colour);
                    displayText(g, xmin, ymax, colour);
                    displayText(g, xmax, ymin, colour);
                    displayText(g, xmax, ymax, colour);
            }
    }

	
	public boolean makeBlink(int counter, int blinkingAnimationTime)
	{
		if((counter/blinkingAnimationTime) % 2 == 0)
			return false;
		return true;
	}
	
	//Makes a polygon, arguments are Polygon array having vertices and count=number of vertices in array and r g b color
	public void makeCPolygon(Graphics g, int polyVertices[][],int height, int value, float radii, Color colour)
	{
		int i;
		
		for(i=0;i<value-1;i++)
		{
			makeFilledRoundPoint(g, polyVertices[i][0], polyVertices[i][1], radii, colour);
			makeLine(g, polyVertices[i][0], polyVertices[i][1], polyVertices[i+1][0], polyVertices[i+1][1], colour, false);
			writeCoordinates(g, polyVertices[i][0], polyVertices[i][1], polyVertices[i][0], height-polyVertices[i][1],colour);
			writeCoordinates(g, polyVertices[i+1][0], polyVertices[i+1][1], polyVertices[i+1][0], height-polyVertices[i+1][1],colour);
		}
		makeFilledRoundPoint(g, polyVertices[i][0], polyVertices[i][1], radii, colour);
		makeLine(g, polyVertices[i][0], polyVertices[i][1], polyVertices[0][0], polyVertices[0][1], colour, false);
		writeCoordinates(g, polyVertices[i][0], polyVertices[i][1], polyVertices[i][0], height-polyVertices[i][1],colour);
		writeCoordinates(g, polyVertices[0][0], polyVertices[0][1], polyVertices[0][0], height-polyVertices[0][1],colour);

	}
	
	 //Makes a polygon, arguments are Polygon array having vertices and count=number of vertices in array and r g b color
    public void makePolygon(Graphics g, int polyVertices[][], int value, float radii, Color colour)
    {
            int i;
            for(i=0;i<value-1;i++)
            {
                    makeRoundPoint(g, polyVertices[i][0], polyVertices[i][1], radii, colour);
                    makeLine(g, polyVertices[i][0], polyVertices[i][1], polyVertices[i+1][0], polyVertices[i+1][1], colour, true); 
            }
            makeRoundPoint(g, polyVertices[i][0], polyVertices[i][1], radii, colour);
            makeLine(g, polyVertices[i][0], polyVertices[i][1], polyVertices[0][0], polyVertices[0][1], colour, true);
    }

}
