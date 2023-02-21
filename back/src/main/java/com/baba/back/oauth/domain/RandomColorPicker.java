package com.baba.back.oauth.domain;

import com.baba.back.oauth.domain.member.IconColor;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RandomColorPicker implements ColorPicker {

    @Override
    public IconColor pick(List<IconColor> colors) {
        int colorIndex = (int) (Math.random() * colors.size());
        return colors.get(colorIndex);
    }
}
