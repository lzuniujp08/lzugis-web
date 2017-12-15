package com.lzugis.dao;

import com.lzugis.dao.jdbc.DbFactory;
import com.lzugis.dao.jdbc.DbType;
import com.lzugis.dao.jdbc.util.AnnotationUtil;
import com.lzugis.helper.CommonConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.sqlite.SQLiteDataSource;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by admin on 2017/11/14.
 */
public class CommonDao {
    // 创建JDBC模板
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected LobHandler lobHandler;

    protected JdbcTemplate sqliteJdbcTemplate;

    private DbType databaseType;

    public CommonDao(){
        super();
        /**
         * 初始化sqlite
         */
        SQLiteDataSource source = new SQLiteDataSource();
        String dbPath = CommonConfig.getVal("geocode.dbpath");
        source.setUrl("jdbc:sqlite:"+dbPath);
        sqliteJdbcTemplate = new JdbcTemplate(source);
    }

    public String table(Class clazz) {
        return AnnotationUtil.table(clazz);
    }
    public String table(Object bean){
        return AnnotationUtil.table(bean);
    }

    public int add(Object bean,boolean ispart){
        String table = AnnotationUtil.table(bean);
        Object[] obj = AnnotationUtil.field(bean, ispart);
        List keys= (List)obj[0];
        List vals = (List)obj[1];
        return insert(table,vals.toArray(new Object[vals.size()]),(String[])keys.toArray(new String[keys.size()]));

    }

    public int add(Object bean){
        return add(bean,false);
    }

    public int edit(Object bean,boolean ispart){
        String table = table(bean);
        Object[] obj = AnnotationUtil.updatefield(bean, ispart);
        List keys= (List)obj[0];
        List ids = (List)obj[1];
        return update(table,(String[])ids.toArray(new String[ids.size()]),(String[])keys.toArray(new String[keys.size()]),bean);

    }

    public int edit(Object bean){
        return edit(bean,false);
    }

    public Object get(Object bean){
        String table = table(bean);
        Object[] obj = AnnotationUtil.updatefield(bean, false);
        List ids = (List)obj[1];
        String sql = String.format("select * from %1$s  where %2$s ",
                new Object[] { table, StringUtils.join(ids, " = ? and ") + " = ?" });

        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        List<Object> values = new ArrayList();
        int i = 0;
        for (int len = ids.size(); i < len; i++) {
            values.add(bw.getPropertyValue((String)ids.get(i)));
        }
        List bs= this.jdbcTemplate.query(sql, values.toArray(), new BeanPropertyRowMapper(bean.getClass()));
        if(bs == null||bs.isEmpty()){
            return null;
        }else{
            return bs.get(0);
        }
    }
    public Object get(Class clazz,String val,String key){
        String table = table(clazz);
        String sql = String.format("select * from %1$s  where %2$s ",
                new Object[] { table, key+" = ?" });
        List bs= this.jdbcTemplate.query(sql, new Object[]{val}, new BeanPropertyRowMapper(clazz));
        if(bs == null||bs.isEmpty()){
            return null;
        }else{
            return bs.get(0);
        }
    }

    public Object get(Class clazz,String val){
        return get(clazz,val,"id");
    }

    public int save(Object bean,boolean ispart){
        Object b = get(bean);
        if(b==null){
            return add(bean,ispart);
        }else{
            return edit(bean,ispart);
        }
    }

