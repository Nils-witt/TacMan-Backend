package dev.nilswitt.tacman.api.ws;

import dev.nilswitt.tacman.security.jwt.JWTTokenComponent;
import dev.nilswitt.tacman.services.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PlainWebSocketHandler plainWebSocketHandler;
    private final JWTTokenComponent jwtComponent;
    private final UserService userService;

    private final String WS_PATH = "/api/ws";

    public WebSocketConfig(
        PlainWebSocketHandler plainWebSocketHandler,
        JWTTokenComponent jwtComponent,
        UserService userService
    ) {
        this.plainWebSocketHandler = plainWebSocketHandler;
        this.jwtComponent = jwtComponent;
        this.userService = userService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(this.plainWebSocketHandler, this.WS_PATH)
            .addInterceptors(new JWTHandshakeInterceptor(this.jwtComponent, this.userService))
            .setAllowedOriginPatterns("*");
    }
}
