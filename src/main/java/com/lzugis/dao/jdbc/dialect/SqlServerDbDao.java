package com.lzugis.dao.jdbc.dialect;

import com.lzugis.dao.jdbc.IDbDao;

public class SqlServerDbDao implements IDbDao {

	public void forPaginate(StringBuilder sql, int pageNumber, int pageSize,
                            String select, String sqlExceptSelect) {
		int start = (pageNumber - 1) * pageSize + 1;
		int end = pageNumber * pageSize;
		sql.append("SELECT * FROM (SELECT *,ROW_NUMBER() OVER(")
		   .append(formatSqlOrderBy(sqlExceptSelect)).append(") AS rownum_ FROM (")
		   .append(select).append(" ").append(sqlExceptSelect)
		   .append(" ) as tabname_ ) as tabname_ WHERE tabname_.rownum_ > ")
		   .append(start).append(" and tabname_.rownum_ <=").append(end);
	}
	
	public static String formatSqlOrderBy(String sql) {
		sql = sql.replaceAll("(\\s)+", " ");
		int index = sql.toLowerCase().lastIndexOf("order by");
		if (index > sql.toLowerCase().lastIndexOf(")")) {
			String sql2 = sql.substring(index);
			return sql2;
		}
		//return " ";
		return " order by id ";
	}
}
