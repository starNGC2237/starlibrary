function get_information(){
    //获得个人信息
    $(document).ready(function(){
        var user = getCookie("userName");
        $.ajax({
            type:"get",
            url:"https://starlibrary.online/penghao/user/login",
            dataType:"json",
            data:{
                userName:user
            },
            success:function(response){
                console.log(response);
                $("#nickName").val(response.data.nickName);
                $("#userName_get").empty();
                $("#userName_get").append(response.data.userName);
                if(getCookie("card")!=""){
                    $("#libraryCardNumber_get").empty();
                    $("#libraryCardNumber_get").append(getCookie("card"));  
                }
                $("#phone").empty();
                $("#phone").append(response.data.phone);
                $("#createTime").empty();
                $("#createTime").append(response.data.createTime);
            },
            error : function(){
                alert("异常！");
            }
        });
    });
}