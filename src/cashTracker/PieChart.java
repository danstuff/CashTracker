package cashTracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

class Slice {
	int value;
	Color color;
	String name;

	public Slice(int value, Color color, String name) {
		this.value = value;
		this.color = color;
		this.name = name;
	}
}

public class PieChart extends JPanel {

	private static final long serialVersionUID = 1L;

	private Slice slices[];

	public PieChart() {
		slices = new Slice[7];

		for(int i = 0; i < slices.length; i++){
			slices[i] = new Slice(10, Color.WHITE, "blank");
		}
		
		slices[0].color = Color.RED;
		slices[1].color = Color.BLUE;
		slices[2].color = Color.GREEN;
		slices[3].color = Color.BLACK;
		slices[4].color = Color.MAGENTA;
		slices[5].color = Color.YELLOW;
		slices[6].color = Color.CYAN;
		
		setPreferredSize(new Dimension(200,100));
		
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
		setFont(font);
		
	}

	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		super.paintComponent(g);
		

		g2d.setColor(new Color(0, 0, 0, 100));
		g2d.fillArc(1, 1, 98, 98, 0, 360);
		
		double total = 0;
		
		for(Slice slice : slices){
			total += slice.value;
		}
		
		double current_value = 0;
		int start_angle = 0;
		
		for(int i = 0; i < slices.length; i++){
			start_angle = (int) (current_value * 360 / total);
			
			int arc_angle = (int) (slices[i].value * 360 / total);
			
			g2d.setColor(slices[i].color);
			g2d.fillArc(0, 0, 100, 100, start_angle, arc_angle);
			
			current_value += slices[i].value;
			
			if(slices[i].name != "")
				g2d.drawString(slices[i].name + " " + Integer.toString((int) (slices[i].value*100/total)) + "%", 102, i*13+13);
		}
	}
	
	public void update(int[] values, String[] names) {
		for (int i = 0; i < slices.length; i++) {
			if (i >= values.length || i >= names.length){
				slices[i].value = 0;
				slices[i].name = "";
			}
			else{
				slices[i].name = names[i];
				slices[i].value = values[i];
			}
		}
	}
}
