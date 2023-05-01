package com.baba.back.fixture;

import static com.baba.back.content.domain.content.CardStyle.CARD_BASIC_1;
import static com.baba.back.content.domain.content.CardStyle.CARD_CHECK_1;
import static com.baba.back.fixture.DomainFixture.멤버2;
import static com.baba.back.fixture.DomainFixture.멤버3;

import com.baba.back.baby.dto.BabyNameRequest;
import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.baby.dto.CreateBabyRequest;
import com.baba.back.baby.dto.CreateInviteCodeRequest;
import com.baba.back.baby.dto.InviteCodeRequest;
import com.baba.back.content.dto.ContentUpdateTitleAndCardStyleRequest;
import com.baba.back.content.dto.CreateCommentRequest;
import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.content.dto.UpdateContentPhotoRequest;
import com.baba.back.oauth.domain.Terms;
import com.baba.back.oauth.dto.AgreeTermsRequest;
import com.baba.back.oauth.dto.CreateGroupRequest;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberUpdateRequest;
import com.baba.back.oauth.dto.SignUpWithCodeRequest;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TermsRequest;
import com.baba.back.oauth.dto.UpdateGroupRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;

public class RequestFixture {

    public static final TermsRequest 약관_요청_데이터1 = new TermsRequest(Terms.TERMS_1.getName(), true);
    public static final TermsRequest 약관_요청_데이터2 = new TermsRequest(Terms.TERMS_2.getName(), true);
    public static final TermsRequest 약관_요청_데이터3 = new TermsRequest(Terms.TERMS_3.getName(), true);
    public static final AgreeTermsRequest 약관_동의_요청_데이터 = new AgreeTermsRequest(
            "socialToken", List.of(약관_요청_데이터1, 약관_요청_데이터2, 약관_요청_데이터3)
    );
    public static final CreateBabyRequest 아기_추가_요청_데이터 = new CreateBabyRequest("아기3", "아빠", LocalDate.now());
    public static final CreateGroupRequest 그룹_추가_요청_데이터1 = new CreateGroupRequest("외가", "#FFAEBA");
    public static final CreateGroupRequest 그룹_추가_요청_데이터2 = new CreateGroupRequest("친가", "#FFAEBA");

    public static final UpdateGroupRequest 그룹_정보_변경_요청_데이터 = new UpdateGroupRequest("친가");

    public static final SignUpWithCodeRequest 초대코드로_멤버_가입_요청_데이터 = new SignUpWithCodeRequest(
            "AAAAAA", "박재희", "PROFILE_W_1"
    );
    public static final SocialTokenRequest 소셜_토큰_요청_데이터 = new SocialTokenRequest("socialToken");
    public static final CreateContentRequest 컨텐츠_생성_요청_데이터 = new CreateContentRequest(LocalDate.now(), "제목",
            new MockMultipartFile("photo", "file.png", "image/png",
                    "Mock File".getBytes()), CARD_BASIC_1.toString());
    public static final CreateCommentRequest 태그_댓글_생성_요청_데이터1 = new CreateCommentRequest(멤버2.getId(), "댓글!");
    public static final CreateCommentRequest 태그_댓글_생성_요청_데이터2 = new CreateCommentRequest(멤버3.getId(), "댓글!");
    public static final CreateCommentRequest 댓글_생성_요청_데이터 = new CreateCommentRequest("", "댓글!");
    public static final CreateInviteCodeRequest 초대코드_생성_요청_데이터1 = new CreateInviteCodeRequest(
            "외가", "이모");
    public static final CreateInviteCodeRequest 초대코드_생성_요청_데이터2 = new CreateInviteCodeRequest(
            "가족", "아빠");
    public static final InviteCodeRequest 초대코드로_아기_추가_요청_데이터 = new InviteCodeRequest("AAAAAA");
    public static final MemberUpdateRequest 마이_프로필_변경_요청_데이터 = new MemberUpdateRequest(
            "박재희2", "안녕하세요!", "PROFILE_W_2", "#81E0D5"
    );
    public static final BabyNameRequest 아기_이름_변경_요청_데이터 = new BabyNameRequest("아기11");
    public static final ContentUpdateTitleAndCardStyleRequest 콘텐츠_제목_카드스타일_변경_요청_데이터 = new ContentUpdateTitleAndCardStyleRequest(
            "제목 수정", CARD_CHECK_1.name());
    public static final UpdateContentPhotoRequest 컨텐츠_사진_수정_요청_데이터 = new UpdateContentPhotoRequest(
            new MockMultipartFile("photo2", "file.png", "image/png", "Mock File".getBytes())
    );
    private static final BabyRequest 아기_생성_요청_데이터_1 = new BabyRequest("아기1", LocalDate.now());
    private static final BabyRequest 아기_생성_요청_데이터_2 = new BabyRequest("아기2", LocalDate.now());
    public static final MemberSignUpRequest 멤버_가입_요청_데이터 = new MemberSignUpRequest(
            "박재희", "PROFILE_W_1", "엄마", List.of(아기_생성_요청_데이터_1, 아기_생성_요청_데이터_2)
    );
}
