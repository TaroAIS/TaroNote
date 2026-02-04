package com.taronote.common.util;

public final class VectorUtil {
    private VectorUtil() {
    }

    public static String toPgVector(float[] vector) {
        if (vector == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(vector[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
