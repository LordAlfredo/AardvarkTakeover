
public class RemoteProcessItem {
public RemoteProcessItem(String name, String pid, String pri, String hnd,
			String priv, String cpu, String elapsed) {
		super();
		this.name = name.trim();
		this.pid = pid.trim();
		this.pri = pri;
		this.hnd = hnd;
		this.priv = priv;
		this.cpu = cpu;
		this.elapsed = elapsed;
	}
public String name;
public String pid;
public String pri;
public String hnd;
public String priv;
public String cpu;
public String elapsed;
public int getPid(){
	return Integer.parseInt(pid);
}
public String toString(){
	return name;
}
}
