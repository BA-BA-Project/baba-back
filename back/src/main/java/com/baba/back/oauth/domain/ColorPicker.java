package com.baba.back.oauth.domain;

import java.util.List;

@FunctionalInterface
public interface ColorPicker<T> {
    T pick(List<T> colors);
}
