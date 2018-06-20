package com.lzugis.web.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name="WebAgent", urlPatterns="/web/agent")
public class UrlContentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public UrlContentServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        /* 允许跨域的主机地址 */
        response.setHeader("Access-Control-Allow-Origin", "*");
        /* 允许跨域的请求方法GET, POST, HEAD 等 */
        response.setHeader("Access-Control-Allow-Methods", "*");
        /* 重新预检验跨域的缓存时间 (s) */
        response.setHeader("Access-Control-Max-Age", "3600");
        /* 允许跨域的请求头 */
        response.setHeader("Access-Control-Allow-Headers", "*");
        /* 是否携带cookie */
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String encoding = getInitParameter("encoding");
        if ((encoding != null) && (!"".equals(encoding))) {
            request.setCharacterEncoding(encoding);
        }
        String url = request.getParameter("url");
        if (!"".equals(url)) {
            responseFile(request, response, url);
            return;
        }
    }

    public void responseFile(HttpServletRequest req, HttpServletResponse resp, String url) {
        try {
            URL u = new URL(url);

            URLConnection urlc = u.openConnection();
            urlc.setConnectTimeout(3000);
            urlc.setReadTimeout(100000);

            resp.setContentType(urlc.getContentType());

            ServletOutputStream out = null;
            BufferedInputStream input = null;
            byte[] data = new byte[1024];
            int length = -1;
            input = new BufferedInputStream(urlc.getInputStream());
            out = resp.getOutputStream();
            while ((length = input.read(data)) != -1) {
                out.write(data, 0, length);
            }
            input.close();
            out.flush();
            out.close();
        }
        catch (IOException localIOException)
        {
        }
    }

    private String readXMLFromRequestBody(HttpServletRequest request) {
        StringBuffer strBuf = new StringBuffer();
        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null)
                strBuf.append(line).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strBuf.toString();
    }
}
