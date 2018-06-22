# postgis最优路径查询

## 1. 创建拓扑
```sql
--添加起点id
ALTER TABLE crossroad ADD COLUMN source integer;

--添加终点id
ALTER TABLE crossroad ADD COLUMN target integer;

--添加道路权重值
ALTER TABLE crossroad ADD COLUMN length double precision;

--为sampledata表创建拓扑布局，即为source和target字段赋值，生成road_vertices_pgr
SELECT pgr_createTopology('road',0.00001, 'geom', 'gid');

--为source和target字段创建索引
CREATE INDEX source_idx ON road ("source");
CREATE INDEX target_idx ON road ("target");

--为length赋值
update road set length =st_length(geom);

--添加reverse_cost字段并用length的值赋值
ALTER TABLE road ADD COLUMN reverse_cost double precision;
UPDATE road SET reverse_cost =length;
```


## 2.创建查询函数
```sql
DROP FUNCTION pgr_fromAtoB(tbl varchar,startx float, starty float,endx float,endy float);
CREATE OR REPLACE function pgr_fromAtoB(tbl varchar,startx float, starty float,endx float,endy float)
returns  geometry as
$body$
declare
    v_startLine geometry;--离起点最近的线
    v_endLine geometry;--离终点最近的线

    v_startTarget integer;--距离起点最近线的终点
    v_endSource integer;--距离终点最近线的起点

    v_statpoint geometry;--在v_startLine上距离起点最近的点
    v_endpoint geometry;--在v_endLine上距离终点最近的点

    v_res geometry;--最短路径分析结果


    v_perStart float;--v_statpoint在v_res上的百分比
    v_perEnd float;--v_endpoint在v_res上的百分比

    v_shPath geometry;--最终结果
    tempnode float;
begin

    --查询离起点最近的线
    execute 'select geom ,target  from ' ||tbl||
			' where
			ST_DWithin(geom,ST_Geometryfromtext(''point('||	startx ||' ' || starty||')''),15)
			order by ST_Distance(geom,ST_GeometryFromText(''point('|| startx ||' '|| starty ||')''))  limit 1'
			into v_startLine ,v_startTarget;

    --查询离终点最近的线
    execute 'select geom,source  from ' ||tbl||
			' where ST_DWithin(geom,ST_Geometryfromtext(''point('|| endx || ' ' || endy ||')''),15)
			order by ST_Distance(geom,ST_GeometryFromText(''point('|| endx ||' ' || endy ||')''))  limit 1'
			into v_endLine,v_endSource;

    --如果没找到最近的线，就返回null
    if (v_startLine is null) or (v_endLine is null) then
        return null;
    end if ;

    select  ST_ClosestPoint(v_startLine, ST_Geometryfromtext('point('|| startx ||' ' || starty ||')')) into v_statpoint;
    select  ST_ClosestPoint(v_endLine, ST_GeometryFromText('point('|| endx ||' ' || endy ||')')) into v_endpoint;


    --最短路径
    execute 'SELECT st_linemerge(st_union(b.geom)) ' ||
    'FROM pgr_kdijkstraPath(
    ''SELECT gid as id, source, target, length as cost FROM ' || tbl ||''','
    ||v_startTarget || ', ' ||'array['||v_endSource||'] , false, false
    ) a, '
    || tbl || ' b
    WHERE a.id3=b.gid
    GROUP by id1
    ORDER by id1' into v_res ;

    --如果找不到最短路径，就返回null
    --if(v_res is null) then
    --    return null;
    --end if;

    --将v_res,v_startLine,v_endLine进行拼接
    select  st_linemerge(ST_Union(array[v_res,v_startLine,v_endLine])) into v_res;

    select  ST_Line_Locate_Point(v_res, v_statpoint) into v_perStart;
    select  ST_Line_Locate_Point(v_res, v_endpoint) into v_perEnd;

	if(v_perStart > v_perEnd) then
        tempnode =  v_perStart;
		v_perStart = v_perEnd;
		v_perEnd = tempnode;
    end if;

    --截取v_res
    SELECT ST_Line_SubString(v_res,v_perStart, v_perEnd) into v_shPath;

    return v_shPath;

end;
$body$
LANGUAGE plpgsql VOLATILE STRICT
```
查询测试sql：
```sql
SELECT ST_AsGeoJson(pgr_fromatob) AS geojson FROM pgr_fromAtoB('road', 114.740838261947, 36.5671993277619, 115.230058896741, 37.5105107733159)
```