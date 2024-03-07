package com.laabhum.posttradestreamingservice.constants;

public enum Minutes {
    ONE(1),
    THREE(3),
    FIVE(5),
    TEN(10),
    FIFTEEN(15),
    THIRTY(30),
    SIXTY(60);

    private final int value;

    Minutes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
