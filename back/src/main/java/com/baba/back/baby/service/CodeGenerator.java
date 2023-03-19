package com.baba.back.baby.service;

@FunctionalInterface
public interface CodeGenerator {
    String generate(int length, String chars);
}
