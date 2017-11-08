package com.lzugis.web.servlet;

import com.lzugis.web.helper.CommonConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/10/27.
 */
@WebServlet(description = "close browser", urlPatterns =  {"/close"})
public class CloseBrowserServlet  extends HttpServlet {
    private List<Double> list;

    public CloseBrowserServlet() {
        super();
        list = new ArrayList();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String timee = request.getParameter("timee");
        String type = request.getParameter("type");
        list.add(Double.parseDouble(timee));
        int _s = list.size();
        double _abs = list.get(_s-1)-list.get(_s-2);
        _abs = _abs/1000;
        System.out.println(_abs);
        if(_abs<1){
            System.out.println("刷新");
        }else{
            System.out.println("关闭");
        }
    }

}
