package com.lzugis.helper;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.poi.xssf.usermodel.helpers.RichTextStringHelper;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/11/20.
 */
public class CommonMethod {
    /**
     * 获取文件内容
     * @param filePath
     * @return
     */
    public String getFileContent(String filePath){
        StringBuffer sb = new StringBuffer();
        try {
            String encoding="GBK";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    sb.append(lineTxt);
                }
                read.close();
            }
            else{
                System.out.println("找不到指定的文件");
            }
        }
        catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return sb.toString();
    }
    public void append2File(String file, String content, boolean isclear) {
        File f = new File(file);

        //文件夹是否存在，不存在，则创建
        File filePath = new File(f.getPath());
        if(!filePath.exists()) filePath.mkdirs();

        //文件存在且删除，删除原有文件
        if(isclear && f.exists()) f.delete();

        FileWriter fw = null;
        try {
            fw = new FileWriter(f, true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(content);
        pw.flush();

        try {
            fw.flush();
            pw.close();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrlContent(String url){
        String content = "";
        InputStream is = null;
        try {
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            content = sb.toString();
            is.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public JSONObject getUrlJSON(String url) {
        JSONObject json = null;
        InputStream is = null;
        try {
            String strJson = getUrlContent(url);
            json = new JSONObject(strJson);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public Map geoCode(String keyword){
        Map result = new HashMap();
        JSONObject json = null;
        try {
            StringBuffer url = new StringBuffer();
            url.append("http://api.tianditu.com/apiserver/ajaxproxy?proxyReqUrl=http://api.tianditu.com/search?postStr=");
            Map para = new HashMap();
            para.put("keyWord", keyword);
            para.put("level", "4");
            para.put("mapBound", "63.10547,22.99885,163.125,50.56928");
            para.put("queryType", "2");
            para.put("start", "0");
            para.put("count", "10");
            url.append(org.json.simple.JSONObject.toJSONString(para));
            url.append("&type=query");
            String content = getUrlContent(url.toString());
            content = content.substring(19, content.length() - 1);
            json = new JSONObject(content);
            System.out.println(content);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将汉字转换为全拼
     *
     * @param src
     * @return
     */
    public String getPingYin(String src) {
        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();

        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (Character.toString(t1[i]).matches(
                        "[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else
                    t4 += Character.toString(t1[i]);
            }
            // System.out.println(t4);
            return t4;
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        return t4;
    }

    /**
     * 返回中文的首字母
     *
     * @param str
     * @return
     */
    public String getPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    /**
     * 将字符串转移为ASCII码
     *
     * @param cnStr
     * @return
     */
    public String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }

    public static void main(String[] args){
        File file = new File("d:/test/province.zip");
        System.out.println(file.getPath());
        File filePath = new File(file.getPath());
        if(!filePath.exists()){
            filePath.mkdirs();
        }
    }
}
