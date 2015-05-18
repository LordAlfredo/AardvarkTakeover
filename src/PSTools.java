import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class PSTools {

	
	public static Vector<RemoteProcessItem> getList(String addr){
		// TODO Auto-generated method stub
		String output = "";
		Vector<RemoteProcessItem> processes = new Vector<RemoteProcessItem>();
		try {
			Process p = Runtime.getRuntime().exec("psList.exe \\\\"+addr);
			BufferedReader input = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String tmp;
			while ((tmp= input.readLine().trim()) != "") {
				output = output + tmp + "\n";
				StringTokenizer st;
				if (!(tmp.equals("") || tmp.startsWith("Process information") || tmp.startsWith("Name"))){
					 st = new StringTokenizer(tmp);
					try{
						RemoteProcessItem newItem = new RemoteProcessItem(st
								.nextToken(), st.nextToken(), st.nextToken(),
								st.nextToken(), st.nextToken(), st.nextToken(),
								st.nextToken());
						processes.add(newItem);
					}catch (Exception errrrr){
						JOptionPane.showMessageDialog(null, errrrr.getMessage(), "Parse error", JOptionPane.ERROR_MESSAGE);
						return null;
					}
				}
			}
			input.close();
			if (p.exitValue() != 0) {
				JOptionPane.showMessageDialog(null, output, "Exited with code "
						+ p.exitValue(), JOptionPane.ERROR_MESSAGE);
				return null;
			} else {
				
			}
		} catch (Exception err) {
			if (err.getMessage()!=null)
			JOptionPane.showMessageDialog(null, err.getMessage(), "Execution error", JOptionPane.ERROR_MESSAGE);
		}
		return processes;
	}
	
	public static void kill(RemoteProcessItem item, String addr){
		kill(item.getPid(),addr);
	}
	public static void kill(int id, String addr){
		executeInBackground("pskill.exe -t \\\\"+addr+" "+id);
	}
	public static void suspend(RemoteProcessItem item, String addr){
		suspend(item.getPid(),addr);
	}
	public static void suspend(int id, String addr){
		executeInBackground("pssuspend.exe \\\\"+addr+" "+id);
	}
	public static void resume(RemoteProcessItem item, String addr){
		resume(item.getPid(),addr);
	}
	public static void resume(int id, String addr){
		executeInBackground("pssuspend.exe -r \\\\"+addr+" "+id);
	}
	public static void shutdown(String addr, char action, boolean force, int time){
		executeInBackground("psshutdown.exe "+ "-"+action +" "+ (force&&(action=='s'||action=='r'||action=='o')?"-f":"") + " -v "+time + " \\\\"+addr);
	}
	public static void exec(String addr, String commandAndArgs, boolean interactive, boolean copy, boolean forcecopy, boolean background){
		String flags ="d";
		if (interactive)
			flags += "i";
		if (copy){
			flags +="c";
			if (forcecopy)
				flags +="f";
		}
		String command = "psExec.exe \\\\" + addr+ " -"+ flags+ " "+commandAndArgs;
		if (background)
			executeInBackground(command);
		else
			execute(command);
	}
	public static String info(String addr){
		return execute("psInfo.exe \\\\"+addr);
	}
	public static String loggedOn(String addr){
		return execute("psLoggedon.exe \\\\"+addr);
	}

	
	
	
	
	private static void executeInBackground(final String command) {
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				execute(command);
			}
		}).start();
	}
	private static String execute(String command){
		String output = null;
		try {
			output = command+"\n";
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader input = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String tmp;
			while ((tmp = input.readLine()) != null) {
				output = output + tmp + "\n";
				System.out.println(tmp);
			}
			input.close();
			if (p.exitValue() != 0)
				JOptionPane.showMessageDialog(null, output,
						"Exited with code "+p.exitValue(), JOptionPane.ERROR_MESSAGE);
		} catch (Exception err) {
			if (err.getMessage()!=null)
			JOptionPane.showMessageDialog(null, err.getMessage(), "Execution error", JOptionPane.ERROR_MESSAGE);
		}
		return output;
	}

}
