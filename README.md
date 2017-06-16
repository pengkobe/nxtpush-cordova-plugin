# NXT-Cordova-PushPlugin
集成 极光推送、华为推送、小米推送的 Cordova 插件。

## 注意事项
* 安装插件时需要填写极光推送对应的 APPKEY，参考 plugin.xml 中 `$JIPUSH_APPKEY`
* 安装插件时需要填写小米推送对应的 APPKEY 与 APPID，参考 plugin.xml 中 `$MI_PUSH_APPID` 与 `$MI_PUSH_APPKEY`
* 华为不需要填写相关信息
* IOS 端推送逻辑使用极光版本
  
## 安装插件
```bash
cordova plugin add Your_Plugin_Path --nofetch --variable JIPUSH_APPKEY=YOUR_JIPUSH_APPKEY --variable MI_PUSH_APPID=YOUR_MI_PUSH_APPID --variable MI_PUSH_APPKEY=
=YOUR_MI_PUSH_APPKEY
```

## 关于example
例子基于 ionic ，当然你也可以不基于 ionic ，参考极光官方例子即可。
可以使用 ```ionic start myApp tabs```安装 ionic 初始模板，
 将 www/ 下的代码替换为 example/ 里的代码即可。

## License
MIT