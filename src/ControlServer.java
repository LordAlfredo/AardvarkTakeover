import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.imageio.stream.*;


public class ControlServer {

	/**
	 * @param args
	 */
	final static int port = 1081;
	static Dimension screen;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		ServerSocket serverSocket = null;
		try {
		    serverSocket = new ServerSocket(port);
		    final Socket s = serverSocket.accept();
//		    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		    final OutputStream out = s.getOutputStream();
	        BufferedReader in = new BufferedReader(
					new InputStreamReader(
					s.getInputStream()));
	        
	        Robot robot = new Robot();
	        while (s.isConnected()){
	        	String header = in.readLine();
	        	// format is each info on one line. A header, then any necessary following lines like an x and y coordinate
				if (header.equals("MOUSE PRESS")) {
					//int button
					robot.mousePress(Integer.parseInt(in.readLine()));
				} else if (header.equals("MOUSE RELEASE")) {
					robot.mouseRelease(Integer.parseInt(in.readLine()));
				} else if (header.equals("MOUSE MOVE")) {
					double xs = Double.parseDouble(in.readLine());
					double ys = Double.parseDouble(in.readLine());
					robot.mouseMove((int)(screen.width*xs), (int)(screen.height*ys));
				} else if (header.equals("MOUSE WHEEL")) {
					robot.mouseWheel(Integer.parseInt(in.readLine()));
				} else if (header.equals("KEY PRESS")) {
					//int keyCode
					robot.keyPress(Integer.parseInt(in.readLine()));
				} else if (header.equals("KEY RELEASE")) {
					robot.keyRelease(Integer.parseInt(in.readLine()));
				} else if (header.equals("IMAGE")){
					for (String cur : ImageIO.getWriterFormatNames())
					new Thread(){
						public void run(){//prevents hangs from totally crashing program
			        		try {
			        			Robot robot = null;
								try {
									robot = new Robot();
								} catch (AWTException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								BufferedImage shot = robot.createScreenCapture(new Rectangle(screen));
								ImageIO.write(shot, "PNG", s.getOutputStream());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
	        	}else if (header.equals("QUIT")){
	        		s.close();
	        		in.close();
	        		out.close();
	        		System.exit(0);
	        	} 
	        }
		} catch (IOException e) {
			//could not listen
			e.printStackTrace();
			Toolkit.getDefaultToolkit().beep();
			System.out.print('\007');
			System.exit(-1);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-2);
		}
		
		
	}

}
