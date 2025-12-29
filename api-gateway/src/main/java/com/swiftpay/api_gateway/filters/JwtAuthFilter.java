package com.swiftpay.api_gateway.filters;

import com.swiftpay.api_gateway.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/signup",
            "/auth/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        String path = exchange.getRequest().getPath().value();
        String normalizedPath = path.replaceAll("/+$", "");


        if(PUBLIC_PATHS.contains(normalizedPath)){
            return chain.filter(exchange)
                    .doOnSubscribe(s -> System.out.println("Proceeding without check"))
                    .doOnSuccess(v -> System.out.println("successfully passed"))
                    .doOnError(e -> System.err.println("error occured"));
        }


//        exchange.getRequest().getHeaders().forEach((key, value) -> {
//            System.out.println("HEADER => " + key + " = " + value);
//        });

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);



        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }


        try{
            String token = authHeader.substring(7);
            Claims claims = JwtUtil.validateToken(token);
            Integer userId = claims.get("userId", Integer.class);
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Email", claims.getSubject())
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", claims.get("role", String.class))
                            .build())
                    .build();

            mutatedExchange.getRequest().getHeaders().forEach((k, v) ->
                    System.out.println("FORWARDED HEADER => " + k + " = " + v)
            );


            return chain.filter(mutatedExchange)
                    .doOnSubscribe(s -> System.out.println("Proceeding without check"))
                    .doOnSuccess(v -> System.out.println("successfully passed"))
                    .doOnError(e -> System.err.println("error occured"));

        }catch (Exception e){
            System.out.println(e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    @Override
    public int getOrder() {
        return -100;
    }
}
