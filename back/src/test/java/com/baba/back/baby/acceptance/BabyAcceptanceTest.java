package com.baba.back.baby.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.ColorPicker;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.service.TokenProvider;
import com.baba.back.relation.domain.DefaultRelation;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.repository.RelationRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class BabyAcceptanceTest extends AcceptanceTest {
    public static final String MEMBER_ID = "1234";
    public static final String BABY_ID = "1234";
    public static final String BABY_BASE_PATH = "/baby";

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private RelationRepository relationRepository;

    @AfterEach
    void 초기화() {
        relationRepository.deleteAll();
        memberRepository.deleteAll();
        babyRepository.deleteAll();
    }

    @Test
    void 관계가_없으면_404를_던진다() {
        // given
        final String token = tokenProvider.createToken(MEMBER_ID);

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .when()
                .get(BABY_BASE_PATH + "/default")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void 디폴트_아기를_조회한다() {
        // given
        final String token = tokenProvider.createToken(MEMBER_ID);

        LocalDate birthday = LocalDate.of(2024, 1, 25);
        LocalDate now = LocalDate.of(2023, 1, 25);
        final String color = "FFAEBA";
        ColorPicker<String> colorPicker = (List<String> colors) -> color;

        Member member = memberRepository.save(Member.builder()
                .id(MEMBER_ID)
                .name("박재희")
                .introduction("")
                .colorPicker(colorPicker)
                .iconName("icon1")
                .build());

        Baby baby = babyRepository.save(Baby.builder()
                .id(BABY_ID)
                .name("앙쥬")
                .birthday(birthday)
                .now(now)
                .build());

        relationRepository.save(Relation.builder()
                .member(member)
                .baby(baby)
                .relationName("엄마")
                .relationGroup(RelationGroup.FAMILY)
                .defaultRelation(DefaultRelation.DEFAULT)
                .build());

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .when()
                .get(BABY_BASE_PATH + "/default")
                .then()
                .log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }
}