    public int save(Object bean){
        return save(bean,false);
    }
    /**
     *
     * @param bean 对象，包括JTable注解的对象
     * @param orderKey 字符串 ，比如  id asc 或者  addtime desc,id desc 等
     * @return
     */
    public List find(Object bean,String orderKey){
        String table = table(bean);
        Object[] obj = AnnotationUtil.field(bean, true);
        List keys= (List)obj[0];
        List vals = (List)obj[1];

        if(StringUtils.isBlank(orderKey)){
            orderKey = keys.get(0).toString() +" asc ";
        }

        String sql = String.format("select * from %1$s where %2$s order by %3$s", new Object[] {
                table, StringUtils.join(keys, " = ? and ") + " = ?",
                orderKey +" "});

        return this.jdbcTemplate.query(sql, vals.toArray(),new BeanPropertyRowMapper(bean.getClass()));
    }
    /**
     * List find(Object bean,String orderKey) 的默认简写，默认排序为 ：addtime desc
     * @param bean 对象，包括JTable注解的对象
     * @return
     */
    public List find(Object bean){
        return find(bean," addtime desc ");
    }

    /**
     * @描述 新增方法
     * @param table 字符串，表名，比如：sys_user
     * @param data 对象数组，要插入的值，比如：['A001','zhangsan','张三']
     * @param fields 字符串数组，字段， 比如：['id','usercode','username']
     * @return
     */
    public int insert(String table, Object[] data, String[] fields) {
        String sql = String.format("insert into %1$s (%2$s) values (%3$s)",
                new Object[] { table,
                        StringUtils.join(fields, ','),
                        StringUtils.rightPad("?", fields.length * 2 - 1, ",?") }
        );

        return this.jdbcTemplate.update(sql, data);
    }

    /**
     * @描述 新增方法
     * @param table 字符串，表名，比如：sys_user
     * @param data 对象数组，要插入的值，比如：['A001','zhangsan','张三']
     * @param fields 字符串，字段， 比如："id,usercode,username"
     * @return
     */
    public int insert(String table, Object[] data, String fields) {
        String[] names = StringUtils.split(fields, ",");
        return this.insert(table, data, names);
    }

    public int insert(String table, Class clazz, Object bean) {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        Field[] fields = clazz.getDeclaredFields();
        List<String> names = new ArrayList();
        List<Object> values = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            names.add(f.getName());
            values.add(bw.getPropertyValue(f.getName()));
        }
        String sql = String.format("insert into %1$s (%2$s) values (%3$s)",
                new Object[] { table, StringUtils.join(names, ','),
                        StringUtils.rightPad("?", names.size() * 2 - 1, ",?") });

