package com.baba.back.baby.invitation.service;

@FunctionalInterface
public interface CodeGenerator {
    String generate(int length, String chars);
}
