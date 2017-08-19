package cashTracker;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Application extends JFrame implements ActionListener, ChangeListener {

	private static final long serialVersionUID = 1L;

	CashTracker ct;

	public void format(CashTracker ct) {
		//use the cross platform look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setLayout(new BorderLayout());

		// left side
		JPanel left = new JPanel(new BorderLayout());

		left.add(ct.day_title, BorderLayout.NORTH);
		left.add(ct.day, BorderLayout.CENTER);
		left.add(ct.day_modifier, BorderLayout.SOUTH);

		// right side
		JPanel right = new JPanel(new BorderLayout());

		right.add(ct.month_title, BorderLayout.NORTH);
		right.add(ct.month, BorderLayout.CENTER);
		
		JPanel pies = new JPanel();
		pies.setLayout(new BoxLayout(pies, BoxLayout.PAGE_AXIS));
		
		pies.add(ct.pie_additions);
		pies.add(ct.pie_subtractions);
		pies.add(ct.pie_tds);

		right.add(pies, BorderLayout.SOUTH);
		
		// overall panel
		add(left, BorderLayout.WEST);
		add(ct.tabs, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);
	}

	public Application() {
		setTitle("CashTracker");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// initialize
		ct = new CashTracker(this);

		// set up key bindings
		ct.tabs.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
		ct.tabs.getActionMap().put("left", left);

		ct.tabs.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
		ct.tabs.getActionMap().put("right", right);

		ct.tabs.addChangeListener(this);
		
		format(ct);
		ct.setup();

		ct.update();

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

		if ((e.getSource() == ct.day_modifier.update || e.getSource() == ct.day_modifier.value)
				&& ct.day_modifier.get() != 0) {
			ct.actionAdd(this);
		} else if (e.getSource() == ct.day_modifier.reset) {
			ct.actionReset();
		}
		
		repaint();
	}

	AbstractAction left = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ct.actionLeft();
			repaint();
		}
	};

	AbstractAction right = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ct.actionRight();
			repaint();
		}
	};

	@Override
	public void stateChanged(ChangeEvent e) {
		ct.update();
		repaint();
	}
}
