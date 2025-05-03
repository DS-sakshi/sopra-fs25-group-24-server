package ch.uzh.ifi.hase.soprafs24.websocket;

import ch.uzh.ifi.hase.soprafs24.websocket.RefreshWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RefreshWebSocketHandler refreshWebSocketHandler;

    public WebSocketConfig(RefreshWebSocketHandler refreshWebSocketHandler) {
        this.refreshWebSocketHandler = refreshWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(refreshWebSocketHandler, "/refresh-websocket")
                .setAllowedOrigins("*"); // adjust origins as needed
    }
}
