package com.lzugis.web.controller;

import com.lzugis.services.UrlsServices;
import com.lzugis.services.model.UrlsModel;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

@Controller
public class UrlsController {
    @Autowired
    private UrlsServices services;

    @InitBinder("urlsModel")
    public void initBinderServer(WebDataBinder binder) {
        binder.setFieldDefaultPrefix("urlsModel.");
    }

    @RequestMapping(value="urls/getTreeData")
    public void getTreeData(boolean isdown, HttpServletResponse response){
        List treeData = services.getTreeData();
        try {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.println(JSONArray.toJSONString(treeData));
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequestMapping(value="urls/addUrl", method= RequestMethod.POST)
    public void addUrl(UrlsModel urlsModel, HttpServletResponse response){
        try {
            if(!StringUtils.isNotBlank(urlsModel.getId())) {
                urlsModel.setId(UUID.randomUUID().toString());
            }
            services.save(urlsModel);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.println("success");
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
