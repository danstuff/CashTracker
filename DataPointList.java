package cashTracker;

import java.util.ArrayList;
import java.util.Calendar;

public class DataPointList {
	public static double allowance_fac;
	public static double allowance_days;
	
	public ArrayList<DataPoint> past, future;
	public DataPoint current, selected;

	public DataPointList() {
		past = new ArrayList<>();
		future = new ArrayList<>();
	}

	public double getAverageGain() {
		if (past.size() <= 0)
			return 0;

		double avg_gain = 0;

		DataPoint last = past.get(0);

		for (DataPoint day : past) {
			avg_gain += day.getBalance() - last.getBalance();

			last = day;
		}

		if (last != null)
			avg_gain += current.getBalance() - last.getBalance();

		avg_gain /= past.size() + 1;

		return avg_gain;
	}

	public double getAllowance() {
		double balance_fac = current.getBalance()/allowance_days;
		double gain_fac = getAverageGain()*allowance_fac;
		
		return balance_fac + gain_fac;
	}

	public void predict() {
		future.clear();

		// predict future fund increase
		Calendar maximum_date = (Calendar) DataPoint.minimum_date.clone();
		maximum_date.add(Calendar.DAY_OF_MONTH, Graph.max_days);

		Calendar date = Calendar.getInstance();

		double avg_gain = getAverageGain();
		double last = current.getBalance();

		while (!date.after(maximum_date)) {
			date.add(Calendar.DAY_OF_MONTH, 1);

			DataPoint dp = new DataPoint(last, date);
			dp.update(last, avg_gain, "Prediction");

			future.add(dp);

			last = dp.getBalance();
		}
	}

	public void update(double mod, String additional_desc) {
		double last = 0;

		if (past.size() > 0)
			last = past.get(past.size() - 1).getBalance();

		current.update(last, mod, additional_desc);

		predict();

		selected = current;
	}

	public void reset() {
		if (past.size() <= 0)
			current.reset(0);
		else
			current.reset(past.get(past.size() - 1).getBalance());

		predict();

		selected = current;
	}

	public void forward() {
		// shift the selected day "pointer" to the right
		if (future.contains(selected)) {
			int i = future.indexOf(selected);

			if (i + 1 < future.size())
				selected = future.get(i + 1);
		} else if (selected == current && future.size() > 0) {
			selected = future.get(0);
		} else if (past.contains(selected)) {
			int i = past.indexOf(selected);

			if (i + 1 < past.size())
				selected = past.get(i + 1);
			else
				selected = current;
		}
	}

	public void back() {
		// shift the selected day "pointer" to the left
		if (future.contains(selected)) {
			int i = future.indexOf(selected);

			if (i > 0)
				selected = future.get(i - 1);
			else
				selected = current;
		} else if (selected == current && past.size() > 0) {
			selected = past.get(past.size() - 1);
		} else if (past.contains(selected)) {
			int i = past.indexOf(selected);

			if (i > 0)
				selected = past.get(i - 1);
		}
	}
}
