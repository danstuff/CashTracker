package cashTracker;

import java.awt.Color;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DataPoint {
	public static Calendar minimum_date;
	
	public static final String regex = Character.toString((char) (181));
	public static final String newline_rep = Character.toString((char) (182));

	private Calendar date;

	private double balance;
	
	private String description;

	Color color;
	int x, y;

	public static void init(){
		minimum_date = Calendar.getInstance();
		minimum_date.add(Calendar.DAY_OF_MONTH, -Graph.max_days / 2);
	}
	
	public DataPoint(double i_balance, Calendar mdate) {
		if (mdate != null)
			date = (Calendar) mdate.clone();
		else
			date = Calendar.getInstance();
		
		clean();
		
		balance  = i_balance;

		description = "";

		update(i_balance, 0, "");
	}

	public DataPoint(double i_balance, String parse) {
		String[] sline = parse.split(regex);

		if (sline.length >= 5) {
			int day = Integer.parseInt(sline[0]);
			int month = Integer.parseInt(sline[1]);
			int year = Integer.parseInt(sline[2]);

			double balance = Double.parseDouble(sline[3]);
			String description = sline[4];

			Calendar date = new GregorianCalendar(year, month, day);

			this.date = (Calendar) date.clone();

			this.balance = balance;

			this.description = description.replace(newline_rep, "\n");

			update(i_balance, 0, "");
		}
	}
	
	private void clean(){
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);
		int day = date.get(Calendar.DAY_OF_MONTH);
		
		date.set(year, month, day, 0, 0, 0);
	}

	public boolean initialized() {
		return (date != null && description != null);
	}

	public boolean isCurrent() {
		Calendar curr = Calendar.getInstance();

		return (date.get(Calendar.DAY_OF_YEAR) == curr.get(Calendar.DAY_OF_YEAR)
				&& date.get(Calendar.YEAR) == curr.get(Calendar.YEAR));
	}

	public boolean isBefore(Calendar bdate) {
		return (date.get(Calendar.DAY_OF_YEAR) < bdate.get(Calendar.DAY_OF_YEAR)
				|| date.get(Calendar.YEAR) < bdate.get(Calendar.YEAR));
	}

	public void update(double i_balance, double mod, String additional_desc) {
		balance += mod;

		// if this update isn't empty, add it to description
		if (mod != 0 && !additional_desc.equals("")) {
			mod = Math.round(mod * 100) / 100.0;

			if (mod > 0)
				description += "   +" + Double.toString(mod) + " " + additional_desc + "\n";
			else
				description += "    " + Double.toString(mod) + " " + additional_desc + "\n";
		}

		// get number of days since the start date
		Calendar temp = (Calendar) DataPoint.minimum_date.clone();

		int days_since_minimum = 0;

//		while (temp.get(Calendar.YEAR) < date.get(Calendar.YEAR)) {
//			temp.add(Calendar.YEAR, 1);
//			days_since_minimum += temp.getActualMaximum(Calendar.DAY_OF_YEAR);
//		}
//		while (temp.get(Calendar.DAY_OF_YEAR) < date.get(Calendar.DAY_OF_YEAR)) {
//			temp.add(Calendar.DAY_OF_YEAR, 1);
//			days_since_minimum += 1;
//		}
		
		while (temp.before(date)) {
			temp.add(Calendar.DAY_OF_YEAR, 1);
			days_since_minimum++;
		}

		// set color		
		if (balance - i_balance > 0) {
			int x = (int) (balance - i_balance);
			int y = (int) (255 - Math.pow(Math.E, -0.002*x + Math.log(255)));

			color = new Color(0, y, 255 - y);
		} else {
			int x = (int) (i_balance - balance);
			int y = (int) (255 - Math.pow(Math.E, -0.002*x + Math.log(255)));

			color = new Color(y, 0, 255 - y);
		}

		// set position
		x = days_since_minimum * Graph.pixels_per_day;
		y = Graph.height - ((int) balance * Graph.pixels_per_thousand / 1000);
	}

	public void reset(double i_balance) {
		balance = i_balance;
		description = "";

		update(i_balance, 0, "");
	}

	public double getBalance() {
		return balance;
	}

	public String getStr(boolean readable) {
		String str = "";

		if (readable) {
			// output a the point's date in a readable format
			DateFormatSymbols dfs = new DateFormatSymbols();

			String sdate = dfs.getMonths()[date.get(Calendar.MONTH)] + " ";
			sdate += date.get(Calendar.DAY_OF_MONTH) + ", ";
			sdate += date.get(Calendar.YEAR);

			String sbalance = Double.toString(Math.round(balance * 100) / 100.0);

			str = sdate + "\n" + "Balance: $" + sbalance + "\n" + description;
		} else {
			// output all of the point's data in a saveable format
			str += date.get(Calendar.DAY_OF_MONTH) + regex;
			str += date.get(Calendar.MONTH) + regex;
			str += date.get(Calendar.YEAR) + regex;

			str += balance + regex;

			String sdesc = description.replace("\n", newline_rep);

			if (sdesc.equals(""))
				sdesc = " ";

			str += sdesc + regex + "\n";
		}

		return str;
	}
}
