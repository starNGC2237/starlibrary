/**
 * 该方法用于检测注册前的格式并显示给用户
 */
function alertError(){
    var userName=document.getElementById('userName').value;
    var userPassword=document.getElementById('userPassword').value;
    console.log(userPassword.length);
    if(userName.length<16&&userName.length>3){
        if(userPassword.length>=6&&userPassword.length<=16){
            return submit_register();
        }else{
            document.getElementById('userPasswordTip').innerHTML="密码由6到16个字符组成，区分大小写";
            return false;
        }
    }else{
        document.getElementById('userNameTip').innerHTML="用户名由3到16个字符组成";
        if(userPassword.length>=6&&userPassword.length<=16){
            return false;
        }else{
            document.getElementById('userPasswordTip').innerHTML="密码由6-16个字符组成，区分大小写";
            return false;
        }
    }   
}
