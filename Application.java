package cashTracker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Application extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	public static final Color background = new Color(230, 250, 250);
	public static final Color foreground = new Color(80, 100, 100);

	StatsPanel stats;
	ModPanel mod;

	Graph graph;

	public Application() {
		setTitle("CashTracker");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// settings stuff
		TitlePanel title = new TitlePanel(24, "CashTracker");

		stats = new StatsPanel();
		mod = new ModPanel(this);

		// settings panel
		JPanel settings = new JPanel();
		settings.setBackground(background);
		settings.setLayout(new BorderLayout());

		settings.add(title, BorderLayout.NORTH);
		settings.add(stats, BorderLayout.CENTER);
		settings.add(mod, BorderLayout.SOUTH);

		// graph
		graph = new Graph(stats);

		// overall panel
		setLayout(new BorderLayout());

		add(settings, BorderLayout.WEST);
		add(graph, BorderLayout.CENTER);

		pack();
		setFocusable(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Application app = new Application();
				app.setLocationRelativeTo(null);
				app.setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		setFocusable(true);

		if ((e.getSource() == mod.update || e.getSource() == mod.value) && mod.get() != 0) {

			String question = "Why were ";

			if (mod.get() > 0)
				question += "$" + Double.toString(Math.abs(mod.get())) + " added?";
			else
				question += "$" + Double.toString(Math.abs(mod.get())) + " removed?";

			String description = (String) JOptionPane.showInputDialog(this, question, "Enter Description",
					JOptionPane.PLAIN_MESSAGE, null, null, "Unknown");

			graph.update(mod.get(), description);
			mod.clear();
		} else if (e.getSource() == mod.reset) {
			graph.reset();
		}
	}
}
