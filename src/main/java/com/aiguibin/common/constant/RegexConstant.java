package com.aiguibin.common.constant;

import java.util.regex.Pattern;

public class RegexConstant {
    public static final String regex = ",(?=([^\"^']*\"[^\"^']*\")*[^\"^']*$)";
    public static final Pattern SPACE_PATTERN = Pattern.compile(
            "([0-9]+([.,][0-9]+)?)\\s*(|K|M|G|T|P)B?",
            Pattern.CASE_INSENSITIVE
    );
    public static final Pattern TIME_PERIOD_PATTERN = Pattern.compile(
            "([0-9]+([.,][0-9]+)?)\\s*(|SEC|MIN|HOUR|DAY|WEEK|MON|QUAR|YEAR)",
            Pattern.CASE_INSENSITIVE
    );

}
