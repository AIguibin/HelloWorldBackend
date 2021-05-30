<%--
  Created by IntelliJ IDEA.
  User: AIguibin
  Date: 2018/12/23
  Time: 0:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.0.js">
    </script>
</head>
<body>
<div class="wrapper">
    <h1>SUCCESS</h1>
    <img src="/image/background.png" onclick="getRedPage()">
    <a href="/grap">进入红包页，上边AJax的请求只会刷新页面不会跳转页面</a>
</div>
<script>
   function getRedPage() {
       $.get({
           url:"/grap",
           success: function(res){
               res.get();
           }
       })
   }
</script>
</body>
</html>
