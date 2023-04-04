package dev.practice.snsmysql.util;

import dev.practice.snsmysql.domain.member.entity.Member;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

/**
 * Object Mother Pattern, Matin Fowler
 */
public class MemberFixtureFactory {

    private static Long randomSeed() {
        return (long) (Math.random() * 1000);
    }

    public static Member create() {
        var param = new EasyRandomParameters().seed(randomSeed());
        return new EasyRandom(param).nextObject(Member.class);
    }

    //seed 가 다르면 다른 객체가 생성된다. 같으면 같은 객체가 생성된다.
    public static Member create(Long seed) {

        var param = new EasyRandomParameters().seed(seed);
        return new EasyRandom(param).nextObject(Member.class);
    }
}
