package rasterization;

import java.util.ListIterator;
import java.util.Vector;

import library.FrameBuffer;
import library.Line;
import library.Point;
import library.Polygon;

public class ScanConversion
{
	private static int SLOPE_ZERO = 0;
	private static int SLOPE_LESS_ONE = 1;
	private static int SLOPE_GREATER_ONE = 2;
	private static int NEGATIVE_SLOPE_LESS_ONE = 3;
	private static int NEGATIVE_SLOPE_GREATER_ONE = 4;
	private static int SLOPE_INT_MAX = 5;


	// structure for storing vertices for drawing purposes
	private FrameBuffer myFrame;

	// storing the selected and not selected points for animation
	private Vector<Point> seqOrderSelPoints;
	private Vector<Point> seqOrderNotSelPoints;

	//POLYGON SCAN
	Vector<Vector<Edge> > activeList;
	Vector<Boolean> checked;

	public ScanConversion()
	{
		seqOrderSelPoints = new Vector<Point>();
		seqOrderNotSelPoints = new Vector<Point>();

		activeList = new Vector<Vector<Edge>>();
		checked = new Vector<Boolean>();

		myFrame = new FrameBuffer();
	}

	public void storePoints(int i, int j, int i1, int j1)
	{
		Point store = new Point(i, j);
		seqOrderSelPoints.add(store);

		Point notStore = new Point(i1, j1);
		seqOrderNotSelPoints.add(notStore);
	}

	public void setPixel(int i, int j)
	{
		myFrame.setPixel(i, j);
		//====System.out.println("Pixels being set - " + (i - 1) + "\t" + (j - 1));
	}

	public void lineScan(int a, int b, int otherA, int del1, int del2, int m)
	{
		int begin = a;
		int other = otherA;
		int end = b;
		int twoDel1 = 2 * del1;
		int twoDel2 = 2 * del2;
		int p;

		boolean change = false;

		if(m == NEGATIVE_SLOPE_LESS_ONE || m == NEGATIVE_SLOPE_GREATER_ONE)
			p = twoDel2 + del1;
		else
			p = twoDel2 - del1;

		if(m == SLOPE_LESS_ONE || m == NEGATIVE_SLOPE_LESS_ONE || m == SLOPE_ZERO)
			setPixel(begin, other);
		else
			setPixel(other, begin);

		if(m == SLOPE_LESS_ONE || m == NEGATIVE_SLOPE_LESS_ONE || m == SLOPE_ZERO)
			storePoints(begin, other, begin, other);
		else
			storePoints(other, begin, other, begin);

		while(begin < end)
		{
			begin++;
			if(m == SLOPE_ZERO)
			{
				setPixel(begin, other);
				storePoints(begin, other, begin, other);
				continue;
			}
			if(m == SLOPE_INT_MAX)
			{
				setPixel(other, begin);
				storePoints(other, begin, other, begin);
				continue;
			}


			if(p < 0 && (m == NEGATIVE_SLOPE_LESS_ONE || m == NEGATIVE_SLOPE_GREATER_ONE))
			{
				other--;
				p += twoDel1;
				change = true;
			}
			else if(p >= 0 && (m == SLOPE_LESS_ONE || m == SLOPE_GREATER_ONE))
			{
				other++;
				p -= twoDel1;
				change = true;
			}

			p += twoDel2;

			if(m == SLOPE_LESS_ONE || m == NEGATIVE_SLOPE_LESS_ONE)
				setPixel(begin, other);
			else
				setPixel(other, begin);

			if(m == SLOPE_LESS_ONE)
			{
				if(change)
					storePoints(begin, other, begin, other - 1);
				else
					storePoints(begin, other, begin, other + 1);
			}
			else if(m == SLOPE_GREATER_ONE)
			{
				if(change)
					storePoints(other, begin, other - 1, begin);
				else
					storePoints(other, begin, other + 1, begin);
			}
			else if(m == NEGATIVE_SLOPE_LESS_ONE)
			{
				if(change)
					storePoints(begin, other, begin, other + 1);
				else
					storePoints(begin, other, begin, other - 1);
			}
			else if(m == NEGATIVE_SLOPE_GREATER_ONE)
			{
				if(change)
					storePoints(other, begin, other + 1, begin);
				else
					storePoints(other, begin, other - 1, begin);
			}

			change = false;
		}
	}

