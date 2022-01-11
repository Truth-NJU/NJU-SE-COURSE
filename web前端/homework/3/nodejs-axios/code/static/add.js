var express = require('express');
var fs = require("fs");
var app = express();

// 使用bcryptjs进行加密
const bcryptjs = require('bcryptjs');

app.use(express.static(__dirname));

// 解决跨域问题
var cors = require('cors');
app.use(cors({
    // 63342是前端的端口地址
    origin:['http://localhost:63342'],
    methods:['GET','POST'],
    alloweHeaders:['Conten-Type', 'Authorization']
}));

app.get('/signUp', function (req, res) {

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

    // 获得前端传过来的参数
    var response = {
        "username":req.query.username,
        "password":req.query.password
    };

    // 加密
    const SALT_FACTOR = 10;
    // 加密后的密码存入数据库中
    const password = bcryptjs.hashSync(response.password, bcryptjs.genSaltSync(SALT_FACTOR));

    var sql = "SELECT password FROM user WHERE username=(?)";
    var sqlParam = [response.username];

    // 根据用户名查询数据库查找是否已经存在重复用户名
    connection.query(sql, sqlParam, function (err, result) {
        if (err) {
            console.log('[SELECT ERROR] - ', err.message);
            return;
        }
        // console.log(result);
        if (result.length===0) {
            // 用户名不存在
            // 插入新的用户名和密码数据
            var  addSql = 'INSERT INTO user(username,password) VALUES(?,?)';
            var  addSqlParams = [response.username, password];
            //增
            connection.query(addSql,addSqlParams,function (err, result) {
                if(err){
                    console.log('[INSERT ERROR] - ',err.message);
                    return;
                }

                console.log(result);
                // 将结果返回到前端
                res.send(result);
            });
        } else {
            // 用户名存在
            res.send("1");
        }
    });

});


var server = app.listen(8081, function () {

    var host = server.address().address;
    var port = server.address().port;

});


