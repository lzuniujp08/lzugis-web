package com.lzugis.services;

import com.lzugis.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/13.
 */
@Service
public class TestService {
    @Autowired
    TestDao testDao;

    public Double calcNums(double num1, double num2, String type){
        Map<String, Double> mapR = new HashMap();
        mapR.put("plus",num1+num2);
        mapR.put("minus",num1-num2);
        mapR.put("multi",num1*num2);
        mapR.put("division",num2==0?0:num1/num2);
        return mapR.get(type);
    }

    public List getDbData() throws Exception{
        List list = testDao.getDbData();
        return list;
    }
}
