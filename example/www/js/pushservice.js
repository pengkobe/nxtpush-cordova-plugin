/**
 * Created by Administrator on 2017/4/1.
 */
angular.module("starter.services").factory("JPushService", [
  "$window",
  "$rootScope",
  "$state",
  "$timeout",
  function($window, $rootScope, $state, $timeout) {
    function documentBindEventListener(eventType, callback) {
      document.removeEventListener(eventType, callback, false);
      document.addEventListener(eventType, callback, false);
    }

    var setTagsWithAliasCallback = function(event) {
      if (event.resultCode !== 0) {
        //设置失败
        $timeout(function() {
          _setAlias("9876");
        }, 6000);
      } else {
        var pushState = true;
        //用户没有设置,默认开启
        if (pushState === false) {
          _stopPush();
        }
      }
      alert("setTagsWithAliasCallback啦！");
      $rootScope.$broadcast("setTagsWithAliasCallback", event);
    };
    var openNotificationCallback = function(data) {
      var type = ionic.Platform.isAndroid() ? data.extras.Type : data.Type;
      // 处理华为手机
      if(!type){
           var extras = JSON.parse(data.extras);
           type=ionic.Platform.isAndroid()?extras[0].Type:data.Type;
      }
      if (type == 1) {
        $state.go("tab.chats");
      } else if (type == 2) {
        $state.go("tab.chats");
      }
      alert("打开告警啦！");
      $rootScope.$broadcast("openNotificationCallback", data);
    };
    var receiveNotificationCallback = function(data) {
      var type = ionic.Platform.isAndroid() ? data.extras.Type : data.Type;
      var pcode = ionic.Platform.isAndroid() ? data.extras.PCode : data.PCode;
      var count = ionic.Platform.isAndroid() ? data.extras.Count : data.Count;
      if (type == 1) {
        //当前项目的才设置告警，因为服务端推送的是新产生的告警数目，因此data.extras.Count字段不可用，需调用接口获取
      } else if (type == 2) {
        // MemoryFactory.setMonitorCount(count, {isRoot: true, isCache: false, key: "monitorCount"});
      }
      alert("收到告警啦！");
      $rootScope.$broadcast("receiveNotificationCallback", data);
    };

    //启动极光推送,读取用户设置，进行初始化操作，如果没有自定义设置，默认开启
    var _init = function() {
      document.addEventListener(
        "deviceready",
        function() {
          _getUserNotificationSettings(function(result) {
            if (result === 0) {
              //推送被关闭,提示用户开启权限
              alert("告警推送需要在设置中开启应用推送权限");
            }
          });
          _resumePush(); //如果stopPush后，必须resume再init，否则无效
          var config = {
            stac: setTagsWithAliasCallback,
            onc: openNotificationCallback,
            rnc: receiveNotificationCallback
          };
          if (ionic.Platform.isIOS()) {
            //ios不同于android，需要注册apns服务
            $window.plugins.NXTPlugin.startJPushSDK();
          }
          //本功能是一个完全本地的状态操作，也就是说：停止推送服务的状态不会保存到服务器上。
          //推送服务停止期间推送的消息，恢复推送服务后，如果推送的消息还在保留的时长范围内，则客户端是会收到离线消息。
          $window.plugins.NXTPlugin.init();
          //设置tag和Alias触发事件处理
          documentBindEventListener("jpush.setTagsWithAlias", config.stac);
          documentBindEventListener("jpush.openNotification", config.onc);
          documentBindEventListener("jpush.receiveNotification", config.rnc);
          documentBindEventListener("jpush.onReceiveHuaWeiToken", function(token) {
            // 这里需要保存到本地，这样就不需要反复获取了
            alert("我收到华为 token 啦:" + token);
          });
          //开发周期建议打开debug模式，可以获得额外帮助
          $window.plugins.NXTPlugin.setDebugMode(true);
          //以下仅限android，插件内部做了平台判断
          $window.plugins.NXTPlugin.setBasicPushNotificationBuilder(); //声音，振动提示等,
          $window.plugins.NXTPlugin.requestPermission(); //用于在 Android 6.0 及以上系统，申请一些权限
          _setAlias("9876");
        },
        false
      );
    };

    /**
       * @function: 停止极光推送
       * android平台:
       * JPush Service 不在后台运行。
       * 收不到推送消息。
       * 不能通过 JPushInterface.init 恢复，需要调用 resumePush 恢复。
       * 极光推送所有的其他 API 调用都无效。
       * ios平台：
       * 不推荐调用，因为这个 API 只是让你的 DeviceToken 失效，在 设置－通知 中您的应用程序没有任何变化。
       * 建议设置一个 UI 界面， 提醒用户在 设置－通知 中关闭推送服务。
       * */
    var _stopPush = function() {
      $window.plugins.NXTPlugin.stopPush();
    };

    /**
       * @function 重启极光推送
       * @description Android平台:极光推送完全恢复正常工作。
       * @description iOS平台:重新去 APNS 注册。
       * */
    var _resumePush = function() {
      $window.plugins.NXTPlugin.resumePush();
    };

    /**
       * @function: 获取状态
       * @description Android 平台:用来检查 Push Service 是否已经被停止。
       * @description iOS 平台:平台检查推送服务是否注册。
       * @param 参数是回调函数：result为0表示开启，其他表示关闭
       * ps:调用stopPush关闭之后，不会马上生效
       * */
    var _isPushStopped = function(fun) {
      $window.plugins.NXTPlugin.isPushStopped(fun);
    };

    /**
       * @function 得到RegistrationID
       * @description 集成了 JPush SDK 的应用程序在第一次成功注册到 JPush 服务器时，JPush 服务器会给客户端返回一个唯一的该设备的标识 - RegistrationID。
       * @description 应用程序可以把此 RegistrationID 保存以自己的应用服务器上，然后就可以根据 RegistrationID 来向设备推送消息或者通知。
       * @param 参数是回调函数，result结果为RegistrationID
       * */
    var _getRegistrationID = function(fun) {
      window.plugins.NXTPlugin.getRegistrationID(fun);
    };

    /**
       * 设置别名和标签，本身无返回值，可以注册jpush.setTagsWithAlias 事件来监听设置结果:
       * document.addEventListener("jpush.setTagsWithAlias", function(event) {}, false)
       * event结构为{resultCode:null,tags:null,alias:null}
       * 具体错误码定义见readme.md
       * */

    /**
       * @function 设置别名
       * @description 为安装了应用程序的用户，取个别名来标识。以后给该用户 Push 消息时，就可以用此别名来指定。尽可能根据别名来唯一确定用户。
       * @alias 参数类型为字符串。空字符串 （""）表示取消之前的设置。
       * */
    var _setAlias = function(alias) {
      $window.plugins.NXTPlugin.setAlias(alias);
    };

    /**
       * @function 设置标签
       * @description 为安装了应用程序的用户，打上标签。其目的主要是方便开发者根据标签，来批量下发 Push 消息。
       * @description 可为每个用户打多个标签。
       * @tags 参数类型为数组。空集合表示取消之前的设置。
       * @description 每个 tag 命名长度限制为 40 字节，最多支持设置 100 个 tag，但总长度不得超过1K字节（判断长度需采用 UTF-8 编码）。
       * @description 单个设备最多支持设置 100 个 tag，App 全局 tag 数量无限制。
       * */
    var _setTags = function(tags) {
      $window.plugins.NXTPlugin.setTags(tags);
    };

    //设置标签和别名
    var _setTagsWithAlias = function(tags, alias) {
      $window.plugins.NXTPlugin.setTagsWithAlias(tags, alias);
    };

    /**
       * @function 判断系统设置中是否允许当前应用推送
       * @description 在 Android 中，返回值为 0 时，代表系统设置中关闭了推送；为 1 时，代表打开了推送（目前仅适用于Android 4.4+）。
       * @description 在 iOS 中，返回值为 0 时，代表系统设置中关闭了推送；大于 0 时，代表打开了推送，且能够根据返回值判断具体通知形式
       * @param 参数是回调方法，result状态值
       * */
    var _getUserNotificationSettings = function(fun) {
      window.plugins.NXTPlugin.getUserNotificationSettings(fun);
    };

    var _clearLocalNotifications = function() {
      window.plugins.NXTPlugin.clearLocalNotifications();
    };

    // iOS methods
    var _setBadge = function(value) {
      window.plugins.NXTPlugin.setBadge(value);
    };

    var _resetBadge = function() {
      window.plugins.NXTPlugin.resetBadge();
    };

    return {
      init: _init,
      isPushStopped: _isPushStopped,
      stopPush: _stopPush,
      resumePush: _resumePush,
      setTagsWithAlias: _setTagsWithAlias,
      setTags: _setTags,
      setAlias: _setAlias,
      getRegistrationID: _getRegistrationID,
      getUserNotificationSettings: _getUserNotificationSettings,
      clearLocalNotifications: _clearLocalNotifications,
      setBadge: _setBadge,
      resetBadge: _resetBadge
    };
  }
]);
