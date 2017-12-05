package com.lzugis.dao.jdbc;

public interface IDbDao {

	void forPaginate(StringBuilder sql, int pageNumber, int pageSize, String select, String sqlExceptSelect);
}
