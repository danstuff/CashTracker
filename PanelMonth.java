package cashTracker;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

public class PanelMonth extends JTextArea {

	private static final long serialVersionUID = 1L;

	public PanelMonth() {
		setEditable(false);
		setFocusable(false);
		setBackground(Application.background);
		setForeground(Application.foreground);
		setFont(getFont().deriveFont(16));
		setLineWrap(true);
		setWrapStyleWord(true);
		setPreferredSize(new Dimension(200, 200));
		setText("");
	}

	private String mstr(double val) {
		return Double.toString(Math.round(val * 100) / 100.0);
	}
	
	public double[] getDayIL(DataPoint dp){
		double il[] = { 0, 0 };
		
		Pattern p = Pattern.compile("(?!=\\d)((-[\\d.]+)|([\\d.]+))");
		Matcher m = p.matcher(dp.getDescription());

		while (m.find()) {
			double val = Double.parseDouble(m.group(1));

			if (val > 0)
				il[0] += val;
			if (val < 0)
				il[1] -= val;
		}
		
		return il;
	}

	public double[] getMonthIL(DataPointList dpl) {
		//gets the total value of income and losses for a time period
		int month = dpl.selected.getDate().get(Calendar.MONTH);
		int year = dpl.selected.getDate().get(Calendar.YEAR);

		double il[] = { 0, 0 };

		//if any past days are in selected month, get their income/losses
		for (DataPoint dp : dpl.past) {

			Calendar dpd = dp.getDate();
			if (dpd.get(Calendar.MONTH) == month && dpd.get(Calendar.YEAR) == year) {

				double vals[] = getDayIL(dp);

				il[0] += vals[0];
				il[1] += vals[1];
			}
		}

		//if current day is in selected month, get its income/losses
		if (dpl.current.getDate().get(Calendar.MONTH) == month && dpl.current.getDate().get(Calendar.YEAR) == year) {

			double vals[] = getDayIL(dpl.current);

			il[0] += vals[0];
			il[1] += vals[1];
		}

		return il;
	}

	public void update(DataPointList dpl) {
		double il[] = getMonthIL(dpl);

		double balance = dpl.selected.getBalance();

		double income = il[0];
		double losses = il[1];

		String display_text = "";
		
		//month name, income, loses, and net gain
		display_text += "   Total Income:  $" + mstr(income) + "\n";
		display_text += "   Total Losses:  $" + mstr(losses) + "\n";
		
		double net_gain = income-losses;
		
		if(net_gain < 0)
			display_text += "   Net Gain:         -$" + mstr(Math.abs(income-losses)) + "\n\n";
		else
			display_text += "   Net Gain:          $" + mstr(Math.abs(income-losses)) + "\n\n";

		
		//50 - 30 - 20 estimate (useless?)
		display_text += "   Necessities:      " + "$" + mstr(income * .50) + "\n";
		display_text += "   Lifestyle:             " + "$" + mstr(income * .30) + "\n";
		display_text += "   Savings/Debts: " + "$" + mstr(income * .20) + "\n\n";

		//how long this month's losses can be sustained with no income
		display_text += "   Loss Sustainability:\n";
		display_text += "       " + ((losses == 0) ? "Infinity" : mstr(balance / (losses))) + " months\n\n";

		//6-month emergency allowance		
		double e_losspct = (balance == 0) ? 1 : losses / (balance / 6.0);
		
		display_text += "   Emergency Allowance:\n";
		display_text += "       $" + mstr(balance / 6.0) + " per month\n";
		display_text += "       ("+ mstr(e_losspct*100) + "% used so far)";

		setText(display_text);
	}
}
