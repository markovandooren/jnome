package be.kuleuven.cs.distrinet.jnome.tool;

public abstract class Tool {

	public Tool(String name) {
		_name = name;
	}
	
	public abstract void execute(String[] arguments);
	
	private String _name;
	
	public String name() {
		return _name;
	}
}
