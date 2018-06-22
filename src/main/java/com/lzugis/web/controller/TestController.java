package com.lzugis.web.controller;

import com.lzugis.services.TestService;
import com.lzugis.services.model.GeocodePoint;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by admin on 2017/11/13.
 */
@Controller
public class TestController {
    @Autowired
    private TestService testService;

    @InitBinder("geocodePoint")
    public void initBinderServer(WebDataBinder binder) {
        binder.setFieldDefaultPrefix("geopoi.");
    }

    @RequestMapping(value="test/sayHello")
    @ResponseBody
    public ModelAndView sayHello(String name){
        String msg = "Hello " + name;
        ModelAndView mav = new ModelAndView("hello");
        mav.getModel().put("msg", msg);
        return mav;
    }

    @RequestMapping(value="test/calculate")
    @ResponseBody
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
    @ResponseBody
    public List databaseTest(HttpServletResponse response){
        try {
            List dbData = testService.getDbData();
            return dbData;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value="route")
    @ResponseBody
    public Map getShortRoute(double startx, double starty, double endx, double endy){
        try {
            Map result = testService.getShortRoute(startx, starty, endx, endy);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping({"/poi/add"})
    @ResponseBody
    public List addGeoPOI(GeocodePoint poi){
        List list = new ArrayList();
        list.add(poi);
        list.add(poi);
        list.add(poi);
        list.add(poi);
        return list;
    }

    @RequestMapping({"/dem/query"})
    @ResponseBody
    public List addGeoPOI(String points){
        return testService.getLineDem(points);
    }
}