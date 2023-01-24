package com.baba.back.oauth.domain;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RandomColorPicker<T> implements ColorPicker<T> {
    @Override
    public T pick(List<T> colors) {
        int colorIndex = (int) (Math.random() * colors.size());
        return colors.get(colorIndex);
    }
}
