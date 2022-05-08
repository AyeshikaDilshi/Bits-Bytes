package com.itp.walletguard.util;

import androidx.room.TypeConverter;

import java.math.BigDecimal;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static BigDecimal fromLong(Long value) {
        return value == null ? null : new BigDecimal(value).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    @TypeConverter
    public static Long fromBigDecimal(BigDecimal value) {
        return value == null ? null : value.multiply(new BigDecimal(100)).longValueExact();
    }
}