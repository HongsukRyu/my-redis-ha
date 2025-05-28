package com.backend.api.common.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPropObject {
    String column;
    String value;
    String type;
    String sign;
    String inValues;

    // IN type 처리시
    public SearchPropObject(String column, String type, String sign, String value, String inValues) {
        this.column = column;
        this.value = value;
        this.type = type;
        this.sign = sign;
        this.inValues = inValues;
    }

    // 기본형 type 처리시
    public SearchPropObject(String column, String type, String sign, String value) {
        this.column = column;
        this.value = value;
        this.type = type;
        this.sign = sign;
    }
}