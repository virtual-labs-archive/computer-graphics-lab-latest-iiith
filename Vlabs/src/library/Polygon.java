package library;

import java.util.Vector;

public class Polygon
{
	private Vector<Point> vertices;
	
	public Polygon()
	{
		vertices = new Vector<Point>();
	}
	
	public void addVertex(Point p)
	{
		vertices.add(p);
		return;
	}
	
	public void setVertexAt(int i, Point p_temp)
	{
		Point temp = new Point(p_temp);
		vertices.set(i, temp);
	}
	
	public Vector<Point> getVerticesInOrder()
	{
		return vertices;
	}
	
	public Point getVertexAt(int i)
	{
		return vertices.get(i);
	}
	
	public int getVertexCount( )
	{
		return vertices.size();
	}
	
	public void getRectangleBoundary(int boundary[])
	{
		//In format xmin,xmax,ymin,ymax
		int i = 0;
		int no_of_sides = vertices.size();
		int[][] array = new int[no_of_sides][Point.NO_OF_COORDINATES];
		
		boundary[0] = Integer.MAX_VALUE;
		boundary[1] = Integer.MIN_VALUE;
		boundary[2] = Integer.MAX_VALUE;
		boundary[3] = Integer.MIN_VALUE;
		
		for(i = 0; i < no_of_sides; i++)
		{
			vertices.get(i).getCoordinates(array[i]);
			
			if(boundary[0] > array[i][Point.xCoord])
				boundary[0] = array[i][Point.xCoord];
			
			if(boundary[1] < array[i][Point.xCoord])
				boundary[1] = array[i][Point.xCoord];
			
			if(boundary[2] > array[i][Point.yCoord])
				boundary[2] = array[i][Point.yCoord];
			
			if(boundary[3] < array[i][Point.yCoord])
				boundary[3] = array[i][Point.yCoord];
		}
	}
}
