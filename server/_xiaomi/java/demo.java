class Demo{
    private void sendMessage() throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload= "This is a message";
        String title = "notification title";
        String description = "notification description";
        Message message = new Message.Builder()
                    .title(title)
                    .description(description).payload(messagePayload)
                    .restrictedPackageName(MY_PACKAGE_NAME)
                    .notifyType(1)     // 使用默认提示音提示
                    .build();
        sender.send(message, regId, 0); //根据regID，发送消息到指定设备上，不重试。
    }

    private void sendMessages() throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET_KEY);
        List<TargetedMessage> messages = new ArrayList<TargetdMessage>();
        TargetedMessage targetedMessage1 = new TargetedMessage();
        targetedMessage1.setTarget(TargetedMessage.TARGET_TYPE_ALIAS, "alias1");
        String messagePayload1= "This is a message1";
        String title1 = "notification title1";
        String description1 = "notification description1";
        Message message1 = new Message.Builder()
                    .title(title1)
                    .description(description1).payload(messagePayload1)
                    .restrictedPackageName(MY_PACKAGE_NAME)
                    .notifyType(1)     // 使用默认提示音提示
                    .build();
        targetedMessage1.setMessage(message1);
        messages.add(targetedMessage1);
        TargetedMessage targetedMessage2 = new TargetedMessage();
        targetedMessage1.setTarget(TargetedMessage.TARGET_TYPE_ALIAS, "alias2");
        String messagePayload2= "This is a message2";
        String title2 = "notification title2";
        String description2 = "notification description2";
        Message message2 = new Message.Builder()
                    .title(title2)
                    .description(description2).payload(messagePayload2)
                    .restrictedPackageName(MY_PACKAGE_NAME)
                    .notifyType(1)     // 使用默认提示音提示
                    .build();
        targetedMessage2.setMessage(message2);
        messages.add(targetedMessage2);
        sender.send(messages, 0); //根据alias，发送消息到指定设备上，不重试。
    }

    private void sendMessageToAlias() throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "This is a message";
        String title = "notification title";
        String description = "notification description";
        String alias = "testAlias";    //alias非空白，不能包含逗号，长度小于128。
        Message message = new Message.Builder()
                    .title(title)
                    .description(description).payload(messagePayload)
                    .restrictedPackageName(MY_PACKAGE_NAME)
                    .notifyType(1)     // 使用默认提示音提示
                    .build();
        sender.sendToAlias(message, alias, 0); //根据alias，发送消息到指定设备上，不重试。
    }

    private void sendMessageToAliases() throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "This is a message";
        String title = "notification title";
        String description = "notification description";
        List<String> aliasList = new ArrayList<String>();
        aliasList.add("testAlias1");  //alias非空白，不能包含逗号，长度小于128。
        aliasList.add("testAlias2");  //alias非空白，不能包含逗号，长度小于128。
        aliasList.add("testAlias3");  //alias非空白，不能包含逗号，长度小于128。
        Message message = new Message.Builder()
                    .title(title)
                    .description(description).payload(messagePayload)
                    .restrictedPackageName(MY_PACKAGE_NAME)
                    .notifyType(1)     // 使用默认提示音提示
                    .build();
        sender.sendToAlias(message, aliasList, 0); //根据aliasList，发送消息到指定设备上，不重试。
    }

    private void sendBroadcast() throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "This is a message";
        String title = "notification title";
        String description = "notification description";
        String topic = "testTopic";
        Message message = new Message.Builder()
                    .title(title)
                    .description(description).payload(messagePayload)
                    .restrictedPackageName(MY_PACKAGE_NAME)
                    .notifyType(1)     // 使用默认提示音提示
                    .build();
        sender.broadcast(message, topic, 0); //根据topic，发送消息到指定一组设备上，不重试。
    }

}