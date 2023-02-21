package com.baba.back.oauth.domain;

import java.util.Collection;

@FunctionalInterface
public interface Picker<T> {
    T pick(Collection<T> colors);
}
