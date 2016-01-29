package clipping;

public class Messages {


	// String Display Position
	private static int stringxSelectLineDisplay;
	private static int stringySelectLineDisplay;
	private static int stringxClipRectangleDisplay;
	private static int stringyClipRectangleDisplay;
	private static int stringxCodeDisplay;
	private static int stringyCodeDisplay;
	
	public static final String topSide = "Clipping against Top Side"; 
	public static final String leftSide = "Clipping against Left Side";
	public static final String rightSide = "Clipping against Right Side";
	public static final String bottomSide = "Clipping against Bottom Side";
	public static final String clipRectangle = "Clip Rectangle Coordinates are: ";
	public static final String clipLine = "Clipping Line joining ";
	
	public static int getStringxSelectLinedisplay() {
		return stringxSelectLineDisplay;
	}
	public static int getStringySelectLinedisplay() {
		return stringySelectLineDisplay;
	}
	public static void setStringxSelectLinedisplay(int xcoordinate) {
		stringxSelectLineDisplay = xcoordinate;
	}
	public static void setStringySelectLinedisplay(int ycoordinate) {
		stringySelectLineDisplay = ycoordinate;
	}
	public static int getStringxClipRectangleDisplay() {
		return stringxClipRectangleDisplay;
	}
	public static void setStringxClipRectangleDisplay(
			int stringxClipRectangleDisplay) {
		Messages.stringxClipRectangleDisplay = stringxClipRectangleDisplay;
	}
	public static int getStringyClipRectangleDisplay() {
		return stringyClipRectangleDisplay;
	}
	public static void setStringyClipRectangleDisplay(
			int stringyClipRectangleDisplay) {
		Messages.stringyClipRectangleDisplay = stringyClipRectangleDisplay;
	}
	public static int getStringxCodeDisplay() {
		return stringxCodeDisplay;
	}
	public static void setStringxCodeDisplay(int stringxCodeDisplay) {
		Messages.stringxCodeDisplay = stringxCodeDisplay;
	}
	public static int getStringyCodeDisplay() {
		return stringyCodeDisplay;
	}
	public static void setStringyCodeDisplay(int stringyCodeDisplay) {
		Messages.stringyCodeDisplay = stringyCodeDisplay;
	}
	
}
