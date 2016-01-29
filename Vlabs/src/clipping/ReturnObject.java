package clipping;



public class ReturnObject {

	public int[][] RenderObject;
	public int count;
	public boolean accept;
	void put(int [][] r)
	{
	RenderObject=r;	
	}
	void put(int r)
	{
	count=r;	
	}
	void put(boolean r)
	{
	accept=r;	
	}
}