package com.msr.kill.controller;

import com.msr.kill.dto.KillDto;
import com.msr.kill.dto.KillSuccessUserInfo;
import com.msr.kill.enums.StatusCode;
import com.msr.kill.mapper.ItemKillSuccessMapper;
import com.msr.kill.response.BaseResponse;
import com.msr.kill.service.ItemKillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/6 15:49
 */
@Controller
@Slf4j
public class KillController {

    private static final String PREFIX = "kill";

    private final ItemKillService itemKillService;

    private final ItemKillSuccessMapper itemKillSuccessMapper;

    public KillController(ItemKillService itemKillService, ItemKillSuccessMapper itemKillSuccessMapper) {
        this.itemKillService = itemKillService;
        this.itemKillSuccessMapper = itemKillSuccessMapper;
    }

    /**
     * 秒杀商品
     *
     * @param killDto 秒杀商品数据
     * @param result  result
     * @return 响应结果
     */
    @PostMapping(value = PREFIX + "execute", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse execute(@RequestBody @Validated KillDto killDto, BindingResult result) {
        if (result.hasErrors() || killDto.getKillId() < 0) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);

        try {
            Boolean res = itemKillService.killItem(killDto.getKillId(), killDto.getUserId());
            if (!res) {
                return new BaseResponse(StatusCode.Fail.getCode(), "商品已经抢购完毕或者不在抢购时间段！");
            }

        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
            log.error("抢购错误！{}", this.getClass());
        }
        return response;
    }

    /**
     * @return 返回抢购成功页面
     */
    @GetMapping(value = PREFIX + "/execute/success")
    public String executeSuccess() {
        return "executeSuccess";
    }

    /**
     * @return 返回抢购失败页面
     */
    @GetMapping(value = PREFIX + "/execute/success")
    public String executeFail() {
        return "executeFail";
    }

    /**
     * 查看订单详情
     *
     * @return 跳转至详情页面
     */
    @RequestMapping(value = PREFIX + "/record/detail/{orderNo}", method = RequestMethod.GET)
    public String killRecordDetail(@PathVariable String orderNo, ModelMap modelMap) {
        if (StringUtils.isBlank(orderNo)) {
            return "error";
        }
        KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);
        if (info == null) {
            return "error";
        }
        modelMap.put("info", info);
        return "killRecord";
    }
}
