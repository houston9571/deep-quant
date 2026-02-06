package com.optimus.constant;

import java.math.BigDecimal;

import static cn.hutool.core.text.StrPool.COLON;


public interface Constants {


    String DEV = "dev";

    String APP_INFO = "APP_INFO";



    String DARK = "dark";

    int MAX_PAGE_SIZE = 10000;

    int ENABLE = 1;
    int DISABLED = 0;
    int DELETED = -1;

    BigDecimal HUNDRED = new BigDecimal(100);

    BigDecimal THOUSAND = new BigDecimal("1000");

    String DATETIME_REGEXP = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String POSITIVE_TIPS = "必须正整数";

    String EMPTY_TIPS = " can`t empty";

    String DEFAULT_TYPE = "-1";

    String ID = "id";

    String NA = "NA";

    String OK = "OK";

    String FAILED = "failed";

    String PROCESSING = "processing";

    String LABEL_CODE = "code";

    String LABEL_DATA = "data";


    int STATE_NO = 0;
    int STATE_YES = 1;


    int THREE_MINUTES = 180;

    int THIRTY_SECONDS = 30;

    int THIRTY_MINUTES = 1800;

    String MIXED_CODE = "@&19^";

    String PERCENT = "%";

}
