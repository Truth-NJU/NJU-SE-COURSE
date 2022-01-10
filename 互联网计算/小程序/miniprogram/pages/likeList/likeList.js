// pages/likeList/likeList.js
const db=wx.cloud.database();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    openid:"",
    newsId:[],
    news:[]
  },

  // t(){
  //   console.log(this.data.newsId)
  // },
  /**
   * 生命周期函数--监听页面加载
   */
  getNews:function (news_Id) {
   /* wx.showLoading({
      title: "加载中",
    })*/
    wx.cloud.callFunction(
      {
        name:'newsDetail',
        data:{
          postId: news_Id
        }
      }
    ).then(res => {
        console.log(res)
        this.setData({
          news: this.data.news.concat(JSON.parse(res.result)[news_Id] )
        });
        wx.hideLoading({
          success: (res) => {},
        })
      }).catch(err =>{
        console.error(err);
      })
  
  },
  onLoad: function (options) {
    var that=this
    wx.cloud.callFunction({
      name:"login"
    }).then(res=>{
      that.setData({
        openid:res.result.openid
      })
    })
    db.collection('likeList').where({
      _openid:that.data.openid
    }).get().then(res=>{
      for(var i=0;i<res.data.length;i++){
        console.log(res.data[i].newsId)
        that.getNews(res.data[i].newsId)
      }
    })
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

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})