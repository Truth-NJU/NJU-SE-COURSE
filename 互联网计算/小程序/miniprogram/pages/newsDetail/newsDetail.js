// pages/newsDetail/newsDetail.js
const db=wx.cloud.database();
var newsId=null;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    x: '597rpx',
    y: '870rpx',
    x1:'597rpx',
    y1:'1000rpx',

    showShare: false,
    options: [
      { name: '微信', icon: 'wechat', openType: 'share' },
      { name: '微博', icon: 'weibo' },
      { name: '复制链接', icon: 'link' },
      { name: '分享海报', icon: 'poster' },
      { name: '二维码', icon: 'qrcode' },
    ],
  
    newsDetail:[],
    newsId:null,
    like:false

  },

  like(){
    this.setData({
      like:!this.data.like
    })
    if(this.data.like){
      db.collection('likeList').add({
        data:{
          newsId:this.data.newsId
        }
      }).then(res=>{
        //console.log(res)
        wx.showToast({
          title: '收藏成功',
        })
      })
    }
    if(!this.data.like){
      db.collection('likeList').where({
        newsId:this.data.newsId
      }).remove().then(res=>{
        //console.log(res)
      })
    }
  },
  onClick(event) {
    this.setData({ showShare: true });
  },

  onClose() {
    this.setData({ showShare: false });
  },

  onSelect(event) {
    wx.showToast({
      title: event.detail.name+'分享成功',
    });
    this.onClose();
  },
  onShareAppMessage() {
    return {
      title: '自定义转发标题blabla',
      path: "../newsDetail/newsDetail?newsId="+ this.data.newsId
    }
  },
  getNewsDetail:function () {
    wx.showLoading({
      title: "加载中",
    })
    wx.cloud.callFunction(
      {
        name:'newsDetail',
        data:{
          postId: newsId
        }
      }
    ).then(res => {
        console.log(res)
        this.setData({
          newsDetail: this.data.newsDetail.concat(JSON.parse(res.result)[newsId] )
        });
        wx.hideLoading({
          success: (res) => {},
        })
      }).catch(err =>{
        console.error(err);
      })
  
  },
  setLoadLikeState:function(){
    var that=this
    db.collection('likeList').where({
      newsId:this.data.newsId
    }).get().then(res=>{
      
      console.log(res.data)
      if(res.data.length!=0){
        this.setData({like:true})
      }
      
    })

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    newsId=options.newsId;
    this.setData({
      newsId:newsId
    })
    //console.log(this.data.newsId)
  // console.log(options)
    this.getNewsDetail();
    this.setLoadLikeState();
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