package shoppingmall.ankim.global.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.product.repository.query.helper.ColorCondition;

@Component
public class StringToColorConditionConverter implements Converter<String, ColorCondition> {

    @Override
    public ColorCondition convert(String source) {
        for (ColorCondition condition : ColorCondition.values()) {
            if (condition.getHexCode().equalsIgnoreCase(source)) {
                return condition;
            }
        }
        throw new IllegalArgumentException("Invalid color condition: " + source);
    }
}
