
/**
 * 该方法是用来设置cookie的
 * @param {String} cname cookie的名字
 * @param {String} cvalue cookie的值
 * @param {number} exdays 持续时间(天)
 */
function setCookie(cname,cvalue,exdays){
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
/**
 * 该方法返回指定cookie的值
 * @param {String} cname cookie的名字
 */
function getCookie(cname){
    var name = cname + "=";
    var decodedCookie= document.cookie
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
         }
         if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
         }
     }
    return "";
}