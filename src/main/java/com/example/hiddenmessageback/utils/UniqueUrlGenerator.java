package com.example.hiddenmessageback.utils;
import java.util.UUID;

public class UniqueUrlGenerator {
    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
