/**
 * Created by xl on 2017/6/20.
 */
// 下拉框选择
function bindVehicleDDL(name,code,opname,hidname){
    //$.ajaxSetup({ async:false });
    var url="/platform/sys/dict/bindVehicleDDL";
    var param={"name": name,"code":code};
    $.post(url,param,function(d){
        $("#"+opname).append("<option value='' selected = 'selected'>请选择</option>");
        for(var i=0;i<d.length;i++)
        {
            $("#"+opname).append("<option value="+d[i].id+">"+d[i].name+"</option>");
        }
        $("#"+hidname).val($("#"+opname+" option:selected").val());
    });
}
// 下拉框选择
function bindVehicleDDLREL(parentId,opname,hidname){
    //$.ajaxSetup({ async:false });
    var url="/platform/sys/dict/bindVehicleDDLREL";
    var param={"parentid":parentId};
    $.post(url,param,function(d){
        $("#"+opname ).empty();
        $("#"+opname).append("<option value='' selected = 'selected'>请选择</option>");
        for(var i=0;i<d.length;i++)
        {
            $("#"+opname).append("<option value="+d[i].id+">"+d[i].name+"</option>");
        }
        $("#"+hidname).val($("#"+opname+" option:selected").val());
    });
}
// 下拉框选择
function bindVehicleDDL(name,code,opname,hidname,id) {
    //$.ajaxSetup({ async:false });
    var url = "/platform/sys/dict/bindVehicleDDL";
    var param = {"name": name, "code": code};
    $.post(url, param, function (d) {
        $("#" + opname).append("<option value='' selected = 'selected'>请选择</option>");
        for (var i = 0; i < d.length; i++) {
            if (id == d[i].id) {
                $("#" + opname).append("<option value=" + d[i].id + " selected>" + d[i].name + "</option>");
            } else {
                $("#" + opname).append("<option value=" + d[i].id + " >" + d[i].name + "</option>");
            }
        }
        $("#" + hidname).val($("#" + opname + " option:selected").val());
    });
}

function bindVehicleDDLBySelector(name,code,opname,hidname,selector) {
    //$.ajaxSetup({ async:false });
    var url = "/platform/sys/dict/bindVehicleDDL";
    var param = {"name": name, "code": code};
    $.post(url, param, function (d) {
        $("#" + opname).append("<option value='' selected = 'selected'>请选择</option>");
        for (var i = 0; i < d.length; i++) {
            // alert(selector);
            if (selector == d[i].name) {
                $("#" + opname).append("<option value=" + d[i].id + " selected>" + d[i].name + "</option>");
            } else {
                $("#" + opname).append("<option value=" + d[i].id + " >" + d[i].name + "</option>");
            }
        }
        $("#" + hidname).val($("#" + opname + " option:selected").val());
    });
}



$.ajaxSetup({
    contentType: "application/x-www-form-urlencoded; charset=utf-8"
});
var DataDeal = {
//将从form中通过$('#form').serialize()获取的值转成json
    formToJson: function (data) {
        data=data.replace(/&/g,"\",\"");
        data=data.replace(/=/g,"\":\"");
        data="{\""+data+"\"}";
        return data;
    },
};
$.fn.tableinputtoJson=function(){
    var dataJson=[];
    $(this).find('tr:not(:first)').each(function () {
        var o = {};
        $(this).find("input").each(function(){
            o[this.name] = this.value;
        });
        if(!$.isEmptyObject(o)){
            dataJson.push(o);
        }
    });
    return dataJson;
};

//智能柜状态
function getCabStatus(pstatus){
    if(pstatus == 0){
        return "上线";
    }else if(pstatus == 1){
        return "下线";
    }else{
        return "";
    }
}
//仓位状态
function getSpaceStatus(pstatus){
    if(pstatus  == 0){
        return "关";
    }else if(pstatus  == 1){
        return "开";
    }else {
        return "";
    }
}
//RFID状态
function getRFIDStatus(pstatus){
    if(pstatus  == 0){
        return "未使用";
    }else if(pstatus  == 1){
        return "使用中";
    }else {
        return "";
    }
}
//工具状态
function getInsStatus(pstatus){

    /*if(pstatus == 0){
        return "未借出";
    }else if(pstatus == 1){
        return "待借用"
    }else if(pstatus == 2){
        return "借用中"
    }else if(pstatus == 3){
        return "已归还";
    }else{
        return "";
    }*/
    if(pstatus == 0){
        return "未借出";
    }else if(pstatus == 1){
        return "待借用"
    }else if(pstatus == 2){
        return "借用中"
    }else{
        return "";
    }
}
//基础工具状态
function getBaseInsPstatus(pstatus) {
    switch(pstatus){
        case 0 :return  "下线"
        case 1 : return "上线"
        default : return "";
    }
}
function getOrderStatus(pstatus){
    if(pstatus == 0){
        return "未完成";
    }else if(pstatus ==1){
        return "已完成";
    }else{
        return "";
    }
}

//工具上下线状态
function getInsLineStatus(pstatus){
    if(pstatus == 0){
        return "下线";
    }else if(pstatus ==1){
        return "上线";
    }else{
        return "";
    }
}
//工具锁状态
function getLockStatus(pstatus){
    if(pstatus == 0){
        return "关闭";
    }else if(pstatus ==1){
        return "打开";
    }else{
        return "";
    }
}
//工具锁绑定状态
function getBindStatus(pstatus){
    if(pstatus == 0){
        return "未绑定";
    }else if(pstatus ==1){
        return "已绑定";
    }else{
        return "";
    }
}

$('body').on('hidden.bs.modal', '.modal', function () {
    $(this).removeData('bs.modal');
});
