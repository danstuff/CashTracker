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

	public double[] getMonthIL(DataPointList dpl, int months_before) {
		Calendar date = dpl.current.getDate();
		date.add(Calendar.MONTH, -months_before);

		int month = date.get(Calendar.MONTH);
		int year = date.get(Calendar.YEAR);

		double il[] = { 0, 0 };

		for (DataPoint dp : dpl.past) {

			Calendar dpd = dp.getDate();
			if (dpd.get(Calendar.MONTH) == month && dpd.get(Calendar.YEAR) == year) {

				Pattern p = Pattern.compile("(?!=\\d)((-[\\d.]+)|([\\d.]+))");
				Matcher m = p.matcher(dp.getDescription());

				while (m.find()) {
					double val = Double.parseDouble(m.group(1));

					if (val > 0)
						il[0] += val;
					if (val < 0)
						il[1] -= val;
				}
			}
		}

		if (dpl.current.getDate().get(Calendar.MONTH) == month && dpl.current.getDate().get(Calendar.YEAR) == year) {
			Pattern p = Pattern.compile("(?!=\\d)((-[\\d.]+)|([\\d.]+))");
			Matcher m = p.matcher(dpl.current.getDescription());

			while (m.find()) {
				double val = Double.parseDouble(m.group(1));

				if (val > 0)
					il[0] += val;
				if (val < 0)
					il[1] -= val;
			}
		}

		return il;
	}

	public void update(DataPointList dpl, int months_before) {
		double il[] = getMonthIL(dpl, months_before);

		double balance = dpl.current.getBalance();

		double income = il[0];
		double losses = il[1];

		String sincome = "";
		
		if(months_before == 0){
			sincome += "\nThis Month- \n";
		}
		else if(months_before == 1){
			sincome += "\nLast Month- \n";			
		}
		else{
			sincome += "\n" + months_before + "Months Ago- \n";
		}
		
		sincome += "   Total Income:  $" + mstr(income) + "\n";

		sincome += "   Total Losses: -$" + mstr(losses) + "\n\n";

		if (months_before <= 0) {
			double il_p[] = getMonthIL(dpl, months_before - 1);

			double losses_p = il_p[1];

			double losspct = (losses_p == 0) ? 1 : losses / losses_p;

			sincome += "   Monthly Fund Use:\n";
			sincome += "       $" + mstr(losses) + " / $" + mstr(losses_p) + " = " + mstr(losspct*100) + "% \n\n";

			double e_losspct = (balance == 0) ? 1 : losses / (balance / 6.0);

			sincome += "   Emergency Fund Use:\n";
			sincome += "       $" + mstr(losses) + " / $" + mstr(balance / 6.0) + " = " + mstr(e_losspct*100) + "% \n";

		} else {
			sincome += "   Necessities: " + "$" + mstr(income * .50) + "\n";
			sincome += "   Lifestyle: " + "$" + mstr(income * .30) + "\n";
			sincome += "   Savings/Debts: " + "$" + mstr(income * .20) + "\n\n";

			sincome += "   Loss Sustainability:\n";
			sincome += "       " + mstr(balance / (losses)) + " months\n\n";

			sincome += "   Emergency Allowance:\n";
			sincome += "       $" + mstr(balance / 6.0) + " per month\n";
		}

		setText(sincome);
	}
}
