package com.baba.back.baby.domain.invitation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.baby.exception.InviteCodeBadRequestException;
import com.baba.back.baby.service.CodeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CodeTest {

    @Test
    void generator를_통해_초대코드를_생성한다() {
        // given
        final String expectedCode = "ABCDEF";
        final CodeGenerator generator = (length, chars) -> expectedCode;

        // when
        final Code code = Code.from(generator);

        // then
        assertThat(code.getValue()).isEqualTo(expectedCode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcddef", "가나다라마바사"})
    @NullAndEmptySource
    void 초대코드가_6자가_아니라면_예외를_던진다(String value) {
        assertThatThrownBy(() -> Code.from(value))
                .isInstanceOf(InviteCodeBadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABCDE가", "ABCDEa", "ABC@DE", "ABC/DE"})
    void 초대코드가_숫자_또는_대문자로_이루어지지_않았으면_예외를_던진다(String value) {
        assertThatThrownBy(() -> Code.from(value))
                .isInstanceOf(InviteCodeBadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABCDEF", "AB1D2F", "VSK18Z"})
    void 초대코드를_생성한다(String value) {
        assertThatCode(() -> Code.from(value))
                .doesNotThrowAnyException();
    }
}