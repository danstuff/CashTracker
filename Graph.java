package cashTracker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Graph extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int width = 640;
	public static final int height = 480;

	private static final Color background = new Color(240, 250, 250);
	private static final Color foreground = new Color(220, 230, 230);
	private static final Color highlight = new Color(80, 100, 100);

	private static final int point_diam = 8;
	private static final int select_diam = point_diam * 2;

	public static int max_days;
	public static int max_balance;

	public static int pixels_per_day;
	public static int pixels_per_thousand;

	private DataPointList dpl;
	private PanelDay stats;

	AbstractAction left = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			dpl.back();
			repaint();
		}
	};

	AbstractAction right = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			dpl.forward();
			repaint();
		}
	};

	public static void recalculate() {
		pixels_per_day = (int) Math.round(width / (double)max_days);
		pixels_per_thousand = (int) Math.round((double) height / max_balance * 1000);

		DataPoint.init();
	}

	public Graph(DataPointList dpl, PanelDay stats) {
		this.dpl = dpl;
		this.stats = stats;

		// set up key bindings
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
		getActionMap().put("left", left);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
		getActionMap().put("right", right);

		setPreferredSize(new Dimension(width, height));
		setBackground(background);
		setFocusable(true);
	}

	private Color getMonthColor(int month) {
		Color color = Color.WHITE;
		switch (month) {
		case Calendar.JANUARY:
			color = new Color(98, 189, 24, 100);
			break;
		case Calendar.FEBRUARY:
			color = new Color(141, 221, 0, 100);
			break;
		case Calendar.MARCH:
			color = new Color(163, 238, 63, 100);
			break;
		case Calendar.APRIL:
			color = new Color(255, 83, 0, 100);
			break;
		case Calendar.MAY:
			color = new Color(255, 114, 0, 100);
			break;
		case Calendar.JUNE:
			color = new Color(255, 167, 21, 100);
			break;
		case Calendar.JULY:
			color = new Color(210, 16, 52, 100);
			break;
		case Calendar.AUGUST:
			color = new Color(252, 61, 49, 100);
			break;
		case Calendar.SEPTEMBER:
			color = new Color(255, 71, 92, 100);
			break;
		case Calendar.OCTOBER:
			color = new Color(143, 22, 178, 100);
			break;
		case Calendar.NOVEMBER:
			color = new Color(128, 66, 181, 100);
			break;
		case Calendar.DECEMBER:
			color = new Color(165, 119, 249, 100);
			break;
		}

		return color;
	}

	public int getx(int day) {
		return day * pixels_per_day;
	}

	public int gety(double value) {
		return height - (int) Math.round(value * (pixels_per_thousand / 1000.0f));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		requestFocus();

		// set stroke
		Graphics2D g2d = (Graphics2D) g;

		g2d.setStroke(new BasicStroke());

		// draw the lines on the graph
		Calendar start_date = dpl.current.getDate();
		start_date.add(Calendar.DAY_OF_MONTH, -Math.round(2 * max_days / 3.0f));

		// draw vertical lines
		for (int y = 0; y < height; y += pixels_per_thousand) {
			g.setColor(foreground);
			g.drawLine(0, y, width, y);
		}

		// draw horizontal elements: lines, month coloring, data points
		double last_bal = 0;
		int last_day = -1;

		DataPoint first = null;

		Calendar loop_date = (Calendar) start_date.clone();

		for (int day = 0; day < max_days + 10; day++) {
			g.setColor(getMonthColor(loop_date.get(Calendar.MONTH)));
			g.fillRect(getx(day) - pixels_per_day / 2, 0, pixels_per_day, height);

			g.drawLine(getx(day), 0, getx(day), height);

			// draw datapoint
			g.setColor(highlight);

			DataPoint dp = dpl.get(loop_date);

			if (dp != null) {
				g.fillOval(getx(day) - point_diam / 2, gety(dp.getBalance()) - point_diam / 2, point_diam, point_diam);

				if (last_day >= 0)
					g.drawLine(getx(last_day), gety(last_bal), getx(day), gety(dp.getBalance()));

				last_bal = dp.getBalance();
				last_day = day;

				if (first == null)
					first = dp;
			}

			loop_date.add(Calendar.DAY_OF_MONTH, 1);
		}

		// draw month names
		loop_date = (Calendar) start_date.clone();

		for (int day = 0; day < max_days + 1; day++) {

			if (loop_date.get(Calendar.DAY_OF_MONTH) == 1) {
				g.setColor(highlight);
				g.drawString(new SimpleDateFormat("MMMMMMMMMM").format(loop_date.getTime()), getx(day), 12);
			}

			loop_date.add(Calendar.DAY_OF_MONTH, 1);
		}

		// draw line to connect with past undrawn point
		if (first != null) {
			int i = dpl.past.indexOf(first) - 1;

			if (i >= 0) {
				DataPoint und = dpl.past.get(i);

				double first_val = first.getBalance();
				int first_days = Math.abs(first.minus(start_date));

				double before_val = und.getBalance();
				int before_days = Math.abs(und.minus(start_date));

				g.setColor(highlight);
				g.drawLine(getx(-before_days), gety(before_val), getx(first_days), gety(first_val));
			}
		}

		// draw a predictive average gain line
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 16 }, 0));

		g.drawLine(getx(last_day), gety(last_bal), getx(last_day + 1000), gety(last_bal + dpl.getAverageGain() * 1000));

		// draw the selection square
		double sel_bal = dpl.selected.getBalance();
		int sel_day = dpl.selected.minus(start_date);

		g2d.setStroke(new BasicStroke());

		g.drawRect(getx(sel_day) - select_diam / 2, gety(sel_bal) - select_diam / 2, select_diam, select_diam);

		// update stats graph
		stats.update(dpl);

		// for compatibility with Linux
		Toolkit.getDefaultToolkit().sync();
	}
}
