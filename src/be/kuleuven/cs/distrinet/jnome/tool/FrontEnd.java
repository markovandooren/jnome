package be.kuleuven.cs.distrinet.jnome.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.cs.distrinet.jnome.eclipse.DesignChecker;

public class FrontEnd {

	public static void main(String[] args) {
		new FrontEnd().process(args);
	}
	
	public FrontEnd() {
		add(new DesignChecker());
	}
	
	private void add(Tool tool) {
		_tools.put(tool.name(), tool);
	}
	
	public void process(String[] args) {
		if(args.length == 0) {
			System.out.println("Please provide the name of the tool and its arguments.");
		} else {
			//Arrays.asList is immutable.
			List<String> arguments = new ArrayList(Arrays.asList(args));
			String toolName = arguments.get(0);
			Tool tool = _tools.get(toolName);
			arguments.remove(0);
			String[] newArgs = new String[args.length-1];
			tool.execute(arguments.toArray(newArgs));
		}
	}
	
	private Map<String, Tool> _tools = new HashMap<String,Tool>();
}
