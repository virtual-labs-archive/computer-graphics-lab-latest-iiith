package library;

public class FrameBuffer
{
	private boolean frame[][];
	private float redFrame[][];
	private float greenFrame[][];
	private float blueFrame[][];
	private int screenWidth, screenHeight;
	private int rasterSize;

	public FrameBuffer()
	{
	}

	public FrameBuffer(int width, int height, int size)
	{
		screenWidth = width;
		screenHeight = height;
		rasterSize = size;

		frame = new boolean[screenWidth][screenHeight];
		redFrame = new float[screenWidth][screenHeight];
		greenFrame = new float[screenWidth][screenHeight];
		blueFrame = new float[screenWidth][screenHeight];
	}
	
	public int getRasterSize()
	{
		return rasterSize;
	}

	public int getScreenWidth()
	{
		return screenWidth;
	}

	public int getScreenHeight()
	{
		return screenHeight;
	}

	public void setPixel(int i, int j, float red, float green, float blue)
	{
		//===========System.out.println("setting (" + i + ", " + j + ") with " + red + ", " + green + ", " + blue);
		frame[i][j] = true;
		redFrame[i][j] = red;
		greenFrame[i][j] = green;
		blueFrame[i][j] = blue;
	}

	public void setPixel(int i, int j)
	{
		frame[i][j] = true;
		redFrame[i][j]= 0;
		greenFrame[i][j] = 0;
		blueFrame[i][j] = 0;
	}

	public void unsetPixel(int i, int j)
	{
		frame[i][j] = false;
	}

	public void draw()
	{
		/*
		int i, j;
		for(j = screenHeight - 1; j >= 0; j--)
		{
			for (i = 0; i < screenWidth; i++)
			{
				if(frame[i][j])
					System.out.print("1 ");
				else
					System.out.print("0 ");;
			}
			System.out.println();
		}
		*/
	}

	public boolean pixelActive(int i, int j)
	{
		return frame[i][j];
	}

	public float getRedForPixel(int i, int j)
	{
		return redFrame[i][j];
	}

	public float getGreenForPixel(int i, int j)
	{
		return greenFrame[i][j];
	}

	public float getBlueForPixel(int i, int j)
	{
		return blueFrame[i][j];
	}
	
	public int getWidth()
	{
		return screenWidth;
	}
	
	public int getHeight()
	{
		return screenHeight;
	}
}
