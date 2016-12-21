package cashTracker;

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

	public static void init() {
		minimum_date = Calendar.getInstance();
		minimum_date.add(Calendar.DAY_OF_MONTH, -Graph.max_days / 2);
	}

	public DataPoint(double i_balance, Calendar mdate) {
		if (mdate != null)
			date = (Calendar) mdate.clone();
		else
			date = Calendar.getInstance();
		
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);
		int day = date.get(Calendar.DAY_OF_MONTH);

		date.set(year, month, day, 0, 0, 0);
		
		balance = i_balance;

		description = "";

		update(0, "");
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

			update(0, "");
		}
	}

	public boolean initialized() {
		return (date != null && description != null);
	}
	
	public boolean equals(Calendar cdate){
		return (date.get(Calendar.DAY_OF_YEAR) == cdate.get(Calendar.DAY_OF_YEAR)
				&& date.get(Calendar.YEAR) == cdate.get(Calendar.YEAR));
	}
	
	public int minus(Calendar cdate){
		return (int) Math.round((date.getTime().getTime() - cdate.getTime().getTime()) / (1000.0 * 60.0 * 60.0 * 24.0));
	}

	public void update(double mod, String additional_desc) {
		balance += mod;

		// if this update isn't empty, add it to description
		if (mod != 0 && !additional_desc.equals("")) {
			mod = Math.round(mod * 100) / 100.0;

			if (mod > 0)
				description += "   +" + Double.toString(mod) + " " + additional_desc + "\n";
			else
				description += "    " + Double.toString(mod) + " " + additional_desc + "\n";
		}
	}

	public void reset(double i_balance) {
		balance = i_balance;
		description = "";

		update(0, "");
	}

	public double getBalance() {
		return balance;
	}

	public Calendar getDate() {
		return (Calendar) date.clone();
	}
	
	public String getDescription(){
		return description;
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

			str = sdate + "\n\n" + "Balance: $" + sbalance + "\n" + description;
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
