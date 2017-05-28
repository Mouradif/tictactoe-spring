package com.example.demo.ws;

import com.example.demo.utils.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

/**
 * Created by mkejji on 27/05/2017.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	    Logger.d(this.getClass().getName(), "Opening path to GAME");
	    registry.addHandler(new GameHandler(), "/game").withSockJS();
    }

}
