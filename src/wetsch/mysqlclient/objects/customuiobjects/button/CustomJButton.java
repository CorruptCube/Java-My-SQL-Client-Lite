package wetsch.mysqlclient.objects.customuiobjects.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;


public class CustomJButton extends JButton{
	private static final long serialVersionUID = 1L;
	private ButtonUI buttonUI;
	private Color normalForgroundColor = Color.white;
	public CustomJButton(String text) {
		super(text);
		buttonUI = new ButtonUI();
		setUI(buttonUI);
//		addMouseListener(new MyMouseAdapter());
		setForeground(normalForgroundColor);
		setPreferredSize(new Dimension(52,52));
		setSize(getPreferredSize());
		setHorizontalAlignment(SwingConstants.CENTER);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFont(getFont().deriveFont(9f));
		setMargin(new Insets(1, 1, 1, 1));
	 
	}
	

	private class ButtonUI extends BasicButtonUI{
		private BufferedImage normalImage;

		public ButtonUI(){
			try{
				URL normalURL = getClass().getResource("/button-icons/button-templet.png");
				normalImage = ImageIO.read(normalURL);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		public void paint(Graphics g, JComponent c) {
	        
//			if(isHover)
//				g.drawImage(hoverImage, 0, 0, hoverImage.getWidth(),hoverImage.getHeight(),null);
//			else
				g.drawImage(normalImage, 0, 0, normalImage.getWidth(), normalImage.getHeight(),null);
			c.setSize(normalImage.getWidth(), normalImage.getHeight());
			super.paint(g, c);
		}

		@Override
		protected void paintButtonPressed(Graphics g, AbstractButton b) {
			g.setColor(new Color(0,0,255,50));
			if(b.getModel().isPressed())
				g.fillOval(0, 0, getWidth(), getHeight());
			else
				g.setColor(new Color(0,0,0,0));
			g.fillOval(0, 0, getWidth(), getHeight());
			super.paintButtonPressed(g, b);
		}

	}
	
}
