package com.harryberlin.cvnest.util.constant;

import lombok.Getter;

@Getter
public enum IndustryEnum {
    IT("Công nghệ"),
    BANK("Ngân hàng"),
    INSURANCE("Bảo hiểm"),
    E_COMMERCE("Thương mại điện tử"),
    COMMUNICATION("Truyền thông"),
    TOURISM("Du lịch"),
    REAL_ESTATE("Bất động sản"),
    OUTSOURCE("Outsource"),
    FINTECH("Fintech"),
    MANUFACTURING("Sản xuất");

    private final String message;

    IndustryEnum(String message) {
        this.message = message;
    }
}
