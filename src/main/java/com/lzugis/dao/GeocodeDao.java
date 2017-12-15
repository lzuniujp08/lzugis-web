package com.lzugis.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/14.
 */
@Repository
public class GeocodeDao extends CommonDao {
    /**
     * 根据条件获取数据
     * @param table
     * @param fields
     * @param filters
     * @param values
     * @return
     */
    public List getDataByFilter(String table, String[] fields, String filters, Object[] values){
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        for(int i=0;i<fields.length;i++){
            sql.append(fields[i]);
            if(i!=fields.length-1){
                sql.append(", ");
            }
        }
        sql.append(" from "+table + " "+ filters);
//        sql.append(" from "+table + " ORDER BY RANDOM() "+ filters);
        return sqliteJdbcTemplate.queryForList(sql.toString(), values);
    }

    /**
     * 根据经纬度获取最近点
     * @param table
     * @param fields
     * @param lon
     * @param lat
     * @return
     */
    public Map getNearestPoint(String table, String fields,
        double lon, double lat, String filter, Object[] values){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ").append(fields).append(" from( ")
                .append("SELECT ").append(fields)
                .append(", SQRT(POWER((x-"+lon+"),2)+POWER((y-"+lat+"),2)) as dis from ")
                .append(table).append(filter)
                .append(" ORDER BY dis) limit 1");
        List result =  sqliteJdbcTemplate.queryForList(sql.toString(), values);
        return result.size()>0?(Map)result.get(0):null;
    }

    /**
     * 判断数据是否存在
     * @param table
     * @param fields
     * @param values
     * @return
     */
    public int getCountByFilter(String table, String[] fields, Object[] values){
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) as count from "+table);
        if(fields.length>0)sql.append(" where ");
        for(int i=0;i<fields.length;i++){
            sql.append(fields[i]+"=?");
            if(i!=fields.length-1){
                sql.append(" and ");
            }
        }
        Map map = sqliteJdbcTemplate.queryForMap(sql.toString(), values);
        return (Integer) map.get("count");
    }

    /**
     * 数据删除操作
     * @param table
     * @param values
     * @return
     */
    public int delete(String table, String condition, Object[] values){
        StringBuffer sql = new StringBuffer();
        sql.append("delete from "+table+" where ")
            .append(condition);
        return sqliteJdbcTemplate.update(sql.toString(), values);
    }
    /**
     * 插入操作
     * @param table
     * @param fields
     * @param values
     * @return
     */
    public int insert(String table, String[] fields, Object[] values){
        StringBuffer sql = new StringBuffer();
        sql.append("insert into "+table+" (");
        String val = "";
        for(int i=0;i<fields.length;i++){
            sql.append(fields[i]);
            val = val + "?";
            if(i!=fields.length-1){
                sql.append(", ");
                val = val + ", ";
            }
        }
        sql.append(") values (").append(val).append(")");
        return sqliteJdbcTemplate.update(sql.toString(), values);
    }
    /**
     * 大数据量数据的入库
     * insert into table(field1, field2)
     *  select "value1", "value2" union
     *  select "value3", "value4" union
     * @param table
     * @param fields
     * @param unionsql
     * @return
     */
    public int insert(String table, String[] fields, String unionsql){
        StringBuffer sql = new StringBuffer();
        sql.append("insert into "+table+" (");
        String val = "";
        for(int i=0;i<fields.length;i++){
            sql.append(fields[i]);
            if(i!=fields.length-1){
                sql.append(", ");
            }
        }
        sql.append(")").append(unionsql);
        return sqliteJdbcTemplate.update(sql.toString());
    }

    /**
     * 更新操作
     * @param table
     * @param fields
     * @param filters
     * @param values
     * @return
     */
    public int update (String table, String[] fields,
                       String[] filters, Object[] values){
        StringBuffer sql = new StringBuffer();
        sql.append("update "+table+" set ");
        for(int i=0;i<fields.length;i++){
            sql.append(fields[i]+"=?");
            if(i!=fields.length-1){
                sql.append(", ");
            }
        }
        sql.append(" where ");
        for(int i=0;i<filters.length;i++){
            sql.append(filters[i]+"=?");
            if(i!=filters.length-1){
                sql.append(", ");
            }
        }
        return sqliteJdbcTemplate.update(sql.toString(), values);
    }
}
