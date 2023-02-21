package com.baba.back.oauth.domain;

import java.util.Collection;
import org.springframework.stereotype.Component;

@Component
public class RandomPicker<T> implements Picker<T> {

    @Override
    public T pick(Collection<T> elements) {
        if (elements == null || elements.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int colorIndex = (int) (Math.random() * elements.size());
        int i = 0;
        for (T element : elements) {
            if (i == colorIndex) {
                return element;
            }
            i++;
        }

        throw new IllegalArgumentException();
    }
}