        return this.jdbcTemplate.update(sql, values.toArray());
    }

    public int insert(String table, Class clazz, Object bean, String fields) {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        String[] names = StringUtils.split(fields, ",");
        List<Object> values = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            values.add(bw.getPropertyValue(names[i]));
        }
        String sql = String.format("insert into %1$s (%2$s) values (%3$s)",
                new Object[] { table, StringUtils.join(names, ','),
                        StringUtils.rightPad("?", names.length * 2 - 1, ",?") });

        return this.jdbcTemplate.update(sql, values.toArray());
    }

    public int insert(String table, Map data, String fields) {
        String[] names = StringUtils.split(fields, ",");
        String sql = String.format("insert into %1$s (%2$s) values (%3$s)",
                new Object[] { table, StringUtils.join(names, ','),
                        StringUtils.rightPad("?", names.length * 2 - 1, ",?") });

        List list = new ArrayList();
        for (String name : names) {
            list.add(data.get(name));
        }
        return this.jdbcTemplate.update(sql, list.toArray());
    }

    public int insert(String table, Map data) {
        List fields = new ArrayList();
        for(Iterator it = data.keySet().iterator(); it.hasNext();){
            fields.add(it.next());
        }
        String fieldstr = StringUtils.join(fields, ",");
        return this.insert(table, data, fieldstr);
    }

    public int update(String table, String primaryKey, Class clazz, Object bean) {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        Field[] fields = clazz.getDeclaredFields();
        List<String> names = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();
        Object pkValue = null;
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (primaryKey.equalsIgnoreCase(f.getName())) {
                pkValue = bw.getPropertyValue(f.getName());
            } else {
                names.add(f.getName());
                values.add(bw.getPropertyValue(f.getName()));
            }
        }
        values.add(pkValue);
        String sql = String.format("update %1$s set %2$s where %3$s", new Object[] { table,
                StringUtils.join(names, "=?,") + "=?", primaryKey + " = ?" });


        return this.jdbcTemplate.update(sql, values.toArray());
    }

    public int update(String table, String primaryKey, Class clazz, Object bean,
                      String[] ignoreFields) {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        Field[] fields = clazz.getDeclaredFields();
        List<String> names = new ArrayList();
        List<Object> values = new ArrayList();
        Object pkValue = null;
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (primaryKey.equalsIgnoreCase(f.getName())) {
                pkValue = bw.getPropertyValue(f.getName());
            } else {
                boolean isIgnore = false;
                int j = 0;
                for (int len = ignoreFields.length; j < len; j++) {
                    if (ignoreFields[j].equalsIgnoreCase(f.getName())) {
                        isIgnore = true;
                        break;
                    }
                }
                if (!isIgnore) {
                    names.add(f.getName());
                    values.add(bw.getPropertyValue(f.getName()));
                }
            }
        }
        values.add(pkValue);
        String sql = String.format("update %1$s set %2$s where %3$s", new Object[] { table,
                StringUtils.join(names, "=?,") + "=?", primaryKey + " = ?" });

        return this.jdbcTemplate.update(sql, values.toArray());
    }

    /**
     * @描述 更新语句
     * @param table 字符串，表名，比如："sys_user"
     * @param primaryKey 字符串，主键，一个值，比如："id"
     * @param data  Map对象，需要更新的数据。比如{'id':'A001','username':'张三','usercode':'zhangsan'}
     * @param fields 字符串，可以对个值，用英文符号逗号（,）隔开，比如："username,usercode"
     * @return 返回更新条数
     */
    public int update(String table, String primaryKey, Map data, String fields) {
        String[] names = fields.split(",");
        List<Object> values = new ArrayList();
        Object pkValue = data.get(primaryKey);
        for (int i = 0; i < names.length; i++) {
            values.add(data.get(names[i]));
        }
        values.add(pkValue);
        String sql = String.format("update %1$s set %2$s where %3$s", new Object[] { table,
                StringUtils.join(names, "=?,") + "=?", primaryKey + " = ?" });

        return this.jdbcTemplate.update(sql, values.toArray());
    }

    public int update(String table, String primaryKey, Object bean, String fields) {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        String[] names = fields.split(",");
        List<Object> values = new ArrayList();
        Object pkValue = null;
        for (int i = 0; i < names.length; i++) {
            if (!primaryKey.equalsIgnoreCase(names[i])) {
                values.add(bw.getPropertyValue(names[i]));
            }
        }
        pkValue = bw.getPropertyValue(primaryKey);
        values.add(pkValue);
        String sql = String.format("update %1$s set %2$s where %3$s", new Object[] { table,
                StringUtils.join(names, "=?,") + "=?", primaryKey + " = ?" });

        return this.jdbcTemplate.update(sql, values.toArray());
    }
    /**
     * 更新方法
     * @param sql 字符串，更新的语句，比如："update sys_user set username=?,usercoce=? where id=? "
     * @param params 对象数组，注意和语句中占位符（?）的对应顺序
     * @return 返回影响的记录数
     */
    public int update(String sql, Object[] params) {
        return this.jdbcTemplate.update(sql, params);
    }

    public int update(String table, String[] primaryKeys, String[] fields, Object bean) {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        List<Object> values = new ArrayList();
        int i = 0;
        for (int len = fields.length; i < len; i++) {
            values.add(bw.getPropertyValue(fields[i]));
        }
        i = 0;
        for (int len = primaryKeys.length; i < len; i++) {
            values.add(bw.getPropertyValue(primaryKeys[i]));
        }
        String sql = String.format("update %1$s set %2$s where %3$s",
                new Object[] { table,
                        StringUtils.join(fields, " = ?,") + " = ?",
                        StringUtils.join(primaryKeys, " = ? and ") + " = ?" });

        return this.jdbcTemplate.update(sql, values.toArray());
    }

    public int update(String table, String[] primaryKeys, String[] fields, Map data) {
        List<Object> values = new ArrayList();
        int i = 0;
        for (int len = fields.length; i < len; i++) {
            values.add(data.get(fields[i]));
        }
        i = 0;
        for (int len = primaryKeys.length; i < len; i++) {
            values.add(data.get(primaryKeys[i]));
        }
        String sql = String.format("update %1$s set %2$s where %3$s", new Object[] { table,
                StringUtils.join(fields, " = ?,") + " = ?",
                StringUtils.join(primaryKeys, " = ? and ") + " = ?" });

        return this.jdbcTemplate.update(sql, values.toArray());
    }
    public void batchUpdate(String sql, final Object[] arr, final Object[] ids) {
        int[] result = this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int index = 0;
                if (arr != null) {
                    for (; index < arr.length; index++) {
                        ps.setObject(index + 1, arr[index]);
                    }
                }
                ps.setObject(index + 1, ids[i]);
            }

            public int getBatchSize() {
                return ids.length;
            }
        });
        if ((result == null) || (result.length != ids.length)) {
            throw new RuntimeException("批量更新错误！");
        }
    }

    public void batchUpdate(String sql, final List<List> items) {
        int[] result = this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int index = 0;
                List item = items.get(i);
                if (item != null) {
                    for (; index < item.size(); index++) {
                        ps.setObject(index + 1, item.get(index));
                    }
                }
            }
            public int getBatchSize() {
                return items.size();
            }
        });
        if ((result == null) || (result.length != items.size())) {
            throw new RuntimeException("批量更新错误！");
        }
    }

    public void updateClobAsString(String table, String clobFiled, String[] primaryKeys,
                                   final Object[] params) {
        String sql = String.format("update %1$s set %2$s where %3$s", new Object[] { table,
                clobFiled + " = ? ", StringUtils.join(primaryKeys, " = ? and ") + " = ?" });

        this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                CommonDao.this.lobHandler.getLobCreator().setClobAsString(ps, 1, (String) params[0]);
                for (int i = 1; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
        });
    }


    public int delete(String table, String[] primaryKeys, Map data) {
        List<Object> values = new ArrayList();
        for (String key : primaryKeys) {
            values.add(data.get(key));
        }
        String sql = String.format("delete from %1$s where %2$s", new Object[] { table,
                StringUtils.join(primaryKeys, " = ? and ") + " = ?" });

        return this.jdbcTemplate.update(sql, values.toArray());
    }

    public int delete(String table, String primaryKey, int value) {
        String sql = String.format("delete from %1$s where %2$s = ?", new Object[] { table,
                primaryKey });
        return this.jdbcTemplate.update(sql, new Object[] { Integer.valueOf(value) });
    }

    public int delete(String table, String primaryKey, String value) {
        String sql = String.format("delete from %1$s where %2$s = ?", new Object[] { table,
                primaryKey });
        return this.jdbcTemplate.update(sql, new Object[] { value });
    }

    public int delete(String table, Object[] values, String primarykeys) {
        String sql = String.format("delete from %1$s where %2$s", new Object[] { table,
                primarykeys.replaceAll(",", "=? and ") + "=?" });
        return this.jdbcTemplate.update(sql, values);
    }


    public List select(String table, String[] filedNames, Class clazz, Object[] value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filedNames.length; i++) {
            sb.append(" and ").append(filedNames[i]).append("=? ");
        }
        String sql = String.format("select * from %1$s where 1 = 1 ", new Object[] { table });
        return this.jdbcTemplate.query(sql + sb.toString(), value, new BeanPropertyRowMapper<List>(clazz));
    }

    public Object select(String table, String primaryKey, Class clazz, Object value) {
        String sql = String.format("select * from %1$s where %2$s = ?", new Object[] {table,primaryKey });
        List result = this.jdbcTemplate.query(sql, new Object[] { value }, new BeanPropertyRowMapper<Object>(clazz));
        return result.size() > 0 ? result.get(0) : null;
    }

    public List<Map<String, Object>> select(String table, String primaryKey, Object value) {
        String sql = String.format("select * from %1$s where %2$s = ?", new Object[] { table,
                primaryKey });
        return this.jdbcTemplate.queryForList(sql, new Object[] { value });
    }

    public <T> List<T> select(String table, Map<String, String> aliasMap, Class<T> clazz,
                              Map<String, ?> conds) {
        String sql = buildSql(table, aliasMap, clazz, conds);
        Object[] values = new Object[conds.keySet().size()];
        Integer index = Integer.valueOf(0);
        String key;
        Integer localInteger1;
        for (Iterator<String> itr = conds.keySet().iterator(); itr.hasNext(); values[localInteger1
                .intValue()] = conds.get(key)) {
            key = (String) itr.next();
            localInteger1 = index;
            Integer localInteger2 = index = Integer.valueOf(index.intValue() + 1);
        }
        return this.jdbcTemplate.query(sql, values, new BeanPropertyRowMapper(clazz));
    }

    public <T> List<T> select(String table, Map<String, String> aliasMap, Class<T> clazz) {
        String sql = buildSql(table, aliasMap, clazz);
        return this.jdbcTemplate.query(sql, new BeanPropertyRowMapper(clazz));
    }

    public <T> List<T> select(String table, Class<T> clazz) {
        String sql = buildSql(table, clazz);
        return this.jdbcTemplate.query(sql, new BeanPropertyRowMapper(clazz));
    }


    public List<Map<String, Object>> select(String table, Object[] data, String[] fields,
                                            String orderKey, boolean asc) {
        String sql = String.format("select * from %1$s where %2$s order by %3$s", new Object[] {
                table, StringUtils.join(fields, " = ? and ") + " = ?",
                orderKey + " " + (asc ? "asc" : "desc") });

        return this.jdbcTemplate.queryForList(sql, data);
    }


    public List<Map<String, Object>> selectForList(String tableName, String[] primaryKeys,
                                                   String[] fieldNames, Object[] values) {
        String sql = String.format("select %1$s from %2$s where %3$s", new Object[] {
                StringUtils.join(fieldNames, ","), tableName,
                StringUtils.join(primaryKeys, " = ? and ") + " = ?" });

        return this.jdbcTemplate.queryForList(sql, values);
    }

    public List<Map<String, Object>> selectForList(String sql, Object[] params) {
        return this.jdbcTemplate.queryForList(sql, params);
    }

    public List<Map<String, Object>> selectForList(String sql) {
        return this.jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> selectForMap(String tableName, String[] primaryKeys,
                                            String fieldName, Object[] values) {
        String sql = String.format("select %1$s from %2$s where %3$s", new Object[] { fieldName,
                tableName, StringUtils.join(primaryKeys, " = ? and ") + " = ?" });

        return this.jdbcTemplate.queryForMap(sql, values);
    }

    public Map<String, Object> selectForMap(String tableName, String[] primaryKeys,
                                            String[] fieldNames, Object[] values) {
        String sql = String.format("select %1$s from %2$s where %3$s", new Object[] {
                StringUtils.join(fieldNames, ","), tableName,
                StringUtils.join(primaryKeys, " = ? and ") + " = ?" });

        return this.jdbcTemplate.queryForMap(sql, values);
    }

    public String selectForString(String tableName, String[] primaryKeys, String fieldName,
                                  Object[] values) {
        String sql = String.format("select %1$s from %2$s where %3$s", new Object[] { fieldName,
                tableName, StringUtils.join(primaryKeys, " = ? and ") + " = ?" });

        List<String> list = this.jdbcTemplate.query(sql, values, new SingleColumnRowMapper<String>(String.class));
        return list.size() == 0 ? "" : (String) list.get(0);
    }

    /**
     *
     * @param sql  字符串，比如：select * from sys_user order by id desc
     * @param clazz  类，比如  User，该对象要和查询sql匹配
     * @return 包括User对象的List集合
     */
    public List find(String sql,Class clazz){
        return this.jdbcTemplate.query(sql,new BeanPropertyRowMapper(clazz));
    }

    /**
     *
     * @param sql  字符串，比如：select * from sys_user  where usercode=? order by id desc
     * @param clazz  类，比如  User，该对象要和查询sql匹配
     * @param params 对象数组，比如：zhangsan
     * @return 包括User对象的List集合
     */
    public List find(String sql,Class clazz,Object...params){
        return this.jdbcTemplate.query(sql,params,new BeanPropertyRowMapper(clazz));
    }
    /**
     * @描述 查询方法
     * @param clazz 类，包括注解JTable的的类，比如 User
     * @param where 字符串，查询条件,比如：order by id desc 或者 where id=? and 2=2
     * @param params -数组对象，和where中占位符（?）对应,注意对应顺序，
     * @return 返回包括对象的列表
     */
    public List findBy(Class clazz,String where,Object...params){
        String sql = String.format("select * from %1$s  %2$s", new Object[] { table(clazz),	where});
        return this.jdbcTemplate.query(sql,params,new BeanPropertyRowMapper(clazz));
    }
    /**
     * @描述 查询方法，查询第一个对象
     * @param clazz 类，包括注解JTable的的类，比如 User
     * @param where 字符串，查询条件,比如：order by id desc 或者 where id=? and 2=2
     * @param params 数组对象，和where中占位符（?）对应,注意对应顺序，
     * @return  返回包括查询列表中的第一个对象
     */
    public Object findFirst(Class clazz,String where,Object...params){
        List result = findBy(clazz,where,params);
        if(result==null||result.isEmpty()){
            return null;
        }
        return result.get(0);
    }
    /**
     *  @描述 查询方法，返回查询的数量
     * @param clazz 类，包括注解JTable的的类，比如 User
     * @param where 字符串，查询条件,比如：order by id desc 或者 where id=? and 2=2
     * @param params 数组对象，和where中占位符（?）对应,注意对应顺序，
     * @return
     */
    public int countBy(Class clazz,String where,Object...params){
        String sql = String.format("select count(*) as n from %1$s  %2$s", new Object[] { table(clazz),	where});
        List <Long>result =  this.jdbcTemplate.queryForList(sql,params,Long.class);
        Long count = result.get(0);
        return count.intValue();
    }

    private <T> String buildSql(String table, Map<String, String> aliasMap, Class<T> clazz,Map<String, ?> conds) {
        StringBuffer sql = new StringBuffer(buildSql(table, aliasMap, clazz));
        if (conds.isEmpty()) {
            return sql.toString();
        }
        sql.append(" \n where ");
        for (Iterator<String> itr = conds.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            sql.append(key).append("=?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        return sql.toString();
    }

    private <T> String buildSql(String table, Map<String, String> aliasMap, Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 0) {
            throw new RuntimeException("no fields declared in class ".concat(clazz.getName()));
        }
        StringBuilder sb = new StringBuilder("select ");
        for (Field f : fields) {
            String key = f.getName();
            String keyAlias = (String) aliasMap.get(key);
            if (StringUtils.isNotBlank(keyAlias)) {
                if (getDatabaseType()==DbType.SQLSERVER) {
                    sb.append(keyAlias).append(" [").append(key).append("],");
                } else {
                    sb.append(keyAlias).append(" ").append(key).append(",");
                }
            }else{
                sb.append(key).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ").append(table);
        return sb.toString();
    }

    private String buildSql(String table, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 0) {
            throw new RuntimeException("no fields declared in class".concat(clazz.getName()));
        }
        StringBuilder sb = new StringBuilder("select ");
        for (Field f : fields) {
            String key = f.getName();
            sb.append(key).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ").append(table);
        return sb.toString();
    }

    public String insertSql(String table, String fields) {
        String[] names = StringUtils.split(fields, ",");
        return this.insertSql(table, names);
    }
    public String insertSql(String table, String[] fields) {
        String sql = String.format("insert into %1$s (%2$s) values (%3$s)",
                new Object[] { table, StringUtils.join(fields, ','),
                        StringUtils.rightPad("?", fields.length * 2 - 1, ",?") });

        return sql;
    }

    /**
     * @描述 将map对象中的，key 全部转换为小写。
     *      比如：{"CODE":"A001"}转换为{"code":"A001"}
     * @param map
     * @return
     */
    protected Map lowerMap(Map map){
        if(map ==null||map.isEmpty()){
            return map;
        }

        Map lowMap = null;
        try {
            lowMap = map.getClass().newInstance();
        } catch (Exception e) {
            lowMap = new HashMap();
        }
        Iterator it = map.keySet().iterator();
        while(it.hasNext()){
            String key = (String)it.next();
            lowMap.put(key.toLowerCase(),map.get(key));
        }
        return lowMap;
    }
    /**
     * @描述 将map对象中的，key 全部转换为小写。
     *       比如：[{"CODE":"A001"},{"CODE":"A004"},]转换为[{"code":"A001"},{"code":"A004"}]
     * @param result
     * @return
     */
    protected List lowerList(List<Map> result){
        if(result==null ||result.isEmpty()){
            return result;
        }
        List ret=new ArrayList();
        for(Map m:result){
            ret.add(lowerMap(m));
        }
        return ret;
    }

    public static String replaceFormatSqlOrderBy(String sql) {
        sql = sql.replaceAll("(\\s)+", " ");
        int index = sql.toLowerCase().lastIndexOf("order by");
        if (index > sql.toLowerCase().lastIndexOf(")")) {
            String sql1 = sql.substring(0, index);
            String sql2 = sql.substring(index);
            sql2 = sql2.replaceAll("[oO][rR][dD][eE][rR] [bB][yY] [\u4e00-\u9fa5a-zA-Z0-9_.]+((\\s)+(([dD][eE][sS][cC])|([aA][sS][cC])))?(( )*,( )*[\u4e00-\u9fa5a-zA-Z0-9_.]+(( )+(([dD][eE][sS][cC])|([aA][sS][cC])))?)*", "");
            return sql1 + sql2;
        }
        return sql;
    }

    public Page paginate(Class clazz,int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
        if (pageNumber < 1 || pageSize < 1){
            throw new RuntimeException("pageNumber and pageSize must be more than 0");
        }
        long totalRow = 0;
        int totalPage = 0;
        //List result = jt.queryForList("select count(*) " + replaceFormatSqlOrderBy(sqlExceptSelect), paras);
        List<Map<String,Object>> result = this.jdbcTemplate.queryForList("select count(*) as num" + replaceFormatSqlOrderBy(sqlExceptSelect), paras);
        int size = result.size();
        if (size == 1){
            //totalRow = ((Number)result.get(0)).longValue();
            totalRow = ((Number)(result.get(0).get("num"))).longValue();
        }else if (size > 1){
            totalRow = result.size();
        }else{
            return new Page(new ArrayList(0), pageNumber, pageSize, 0, 0);
        }

        totalPage = (int) (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }

        // --------
        StringBuilder sql = new StringBuilder();
        DbFactory.getDbDao(this.getDatabaseType()).forPaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
        //List<Record> list = this.find(sql.toString(), paras);
        List list = this.jdbcTemplate.query(sql.toString(), paras,new BeanPropertyRowMapper(clazz));
        return new Page(list, pageNumber, pageSize, totalPage, (int)totalRow);
    }

    public DbType getDatabaseType() {
        if (this.databaseType == null) {
            try {
                String databaseProductName = this.jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName();
                this.databaseType = DbFactory.getDbType(databaseProductName);
            } catch (SQLException e) {
            }
        }
        return this.databaseType;
    }

}
