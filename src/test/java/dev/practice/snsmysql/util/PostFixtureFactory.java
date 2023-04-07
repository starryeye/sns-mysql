package dev.practice.snsmysql.util;

import dev.practice.snsmysql.domain.post.entity.Post;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;

import java.time.LocalDate;

/**
 * Object Mother Pattern, Matin Fowler
 */
public class PostFixtureFactory {

    public static EasyRandom get(Long memberId, LocalDate firstDate, LocalDate lastDate) {

        //Specification Pattern
        //id 필드 제외 하기 위함, id 필드는 DB 에서 생성되기 때문에
        var idPredicate = FieldPredicates
                .named("id")
                .and(FieldPredicates.ofType(Long.class))
                .and(FieldPredicates.inClass(Post.class));

        //Specification Pattern
        //memberId 필드 고정 하기 위함
        var memberIdPredicate = FieldPredicates
                .named("memberId")
                .and(FieldPredicates.ofType(Long.class))
                .and(FieldPredicates.inClass(Post.class));

        EasyRandomParameters parameters = new EasyRandomParameters()
                .excludeField(idPredicate) //id 필드 제외
                .dateRange(firstDate, lastDate) //해당 영역 내의 날짜로만 생성
                //.randomize(Long.class, () -> memberId) //모든 Long 타입의 필드는 memberId 값으로 고정
                .randomize(memberIdPredicate, () -> memberId); //"memberId" 필드는 memberId 값으로 고정

        //리턴 받아서 .nextObject(Post.class) 로 사용
        return new EasyRandom(parameters);
    }
}
