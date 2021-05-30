<html>
<head>
    <script src="jquery-3.3.1.min.js"></script>
    <script>
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/save',
            /*data: JSON.stringify(
                {
                    "admin":"amin",
                    "userDTO":{
                        "name":"姓名",
                        "password":"12",
                        "confirm":"电影"
                    }
                }
            ),
            contentType: "application/json; charset=utf-8",*/
            data:{
                "admin":"amin",
                "userDTO":{
                    "name":"姓名",
                    "password":"12",
                    "confirm":"电影"
                }
            },
            dataType:"json",
            success: function(res){
                console.log(res)
            },
            error:function (err) {
                console.log(err)
            }
        });
    </script>
</head>
<body>
<h2>Hello World!</h2>
</body>
</html>
