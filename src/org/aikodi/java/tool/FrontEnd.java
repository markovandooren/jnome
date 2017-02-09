package org.aikodi.java.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aikodi.java.tool.dependency.DependencyAnalysisTool;
import org.aikodi.java.tool.design.DesignAnalysisTool;
import org.aikodi.java.tool.syntax.SyntaxAnalysisTool;

public class FrontEnd {

  /**
   * @param args The first argument determines the analysis that is performed.
   */
	public static void main(String[] args) {
		new FrontEnd().process(args);
	}
	
	public FrontEnd() {
		add(new DesignAnalysisTool());
		add(new SyntaxAnalysisTool());
		add(new DependencyAnalysisTool());
	}
	
	private void add(Tool tool) {
		_tools.put(tool.name(), tool);
	}
	
	public void process(String[] args) {
		if(args.length == 0) {
			System.out.println("Please provide the name of the tool and its arguments.");
			_tools.keySet().forEach(s -> System.out.println(s));
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
