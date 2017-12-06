//1. 初始化地图
var wktformat = new ol.format.WKT();

var base_lyr = new ol.layer.Tile({
    source: getTdtSource("vec_w")
});
var anno_lyr = new ol.layer.Tile({
    source: getTdtSource("cva_w"),
    zIndex:1
});

var source = new ol.source.Vector({
    features: []
});
var features = [];

function styleFunc(fea){
    var name = fea.get("attr").poiname;
    return new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: '#ffcc33',
            width: 3,
            lineJoin: 'round',
            lineDash:[3,5]
        }),
        fill: new ol.style.Fill({
            color: 'rgba(255, 0, 0, 0.2)'
        }),
        image: new ol.style.Circle({
            radius: 6,
            fill: new ol.style.Fill({
                color: '#3400ff'
            })
        }),
        text: new ol.style.Text({
            text: name,
            textAlign:"left",
            textBaseline:"top",
            font:"bold 12px 微软雅黑",
            offsetX:5,
            offsetY:0,
            fill: new ol.style.Fill({
                color: '#000000'
            }),
            stroke: new ol.style.Stroke({
                color: '#ffffff',
                width: 1,
                lineJoin: 'round'
            })
        })

    })
}

var vector = new ol.layer.Vector({
    source: source,
    style: styleFunc
});
var scatterStyle = new ol.style.Style({
    image: new ol.style.Circle({
        radius: 6,
        fill: new ol.style.Fill({
            color: 'rgba(255, 0, 0, 0.6)'
        }),
        stroke: new ol.style.Stroke({
            color: '#ff0000',
            width: 1,
            lineJoin: 'round'
        })
    })
});
var vecLayer = new ol.layer.Vector({
    source: null,
    opacity:.6,
    style:scatterStyle
});
var heatmap = new ol.layer.Heatmap({
    source: null,
//        gradient:['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026'],
    blur: 15,//Blur size in pixels. Default is 15.
    radius: 8,//Radius size in pixels. Default is 8.
    opacity:.6,
    shadow: 250//Shadow size in pixels. Default is 250.
});
var projection = new ol.proj.Projection({
    code: 'EPSG:3857',
    units: 'm'
});
var map = new ol.Map({
    controls: ol.control.defaults({
        attribution: false
    }),
    target: 'map',
    layers: [
        base_lyr,
        anno_lyr,
        vecLayer,
        heatmap,
        vector
    ],
    view: new ol.View({
        projection: projection,
        center: ol.proj.transform([104.214, 35.847], 'EPSG:4326', 'EPSG:3857'),
        zoom: 4
    })
});

//    $("#panel").resize(function(){
//        $("#map").css("bottom", $("#panel").height()+"px");
//        map.updateSize();
//    });
//底部面板切换
$("#panelclose").click(function () {
    var panelbody = $("#panelbody");
    panelbody.toggle("fast", function(){
        if(panelbody.is(":visible")){
            $("#panelclose").removeClass("glyphicon-chevron-up");
            $("#panelclose").addClass("glyphicon-chevron-down");
        }else{
            $("#panelclose").removeClass("glyphicon-chevron-down");
            $("#panelclose").addClass("glyphicon-chevron-up");
        }
    });
});

//底图切换
$("#basemap>li").on("click", function(){
    $("#basemap>li").removeClass("active");
    $(this).addClass("active");
    var type = $(this).attr("type");
    base_lyr.setSource(getTdtSource(type));
    if(type==="vec_w"){
        $("#isano").parent().hide()
        $("#isano")[0].checked = true;
    }else{
        $("#isano")[0].checked = false;
        var right = type==="img_w"?"79px":"18px";
        $("#isano").parent().css("right", right).show();
    }
    anno_lyr.setVisible($("#isano")[0].checked);
});
$("#isano").on('change', function() {
    anno_lyr.setVisible(this.checked);
});

//展示图层切换
$("#showtype>li>a").on("click", function(){
    $("#showtype>li>a>input:radio[name='showtype']").removeAttr("checked");
    var showtypeR = $($(this).children()[0]),
        showtype = showtypeR.val();
    showtypeR.attr("checked", true);
    setSource(showtype);
});

$("#download>li>a").on("click", function(){
    var type = $(this).attr("type");
    $(this).attr("href", "geocode/down?type="+type);
});

// 初始化一个拉框控件
var dragZoom = new ol.interaction.DragZoom({
    condition: ol.events.condition.always,
    out: false, // 此处为设置拉框完成时放大还是缩小
});
map.addInteraction(dragZoom);
dragZoom.on('boxend', function(e) {
    dragZoom.setActive(false);
    map.getViewport().style.cursor="default";
});
dragZoom.setActive(false);
$("#maptools>li").on("click", function(){
    var type = $(this).attr("type");
    dragZoom.set("out", type==="zoomin"?false:true);
    dragZoom.setActive(true);
    map.getViewport().style.cursor="crosshair";
});

