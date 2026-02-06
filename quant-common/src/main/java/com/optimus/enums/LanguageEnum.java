package com.optimus.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static cn.hutool.core.text.StrPool.DASHED;
import static java.util.Locale.SIMPLIFIED_CHINESE;

@Getter
@AllArgsConstructor
public enum LanguageEnum {


    CHINESE("zh", "CN"),

//    CHINESE_TW("zh", "TW"),

    ENGLISH("en", "US"),


    DEFAULT(SIMPLIFIED_CHINESE.getLanguage(), SIMPLIFIED_CHINESE.getCountry());


    private final String language;

    private final String country;

    public String getLanguageTag() {
        return language + DASHED + country;
    }


    public static List<String> ranges() {
        List<String> names = Lists.newArrayList();
        for (LanguageEnum value : values()) {
            if (!value.equals(DEFAULT)) {
                names.add(value.getLanguageTag());
            }
        }
        return names;
    }

    public static String getDefaultLangTag(String lang) {
        return ranges().contains(lang) ? lang : DEFAULT.getLanguageTag();
    }


}
