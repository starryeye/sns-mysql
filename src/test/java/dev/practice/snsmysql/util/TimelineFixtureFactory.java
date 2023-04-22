package dev.practice.snsmysql.util;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;

import java.time.LocalDate;

public class TimelineFixtureFactory {

    public static EasyRandom get(Long memberId, LocalDate startTime, LocalDate endTime) {

        //생성 제외 필드 설정
        var idPredicate = FieldPredicates
                .named("id")
                .and(FieldPredicates.ofType(Long.class))
                .and(FieldPredicates.inClass(Timeline.class));

        //고정 생성 필드 설정
        var memberIdPredicate = FieldPredicates
                .named("memberId")
                .and(FieldPredicates.ofType(Long.class))
                .and(FieldPredicates.inClass(Timeline.class));

        EasyRandomParameters parameters = new EasyRandomParameters()
                .excludeField(idPredicate)
                .dateRange(startTime, endTime)
                .randomize(memberIdPredicate, () -> memberId);

        return new EasyRandom(parameters);
    }
}
