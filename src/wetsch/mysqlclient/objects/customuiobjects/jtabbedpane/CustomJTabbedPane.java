package wetsch.mysqlclient.objects.customuiobjects.jtabbedpane;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class CustomJTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	

	public CustomJTabbedPane() {
		setOpaque(false);
		setTabPlacement(JTabbedPane.LEFT);
		setForeground(Color.white);
		setUI(new MyTabedUI());
	}

	private class MyTabedUI extends BasicTabbedPaneUI {
		ImageIcon normal = new ImageIcon(getClass().getResource("/button-icons/tab-normal.png"));
		ImageIcon selected = new ImageIcon(getClass().getResource("/button-icons/tab-selected.png"));

		
		
		@Override
		protected void paintTabArea(Graphics g, int tabPlacement,
				int selectedIndex) {
			BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D) img.createGraphics();
		   	Color COLOR_1 = new Color(0, 0,25);
			Color COLOR_2 = new Color(0,0,50);
			float SIDE = 40;
			GradientPaint gradientPaint = new GradientPaint(0, 0, COLOR_1, SIDE, SIDE, COLOR_2, true);
			g2.setPaint(gradientPaint);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(img, 0, 0, getWidth(),getHeight(), null);
			super.paintTabArea(g, tabPlacement, selectedIndex);
		}

		@Override
		protected void paintTabBackground(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected) {

			Graphics2D g2 = (Graphics2D) g;
			if (isSelected)
				g2.drawImage(selected.getImage(), x, y,
						selected.getIconWidth(), selected.getIconHeight(), null);
			else
				g2.drawImage(normal.getImage(), x, y, normal.getIconWidth(),
						normal.getIconHeight(), null);
		}

		@Override
		protected int calculateTabHeight(int tabPlacement, int tabIndex,
				int fontHeight) {
			return normal.getIconHeight();
		}


		@Override
		protected int calculateTabWidth(int tabPlacement, int tabIndex,
				FontMetrics metrics) {
			return normal.getIconWidth();
		}

		@Override
		protected void paintFocusIndicator(Graphics g, int tabPlacement,
				Rectangle[] rects, int tabIndex, Rectangle iconRect,
				Rectangle textRect, boolean isSelected) {}
	}

}
