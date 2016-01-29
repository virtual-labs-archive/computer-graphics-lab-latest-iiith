package engine.display;

import java.awt.Color;

public class Theme {
	public Palette xColors, yColors, zColors, originColors;
	public Palette vertexColors, triangleColors;
	public Color backgroundColor, gridXYColor, gridYZColor, gridZXColor;
	public boolean drawXYGrid = true, drawYZGrid = false, drawZXGrid = false;

	public Theme() {
		xColors = new Palette();
		xColors.add(Color.RED, Color.ORANGE, Color.ORANGE);

		yColors = new Palette();
		yColors.add(Color.GREEN, Color.PINK, Color.GREEN);

		zColors = new Palette();
		zColors.add(Color.CYAN, Color.BLUE, Color.CYAN);

		originColors = new Palette();
		originColors.add(Color.WHITE, Color.WHITE, Color.GRAY);

		vertexColors = new Palette();
		vertexColors.add(Color.YELLOW, Color.WHITE, Color.LIGHT_GRAY);

		triangleColors = new Palette();
		triangleColors.add(new Color(.5f, 1, .5f, 1), new Color(.5f, 1, .65f, 1), new Color(1, .5f, .5f, 1));

		backgroundColor = Color.DARK_GRAY;
		gridXYColor = gridYZColor = gridZXColor = Color.BLACK;
	}

	public void reset() {
		xColors.reset();
		yColors.reset();
		zColors.reset();
		originColors.reset();
		vertexColors.reset();
		triangleColors.reset();		
	}
}
