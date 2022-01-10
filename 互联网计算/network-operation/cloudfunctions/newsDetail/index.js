// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init()
//引入request请求
var axios = require('axios');

// 云函数入口函数
exports.main = async (event, context) => {

  try {

      const {
      data
      } = await axios({

          url: `http://c.m.163.com/nc/article/${event.postId}/full.html`,
          method: 'get',
          headers: {
          "Host": "frodo.douban.com",
          "Connection": "keep-alive",
          'content-type': 'application/json',
          'Accept-Encoding': 'gzip,compress,br,deflate',
          'User-Agent': 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.2(0x18000236) NetType/WIFI Language/zh_CN',
          'Referer': 'https://servicewechat.com/wx2f9b06c1de1ccfca/81/page-frame.html'

      }

      })

          return data

      } catch (e) {

      console.log(e)

  }

}