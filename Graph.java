package cashTracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Graph extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int width = 730;
	public static final int height = 500;

	private static final Color background = new Color(240, 250, 250);
	private static final Color foreground = new Color(220, 230, 230);
	private static final Color highlight = new Color(180, 200, 200);

	private static final int point_radius = 3;
	private static final int select_radius = point_radius*2;
	
	public static int max_days;
	public static int max_balance;

	public static int pixels_per_day;
	public static int pixels_per_thousand;
	
	private DataPointFile dpf;
	private DataPointList dpl;

	private StatsPanel stats;

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
	
	public static void recalculate(){
		pixels_per_day = width / max_days;
		pixels_per_thousand = (int) ((double) height / max_balance * 1000);
		
		DataPoint.init();
	}

	public Graph(StatsPanel stats) {
		this.stats = stats;

		// set up the dpf and the dpl and link them together
		dpf = new DataPointFile();
		dpl = new DataPointList();
		
		dpl.current = dpf.clipListCurrent();
		dpl.selected = dpl.current;

		dpl.past = dpf.getList();

		dpf.backup(dpl.current);
		dpl.predict();

		// set up key bindings
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
		getActionMap().put("left", left);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
		getActionMap().put("right", right);

		setPreferredSize(new Dimension(width, height));
		setBackground(background);
		setFocusable(true);
	}

	public void drawPoints(Graphics g, DataPoint o, DataPoint e, boolean nocolor, ArrayList<DataPoint> points) {
		Graphics2D g2d = (Graphics2D) g;

		// connect all points with dots and lines
		for (int i = 0; i < points.size(); i++) {
			DataPoint n = points.get(i);

			if (o != null) {

				if (nocolor) {
					g2d.setColor(highlight);
				} else {
					GradientPaint gradient = new GradientPaint(o.x, o.y, o.color, n.x, n.y, n.color);
					g2d.setPaint(gradient);
				}

				g2d.drawLine(o.x, o.y, n.x, n.y);
			}

			if (!nocolor)
				g2d.setColor(n.color);

			g2d.fillOval(n.x - point_radius, n.y - point_radius, point_radius * 2, point_radius * 2);

			o = n;
		}

		// add end cap on if it was specified
		if (e != null && points.size() > 0) {
			DataPoint l = points.get(points.size() - 1);

			if (nocolor) {
				g2d.setColor(highlight);
			} else {
				GradientPaint gradient = new GradientPaint(l.x, l.y, l.color, e.x, e.y, e.color);
				g2d.setPaint(gradient);
			}

			g.drawLine(l.x, l.y, e.x, e.y);

			if (!nocolor)
				g.setColor(e.color);

			g.fillOval(e.x - point_radius, e.y - point_radius, point_radius * 2, point_radius * 2);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		requestFocus();

		// draw the lines on the graph
		g.setColor(foreground);

		for (int y = 0; y < height; y += pixels_per_thousand) {
			g.drawLine(0, y, width, y);
		}

		// draw the selection square
		g.setColor(highlight);
		g.drawRect(dpl.selected.x - select_radius, dpl.selected.y - select_radius, select_radius * 2,
				select_radius * 2);

		// draw all points from the DataPointList
		drawPoints(g, null, dpl.current, false, dpl.past);
		drawPoints(g, dpl.current, null, true, dpl.future);

		// update the stats sidebar
		stats.update(dpl.selected, dpl.getAllowance());

		// for compatibility with Linux
		Toolkit.getDefaultToolkit().sync();
	}

	public void update(double mod, String additional_desc) {
		dpl.update(mod, additional_desc);
		dpf.save(dpl.current);
		
		repaint();
	}

	public void reset() {
		dpl.reset();
		dpf.save(dpl.current);

		repaint();
	}
}
