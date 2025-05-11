
package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ch.uzh.ifi.hase.soprafs24.websocket.RefreshWebSocketHandler;


@RestController
public class RefreshController {

    private final RefreshWebSocketHandler refreshWebSocketHandler;

    public RefreshController(RefreshWebSocketHandler refreshWebSocketHandler) {
        this.refreshWebSocketHandler = refreshWebSocketHandler;
    }

    @PostMapping("/trigger-refresh")
    public String triggerRefresh() {
        refreshWebSocketHandler.broadcastRefresh("1");
        return "Refresh message sent";
    }
}
