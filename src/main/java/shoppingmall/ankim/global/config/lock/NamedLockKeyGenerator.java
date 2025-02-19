package shoppingmall.ankim.global.config.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class NamedLockKeyGenerator {
    public static String generate(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // Bind method parameters to SpEL context
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // Parse and evaluate the SpEL expression
        Object generatedKey = parser.parseExpression(key).getValue(context, Object.class);

        // Ensure the generated key is a valid String
        if (generatedKey == null || !(generatedKey instanceof String)) {
            throw new IllegalArgumentException("Generated key must be a non-null string");
        }

        return (String) generatedKey;
    }
}
