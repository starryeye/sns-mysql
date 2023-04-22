package dev.practice.snsmysql.util;

import org.springframework.data.domain.Sort;

import java.util.List;

public class PageHelper {

    /**
     * Sort 객체를 SQL의 order by 절에 사용할 수 있는 형태로 변환한다.
     */
    public static String orderBy(Sort sort) {

        if (sort.isEmpty()) {
            return "id desc";
        }

        List<Sort.Order> orders = sort.toList();

        StringBuilder stringBuilder = new StringBuilder();

        for (Sort.Order order : orders) {

            String property = order.getProperty();
            String propertySnakeCase = camelToSnakeCase(property);

            stringBuilder
                    .append(propertySnakeCase)
                    .append(" ")
                    .append(order.getDirection().name())
                    .append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    private static String camelToSnakeCase(String camelCaseString) {

        StringBuilder snakeCaseBuilder = new StringBuilder();

        for (char c : camelCaseString.toCharArray()) {
            if (Character.isUpperCase(c)) {
                snakeCaseBuilder.append('_');
                snakeCaseBuilder.append(Character.toLowerCase(c));
            } else {
                snakeCaseBuilder.append(c);
            }
        }
        return snakeCaseBuilder.toString();
    }
}
