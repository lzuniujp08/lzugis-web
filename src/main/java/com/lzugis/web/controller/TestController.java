package com.lzugis.web.controller;

import com.lzugis.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/13.
 */
@Controller
public class TestController {
    @Autowired
    private TestService testService;

    @RequestMapping(value="test/sayHello")
    public ModelAndView sayHello(String name){
        String msg = "Hello " + name;
        ModelAndView mav = new ModelAndView("hello");
        mav.getModel().put("msg", msg);
        return mav;
    }

    @RequestMapping(value="test/calculate")
    public void calcNums(double num1, double num2, String type, HttpServletResponse response) throws IOException{
        double r = testService.calcNums(num1, num2, type);
        Map<String, String> mapC = new HashMap();
        mapC.put("plus","+");
        mapC.put("minus","-");
        mapC.put("multi","×");
        mapC.put("division","➗");
        StringBuffer sb = new StringBuffer();
        sb.append(num1).append(" ").append(mapC.get(type)).append(" ").append(num2).append(" ")
                .append("=").append(" ").append(r);
        response.getWriter().println(sb.toString());
    }

    @RequestMapping(value="test/dbtest")
    public void databaseTest(HttpServletResponse response){
        try {
            List dbData = testService.getDbData();
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.getWriter().println(dbData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
