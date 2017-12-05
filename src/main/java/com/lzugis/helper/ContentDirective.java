package com.lzugis.helper;

import static freemarker.template.ObjectWrapper.DEFAULT_WRAPPER;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;


import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

/**
 * 自定义标签解析类
 * @author Administrator
 *
 */
public class ContentDirective implements TemplateDirectiveModel{

    private static final String PARAM_NAME = "name";
    private static final String PARAM_AGE = "age";

    public void execute(Environment env, Map params,TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        if(body==null){
            throw new TemplateModelException("null body");
        }else{
            String name = getString(PARAM_NAME, params);
            Integer age = getInt(PARAM_AGE, params);
            //接收到参数之后可以根据做具体的操作，然后将数据再在页面中显示出来。
            if(name!=null){
                env.setVariable("output", DEFAULT_WRAPPER.wrap("从ContentDirective解析类中获得的参数是："+name+", "));
            }
            if(age!=null){
                env.setVariable("append", DEFAULT_WRAPPER.wrap("年龄："+age));
            }
            Writer out = env.getOut();
            out.write("从这里输出可以再页面看到具体的内容，就像document.writer写入操作一样。<br/>");
            body.render(out);

            /*
            如果细心的话，会发现页面上是显示out.write（）输出的语句，然后再输出output的内容，
            可见 在body在解析的时候会先把参数放入env中，在页面遇到对应的而来表单时的才会去取值
            但是，如果该表单时不存在，就会报错，  我觉得这里freemarker没有做好，解析的时候更加会把错误暴露在页面上。
            可以这样子弥补${output!"null"},始终感觉没有el表达式那样好。
            */
        }
    }

    /**
     * 获取String类型的参数的值
     * @param paramName
     * @param paramMap
     * @return
     * @throws TemplateModelException
     */
    public static String getString(String paramName, Map<String, TemplateModel> paramMap) throws TemplateModelException{
        TemplateModel model = paramMap.get(paramName);
        if(model == null){
            return null;
        }
        if(model instanceof TemplateScalarModel){
            return ((TemplateScalarModel)model).getAsString();
        }else if (model instanceof TemplateNumberModel) {
            return ((TemplateNumberModel)model).getAsNumber().toString();
        }else{
            throw new TemplateModelException(paramName);
        }
    }

    /**
     *
     * 获得int类型的参数
     * @param paramName
     * @param paramMap
     * @return
     * @throws TemplateModelException
     */
    public static Integer getInt(String paramName, Map<String, TemplateModel> paramMap) throws TemplateModelException{
        TemplateModel model = paramMap.get(paramName);
        if(model==null){
            return null;
        }
        if(model instanceof TemplateScalarModel){
            String str = ((TemplateScalarModel)model).getAsString();
            try {
                return Integer.valueOf(str);
            } catch (NumberFormatException e) {
                throw new TemplateModelException(paramName);
            }
        }else if(model instanceof TemplateNumberModel){
            return ((TemplateNumberModel)model).getAsNumber().intValue();
        }else{
            throw new TemplateModelException(paramName);
        }
    }
}
