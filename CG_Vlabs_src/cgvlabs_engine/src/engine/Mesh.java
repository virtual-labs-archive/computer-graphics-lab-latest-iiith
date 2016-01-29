package engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import engine.display.Display;


public class Mesh extends Shape {
	private static int count = 0;
	
	public Vertex[] vertices;
	public Triangle[] triangles;
	private int currentVertex;
	private String id;

	public Mesh(String filename) throws FileNotFoundException {
		if((filename.substring(filename.length()-4).compareToIgnoreCase(".ply") != 0))
			throw new FileNotFoundException("File extension is not PLY");

		int vertexSize = 0, nVertex = 0, nFace = 0;

		Scanner scanner = new Scanner(new File(filename));
		boolean inHeader = true, inVertex = false;

		while(scanner.hasNext() && inHeader)
		{
			String line = scanner.nextLine();
			Scanner wscanner = new Scanner(line);

			String word = wscanner.next();
			
			if(word.compareToIgnoreCase("element") == 0)
			{
				word = wscanner.next();
				if(word.compareTo("vertex") == 0)
				{
					nVertex = wscanner.nextInt();
					inVertex = true;
				}
				else if(word.compareToIgnoreCase("face") == 0)
				{
					nFace = wscanner.nextInt();
					inVertex = false;
				}
			}
			else if(inVertex && word.compareToIgnoreCase("property") == 0)
			{
				vertexSize++;
			}
			else if(word.compareToIgnoreCase("end_header") == 0)
			{
				inHeader = false;
			}
		}

		vertices = new Vertex[nVertex];
		Vector[] normals = new Vector[nVertex];

		float x, y, z;
		for(int i = 0; i < nVertex; i++)
		{
			x = scanner.nextFloat();
			y = scanner.nextFloat();
			z = scanner.nextFloat();
			vertices[i] = new Vertex(x,y,z);

			int j = 3;
			if(vertexSize >= 6)
			{
				x = scanner.nextFloat();
				y = scanner.nextFloat();
				z = scanner.nextFloat();
				normals[i] = new Vector(x,y,z);
				j = 6;
			}
			while(j++ < vertexSize)
				scanner.nextFloat();
		}

		ArrayList<Triangle> tmpTriangles = new ArrayList<Triangle>();

		for(int i = 0; i < nFace; i++)
		{
			int nTris = scanner.nextInt(), a = scanner.nextInt(), b = scanner.nextInt(), c;
			for(int j = 2; j < nTris; j++)
			{
				c = scanner.nextInt();
				if(vertexSize >= 6)
					tmpTriangles.add(new Triangle(a,b,c,normals[a],normals[b],normals[c]));
				else
					tmpTriangles.add(new Triangle(a,b,c));
				b = c;
			}
		}

		triangles = new Triangle[tmpTriangles.size()];
		for(int i = 0; i < tmpTriangles.size(); i++)
			triangles[i] = tmpTriangles.get(i);

		currentVertex = 0;

		this.id = filename.substring(1+filename.lastIndexOf(File.separatorChar));
	}

	public Mesh(Vector[] vertices, Triangle[] triangles) {
		this.vertices = new Vertex[vertices.length];
		for(int i = 0; i < vertices.length; i++)
			this.vertices[i] = new Vertex(vertices[i]);

		if(triangles != null && triangles.length > 0) {
			this.triangles = new Triangle[triangles.length];
			for(int i = 0; i < triangles.length; i++)
				this.triangles[i] = new Triangle(triangles[i]);
		}

		currentVertex = 0;
		this.id = "Shape " + String.valueOf(++count);
	}

	public Mesh(Vector[] vertices) {
		this(vertices, null);
	}

	@Override
	public void select(Vertex vertex) {
		for(currentVertex = vertices.length-1; currentVertex >= 0; currentVertex--)
			if(vertices[currentVertex] == vertex)
				break;
	}

//	@Override
//	public void deselect() {
//		currentVertex = -1;
//	}

	@Override
	public Vector getSelected(Display display) {
		return (currentVertex >= 0) ? vertices[currentVertex].vector : null;
	}

//	@Override
//	public void selectNext() {
//		if (vertices == null || (vertices.length == 0))
//			currentVertex = -1;
//		else
//			currentVertex = (currentVertex + 1) % vertices.length;
//	}
//
//	@Override
//	public void selectPrevious() {
//		if (vertices == null || (vertices.length == 0))
//			currentVertex = -1;
//		else {
//			currentVertex--;
//			if(currentVertex < 0)
//				currentVertex = vertices.length-1;
//		}
//	}

	@Override
	public void setSelected(Vector coords, Display display) {
		if (currentVertex >= 0) {
			vertices[currentVertex].vector.set(coords);
		}
	}

	@Override
	public boolean isSelected(Vertex vertex) {
		return (currentVertex >= 0) && (currentVertex < vertices.length) && (vertices[currentVertex] == vertex);
	}

	public void startTracking(int index) {
		if(index >= 0 && index < vertices.length)
			vertices[index].isTracking = true;
	}

	public void stopTracking(int index) {
		if(index >= 0 && index < vertices.length)
			vertices[index].isTracking = false;
	}

	@Override
	public void draw(CoordSystem coordSystem, CoordSystem original, Transformation transformation, Display display, boolean isCurrent, boolean isActive) {
		Vector[] ap = new Vector[vertices.length];
		Vector[] rp = new Vector[vertices.length];

		if(!display.transformCoordSystems) {
			for(int i = 0; i < vertices.length; i++) {
				rp[i] = vertices[i].vector;
			}
			rp = transformation.apply(rp, display);
			for(int i = 0; i < vertices.length; i++) {
				ap[i] = coordSystem.toAbsolute(rp[i]);
			}
		} else {
			for(int i = 0; i < vertices.length; i++) {
				ap[i] = original.toAbsolute(vertices[i].vector);
			}
			for(int i = 0; i < vertices.length; i++)
				rp[i] = coordSystem.toRelative(ap[i]);
		}

		// Draw the triangles
		if(triangles != null) {
			display.setColor(display.theme.triangleColors.getNext(isCurrent, isActive));
			for (int i = 0; i < triangles.length; i++) {
				Triangle tri = triangles[i];
				if (tri.hasNormals)
					display.drawTriangleN(ap[tri.a], ap[tri.b], ap[tri.c], tri.na, tri.nb, tri.nc);
				else
					display.drawTriangle(ap[tri.a], ap[tri.b], ap[tri.c]);
			}
		}

		// Draw the vertices
		if(isCurrent) {
			for(int i = 0; i < vertices.length; i++) {
				boolean isCur2 = isCurrent && isSelected(vertices[i]);
				display.setColor(display.theme.vertexColors.getNext(isCur2, isActive));
				display.drawVertex(rp[i], ap[i], coordSystem, isCurrent, isActive, vertices[i].isTracking, coordSystem.showUnits);
			}
		}
	}

	@Override
	public String toString() {
		return id;
	}
}
