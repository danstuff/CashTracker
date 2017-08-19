package cashTracker;

import java.util.ArrayList;
import java.util.Calendar;

public class DataList {
	public String name;
	
	public ArrayList<DataPoint> past;
	public DataPoint current, selected;

	public DataList(String name) {
		this.name = name;
		past = new ArrayList<>();
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

		avg_gain /= past.size();

		return Math.round(avg_gain * 100.0) / 100.0;
	}

	public DataPoint get(Calendar date) {
		for (DataPoint p : past) {
			if (p.equals(date))
				return p;
		}

		if (current.equals(date))
			return current;

		return null;
	}

	public void updateCurrent(double mod, String additional_desc) {
		current.update(mod, additional_desc);
		selected = current;
	}

	public void resetCurrent() {
		if (past.size() <= 0)
			current.reset(0);
		else
			current.reset(past.get(past.size() - 1).getBalance());

		selected = current;
	}

	public void forward() {
		if (past.contains(selected)) {
			int i = past.indexOf(selected);

			if (i + 1 < past.size())
				selected = past.get(i + 1);
			else
				selected = current;
		}
	}

	public void back() {
		if (selected == current && past.size() > 0) {
			selected = past.get(past.size() - 1);
		} else if (past.contains(selected)) {
			int i = past.indexOf(selected);

			if (i > 0)
				selected = past.get(i - 1);
		}
	}
}
