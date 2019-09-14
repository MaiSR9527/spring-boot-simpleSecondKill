package com.msr.kill.dto;

import com.msr.kill.entity.ItemKillSuccess;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 08:45
 */
@Getter
@Setter
@ToString
public class KillSuccessUserInfo extends ItemKillSuccess implements Serializable {
    private static final long serialVersionUID = 4313134880680140568L;
    private String userName;

    private String phone;

    private String email;

    private String itemName;
}
