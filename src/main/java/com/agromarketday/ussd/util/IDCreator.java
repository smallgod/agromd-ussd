package com.agromarketday.ussd.util;

public final class IDCreator {

    protected static long longLast = 0L;
    protected static int intLast = 0;

    public static long GenerateLong() {
        long id = System.currentTimeMillis();
        int milliseconds = (int) (id % 1000L);
        id = id / 1000L << 16;
        if (milliseconds > 0) {
            id |= milliseconds << 6;
        }
        synchronized (IDCreator.class) {
            if (id <= longLast) {
                id = longLast + 1L;
            }
            longLast = id;
        }

        return id;
    }

    public static int GenerateInt() {
        int id = (int) (System.currentTimeMillis() / 1000L);

        synchronized (IDCreator.class) {
            if (id <= intLast) {
                id = intLast + 1;
            }
            intLast = id;
        }

        return id;
    }
}
