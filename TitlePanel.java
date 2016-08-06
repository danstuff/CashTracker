package cashTracker;

import javax.swing.JTextArea;

public class TitlePanel extends JTextArea {

	private static final long serialVersionUID = 1L;

	public TitlePanel(float size, String name) {
		setEditable(false);
		setFocusable(false);
		setBackground(Application.background);
		setForeground(Application.foreground);
		setFont(getFont().deriveFont(size));
		setText(name);
	}
}
