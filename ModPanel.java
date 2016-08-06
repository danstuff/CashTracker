package cashTracker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ModPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int maxNumLength = 10;

	private JTextArea text;

	public JTextField value;

	public JButton update, reset;

	public JPanel tblank, bblank;

	public ModPanel(Application app) {
		setFocusable(false);

		text = new JTextArea();
		text.setBackground(Application.background);
		text.setForeground(Application.foreground);
		text.setEditable(false);
		text.setFocusable(false);
		text.setText("Add/Remove Funds");

		value = new JFormattedTextField();
		value.setColumns(maxNumLength);
		value.setPreferredSize(new Dimension(1, 32));
		value.setText("0");
		value.addActionListener(app);

		update = new JButton("Update Day");
		update.addActionListener(app);

		reset = new JButton("Reset Day");
		reset.addActionListener(app);

		JPanel buttonpanel = new JPanel(new BorderLayout());
		buttonpanel.add(update, BorderLayout.WEST);
		buttonpanel.add(reset, BorderLayout.EAST);

		setLayout(new BorderLayout());

		add(text, BorderLayout.NORTH);
		add(value, BorderLayout.CENTER);
		add(buttonpanel, BorderLayout.SOUTH);
	}

	public double get() {
		try {
			return Double.parseDouble(value.getText().replaceAll("[^\\d.-]", ""));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void clear() {
		value.setText("0");
	}
}
