package cashTracker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class DataFile {
	private String file, file_contents;

	public DataFile(String file) {
		this.file = file;
		file_contents = "";
	}

	public ArrayList<DataList> load() {
		ArrayList<DataList> dpls = new ArrayList<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			double last_bal = 0;

			String line = "";

			while ((line = br.readLine()) != null) {
				// if the line contains a setting, set it
				if (line.contains("days_on_graph=")) {
					String[] split = line.split("=");

					if (split.length >= 2) {
						Graph.max_days = Integer.parseInt(split[1]);
						Graph.recalculate();
					}
				} else if (line.contains("balance_on_graph=")) {
					String[] split = line.split("=");

					if (split.length >= 2) {
						Graph.max_balance = Integer.parseInt(split[1]);
						Graph.recalculate();
					}
				} else if (line.contains("list=")) {

					String[] split = line.split("=");

					if (split.length >= 2) {
						dpls.add(new DataList(split[1]));
					}
				}

				// try to make a data point out of the line
				DataPoint dp = new DataPoint(last_bal, line);

				if (dp.initialized() && dpls.size() > 0) {
					// add the created datapoint to the most recent dpl
					if (dp.equals(Calendar.getInstance())) {
						dpls.get(dpls.size() - 1).current = dp;
					} else {
						dpls.get(dpls.size() - 1).past.add(dp);
					}
				}
				
				file_contents += line + "\n";
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(DataList list : dpls){
			if(list.current == null && list.past.size() > 0){
				double last_bal = list.past.get(list.past.size()-1).getBalance();
				list.current = new DataPoint(last_bal, Calendar.getInstance());
			}else if(list.current == null){
				list.current = new DataPoint(0, Calendar.getInstance());			
			}
			
			list.selected = list.current;
		}

		return dpls;
	}

	public void save(ArrayList<DataList> dpls) {
		// reopen the file and write to it
		try {
			PrintWriter out = new PrintWriter(file);

			// get the basic info from the top
			String[] split = file_contents.split("points");

			String header = split[0] + "points";

			out.flush();

			out.print(header + "\n");

			// output the contents of each dpl
			for (DataList list : dpls) {
				out.print("list=" + list.name + "\n");

				for (DataPoint dp : list.past) {
					out.print(dp.getStr(false) + "\n");
				}

				out.print(list.current.getStr(false) + "\n");
			}

			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void backup() {
		// reopen the file and write to it
		try {
			PrintWriter out = new PrintWriter(file + ".bak");

			out.flush();
			out.print(file_contents);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getStr() {
		return file_contents;
	}
}
