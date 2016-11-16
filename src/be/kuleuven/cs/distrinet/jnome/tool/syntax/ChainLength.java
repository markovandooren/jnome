package be.kuleuven.cs.distrinet.jnome.tool.syntax;

import java.util.HashMap;
import java.util.Map;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.analysis.Result;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.exception.Handler;

public class ChainLength extends Analysis<MethodInvocation, ChainLength.LengthResult,Nothing> {

  public ChainLength() {
    super(MethodInvocation.class, new LengthResult());
  }

  public static class LengthResult extends Result<LengthResult> {

    private LengthResult() {
    }

    private LengthResult(int length) {
      _lengthMap.put(length, 1);
    }

    private Map<Integer, Integer> _lengthMap = new HashMap<>();

    @Override
    public String message() {
      StringBuilder builder = new StringBuilder();
      for (Map.Entry<Integer, Integer> entry : _lengthMap.entrySet()) {
        builder.append("calls in chain: ");
        builder.append(entry.getKey());
        builder.append(" count: ");
        builder.append(entry.getValue());
        builder.append("\n");
      }
      return builder.toString();
    }

    void add(int length, int count) {
      Integer current = _lengthMap.get(length);
      if (current != null) {
        count = count + current;
      }
      _lengthMap.put(length, count);
    }

    @Override
    public LengthResult and(LengthResult other) {
      if (other == null) {
        return this;
      }
      LengthResult result = new LengthResult();
      for (Map.Entry<Integer, Integer> entry : _lengthMap.entrySet()) {
        result.add(entry.getKey(), entry.getValue());
      }
      for (Map.Entry<Integer, Integer> entry : other._lengthMap.entrySet()) {
        result.add(entry.getKey(), entry.getValue());
      }
      return result;
    }

  }

  private int length(CrossReferenceTarget e) {
    if (e instanceof MethodInvocation) {
      CrossReferenceTarget target = ((MethodInvocation) e).getTarget();
      return 1 + (target == null ? 0 : length(target));
    }
    return 0;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  protected void analyze(MethodInvocation invocation) {
    if (!(invocation.parent() instanceof MethodInvocation)) {
      result().add(length(invocation), 1);
    }
  }
}
