package com.baba.back.baby.acceptance;

import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

class BabyAcceptanceTest extends AcceptanceTest {

    // TODO: 2023/03/09 아기 초대 API 생성 후 다른 아기 추가하여 테스트 진행한다.
    @Test
    void 아기_리스트_요청_시_등록된_아기가_조회된다() {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청_멤버_1(), MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response = 아기_리스트_조회_요청(accessToken);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, BabiesResponse.class))
                        .usingRecursiveComparison()
                        .ignoringFields("myBaby.babyId", "others.babyId")
                        .isEqualTo(
                                new BabiesResponse(
                                        List.of(
                                                new BabyResponse(아기1.getId(), Color.COLOR_1.getValue(), 아기1.getName()),
                                                new BabyResponse(아기2.getId(), Color.COLOR_1.getValue(), 아기2.getName())
                                        ),
                                        List.of()
                                )
                        )
        );
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Picker<Color> picker() {
            return colors -> Color.COLOR_1;
        }
    }
}
