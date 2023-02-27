package com.baba.back.baby.service;

import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.repository.RelationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BabyService {

    private final RelationRepository relationRepository;
    private final MemberRepository memberRepository;

}
