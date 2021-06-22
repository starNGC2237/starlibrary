function submit_login(){
    $.ajax({            
        //几个参数需要注意一下
        type: "POST",//方法类型
        dataType: "json",//预期服务器返回的数据类型
        url: "https://starlibrary.online/penghao/user/login",//url
        data: $('#formLogin').serialize(),               
        success: function (result) {                    
            console.log(result);//打印服务端返回的数据(调试用)
            if (result.status==200) {
                var user=document.getElementById('userName').value;
                var userName=result.data.userName;
                var nickName=result.data.nickName;
                console.log(userName);
                console.log(nickName);
                if(result.data.userName==user){
                    if(result.status==200){
                        alert(result.msg);
                    }
                }
                setCookie("userName",userName,7);
                setCookie("nickName",nickName,7);
                window.location.href="https://starlibrary.online";
            }else if(result.status==404){
                alert(result.msg);
            }else{
                alert("系统错误！错误码："+result);
            }
        },
        error : function(XMLHttpRequest, textStatus, errorThrown){
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
    return false;
}