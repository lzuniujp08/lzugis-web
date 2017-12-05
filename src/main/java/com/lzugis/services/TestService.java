package com.lzugis.services;

import com.lzugis.dao.TestDao;
import com.lzugis.dao.jdbc.util.AnnotationUtil;
import com.lzugis.services.model.GeocodePoint;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.*;

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

    public Map addGeoPOI(GeocodePoint poi){
        Map result = new HashMap();
        if(!StringUtils.isNotBlank(poi.getId())){
            poi.setId(UUID.randomUUID().toString());
        }
        result.put("flag", testDao.save(poi));
        return result;
    }
}
