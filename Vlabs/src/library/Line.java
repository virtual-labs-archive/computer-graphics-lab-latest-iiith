package library;

public class Line
{
	private Point p1, p2;
	private double slope, constant;
	private boolean endPointsDefined;
	
	public Line()
	{
		p1 = new Point();
		p2 = new Point();
		endPointsDefined = false;
		slope = 0.0;
		constant = 0.0;
	}
	
	public Line(Point firstPoint, Point secondPoint)
	{
		p1 = firstPoint;
		p2 = secondPoint;
		endPointsDefined = true;
		
		int[] endPoint1 = new int[Point.NO_OF_COORDINATES];
		p1.getCoordinates(endPoint1);

		int[] endPoint2 = new int[Point.NO_OF_COORDINATES];
		p2.getCoordinates(endPoint2);

		// line to be considered in 2D in clipping and scan conversion
		if(Math.abs(endPoint1[Point.xCoord] - endPoint2[Point.xCoord]) < 0.001)
		{
			slope =(float) Integer.MAX_VALUE;
			constant =(float) Integer.MAX_VALUE;
			return;
		}

		slope = (endPoint2[Point.yCoord] - endPoint1[Point.yCoord]) * 1.0/(endPoint2[Point.xCoord] - endPoint1[Point.xCoord]);
		constant = (endPoint1[Point.yCoord] * endPoint2[Point.xCoord]) - (endPoint2[Point.yCoord] * endPoint1[Point.xCoord]);
		constant = constant/(endPoint2[Point.xCoord] - endPoint1[Point.xCoord]);
	}

	public Line(double m, double c)
	{
		p1 = new Point();
		p2 = new Point();
		endPointsDefined= false;

		slope = m;
		constant = c;
	}
	
	public boolean getEndPoints(Point[] array)
	{
		if(endPointsDefined)
		{
			array[0] = p1;
			array[1] = p2;
			return true;
		}
		else
		{
			array[0] = new Point();
			array[1] = new Point();
			return false;
		}
	}
	
	public void getSlopeConstant(double[] array)
	{
		array[0] = slope;
		array[1] = constant;
	}
	
	public Point intersects(Line l)
	{
		if(Math.abs(slope - l.slope) < 0.001)
		{
			Point p = new Point((int)Integer.MAX_VALUE,(int)Integer.MAX_VALUE);
			return p;
		}

		double x, y;
		x = -(constant - l.constant)/(slope - l.slope);
		y = ((slope * l.constant) - (l.slope - constant))/(slope - l.slope);

		Point p = new Point((int)x, (int)y);
		return p;
	}
}
