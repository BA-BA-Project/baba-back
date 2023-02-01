package com.baba.back.fixture;

import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.oauth.dto.MemberJoinRequest;
import java.time.LocalDate;
import java.util.List;

public class RequestFixture {

    public static final BabyRequest 아기_생성_요청1 = new BabyRequest("아기1", LocalDate.now());
    public static final BabyRequest 아기_생성_요청2 = new BabyRequest("아기2", LocalDate.now());
    public static final MemberJoinRequest 멤버_가입_요청 = new MemberJoinRequest(
            "박재희", "icon1", "엄마", List.of(아기_생성_요청1, 아기_생성_요청2)
    );
}
