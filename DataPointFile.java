package cashTracker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class DataPointFile {
	private static final String file = "cashtracker.dat";

	private String file_contents;
	private ArrayList<DataPoint> file_list;

	public DataPointFile() {
		file_contents = "";
		file_list = new ArrayList<>();

		load();
	}

	public void load() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			double last = 0;

			String line = "";

			while ((line = br.readLine()) != null) {
				if(line.contains("days_on_graph=")){
					String[] split = line.split("=");
					
					if(split.length >= 2){
						Graph.max_days = Integer.parseInt(split[1]);
						Graph.recalculate();
					}
				}
				else if(line.contains("balance_on_graph=")){
					String[] split = line.split("=");
					
					if(split.length >= 2){
						Graph.max_balance = Integer.parseInt(split[1]);
						Graph.recalculate();
					}
				}
				else if(line.contains("allowance_generosity=")){
					String[] split = line.split("=");
					
					if(split.length >= 2){
						DataPointList.allowance_fac = Double.parseDouble(split[1]);
					}
				}
				else if(line.contains("allowance_sustain_days=")){
					String[] split = line.split("=");
					
					if(split.length >= 2){
						DataPointList.allowance_days = Double.parseDouble(split[1]);
					}
				}
				
				DataPoint dp = new DataPoint(last, line);

				if (dp.initialized()) {
					if (!dp.isCurrent()) {
						file_contents += dp.getStr(false);
					}

					if (!dp.isBefore(DataPoint.minimum_date)) {
						file_list.add(dp);
						last = dp.getBalance();
					}
				}
				else{
					file_contents += line + "\n";
				}
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save(DataPoint current) {
		// reopen the file and write to it
		try {
			PrintWriter out = new PrintWriter(file);

			out.flush();

			out.print(file_contents);
			out.print(current.getStr(false));

			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void backup(DataPoint current) {
		// reopen the file and write to it
		try {
			PrintWriter out = new PrintWriter(file + ".bak");

			out.flush();

			out.print(file_contents);
			out.print(current.getStr(false));

			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getStr() {
		return file_contents;
	}

	public ArrayList<DataPoint> getList() {
		return file_list;
	}

	public DataPoint clipListCurrent() {
		if (file_list.size() <= 0)
			return new DataPoint(0, (Calendar) null);

		DataPoint last = file_list.get(file_list.size() - 1);
		DataPoint current;

		if (last.isCurrent()) {
			current = last;
			file_list.remove(last);
		} else {
			current = new DataPoint(last.getBalance(), (Calendar) null);
		}

		return current;
	}
}
