/* 导航栏选择状态切换*/
$(".meun-item").click(function () {
    $(".meun-item").removeClass("meun-item-active");
    $(this).addClass("meun-item-active");
});


$(function () {
    /*菜单栏点击跳转页面*/
    $("#welcome").click(function () {
        window.location.href = "/index"; //跳转到欢迎页面
    });
    $("#sysUser").click(function () {
        window.location.href = "/sysUser/query";//系统用户管理
    });
    $("#userPer").click(function () {
        window.location.href = "/userPer/query";//角色管理
    });
    $("#seckill").click(function () {
        window.location.href = "/seckill/query";//秒杀列表页
    });

    $("#saveHtml").click(function () {
        window.location.href = "/saveHtml/query";//
    });

});

function errorMsg(code, msg) {
    alert("错误码：[" + code + "]  摘要:[" + msg + "]");

}

// javascript 模块化
//主要优化了秒杀模块js
var cmsSystem = {
    //get请求
    doGet: function (url) {
        window.location.href = url;
    },
    //post请求
    doPost: function (url, PARAMS) {
        var temp = document.createElement("form");
        temp.action = url;
        temp.method = "post";
        temp.style.display = "none";
        for (var x in PARAMS) {
            var opt = document.createElement("textarea");
            opt.name = x;
            opt.value = PARAMS[x];
            temp.appendChild(opt);
        }
        document.body.appendChild(temp);
        temp.submit();
        return temp;
    },

    //选择菜单栏
    menu: {
        active: function (menuId) {
            $(".meun-item").removeClass("meun-item-active");
            $("#" + menuId).addClass("meun-item-active");
        }
    },
    //页面模块
    page: {
        //get请求打开页面，页面在固定模块渲染
        getPage: function (url, errInfo) {
            $.ajax({
                url: url,
                type: "get",    //不区分大小写
                success: function (view) {
                    $('#rightContent').empty();
                    $('#rightContent').append(view);
                },
                error: function (e) {
                    alert(errInfo);
                    console.error(errInfo + ":" + e)
                }
            });
        }

    },
    //秒杀模块
    seckill: {
        url: {
            //秒杀详情页url
            detailUrl: function (seckillId) {
                return seckillId + "/detail";
            },
            //获取当前系统时间url
            getNowTimeUrl: function () {
                return "../time/now";
            },
            //获取秒杀暴露地址url
            exposerUrl: function (seckillId) {
                return "../" + seckillId + "/exposer";
            },
            //执行秒杀url
            executionUrl: function (url) {
                return "../" + url + "/execution";
            },
            //执行秒杀url(存储过程)
            executionProducerUrl: function (url) {
                return "../" + url + "/executionProducer";
            }
        }

    },
    saveHtml:{
        url:{
            //生成index1页面url
            saveIndex1:function () {
                return "./save/index1";
            },
            getView:function () {
                return "./save/getView";
            }

        }
    }

}