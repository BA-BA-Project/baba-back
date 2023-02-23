package com.baba.back.baby.acceptance;

import static com.baba.back.SimpleRestAssured.get;
import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.dto.SearchDefaultBabyResponse;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.service.AccessTokenProvider;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.repository.RelationRepository;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class BabyAcceptanceTest extends AcceptanceTest {

    public static final String BABY_BASE_PATH = "/api/baby";

    @Autowired
    private AccessTokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Test
    void 기본_설정된_아기가_없으면_404를_던진다() {
        // given
        final String token = tokenProvider.createToken(멤버1.getId());

        // when
        final ExtractableResponse<Response> response =
                get(BABY_BASE_PATH + "/default", Map.of("Authorization", "Bearer " + token));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void 디폴트_아기를_조회한다() {
        // given
        Member member = memberRepository.save(멤버1);
        final String token = tokenProvider.createToken(멤버1.getId());

        Baby baby = babyRepository.save(아기1);

        relationRepository.save(Relation.builder()
                .member(member)
                .baby(baby)
                .relationName("엄마")
                .relationGroup(RelationGroup.FAMILY)
                .defaultRelation(true)
                .build());

        // when
        ExtractableResponse<Response> response =
                get(BABY_BASE_PATH + "/default", Map.of("Authorization", "Bearer " + token));

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, SearchDefaultBabyResponse.class).babyId()).isEqualTo(baby.getId())
        );

    }
}
