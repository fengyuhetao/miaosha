package com.ht.miaosha.exception;

import com.ht.miaosha.result.CodeMsg;
import lombok.Data;

/**
 * Created by hetao on 2018/12/28.
 */
@Data
public class GlobalException extends RuntimeException{


    private static final long serialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }
}