	public FrameBuffer ScanConvert(Line l, FrameBuffer f)
	{
		myFrame = new FrameBuffer(f.getScreenWidth(), f.getScreenHeight(), f.getRasterSize());
		int[] a, b;
		int delX, delY;
		int temp;
		double slopeAndConstant[] = new double[2];
		Point p[] = new Point[2];

		a = new int[Point.NO_OF_COORDINATES];
		b = new int[Point.NO_OF_COORDINATES];
		l.getEndPoints(p);
		p[0].getCoordinates(a);
		p[1].getCoordinates(b);

		l.getSlopeConstant(slopeAndConstant);

		if(Math.abs(slopeAndConstant[0]) <= 1)
		{
			if(a[0] > b[0])
			{
				temp = a[0];
				a[0] = b[0];
				b[0] = temp;
				temp = a[1];
				a[1] = b[1];
				b[1] = temp;
			}
		}
		else
		{
			if(a[1] > b[1])
			{
				temp = a[0];
				a[0] = b[0];
				b[0] = temp;
				temp = a[1];
				a[1] = b[1];
				b[1] = temp;
			}
		}
		delX = b[0] - a[0];
		delY = b[1] - a[1];

		if(slopeAndConstant[0] == 0)
		{
			lineScan(a[0], b[0], a[1], delX, delY, SLOPE_ZERO);
		}
		else if(slopeAndConstant[0] == Integer.MAX_VALUE)
		{
			lineScan(a[1], b[1], a[0], delY, delX, SLOPE_INT_MAX);
		}
		else if(slopeAndConstant[0] <= 1 && slopeAndConstant[0] > 0)
		{
			lineScan(a[0], b[0], a[1], delX, delY, SLOPE_LESS_ONE);
		}
		else if(slopeAndConstant[0] > 1)
		{
			lineScan(a[1], b[1], a[0], delY, delX, SLOPE_GREATER_ONE);
		}
		else if(slopeAndConstant[0] < 0 && slopeAndConstant[0] >= -1)
		{
			lineScan(a[0], b[0], a[1], delX, delY, NEGATIVE_SLOPE_LESS_ONE);
		}
		else if(slopeAndConstant[0] < -1)
		{
			lineScan(a[1], b[1], a[0], delY, delX, NEGATIVE_SLOPE_GREATER_ONE);
		}

		return myFrame;
	}

	public Vector<Point> getSeqOrderOfSelPoints()
	{
		return seqOrderSelPoints;
	}

	public Vector<Point> getSeqOrderOfNotSelPoints()
	{
		return seqOrderNotSelPoints;
	}

	/*
	 * Scan Conversion of Polygons
	 */

	FrameBuffer ScanConvert(Polygon p, FrameBuffer f)
	{
		if(p == null)
		{
			//=====System.out.println("Polygon is null. Go check the mistake!!!");
			System.exit(0);
		}
		if(f == null)
		{
			//====System.out.println("Frame Buffer is null. Go check the mistake!!!");
			System.exit(0);
		}

		myFrame = new FrameBuffer(f.getScreenWidth(), f.getScreenHeight(), f.getRasterSize());
		Vector<Point> points = p.getVerticesInOrder();
		Vector<Vector<Edge> > edges = new Vector<Vector<Edge>>();
		Vector<Edge> active = new Vector<Edge>();

		int scan;

		int height = f.getScreenHeight();

		for(int i = 0; i < height; i++)
		{
			checked.add(0, false);
			edges.add(new Vector<Edge>());
			activeList.add(new Vector<Edge>());
		}

		edges = buildEdgeList(points.size(), points, edges);

		for(scan = 0; scan < height; scan++)
		{
			active = buildActiveList(scan, active, edges);
			ListIterator<Edge> i = active.listIterator();
			int edge_count = 0;
			while(i.hasNext())
			{
				i.next();
				activeList.get(scan).add(new Edge(active.elementAt(edge_count)));
				edge_count++;
			}

			if (!active.isEmpty())
			{
				checked.set(scan, true);
				fillscan(scan, active);
				active = updateActiveList(scan, active);
				active = resetActiveList (active);
			}


		}
		return myFrame;
	}

	private Vector<Edge> insertEdge(Edge edge, Vector<Edge> edgeList)
	{
		ListIterator<Edge> i = edgeList.listIterator();
		int count = 0;
		Edge tmp;
		while(i.hasNext())
		{
			tmp = i.next();
			if(edge.xIntersect < tmp.xIntersect)
				break;
			count++;
		}
		edgeList.add(count, edge);
		return edgeList;
	}

