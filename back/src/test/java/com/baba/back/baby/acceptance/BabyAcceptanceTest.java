package com.baba.back.baby.acceptance;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.service.AccessTokenProvider;
import com.baba.back.relation.repository.RelationRepository;
import org.springframework.beans.factory.annotation.Autowired;

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

}
