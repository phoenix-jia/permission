package com.famesmart.privilege.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TalkMessageBO {

    private String msgtype;

    private Text text;

    @Data
    @AllArgsConstructor
    public static class Text {
        private String Content;
    }
}
