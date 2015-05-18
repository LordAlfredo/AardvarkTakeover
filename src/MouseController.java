import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MouseController extends JFrame implements MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener, WindowListener, FocusListener {
private String ip;
private int port;
private Socket socket;
private BufferedReader reader;
private InputStream readStream;
private PrintWriter writer;
private Dimension screen;
private ImageDisplayPanel content;
private JButton refresh;
	MouseController(String ip,int port){
		super();
		this.port=port;
		this.ip=ip;
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		 try {
	            socket = new Socket(ip, port);
	            writer = new PrintWriter(socket.getOutputStream(), true);
	            readStream = socket.getInputStream();
//	            reader = new BufferedReader(new InputStreamReader(
//	                                        socket.getInputStream()));
	            setTitle(ip);
	            setSize(screen);
	            setLayout(new BorderLayout());
	            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	            
	            content = new ImageDisplayPanel();
	            content.addMouseListener(this);
	            content.addMouseMotionListener(this);
	            content.addMouseWheelListener(this);
	            content.addKeyListener(this);
	            add(content, BorderLayout.CENTER);
	            refresh = new JButton("Take screenshot (Help key)");
	            refresh.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent arg0) {
						updateScreenshot();
					}
	            	
	            });
	            add(refresh, BorderLayout.SOUTH);
	            content.requestFocus();
	            addWindowListener(this);
	            this.setBackground(Color.GRAY);
	            this.setVisible(true);
	            
	            
	        } catch (UnknownHostException e) {
	            JOptionPane.showMessageDialog(null, "Cannot connect to "+ip, "Connection error", JOptionPane.ERROR_MESSAGE);
	        } catch (IOException e) {
	        	JOptionPane.showMessageDialog(null, "Could not connect to: "+ip +":"+port, "Connection error", JOptionPane.ERROR_MESSAGE);
	        	
	        }

	}
	public void updateScreenshot(){
//		refresh.setEnabled(false);
		new Thread(){
			public void run(){
		writer.println("IMAGE");
		try {
			BufferedImage newImage = ImageIO.read(readStream);
			content.setImage(newImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "Error getting screenshot", "Screenshot error", JOptionPane.ERROR_MESSAGE);

		}
		refresh.setEnabled(true);
			}
		}.start();
	}
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		writer.println("MOUSE PRESS");
		writer.println(arg0.getModifiers());
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		writer.println("MOUSE RELEASE");
		writer.println(arg0.getModifiers());
	}

	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//output a scaled factor, which will be scaled to the screen on the other end
		double xs = (double)((double)arg0.getX()/(double)getWidth());
		double ys = (double)((double)arg0.getY()/(double)getHeight());
		writer.println("MOUSE MOVE");
		writer.println(xs);
		writer.println(ys);
	}

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		writer.println("MOUSE WHEEL");
		writer.println(arg0.getWheelRotation());
	}

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		int code = arg0.getKeyCode();
		if (code == KeyEvent.VK_HELP){//help key is for updating image
			updateScreenshot();
			return;
		}
		writer.println("KEY PRESS");
		writer.println(code);
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		writer.println("KEY RELEASE");
		writer.println(arg0.getKeyCode());
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		writer.println("QUIT");

		try {
			
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dispose();
		
	}
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		content.requestFocus();
	}
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
class ImageDisplayPanel extends JPanel implements FocusListener{
	private BufferedImage image = null;
	ImageDisplayPanel(){
		super();
		addFocusListener(this);
	}
	public void setImage(BufferedImage i){
		if (i==null)
			return;
		image = i;
		repaint();
	}
	public void paint(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (image!=null)
			g.drawImage(image, 0, 0, getWidth(), getHeight(), Color.GRAY, null);
	}
	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		requestFocus();
	}
}
