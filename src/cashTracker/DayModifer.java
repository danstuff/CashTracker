package cashTracker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class DayModifer extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int maxNumLength = 10;

	private JTextPane text;

	public JTextField value;

	public JButton update, reset;

	public DayModifer(Application app) {
		setFocusable(false);

		text = new JTextPane();
		text.setEditable(false);
		text.setFocusable(false);
		text.setOpaque(false);
		text.setText("Add/Remove Funds");
		
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
		text.setFont(font);

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
			String trim = value.getText().replaceAll("[^\\d.-]", "");
			
			if(trim != "")
				return Double.parseDouble(trim);
			else
				return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void clear() {
		value.setText("0");
	}
}
