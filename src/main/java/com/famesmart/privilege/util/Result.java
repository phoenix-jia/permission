package com.famesmart.privilege.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result {

    private Integer result;

    private String msg;

    private Object data;

    public Result(Integer result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public Result(Integer result, String msg, Object data) {
        this(result, msg);
        this.data = data;
    }

    public static Result ok() {
        return new Result(0, "success");
    }

    public static Result ok(Object data) {
        return new Result(0, "success", data);
    }

    public static Result error(String msg) {
        return new Result(1, msg);
    }
}
