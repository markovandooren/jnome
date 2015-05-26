package be.kuleuven.cs.distrinet.jnome.tool;

public abstract class Tool {

	public Tool(String name) {
		_name = name;
	}
	
	/**
	 * Execute this tool with the given arguments.
	 * 
	 * @param arguments The arguments for this tool.
	 *                  The array cannot be null.
	 */
	public abstract void execute(String[] arguments);
	
	private String _name;
	
	public String name() {
		return _name;
	}
}
