package cashTracker;

import javax.swing.JTextArea;

public class PanelDay extends JTextArea {

	private static final long serialVersionUID = 1L;

	public PanelDay() {
		setEditable(false);
		setFocusable(false);
		setBackground(Application.background);
		setForeground(Application.foreground);
		setFont(getFont().deriveFont(16));
		setLineWrap(true);
		setText("");
	}

	public void update(DataPointList dpl) {
		if (dpl.selected == null)
			return;

		setText(dpl.selected.getStr(true));
	}
}
