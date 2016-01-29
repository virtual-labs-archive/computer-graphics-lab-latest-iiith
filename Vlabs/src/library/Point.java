package library;

public class Point
{
	public static int NO_OF_COORDINATES = 3;
	public static int NO_OF_COLOURS = 3;
	
	public static int xCoord = 0;
	public static int yCoord = 1;
	public static int zCoord = 2;
	
	public static int rColour = 2;
	public static int gColour = 1;
	public static int bColour = 2;
	
	private int[] coordinates;
	private double[] rgb;
	
	public Point()
	{
		coordinates = new int[NO_OF_COORDINATES];
		rgb = new double[NO_OF_COLOURS];
	}
	
	public Point(Point temp)
	{
		coordinates = new int[NO_OF_COORDINATES];
		rgb = new double[NO_OF_COLOURS];
		
		for(int i = 0; i < NO_OF_COLOURS; i++)
		{
			rgb[i] = temp.rgb[i];
		}

		for(int i = 0; i < NO_OF_COORDINATES; i++)
		{
			coordinates[i] = temp.coordinates[i];
		}
	}
	
	public Point(int x, int y)
	{
		coordinates = new int[NO_OF_COORDINATES];
		rgb = new double[NO_OF_COLOURS];
		
		coordinates[xCoord] = x;
		coordinates[yCoord] = y;
	}
	
	public Point(int x, int y, double r, double g, double b)
	{
		coordinates = new int[NO_OF_COORDINATES];
		rgb = new double[NO_OF_COLOURS];
		
		coordinates[xCoord] = x;
		coordinates[yCoord] = y;
		
		rgb[rColour] = r;
		rgb[gColour] = g;
		rgb[bColour] = b;
	}
	
	public Point(int x, int y, int z)
	{
		coordinates = new int[NO_OF_COORDINATES];
		rgb = new double[NO_OF_COLOURS];
		
		coordinates[xCoord] = x;
		coordinates[yCoord] = y;
		coordinates[zCoord] = z;
	}
	
	public Point(int x, int y, int z, double r, double g, double b)
	{
		coordinates = new int[NO_OF_COORDINATES];
		rgb = new double[NO_OF_COLOURS];
		
		coordinates[xCoord] = x;
		coordinates[yCoord] = y;
		coordinates[zCoord] = z;
		
		rgb[rColour] = r;
		rgb[gColour] = g;
		rgb[bColour] = b;
	}
	
	public void getCoordinates(int[] coord)
	{
		for(int i = 0; i < NO_OF_COORDINATES; i++)
			coord[i] = coordinates[i];
	}
	
	public int getX()
	{
		return coordinates[xCoord];
	}
	
	public int getY()
	{
		return coordinates[yCoord];
	}
	
	public int getZ()
	{
		return coordinates[zCoord];
	}
	
	public void getColour(double[] coord)
	{
		for(int i = 0; i < NO_OF_COORDINATES; i++)
			coord[i] = rgb[i];
	}
	
	public void setPoint(int x, int y)
	{
		coordinates[xCoord] = x;
		coordinates[yCoord] = y;
	}
	
	public void setPoint(int x, int y, int z)
	{
		coordinates[xCoord] = x;
		coordinates[yCoord] = y;
		coordinates[zCoord] = z;
	}
	
	public void setPoint(Point temp)
	{
		coordinates = new int[NO_OF_COORDINATES];
		rgb = new double[NO_OF_COLOURS];
		
		for(int i = 0; i < NO_OF_COLOURS; i++)
		{
			rgb[i] = temp.rgb[i];
		}

		for(int i = 0; i < NO_OF_COORDINATES; i++)
		{
			coordinates[i] = temp.coordinates[i];
		}
	}
}
