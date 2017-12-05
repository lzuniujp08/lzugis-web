package com.lzugis.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by lzugis on 2017/11/13.
 */
@Controller
public class WebController {
    @RequestMapping(value="web/goto")
    public ModelAndView gotoUrl(String url){
        ModelAndView mav = new ModelAndView(url);
        mav.getModel().put("msg", "Hello Lzugis");
        return mav;
    }
}
