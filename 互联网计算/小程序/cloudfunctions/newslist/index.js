// 云函数入口文件
const cloud = require('wx-server-sdk')
const axios = require('axios')
cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

// var rp = require('request-promise');


// 云函数入口函数
exports.main = async (event, context) => {

  return rp(`http://c.m.163.com/nc/article/headline/T1348647853363/${400-event.start}-10.html`)
  .then(function (res) {
    console.log(res);
      return res;
  })
  .catch(function (err) {
      console.err(err);
  });
}
