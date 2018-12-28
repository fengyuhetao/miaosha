package com.ht.miaosha.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.ht.miaosha.validator.IsMobile;
import javax.validation.constraints.NotNull;

/**
 * Created by hetao on 2018/12/27.
 */
@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min=32, max=32)
    private String password;
}
