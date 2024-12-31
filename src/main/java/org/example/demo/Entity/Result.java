package org.example.demo.Entity;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: Result
 * Package: org.example.demo.Entity
 * Description:
 *
 * @Author 谢依雯
 * @Create 2024/12/4 15:38
 */
@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;
    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result =new Result<T>();
        result.code=1;
        result.data=data;
        return result;
    }
    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
