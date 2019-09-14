package com.msr.kill.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 08:59
 */
@Data
public class KillDto implements Serializable {


    private static final long serialVersionUID = 4108761351316264060L;

    @NotNull
    private Integer killId;

    private Integer userId;
}
