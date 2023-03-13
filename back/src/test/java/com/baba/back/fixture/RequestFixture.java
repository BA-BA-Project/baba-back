package com.baba.back.fixture;

import static com.baba.back.content.domain.content.CardStyle.CARD_BASIC_1;

import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.oauth.dto.AgreeTermsRequest;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TermsRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;

public class RequestFixture {

    public static final TermsRequest 약관_요청_데이터1 = new TermsRequest("이용약관 동의", true);
    public static final TermsRequest 약관_요청_데이터2 = new TermsRequest("개인정보 수집 및 이용 동의", true);
    public static final AgreeTermsRequest 약관_동의_요청_데이터 = new AgreeTermsRequest(
            "socialToken", List.of(약관_요청_데이터1, 약관_요청_데이터2)
    );

    public static final BabyRequest 아기_생성_요청_데이터_1 = new BabyRequest("아기1", LocalDate.now());
    public static final BabyRequest 아기_생성_요청_데이터_2 = new BabyRequest("아기2", LocalDate.now());
    public static final MemberSignUpRequest 멤버_가입_요청_데이터 = new MemberSignUpRequest(
            "박재희", "PROFILE_W_1", "엄마", List.of(아기_생성_요청_데이터_1, 아기_생성_요청_데이터_2)
    );

    public static final SocialTokenRequest 소셜_토큰_요청_데이터 = new SocialTokenRequest("socialToken");

    public static final CreateContentRequest 컨텐츠_생성_요청_데이터 = new CreateContentRequest(LocalDate.now(), "제목",
            new MockMultipartFile("photo", "file.png", "image/png",
                    "Mock File".getBytes()), CARD_BASIC_1.toString());
}