$("#query_close").on("click", function(){
    $("#querybox").toggle("slow", function(){
        if($("#querybox").is(":visible")){
            $("#query_close").removeClass("glyphicon-circle-arrow-left");
            $("#query_close").addClass("glyphicon-circle-arrow-right");

        }else{
            $("#query_close").removeClass("glyphicon-circle-arrow-right");
            $("#query_close").addClass("glyphicon-circle-arrow-left");
        }
    });
});

$("#issave").on("change", function(){
    this.checked?$("#btn-save").show():$("#btn-save").hide();
});

$("ul#myTab>li>a").on("click", function(){
    $("ul#myTab>li").removeClass("active");
    $("#querybox>table").hide();
    $(this).parent().addClass("active");
    $($(this).attr("href")).show();
});

function showQueryList(){
    $.post("geocode/getlist", null, function (result) {
        result = JSON.parse(result);
        if(result.code==="200"){
            var list = JSON.parse(result.data);
            $("#sel_querylist").html("").append('<option value="">--请选择--</option>');
            for(var i=0;i<list.length;i++){
                var _opt = $("<option/>").attr("value", list[i].id).html(list[i].name);
                $("#sel_querylist").append(_opt);
            }
        }else{
            $("#alert_info").html(result.msg);
            $("#alert").show();
            setTimeout(function () {
                $("#alert").hide();
            }, 1500);
        }
    })
}

$(".btn-query").on("click", function(){
    var handle = $(this).attr("handle");
    switch (handle){
        case "query":{
            getQueryResult();
            break;
        }
        case "save":{
            var _name = $("#sel_dataset").val()+"_"+$("#sel_var").val()+"_"+$("#sel_facset").val();
            var para = {
                "qlist.name":_name,
                "qlist.autorun":$("#autorun")[0].checked?"1":"0",
                "qlist.query_xml":_name+".xml"
            };
            $.post("geocode/savequery", para, function(result){
                result = JSON.parse(result);
                $("#alert_info").html(result.msg);
                $("#alert").show();
                showQueryList();
                setTimeout(function () {
                    $("#alert").hide();
                }, 1500);
            });
            break;
        }
        default:{
            $("#sel_dataset, #sel_var, #sel_facset, #datetime").val("");
            break;
        }
    }
});

function getTdtSource(lyr) {
    var url = "http://t0.tianditu.com/DataServer?T=" + lyr + "&X={x}&Y={y}&L={z}";
    return new ol.source.XYZ({
        url: url
    });
}

//2. 初始化Table
setTimeout(function(){
    showQueryList();
}, 1000);

function getQueryResult(){
    $('#loading').modal('show');
    $("#querybox").hide("slow");
    var paras = {
        limit: 60000,   //页面大小
        offset: 0,
        pagetype: "client"
    };
    $.get("geocode/getpois", paras, function(result){
        result = eval("("+result+")");
        $("#alert_info").html("共查询到"+result.length+"条记录");
        $("#alert").show();
        $('#loading').modal('hide');
        setTimeout(function(){
            $("#alert").hide();
            var oTable = new TableInit(result);
            oTable.Init();
            $("#panel").show();
        }, 1000);
    })
}

