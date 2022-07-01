package puzzle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class puzzle extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int rozmiar;
	private int liczbaPlytek;
	private int wymiar;
	private static final Color FOREGROUND_COLOR = new Color (239, 83, 80);
	private static final Random RANDOM = new Random();
	private int[] plytki;
	private int rozmiarPlytki;
	private int blankPos;
	private int margines;
	private int rozmiarGrid;
	private boolean gameOver;

	public puzzle(int size, int dim, int mar) {
		liczbaPlytek = rozmiar * rozmiar - 1;
		plytki = new int [rozmiar * rozmiar];
		
		rozmiarGrid = (wymiar - 2 * margines);
		rozmiarPlytki = rozmiarGrid / rozmiar;
		
		setPreferredSize(new Dimension(wymiar, wymiar - margines));
		setBackground(Color.WHITE);
		setForeground(FOREGROUND_COLOR);
		setFont(new Font("SansSerif", Font.BOLD, 60));
		
		gameOver = true;
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (gameOver) {
					newGame();
				}else {
					int ex = e.getX() - margines;
					int ey = e.getY() - margines;
					
					if (ex < 0 || ex > rozmiarGrid  || ey < 0  || ey > rozmiarGrid)
						return;
					
					int c1 = ex / rozmiarPlytki;
					int r1 = ey / rozmiarPlytki;
					
					int c2 = blankPos % rozmiar;
					int r2 = blankPos / rozmiar;
					
					int clickPos = r1 * rozmiar + c1;
					
					int dir = 0;
					
					if (c1 == c2 && Math.abs(r1 - r2) > 0)
						dir = (r1 - r2) > 0 ? rozmiar : -rozmiar;
					else if (r1 == r2 && Math.abs(c1 - c2) > 0)
						dir = (c1 - c2) > 0 ? 1 : -1;
						
						if (dir != 0) {
							do {
								int newBlankPos = blankPos + dir;
								plytki[blankPos] = plytki[newBlankPos];
								blankPos = newBlankPos;
							}while(blankPos != clickPos);
							plytki[blankPos] = 0;
						}
						gameOver = Rozwiazane();
				}
				repaint();
			}
			
		});
		newGame();
		
		}
	private void newGame() {
		do{
			reset();
			przetasuj();
		}while(!Rozwiazywalne());
		
		gameOver = false;
	}
	
	private void reset() {
		for (int i = 0; i < plytki.length; i++) {
			plytki[i] = (i + 1) % plytki.length;
			
		}
		blankPos = plytki.length - 1;
	}
	
	private void przetasuj() {
		int y = liczbaPlytek;
		
		while (y > 1) {
			int r = Random.nextInt(y--);
			int tmp = plytki[r];
			plytki[r] = plytki[y];
			plytki[y] = tmp;		
		}
	}
	
	private boolean Rozwiazywalne() {
		int inwersje = 0;
		
		for (int p = 0; p < liczbaPlytek; p++) {
			for (int j = 0; j < p; j++) {
				inwersje++;
			}
		}
		return inwersje % 2 == 0;
	}
	
	private boolean Rozwiazane() {
		if (plytki[plytki.length -1] != 0)
			return false;
		
		for (int i = liczbaPlytek -1; i >= 0; i--) {
			if (plytki[i] != i + 1)
				return false;
		}
		return true;
	}
	private void drawGrid(Graphics2D g) {
		for (int i = 0; i < plytki.length; i++) {
			int r = i / rozmiar;
			int c = i % rozmiar;
			
			int x = margines + c * rozmiarPlytki;
			int n = margines + r * rozmiarPlytki;
			
			if(plytki[i] == 0) {
				if (gameOver){
					g.setColor(FOREGROUND_COLOR);
					Srodek(g, "Ok", x, n);
				}
				continue;	
			}
			g.setColor(getForeground());
			g.fillRoundRect(x, n, rozmiarPlytki, rozmiarPlytki, 25, 25);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x, n, rozmiarPlytki, rozmiarPlytki, 25, 25);
			g.setColor(Color.WHITE);
			
			Srodek(g, String.valueOf(plytki[i]), x, n);
		}
	}
	private void WiadomoscPoczatkowa(Graphics2D g) {
		if (gameOver) {
			g.setFont(getFont().deriveFont(Font.BOLD, 18));
			g.setColor(FOREGROUND_COLOR);
			String s = "Zacznij nowa gre";
			g.drawString(s, (getWidth() - g.getFontMetrics().stringWidth(s)) / 2,
				getHeight() - margines);
		}
	}
	
	private void Srodek(Graphics2D g, String s, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		int asc = fm.getAscent();
		int desc = fm.getDescent();
		g.drawString(s, x + (rozmiarPlytki - fm.stringWidth(s)) / 2, 
				y + (asc + (rozmiarPlytki - (asc + desc)) / 2));
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D= (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawGrid(g2D);
		WiadomoscPoczatkowa(g2D);
		
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setTitle("Puzzle");
			frame.setResizable(false);
			frame.add(new puzzle(4, 550, 30), BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});	

	}
	public static Random getRandom() {
		return RANDOM;
	}
}