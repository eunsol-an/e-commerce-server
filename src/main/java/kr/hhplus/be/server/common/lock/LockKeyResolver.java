package kr.hhplus.be.server.common.lock;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LockKeyResolver {
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    public static List<String> resolveKeys(Method method, Object[] args, String prefix, String[] keyExpressions) {
        String[] paramNames = NAME_DISCOVERER.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        List<String> keys = new ArrayList<>();
        for (String expr : keyExpressions) {
            if (!StringUtils.hasText(expr)) continue;
            Expression e = PARSER.parseExpression(expr);
            Object value = e.getValue(context);
            if (value == null) continue;
            if (value instanceof Iterable<?> it) {
                for (Object v : it) keys.add(prefix + ":" + String.valueOf(v));
            } else if (value.getClass().isArray()) {
                int len = java.lang.reflect.Array.getLength(value);
                for (int i = 0; i < len; i++) {
                    Object v = java.lang.reflect.Array.get(value, i);
                    keys.add(prefix + ":" + String.valueOf(v));
                }
            } else {
                keys.add(prefix + ":" + String.valueOf(value));
            }
        }
        // 데드락 방지를 위해 사전순 정렬 (모든 노드가 동일 순서로 락 시도)
        return keys.stream().distinct().sorted().collect(Collectors.toList());
    }
}
