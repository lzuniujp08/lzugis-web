<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>切片下载</title>
    <link rel="shortcut icon" type="image/x-icon" href="css/img/logo.jpg" />
    <link rel="stylesheet" href="https://openlayers.org/en/v4.1.1/css/ol.css" type="text/css">
    <style type="text/css">
        body, #map {
            border: 0px;
            margin: 0px;
            padding: 0px;
            width: 100%;
            height: 100%;
            font-size: 13px;
            overflow: hidden;
        }
    </style>
    <script src="https://openlayers.org/en/v4.1.1/build/ol.js"></script>
    <script type="text/javascript" src="plugin/jquery/jquery-3.1.1.min.js"></script>
    <script type="text/javascript">
        var map;
        function init() {
            /**
             * 温暖  warm
             * 清爽  cool
             * 午夜  midnight
             * 暗色  dark
             * 高对比  contrast
             * 浪漫粉  pink
             * 夜视  vision
             * 探险  adventure
             * 魅蓝  blue
             * 浅色  light
             * 清新  fresh
             * 自然  natural
             * 政区  admin
             * 旅游  tourism
             * 水系  river
             * 世界中文  chinese
             */
            var midnight = getGeoheyLayer("midnight");
            var province = new ol.layer.Image({
                source: new ol.source.ImageWMS({
                    ratio: 1,
                    url: 'http://localhost:6080/geoserver/bj_grid/wms',
                    params: {
                        'FORMAT': 'image/png',
                        'VERSION': '1.1.1',
                        STYLES: '',
                        LAYERS: 'bj_grid:bou2_4p'
                    }
                })
            });
            map = new ol.Map({
                controls: ol.control.defaults({
                    attribution: false
                }),
                target: 'map',
                layers: [midnight],
                view: new ol.View({
                    center: ol.proj.transform([104.214, 35.847], 'EPSG:4326', 'EPSG:3857'),
                    zoom: 4
                })
            });
        }
        function getGeoheyLayer(style) {
            var url = "tile?layer=" + style + "&z={z}&x={x}&y={y}";
            var layer = new ol.layer.Tile({
                source: new ol.source.XYZ({
                    url: url
                })
            });
            return layer;
        }
    </script>
</head>
<body onLoad="init()">
<div id="map">
</div>
</body>
</html>