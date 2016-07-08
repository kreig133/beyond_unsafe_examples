package com.kreig133.beyond_unsafe.example1;


import uk.co.real_logic.agrona.UnsafeAccess;

public class CrashJVM {
    public static void main(String[] args) {
        UnsafeAccess.UNSAFE.getLong(-1);
    }
}
