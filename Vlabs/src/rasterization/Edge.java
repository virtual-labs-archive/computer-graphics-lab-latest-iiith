package rasterization;

public class Edge
{
	public int yUpper;
	public double xIntersect;
	public double dxPerScan;
	public Edge next;
	
	public Edge()
	{
		next = null;
	}
	
	public Edge(Edge e)
	{
		yUpper = e.yUpper;
		xIntersect = e.xIntersect;
		dxPerScan = e.dxPerScan;
		next = e.next;
	}
}
