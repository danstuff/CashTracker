package cashTracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

public class CashTracker {

	public static final String add_categories[] = { "Job", "Gift", "Other Add" };
	public static final String sub_categories[] = { "Groceries", "Housing", "Utility", "Clothes", "Lifestyle", "Taxes",
			"Other Sub" };

	Readout day, day_title, month, month_title;
	DayModifer day_modifier;

	PieChart pie_additions, pie_subtractions, pie_tds;

	DataFile data_file;
	ArrayList<DataList> data_lists;
	ArrayList<Graph> graphs;

	JTabbedPane tabs;

	public CashTracker(Application app) {
		// left side
		day_title = new Readout(24);
		day_title.update("CashTracker");

		day = new Readout(10);

		day_modifier = new DayModifer(app);

		// center
		tabs = new JTabbedPane();

		// right side
		month_title = new Readout(20);
		month_title.update("This Month");

		month = new Readout(10);

		pie_additions = new PieChart();

		pie_subtractions = new PieChart();

		pie_tds = new PieChart();
	}

	public void setup() {
		data_file = new DataFile("cashtracker.dat");

		data_lists = data_file.load();
		data_file.backup();

		graphs = new ArrayList<>();

		for (DataList list : data_lists) {
			Graph graph = new Graph(list);
			graphs.add(graph);

			tabs.addTab(list.name + " (" + list.selected.getBalance() + ")", graph);
		}

		if (data_lists.size() > 0)
			month.update(data_lists.get(0));
	}

	public void updateGraphics() {
		int tab = tabs.getSelectedIndex();

		day.update(data_lists.get(tab).selected);

		month.update(data_lists.get(tab));
		month_title.update(
				new SimpleDateFormat("MMMMMMMMMM, YYYY").format(data_lists.get(tab).selected.getDate().getTime()));
	}

	public void updatePieCharts() {
		int tab = tabs.getSelectedIndex();
		DataList dpl = data_lists.get(tab);

		int[] add_values = new int[add_categories.length];
		int[] sub_values = new int[sub_categories.length];
		int[] td_values = new int[2];

		int month = dpl.selected.getDate().get(Calendar.MONTH);
		int year = dpl.selected.getDate().get(Calendar.YEAR);

		for (DataPoint dp : dpl.past) {

			// if past day is within the month
			Calendar dpd = dp.getDate();
			if (dpd.get(Calendar.MONTH) == month && dpd.get(Calendar.YEAR) == year) {

				// get its add and sub categories and add them to the lists
				String[] categories = dp.getCategories();
				double[] values = dp.getValues();
				boolean[] tds = dp.getTDs();

				for (int i = 0; i < categories.length && i < values.length; i++) {
					if (tds[i])
						td_values[0] += Math.round(values[i]);
					else
						td_values[1] += Math.round(values[i]);

					for (int j = 0; j < add_categories.length; j++) {
						if (categories[i].equals(add_categories[j])) {
							add_values[j] += Math.round(values[i]);
						}
					}
					for (int j = 0; j < sub_categories.length; j++) {
						if (categories[i].equals(sub_categories[j])) {
							sub_values[j] += Math.round(values[i]);
						}
					}
				}
			}
		}

		// if current day is within the month
		Calendar dpd = dpl.current.getDate();
		if (dpd.get(Calendar.MONTH) == month && dpd.get(Calendar.YEAR) == year) {

			// get its add and sub categories and add them to the lists
			String[] categories = dpl.current.getCategories();
			double[] values = dpl.current.getValues();
			boolean[] tds = dpl.current.getTDs();

			for (int i = 0; i < categories.length && i < values.length; i++) {
				if (tds[i])
					td_values[0] += Math.round(values[i]);
				else
					td_values[1] += Math.round(values[i]);

				for (int j = 0; j < add_categories.length; j++) {
					if (categories[i].equals(add_categories[j])) {
						add_values[j] += Math.round(values[i]);
					}
				}
				for (int j = 0; j < sub_categories.length; j++) {
					if (categories[i].equals(sub_categories[j])) {
						sub_values[j] += Math.round(values[i]);
					}
				}
			}
		}

		// update the pie charts
		pie_additions.update(add_values, add_categories);
		pie_subtractions.update(sub_values, sub_categories);
		pie_tds.update(td_values, new String[] { "Deductible", "Non-Ded." });

		pie_additions.repaint();
		pie_subtractions.repaint();
		pie_tds.repaint();
	}

	public void update() {
		updateGraphics();
		updatePieCharts();
		
		int tab = tabs.getSelectedIndex();
		graphs.get(tab).repaint();

		data_file.save(data_lists);

		day_modifier.clear();
	}

	public void actionAdd(Application app) {
		int tab = tabs.getSelectedIndex();

		// have the user select a category
		String category = "";

		if (day_modifier.get() > 0) {
			String question = "Enter a category for this addition of $" + Double.toString(Math.abs(day_modifier.get()));

			category = (String) JOptionPane.showInputDialog(app, question, "Enter Category", JOptionPane.PLAIN_MESSAGE,
					null, add_categories, "Other");
		} else {
			String question = "Enter a category for this expense of $" + Double.toString(Math.abs(day_modifier.get()));

			category = (String) JOptionPane.showInputDialog(app, question, "Enter Category", JOptionPane.PLAIN_MESSAGE,
					null, sub_categories, "Other");
		}

		// ask the user for more information
		String description = (String) JOptionPane.showInputDialog(app,
				"Provide details here (Add tag TD if tax deductible):", "Additional information",
				JOptionPane.PLAIN_MESSAGE, null, null, "");

		// update and save
		data_lists.get(tab).updateCurrent(day_modifier.get(),
				(description == null) ? "" : description + " | " + category);

		tabs.setTitleAt(tab, data_lists.get(tab).name + " (" + data_lists.get(tab).selected.getBalance() + ")");

		update();

	}

	public void actionReset() {
		int tab = tabs.getSelectedIndex();

		data_lists.get(tab).resetCurrent();

		update();
	}

	public void actionLeft() {
		int tab = tabs.getSelectedIndex();

		data_lists.get(tab).back();
		update();
	}

	public void actionRight() {
		int tab = tabs.getSelectedIndex();

		data_lists.get(tab).forward();
		update();
	}
}
