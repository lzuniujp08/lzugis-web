package com.lzugis.dao;


import com.lzugis.dao.jdbc.util.AnnotationUtil;
import com.lzugis.services.model.UrlsModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UrlsDao extends CommonDao {

    public List getTreeData(){
        String sql = "SELECT * FROM "+ AnnotationUtil.table(UrlsModel.class);
        return jdbcTemplate.queryForList(sql.toString());
    }
}