	private int yNext(int index, int n, Vector<Point> points)
	{
		int j;
		int[] i1, j1;

		i1 = new int[Point.NO_OF_COORDINATES];
		j1 = new int[Point.NO_OF_COORDINATES];

		points.elementAt(index).getCoordinates(i1);

		if(index + 1 >= n)
			j = 0;
		else
			j = index + 1;

		points.elementAt(j).getCoordinates(j1);

		while(i1[1] == j1[1])
		{
			if(j + 1 < n - 1)
				j++;
			else
				j = 0;

			points.elementAt(j).getCoordinates(j1);
		}
		return j1[1];
	}

	private Vector<Vector<Edge> > makeEdgeRec(Point lower, Point upper, int yComp, Vector<Vector<Edge> > edges)
	{
		int[] Lower, Upper;
		Lower = new int[Point.NO_OF_COORDINATES];
		Upper = new int[Point.NO_OF_COORDINATES];

		lower.getCoordinates(Lower);
		upper.getCoordinates(Upper);

		Edge edge = new Edge();
		if(Math.abs(Upper[1] - Lower[1]) > 0.01)
			edge.dxPerScan = (float)(Upper[0] - Lower[0]) / (Upper[1] - Lower[1]);

		edge.xIntersect = Lower[0];

		if(Upper[1] < yComp)
			edge.yUpper = Upper[1] - 1;
		else
			edge.yUpper = Upper[1];

		edges.setElementAt(insertEdge(edge, edges.elementAt(Lower[1])), Lower[1]);

		return edges;
	}

	private Vector<Vector<Edge> > buildEdgeList(int n, Vector<Point> points, Vector<Vector<Edge> > edges)
	{
		Point v1, v2;
		int i;
		int[] Prev, c1, c2;

		Prev = new int[Point.NO_OF_COORDINATES];
		c1 = new int[Point.NO_OF_COORDINATES];

		points.elementAt(n - 2).getCoordinates(Prev);
		points.elementAt(n - 1).getCoordinates(c1);
		v1 = new Point(c1[0], c1[1]);

		for(i = 0; i < n; i++)
		{
			v2 = points.elementAt(i);
			c2 = new int[Point.NO_OF_COORDINATES];
			v2.getCoordinates(c2);

			if (c1[1] < c2[1])
				edges = makeEdgeRec(v1, v2, yNext(i, n, points), edges);
			else if(c1[1] > c2[1])
				edges = makeEdgeRec(v2, v1, Prev[1], edges);

			Prev[1] = c1[1];
			v1 = v2;
			c1 = c2;
		}

		return edges;
	}

	private Vector<Edge> buildActiveList(int scan, Vector<Edge> active, Vector<Vector<Edge> > edges)
	{
		ListIterator<Edge> i = edges.elementAt(scan).listIterator();
		while(i.hasNext())
		{
			active = insertEdge(i.next(), active);
		}

		return active;
	}

	private void fillscan(int scan, Vector<Edge> active)
	{
		ListIterator<Edge> i = active.listIterator();
		int x;
		while(i.hasNext())
		{
			int start = (int) Math.round(i.next().xIntersect);
			int stop = (int)Math.round(i.next().xIntersect);
			for(x = start; x <= stop; x++)
			{
				setPixel((int)x, scan);
			}
		}
	}

	private Vector<Edge> updateActiveList(int scan, Vector<Edge> active)
	{
		ListIterator<Edge> i = active.listIterator();
		int[] count = new int[active.size()];
		Edge temp = new Edge();
		int k, c, l;
		k = c = 0;
		while(i.hasNext())
		{
			temp = i.next();
			if(scan >= temp.yUpper)
				count[c++] = k;
			else
			{
				temp.xIntersect = temp.xIntersect + temp.dxPerScan;
				active.setElementAt(temp, k);
			}
			k++;
		}
		i = active.listIterator();
		int length_count = c;
		k = c = l = 0;
		while(i.hasNext() && (c < length_count))
		{
			if(count[c] == k)
			{
				active.remove(l);
				l--;
				c++;
			}
			else
				temp = i.next();
			k++;
			l++;
		}
		return active;
	}

	private Vector<Edge> resetActiveList(Vector<Edge> active)
	{
		Vector<Edge> copy = active;
		active = new Vector<Edge>();
		ListIterator<Edge> i = copy.listIterator();

		while(i.hasNext())
		{
			active = insertEdge(i.next(), active);
		}

		return active;
	}


	public Vector<Boolean> getChecked()
	{
		return checked;
	}

	public Vector<Vector<Edge> > getActiveList()
	{
		return activeList;
	}
}
