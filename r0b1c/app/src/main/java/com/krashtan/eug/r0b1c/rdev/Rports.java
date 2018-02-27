package com.krashtan.eug.r0b1c.rdev;

/**
 * Created by ekrashtan on 27.02.2018.
 */

public enum Rports {
    DUMMY ("None", 0, false),
    MOTOR_S ("Motor S", 1, false),
    BUTTON ("Button", 0x20, false),
    ULTRASONIC ("Ultrasonic", 0x30, false),
    LED ("LED", 0x1C, true);

    private final String str;
    private final int val;
    private final boolean isInternal;

    private Rports(final String str, final int val, final boolean isInt) { this.str = str; this.val = val; this.isInternal = isInt; }
    public boolean isInternal() { return this.isInternal; };
    public int val() { return this.val; };
    public String toString() { return this.str; };
}
