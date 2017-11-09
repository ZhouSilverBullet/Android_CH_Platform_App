window.addEventListener("onorientationchange" in window ? "orientationchange" : "resize", function () {
    SetSreenSize();
}, false);
function SetSreenSize() {
    if (window.orientation == 180 || window.orientation == 0) {
        $('#wrap').css({
            '-webkit-transform': 'scale(1) translateY(0%)',
            'transform': 'scale(1) translateY(0%)'
        });
    }
    if (window.orientation == 90 || window.orientation == -90) {
        $('#wrap').css({
            '-webkit-transform': 'scale(0.7) translateY(-20%)',
            'transform': 'scale(0.7) translateY(-20%)'
        });
    }
}
function getUrlParams() {
    var search = window.location.search.substr(1), m = {};
    if (search.indexOf('&') < 0) {
        var arr = search.split('=');
        m[arr[0]] = arr[1];
    } else {
        var arr = search.split('&');
        for (var i in arr) {
            var arr2 = arr[i].split('=');
            m[arr2[0]] = arr2[1];
        }
    }
    return m;
}
function SetFontSize() {
    var deviceWidth = document.documentElement.clientWidth;
    if (deviceWidth > 640) deviceWidth = 640;
    document.documentElement.style.fontSize = deviceWidth / 25 + 'px';
}
function ShowPopup(info) {
    $('.Popup .a1 h4').text(info);
    $('.cover').show(); $('.Popup').show();
}
function ShowLoading() {
    $('.cover').show(); $('.loading').show()
}
function HideLoading() {
    $('.cover').hide(); $('.loading').hide()
}
function setStorage(key, value) {
    window.localStorage.setItem('sdk_' + key,value);
}
function getStorage(key, value) {
    return window.localStorage.getItem('sdk_' + key);
}
function filter(data) {
    if (typeof (data) == 'undefined' || data == null) {
        return '';
    }
    return data;
}
function intEmail(email) {
    var t = email.split('@')[1];
    if (t == '163.com') {
        return "mail.163.com";
    } else if (t == 'vip.163.com') {
        return "vip.163.com";
    } else if (t == '126.com') {
        return "mail.126.com";
    } else if (t == 'qq.com' || t == 'vip.qq.com' || t == 'foxmail.com') {
        return "mail.qq.com";
    } else if (t == 'gmail.com') {
        return "mail.google.com";
    } else if (t == 'sohu.com') {
        return "mail.sohu.com";
    } else if (t == 'tom.com') {
        return "mail.tom.com";
    } else if (t == 'vip.sina.com') {
        return "vip.sina.com";
    } else if (t == 'sina.com.cn' || t == 'sina.com' || t == 'sina.cn') {
        return "mail.sina.com.cn";
    } else if (t == 'tom.com') {
        return "mail.tom.com";
    } else if (t == 'yahoo.com.cn' || t == 'yahoo.cn') {
        return "mail.cn.yahoo.com";
    } else if (t == 'tom.com') {
        return "mail.tom.com";
    } else if (t == 'yeah.net') {
        return "www.yeah.net";
    } else if (t == '21cn.com') {
        return "mail.21cn.com";
    } else if (t == 'hotmail.com') {
        return "www.hotmail.com";
    } else if (t == 'sogou.com') {
        return "mail.sogou.com";
    } else if (t == '188.com') {
        return "www.188.com";
    } else if (t == '139.com') {
        return "mail.10086.cn";
    } else if (t == '189.cn') {
        return "webmail15.189.cn/webmail";
    } else if (t == 'wo.com.cn') {
        return "mail.wo.com.cn/smsmail";
    } else if (t == '139.com') {
        return "mail.10086.cn";
    } else if (t == 'caohua.com') {
        return "exmail.qq.com/cgi-bin/loginpage";
    } else if (t == 'outlook.com') {
        return "login.live.com/login.srf";
    } else {
        return "";
    }
}
window.onresize = function () {
    SetFontSize();
}
window.onload = function () {
    SetFontSize();
    SetSreenSize();
    $('.cover').css('height', String($(window).height()).replace('px', '') + 'px');
}