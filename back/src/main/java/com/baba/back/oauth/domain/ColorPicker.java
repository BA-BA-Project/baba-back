package com.baba.back.oauth.domain;

import com.baba.back.oauth.domain.member.IconColor;
import java.util.List;

@FunctionalInterface
public interface ColorPicker {
    IconColor pick(List<IconColor> colors);
}
