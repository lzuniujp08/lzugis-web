package com.lzugis.web.websocket;

import com.lzugis.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@RequestMapping("/websocket")
public class LocationTrackEndPoint extends TextWebSocketHandler {

    @Autowired
    private TestService testService;

    private Timer timer;

    private static int rownum = 1;
    private List gpsData;

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
        rownum = 0;
        OrderTimeTask orderTimeTask = new OrderTimeTask(session);
        gpsData = testService.getGpsData();
        timer.schedule(orderTimeTask, delay, 1000);// 设定指定的时间time,此处为5s
    }


    class OrderTimeTask extends TimerTask{
        private WebSocketSession session;

        public OrderTimeTask(WebSocketSession session){
            this.session = session;
        }

        @Override
        public void run() {
            try {
                String lonlat = ((Map)gpsData.get(rownum)).get("lonlat").toString();
                TextMessage textMessage = new TextMessage(lonlat);
                handleMessage(session,textMessage);

                rownum++;
                if(rownum>gpsData.size()-1){
                    rownum = 0;
                }
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