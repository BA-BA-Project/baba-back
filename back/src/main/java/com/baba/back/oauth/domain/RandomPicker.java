package com.baba.back.oauth.domain;

import java.util.Collection;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomPicker<T> implements Picker<T> {

    private static final Random RANDOM = new Random();

    @Override
    public T pick(Collection<T> elements) {
        if (elements == null || elements.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int colorIndex = RANDOM.nextInt(elements.size());
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
