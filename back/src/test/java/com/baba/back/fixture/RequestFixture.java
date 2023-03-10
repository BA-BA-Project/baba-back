package com.baba.back.fixture;

import static com.baba.back.content.domain.content.CardStyle.CARD_BASIC_1;

import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.SocialTokenRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;

public class RequestFixture {

    public static final BabyRequest 아기_생성_요청_데아터_1 = new BabyRequest("아기1", LocalDate.now());
    public static final BabyRequest 아기_생성_요청_데이터_2 = new BabyRequest("아기2", LocalDate.now());
    public static final MemberSignUpRequest 멤버_가입_요청_데이터 = new MemberSignUpRequest(
            "박재희", "PROFILE_W_1", "엄마", List.of(아기_생성_요청_데아터_1, 아기_생성_요청_데이터_2)
    );

    public static final SocialTokenRequest 소셜_토큰_요청_데이터 = new SocialTokenRequest("socialToken");

    public static final CreateContentRequest 컨텐츠_생성_요청_데이터 = new CreateContentRequest(LocalDate.now(), "제목",
            new MockMultipartFile("photo", "file.png", "image/png",
                    "Mock File".getBytes()), CARD_BASIC_1.toString());
}
