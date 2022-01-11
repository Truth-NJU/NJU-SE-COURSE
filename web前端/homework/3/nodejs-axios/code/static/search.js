var express = require('express');
var fs = require("fs");
var app = express();

const bcryptjs = require('bcryptjs');

// 解决跨域问题
var cors = require('cors');
app.use(cors({
    origin: ['http://localhost:63342'],
    methods: ['GET', 'POST'],
    alloweHeaders: ['Conten-Type', 'Authorization']
}));


app.use(express.static(__dirname));

app.get('/login', function (req, res) {
    // 连接数据库
    var mysql = require('mysql');
    var connection = mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'tzh444582',
        port: '3306',
        database: 'UserData'
    });

    connection.connect();

    // 获得前端传递过来的参数
    var response = {
        "username": req.query.username,
        "password": req.query.password
    };

    // 根据用户名查询对应的密码，进行登录验证
    var sql = "SELECT password FROM user WHERE username=(?)";
    var sqlParam = [response.username];
    // 根据用户名查询对应密码进行验证
    connection.query(sql, sqlParam, function (err, result) {
        if (err) {
            console.log('[SELECT ERROR] - ', err.message);
            res.send(err);
            return;
        }
        // console.log(result);
        if (result.length===0) {
            // 用户名不存在
            res.send("1");
        } else {
            // 随机字符串
            var salt = bcryptjs.genSaltSync(10);
            // 判断密码是否相等
            var isEqual = bcryptjs.compareSync(response.password, result[0].password);
            console.log(isEqual);
            res.send(isEqual);
        }
    });


    connection.end();
});


var server = app.listen(8081, function () {

    var host = server.address().address;
    var port = server.address().port;
});


