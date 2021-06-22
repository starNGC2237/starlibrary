function submit_register(){
    $.ajax({
        //几个参数需要注意一下
        type: "post",//方法类型
        dataType: "json",//预期服务器返回的数据类型
        url: "https://starlibrary.online/penghao/user/register" ,//url
        data: $('#formLogin').serialize(),               
        success: function (result) {                    
            console.log(result.msg);//打印服务端返回的数据(调试用)
            if (result.status==200) {
                alert(result.msg)
                window.location.href="https://starlibrary.online";
            }else if(result.status==404){
                alert(result.msg);
            }
        },                
        error : function(){
            alert("异常！");
        }
    });
    return false;
}