package com.famesmart.privilege.entity.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SmsMessageBO {

    private List<String> phone;

    @JsonProperty("TemplateID")
    private Integer TemplateID;

    @JsonProperty("TemplateParamSet")
    private List<Integer> TemplateParamSet;
}
