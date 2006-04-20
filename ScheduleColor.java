/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import java.awt.Color;

public class ScheduleColor {
	// red, green, blue, orange, pink, purple

	private static Color[] fillColors = {new Color(255, 173, 175), new Color(119, 245, 125),
											new Color(146, 186, 247), new Color(251, 213, 180),
											new Color(244, 166, 234), new Color(203, 185, 239)};

	private static Color[] borderColors = {new Color(253, 0, 15), new Color(0, 165, 0),
											new Color(0, 78, 218), new Color(225, 118, 0),
											new Color(196, 18, 178), new Color(82, 35, 165)};

	public ScheduleColor() {
		// Nothing to do
	}

	private int fixNumber(int number) {
		int colorNumber = number;

		while (colorNumber < 0)
			colorNumber = colorNumber + fillColors.length;
		while (colorNumber >= fillColors.length)
			colorNumber = colorNumber - fillColors.length;

		return colorNumber;
	}

	public Color getBorder(int colorNumber) {
		return borderColors[fixNumber(colorNumber)];
	}

	public Color getFill(int colorNumber) {
		return fillColors[fixNumber(colorNumber)];
	}
}