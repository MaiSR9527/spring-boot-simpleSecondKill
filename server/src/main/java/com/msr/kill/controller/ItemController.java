package com.msr.kill.controller;

import com.msr.kill.entity.ItemKill;
import com.msr.kill.service.ItemKillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Description: 待秒杀商品controller
 * @Author: maishuren
 * @Date: 2019/9/5 22:42
 */

@Controller
@Slf4j
public class ItemController {

    private static final String PREFIX = "item";

    @Autowired
    private ItemKillService itemKillService;

    /**
     * 获取商品列表
     */
    @GetMapping(value = {"/", "/index", PREFIX + "/index.html"})
    public String list(ModelMap modelMap) {

        try {
            //获待带秒杀商品列表
            List<ItemKill> killItems = itemKillService.getKillItems();
            modelMap.put("list", killItems);
            log.info("获取待秒杀商品成功：{}", killItems.toString());
        } catch (Exception e) {
            log.error("获取待秒杀商品列表-发生异常：", e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "list";
    }

    @GetMapping(value = PREFIX + "/detail/{id}")
    public String detail(@PathVariable("id") Integer id,ModelMap modelMap) {
        if (id == null || id < 0) {
            return "redirect:/base/error";
        }

        try {
            //获取商品详情
            ItemKill detail = itemKillService.getKillById(id);
            modelMap.put("detail",detail);
        } catch (Exception e) {
            log.error("获取待秒杀商品列表-发生异常：", e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "info";
    }
}
