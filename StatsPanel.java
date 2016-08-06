package cashTracker;

import javax.swing.JTextArea;

public class StatsPanel extends JTextArea {

	private static final long serialVersionUID = 1L;

	public StatsPanel() {
		setEditable(false);
		setFocusable(false);
		setBackground(Application.background);
		setForeground(Application.foreground);
		setFont(getFont().deriveFont(16));
		setLineWrap(true);
		setText("");
	}

	public void update(DataPoint point, double allowance) {
		if (point == null)
			return;

		String sallowance = Double.toString(Math.round(allowance * 100) / 100.0);

		String text = point.getStr(true) + "\n" + "Daily Allowance: $" + sallowance;

		setText(text);
	}
}