function TableInit(griddata){
    loadData(griddata);
    var oTableInit = new Object();
    oTableInit.Init = function () {
        $('#datagrid').bootstrapTable({
//                url: 'geocode/getpois',         //请求后台的URL（*）
//                method: 'get',                      //请求方式（*）
            data: griddata,
            striped: false,                      //是否显示行间隔色
            cache: true,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortable: true,                     //是否启用排序
            queryParams: oTableInit.queryParams,//传递参数（*）
            sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber:1,                       //初始化加载第一页，默认第一页
            pageSize: 5,                       //每页的记录行数（*）
            pageList: [5],        //可供选择的每页的行数（*）
            paginationHAlign: 'right', //right, left
            paginationVAlign: 'bottom', //bottom, top, both
            paginationDetailHAlign: 'left', //right, left
            search: true,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
            strictSearch: false,
            showColumns: true,                  //是否显示所有的列
            showRefresh: true,                  //是否显示刷新按钮
            minimumCountColumns: 1,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            uniqueId: "id",                     //每一行的唯一标识，一般为主键列
            showToggle:false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            toolbar: "#toolbar",
            toolbarAlign: "right",
            /*rowStyle: function (row, index) {
                //这里有5个取值代表5中颜色['active', 'success', 'info', 'warning', 'danger'];
                var strclass = "";
                if (row.maxzoom===4) {
                    strclass = 'danger';//还有一个active
                }else if (row.maxzoom===8) {
                    strclass = 'warning';
                }else {
                    strclass = 'info';
                }
                return { classes: strclass }
            },*/
            showExport: true,                     //是否显示导出
            exportDataType: "basic",
            columns: [
                {
                    checkbox: false
                }, {
                    field: 'poiname',
                    title: '点名称',
                    sortable : true
                }, {
                    field: 'x',
                    title: '经度',
                    sortable : true
                },{
                    field: 'y',
                    title: '纬度',
                    sortable : true
                }, {
                    field: 'minzoom',
                    title: '最小级别',
                    sortable : true
                }, {
                    field: 'maxzoom',
                    title: '最大级别',
                    sortable : true
                }
            ],
            onAll: function (name, args) {
                return false;
            },
            onClickCell: function (field, value, row, $element) {
                return false;
            },
            onDblClickCell: function (field, value, row, $element) {
                return false;
            },
            onClickRow: function (item, $element) {
                $element.parent().children().removeClass("active");
                $element.addClass("active");
                source.clear();
                var coords = ol.proj.transform([item.x, item.y], 'EPSG:4326', 'EPSG:3857');
                var feature = new ol.Feature({
                    geometry: new ol.geom.Point(coords),
                    attr: item
                });
                source.addFeature(feature);

                var view = map.getView();
                view.setCenter(coords);
            },
            onDblClickRow: function (item, $element) {
                return false;
            },
            onSort: function (name, order) {
                return false;
            },
            onCheck: function (row) {
                return false;
            },
            onUncheck: function (row) {
                return false;
            },
            onCheckAll: function (rows) {
                return false;
            },
            onUncheckAll: function (rows) {
                return false;
            },
            onCheckSome: function (rows) {
                return false;
            },
            onUncheckSome: function (rows) {
                return false;
            },
            onLoadSuccess: function (data) {
                loadData(data);
            },
            onLoadError: function (status) {
                return false;
            },
            onColumnSwitch: function (field, checked) {
                return false;
            },
            onPageChange: function (number, size) {
                source.clear();
            },
            onSearch: function (text) {
                return false;
            },
            onToggle: function (cardView) {
                return false;
            },
            onPreBody: function (data) {
                return false;
            },
            onPostBody: function () {
                return false;
            },
            onPostHeader: function () {
                return false;
            },
            onExpandRow: function (index, row, $detail) {
                return false;
            },
            onCollapseRow: function (index, row) {
                return false;
            },
            onRefreshOptions: function (options) {
                return false;
            },
            onRefresh: function (params) {
                return false;
            },
            onResetView: function () {
                return false;
            }
        });
    };
    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: 10000,   //页面大小
            offset: 0,
            pagetype: "client"
        };
        return temp;
    };
    return oTableInit;
}

function loadData(data) {
    for(var i=0, dl=data.length;i<dl;i++){
        var _d = data[i];
        var coords = ol.proj.transform([_d.x, _d.y], 'EPSG:4326', 'EPSG:3857');
        var feature = new ol.Feature({
            geometry: new ol.geom.Point(coords),
            attr: _d,
            weight: Math.random()
        });
        features.push(feature);
    }
    setSource("scatter");
}

function setSource(showtype){
    var showLayers = [heatmap, vecLayer];
    for(var i=0;i<showLayers.length;i++){
        showLayers[i].setSource(null);
    }
    var vecSource = new ol.source.Vector({
        features: features
    });
    if(showtype==="cluster"){
        var clusterSource = new ol.source.Cluster({
            distance: 40,
            source: vecSource
        });
        vecLayer.setSource(clusterSource);
        vecLayer.setStyle(getClusterStyle);
    }else if(showtype==="heatmap"){
        heatmap.setSource(vecSource);
    }else{
        vecLayer.setSource(vecSource);
        vecLayer.setStyle(scatterStyle);
    }
}

function getClusterStyle(feature, resolution) {
    var styleCache = {};
    var size = feature.get('features').length;
    var radius = 10,color = "#3399CC",textColor = "#ffffff";
    if(size==1){
        radius = 5;
        color = "#0000ff";
        textColor = "#0000ff";
    }
    else if(size>1&&size<=10){
        radius = 10;
        color = "#1100ff";
    }
    else if(size>10&&size<=40){
        radius = 12;
        color = "#9900ff";
    }
    else if(size>40&&size<=50){
        radius = 14;
        color = "#ff0099";
    }
    else if(size>50&&size<=100){
        radius = 16;
        color = "#ff0099";
    }
    else{
        radius = 18;
        color = "#ff0000";
    }
    var style = styleCache[size];
    if (!style) {
        style = [new ol.style.Style({
            image: new ol.style.Circle({
                radius: radius,
                stroke: new ol.style.Stroke({
                    color: '#fff'
                }),
                fill: new ol.style.Fill({
                    color: color
                })
            }),
            text: new ol.style.Text({
                text: size.toString(),
                fill: new ol.style.Fill({
                    color: textColor
                }),
                font:"Times New Roman"
            })
        })];
        styleCache[size] = style;
    }
    return style;
}