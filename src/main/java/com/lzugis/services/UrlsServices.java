package com.lzugis.services;

import com.lzugis.dao.TestDao;
import com.lzugis.dao.UrlsDao;
import com.lzugis.services.model.UrlsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlsServices {

    @Autowired
    private UrlsDao dao;


    public List getTreeData(){
        return dao.getTreeData();
    }

    public void save(UrlsModel urls){
        dao.save(urls);
    }
}
