--球面距离计算
CREATE OR REPLACE FUNCTION sphere_distance(lon_a FLOAT, lat_a FLOAT, lon_b FLOAT, lat_b FLOAT)
RETURNS FLOAT AS $$
SELECT asin(
sqrt(
sin(0.5 * radians(lat_b - lat_a)) ^ 2 +
sin(0.5 * radians(lon_b - lon_a)) ^ 2 * cos(radians(lat_a)) * cos(radians(lat_b))
)
) * 127561999.961088 AS distance;
$$
LANGUAGE SQL IMMUTABLE COST 100;

--gcj02转换为bd09
CREATE or REPLACE FUNCTION proj_gcj2bd(gcjLon double precision, gcjLat double precision)
	RETURNS point
AS $$
DECLARE
	z double precision;
	theta double precision;
	bdLon double precision;
	bdLat double precision;
BEGIN
	z := sqrt(gcjLon * gcjLon + gcjLat * gcjLat) + 0.00002 * sin(gcjLat * PI() * 3000.0 / 180.0);
	theta = atan2(gcjLat, gcjLon) + 0.000003 * cos(gcjLon * PI() * 3000.0 / 180.0);
	bdLon = z * cos(theta) + 0.0065;
	bdLat = z * sin(theta) + 0.006;
	RETURN point(bdLon, bdLat);
END;
$$ LANGUAGE plpgsql;

--创建表
CREATE TABLE public.base_urls
(
    id CHARACTER VARYING(50) PRIMARY KEY NOT NULL,
    pid CHARACTER VARYING(50),
    isopen BOOLEAN DEFAULT FALSE ,
    isshow BOOLEAN DEFAULT TRUE ,
    title CHARACTER VARYING(100),
    url CHARACTER VARYING(100),
    issenior BOOLEAN,
    expand CHARACTER VARYING(200)
);
CREATE UNIQUE INDEX base_urls_id_uindex ON public.base_urls (id);
COMMENT ON COLUMN public.base_urls.id IS '主键';
COMMENT ON COLUMN public.base_urls.pid IS '父节点ID';
COMMENT ON COLUMN public.base_urls.isopen IS '是否默认打开';
COMMENT ON COLUMN public.base_urls.isshow IS '是否显示，默认为是';
COMMENT ON COLUMN public.base_urls.title IS '显示名称';
COMMENT ON COLUMN public.base_urls.url IS '连接URL';
COMMENT ON COLUMN public.base_urls.issenior IS '是否高级功能';
COMMENT ON COLUMN public.base_urls.expand IS '扩展信息';