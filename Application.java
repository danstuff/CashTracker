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

	PanelDay day;
	PanelModify mod;

	PanelMonth month, tmonth;

	Graph graph;

	DataPointFile dpf;
	DataPointList dpl;

	public Application() {
		setTitle("CashTracker");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		dpf = new DataPointFile();
		dpl = new DataPointList();

		dpl.current = dpf.clipListCurrent();
		dpl.selected = dpl.current;

		dpl.past = dpf.getList();

		dpf.backup(dpl.current);
		
		// left
		JPanel left = new JPanel(new BorderLayout());
		left.setBackground(background);
		
		PanelTitle dtitle = new PanelTitle(24, "CashTracker");
		left.add(dtitle, BorderLayout.NORTH);
		
		day = new PanelDay();
		left.add(day, BorderLayout.CENTER);
		
		mod = new PanelModify(this);
		left.add(mod, BorderLayout.SOUTH);
		
		// right
		JPanel right = new JPanel(new BorderLayout());
		right.setBackground(background);
		
		PanelTitle antitle = new PanelTitle(20, "This Month");
		right.add(antitle, BorderLayout.NORTH);
		
		month = new PanelMonth();
		month.update(dpl);
		right.add(month, BorderLayout.CENTER);
		
		// center
		graph = new Graph(dpl, day, month, antitle);	
		
		// overall panel
		add(left, BorderLayout.WEST);
		add(graph, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);

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

			if (description != null) {
				graph.repaint();

				dpl.updateCurrent(mod.get(), description);

				dpf.save(dpl.current);

				mod.clear();
			}

		} else if (e.getSource() == mod.reset) {
			graph.repaint();

			dpl.resetCurrent();
			dpf.save(dpl.current);

		}
	}
}
