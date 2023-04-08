package dev.practice.snsmysql.util;

import org.springframework.data.domain.Sort;

import java.util.List;

public class PageHelper {

    /**
     * Sort 객체를 SQL의 order by 절에 사용할 수 있는 형태로 변환한다.
     */
    public static String orderBy(Sort sort) {

        if(sort.isEmpty()) {
            return "id desc";
        }

        List<Sort.Order> orders = sort.toList();

        StringBuilder stringBuilder = new StringBuilder();
        for (Sort.Order order : orders) {
            stringBuilder
                    .append(order.getProperty())
                    .append(" ")
                    .append(order.getDirection().name())
                    .append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
