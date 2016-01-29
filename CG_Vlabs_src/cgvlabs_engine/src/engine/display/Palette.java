package engine.display;

import java.awt.Color;
import java.util.ArrayList;

public class Palette {
	private ArrayList<Color> currentColors = new ArrayList<Color>();
	private ArrayList<Color> activeColors = new ArrayList<Color>();
	private ArrayList<Color> inactiveColors = new ArrayList<Color>();
	private int next = 0;

	public Palette() {
	}

	@SuppressWarnings("unchecked")
	public Palette(Palette palette) {
		currentColors = (ArrayList<Color>)palette.currentColors.clone();
		activeColors = (ArrayList<Color>)palette.activeColors.clone();
		inactiveColors = (ArrayList<Color>)palette.inactiveColors.clone();
	}

	public void add(Color currentColor, Color activeColor, Color inactiveColor) {
		currentColors.add(currentColor);
		activeColors.add(activeColor);
		inactiveColors.add(inactiveColor);
	}

	public Color getNext(boolean isCurrent, boolean isActive) {
		if(currentColors.size() == 0) return null;
		next = (next + 1) % activeColors.size();

		if(isCurrent) {
			return currentColors.get(next);
		} else if(isActive) {
			return activeColors.get(next);			
		} else {
			return inactiveColors.get(next);
		}
	}

	public void reset() {
		next = 0;
	}
}
