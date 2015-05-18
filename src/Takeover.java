import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
public class Takeover extends JFrame{

	/**
	 * @param args
	 */
	final static int port = 1081;//port for mouse controller server
	JTextField address;
	JButton refresh, info, loggedOn, kill, suspend, resume, shutdown, launch, mouse;
	JList processList;
	JPanel top, bottom;
	JScrollPane middle;
	
	JPanel shutdownWindow;
	JComboBox shutdownTypeSelection;
	String[] shutdownTypes = {"Shutdown", "Restart", "Log off", "Lock console", "Suspend Computer"};
	char[] shutdownFlags = {'s', 			'r', 		'o',		'l', 		'd'};
	JTextField shutdownDelay;
	JCheckBox shutdownForce;
	
	JPanel execWindow;
	JTextField execCmd, execArgs;
	JButton selectFile;
	JCheckBox interactive, copy, forceCopy;
	JFileChooser chooser;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Takeover();
	}
	
	Takeover(){
		super("Controller");
		setLayout(new BorderLayout());
		top = new JPanel(new GridLayout(1,0));
		try {
			address = new JTextField(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			address=new JTextField();
		}
		top.add(address);
		refresh=new JButton("Refresh");
		this.getRootPane().setDefaultButton(refresh);
		refresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				refresh();
				
			}
			
		});
		top.add(refresh);
		info = new JButton("Info");
		info.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String info = PSTools.info(address.getText());
				if (info!=null)
					JOptionPane.showMessageDialog(null, info, "PSInfo", JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		top.add(info);
		
		loggedOn = new JButton("Logged On");
		loggedOn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String info = PSTools.loggedOn(address.getText());
				if (info!=null)
					JOptionPane.showMessageDialog(null, info, "PSLoggedOn", JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		top.add(loggedOn);
		
		this.add(top, BorderLayout.NORTH);
		
		processList = new JList();
		processList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		processList.addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent arg0) {
				if (processList.getSelectedIndex() == -1) {
			            kill.setEnabled(false);
			            suspend.setEnabled(false);
			            resume.setEnabled(false);
			            

			        } else {
			        	 kill.setEnabled(true);
				            suspend.setEnabled(true);
				            resume.setEnabled(true);
				           
				     }				
			}
			
		});
		middle = new JScrollPane(processList);
		this.add(middle, BorderLayout.CENTER);
		
		bottom = new JPanel(new GridLayout(3, 2));
		kill = new JButton("Kill");
		kill.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				for (Object task : processList.getSelectedValues()){
					PSTools.kill((RemoteProcessItem)task, address.getText());
					refresh();
				}
					
			}
			
		});	
		bottom.add(kill);
		
		suspend = new JButton("Suspend");
		suspend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				for (Object task : processList.getSelectedValues()){
					PSTools.suspend((RemoteProcessItem)task, address.getText());
				}
					
			}
			
		});	
		bottom.add(suspend);
		
		resume = new JButton("Resume");
		resume.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				for (Object task : processList.getSelectedValues()){
					PSTools.resume((RemoteProcessItem)task, address.getText());
				}
					
			}
			
		});	
		bottom.add(resume);
		
		shutdownWindow = new JPanel();
		shutdownWindow.setLayout(new GridLayout(0,1));
		shutdownWindow.setSize(300, 100);
		shutdownTypeSelection = new JComboBox(shutdownTypes);
		shutdownWindow.add(shutdownTypeSelection);
		
		shutdownForce = new JCheckBox("Force");
		shutdownWindow.add(shutdownForce);
		
		shutdownDelay = new JTextField("0", 3);
		JPanel shutdownDelayPanel = new JPanel();
		shutdownDelayPanel.add(shutdownDelay);
		shutdownDelayPanel.add(new JLabel(" second delay and dialog"));
		shutdownWindow.add(shutdownDelayPanel);
		
		shutdown = new JButton("Shutdown");
		shutdown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.showConfirmDialog(null, shutdownWindow, "Poweroff Windows", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION){
					PSTools.shutdown(address.getText(), shutdownFlags[shutdownTypeSelection.getSelectedIndex()], shutdownForce.isSelected(), Integer.parseInt(shutdownDelay.getText()));
				}
					
			}
			
		});	
		bottom.add(shutdown);
		
		execWindow = new JPanel(new GridLayout(2,1));
		execWindow.setSize(300,200);
		execCmd = new JTextField("\"cmd.exe\"");
		execCmd.setColumns(30);
		chooser = new JFileChooser(System.getenv("windir")+"\\system32");
		selectFile = new JButton("...");
		selectFile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (chooser.showOpenDialog(execWindow) == JFileChooser.APPROVE_OPTION)
					execCmd.setText("\""+chooser.getSelectedFile().getAbsolutePath()+"\"");
			}
			
		});
		execArgs = new JTextField(10);
		JPanel execTop = new JPanel();
		execTop.add(execCmd);
		execTop.add(selectFile);
		execTop.add(execArgs);
		interactive = new JCheckBox("GUI", true);
		copy = new JCheckBox("Copy", false);
		copy.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				forceCopy.setEnabled(copy.isSelected());
			}
			
		});
		forceCopy = new JCheckBox("Force copy", false);
		forceCopy.setEnabled(copy.isSelected());
		JPanel execBottom = new JPanel(new GridLayout(0,1));
		execBottom.add(interactive);
		execBottom.add(copy);
		execBottom.add(forceCopy);
		execWindow.add(execTop);
		execWindow.add(execBottom);
		launch = new JButton("Launch");
		launch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null, execWindow, "Remote execution", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
					PSTools.exec(address.getText(), execCmd.getText()+" "+execArgs.getText(), interactive.isSelected(), copy.isSelected(), forceCopy.isSelected(), true);
				}
			}
			
		});
		bottom.add(launch);
		
		mouse=new JButton("Mouse/Keyboard");
		mouse.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				File exe = new File("ControlServer.exe");
				if (!exe.exists()){
					JOptionPane.showMessageDialog(null, exe.getAbsolutePath()+" does not exist");
				}
				else	
					PSTools.exec(address.getText(), "\""+exe.getAbsolutePath()+"\"", true, true, true, false);
				MouseController tmpctrl = new MouseController(address.getText(), port);
				
			}
			
		});
		bottom.add(mouse);
		
		this.add(bottom, BorderLayout.SOUTH);
		
		this.setSize(400,600);
		this.setVisible(true);
		refresh();
	}
	void refresh(){
		Vector<RemoteProcessItem> v = PSTools.getList(address.getText());
		if (v!=null)
			processList.setListData(v);
	}
}
