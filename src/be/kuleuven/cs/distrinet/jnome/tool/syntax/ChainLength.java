package be.kuleuven.cs.distrinet.jnome.tool.syntax;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.cs.distrinet.chameleon.analysis.Analysis;
import be.kuleuven.cs.distrinet.chameleon.analysis.Result;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;

public class ChainLength extends Analysis<MethodInvocation, ChainLength.LengthResult>{

	public ChainLength() {
		super(MethodInvocation.class);
	}

	public static class LengthResult extends Result<LengthResult> {

		private LengthResult() {}
		
		private LengthResult(int length) {
			_lengthMap.put(length, 1);
		}
		
		private Map<Integer, Integer> _lengthMap = new HashMap<>();
		
		@Override
		public String message() {
			StringBuilder builder = new StringBuilder();
			for(Map.Entry<Integer, Integer> entry: _lengthMap.entrySet()) {
				builder.append("calls in chain: ");
				builder.append(entry.getKey());
				builder.append(" count: ");
				builder.append(entry.getValue());
				builder.append("\n");
			}
			return builder.toString();
		}
		
		public void add(int length, int count) {
			Integer current = _lengthMap.get(length);
			if(current != null) {
				count = count + current;
			}
			_lengthMap.put(length, count);
		}

		@Override
		public LengthResult and(LengthResult other) {
			if(other instanceof EmptyResult || other == null) {
				return this;
			}
			LengthResult result = new LengthResult();
			for(Map.Entry<Integer, Integer> entry: _lengthMap.entrySet()) {
				result.add(entry.getKey(), entry.getValue());
			}
			for(Map.Entry<Integer, Integer> entry: other._lengthMap.entrySet()) {
				result.add(entry.getKey(), entry.getValue());
			}
			return result;
		}
		
		private final static LengthResult EMPTY = new EmptyResult();
	}
	
	private static class EmptyResult extends LengthResult {
		@Override
		public LengthResult and(LengthResult other) {
			return other;
		}
	}

	@Override
	protected LengthResult analyse(MethodInvocation e) {
		if(! (e.parent() instanceof MethodInvocation)) {
			return new LengthResult(length(e));
		}
		return LengthResult.EMPTY;
	}
	
	private int length(CrossReferenceTarget e) {
		if(e instanceof MethodInvocation) {
		  CrossReferenceTarget target = ((MethodInvocation)e).getTarget();
		  return 1 + (target == null ? 0 : length(target));
		}
		return 0;
	}
	

}
