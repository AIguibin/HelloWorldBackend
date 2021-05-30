<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <link rel="stylesheet" href="style/index.css">
</head>
<body>
<div class="wrapper">
    <div class="head">
        <img src="image/background.png" alt="" style="width: 100%">
    </div>
    <div class="middle">
        <div class="form">
            <form action="/login" method="post" enctype="application/json" datatype="json">
                <span>用 户: </span><input type="text" name="userName">
                <span>密 码: </span><input type="password" name="passWord">
                <input type="submit" value="登陆">
            </form>
        </div>
    </div>
    <div class="foot"></div>
</div>
</body>
</html>
