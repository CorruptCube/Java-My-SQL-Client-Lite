package wetsch.mysqlclient.objects.customuiobjects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.table.JTableHeader;
public class CustomJTableHeader extends JTableHeader {
	private static final long serialVersionUID = 1L;
	
	 private static final Color COLOR_1 = new Color(255, 100, 250);
	   private static final Color COLOR_2 = new Color(0, 0, 175);
	   private static final float SIDE = 40;
	   private GradientPaint gradientPaint = new GradientPaint(0, 0, COLOR_1, SIDE,
	         SIDE, COLOR_2, true);
	public CustomJTableHeader(){
		//setDefaultRenderer(renderer);
		setOpaque(false);
		setPreferredSize(new Dimension(0,30));
		setBackground(new Color(0,0,0,0));
		setForeground(Color.white);
		
	}
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(gradientPaint);
		g2.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);

	}


}
