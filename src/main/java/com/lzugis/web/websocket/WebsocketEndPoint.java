package com.lzugis.web.websocket;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 推送即将要处理完成的受理单给处理人
 */
@RequestMapping("/websocket")
public class WebsocketEndPoint extends TextWebSocketHandler {
    private Timer timer;
    private static int imgIndex = 0;
    private final String[] imgList = {"1.png","2.png","3.png","4.png","5.png","6.png"};

    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) throws Exception {
        if(!session.isOpen()){
            timer.cancel();
            return;
        }
        super.handleTextMessage(session, message);
        session.sendMessage(message);
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        timer = new Timer(true);
        long delay = 0;
        OrderTimeTask orderTimeTask = new OrderTimeTask(session);
        timer.schedule(orderTimeTask, delay, 5000);// 设定指定的时间time,此处为5s
    }


    class OrderTimeTask extends TimerTask{
        private WebSocketSession session;

        public OrderTimeTask(WebSocketSession session){
            this.session = session;
        }

        @Override
        public void run() {
            try {
                String imgPath = imgList[imgIndex];
                TextMessage textMessage = new TextMessage(imgPath);
                handleMessage(session,textMessage);
                imgIndex++;
                if(imgIndex==imgList.length)imgIndex=0;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Connection Closed！");
    }
}