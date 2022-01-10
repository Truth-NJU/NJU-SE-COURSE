// pages/news/news.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
      newsList:[],
      start:0
  },
  getNews:function () {
    wx.showLoading({
      title: '加载中',
    })
    wx.cloud.callFunction({
      name:"newslist",
      data:{
        start:(this.data.newsList.length/10) *10

      }
    }).then(res => {
      console.log(res);
      this.setData({
        newsList:this.data.newsList.concat(JSON.parse(res.result).T1348647853363) 
      })
      wx.hideLoading({
        success: (res) => {},
      })
    }).catch(
      err =>{
        console.error(err);
      }
    )
},
goToNews:function(event){
 var newsId=event.currentTarget.dataset.newsid
  console.log(event.currentTarget.dataset)
  var path ="../newsDetail/newsDetail?newsId="+ newsId
wx.navigateTo({
  url: path,
  //events: events,
  success: (result) => {},
  fail: (res) => {},
  complete: (res) => {},
})
},
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options){ 
    this.getNews();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    this.getNews();
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})