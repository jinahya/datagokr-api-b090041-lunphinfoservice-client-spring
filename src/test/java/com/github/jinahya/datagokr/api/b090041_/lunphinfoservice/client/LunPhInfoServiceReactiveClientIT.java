package com.github.jinahya.datagokr.api.b090041_.lunphinfoservice.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

@SpringBootTest(
        classes = {
                LunPhInfoServiceReactiveClientIT._Configuration.class,
                LunPhInfoServiceReactiveClient.class
        }
)
@Slf4j
// https://stackoverflow.com/q/48992992/330457
// https://stackoverflow.com/q/48096573/330457
abstract class LunPhInfoServiceReactiveClientIT
        extends AbstractLunPhInfoServiceClientIT<LunPhInfoServiceReactiveClient> {

    @Import(AbstractLunPhInfoServiceClientIT._Configuration.class)
    @Configuration
    static class _Configuration {

        static final int CONNECT_TIME_MILLIS = (int) Duration.ofSeconds(10L).toMillis();

        static final int READ_TIMEOUT_SECONDS = 60;

        static final int WRITE_TIMEOUT_SECONDS = 10;

        @Deprecated
        @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient
        @Bean
        TcpClient tcpClient() {
            return TcpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIME_MILLIS)
                    .doOnConnected(c -> c.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_SECONDS)));
        }

        @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient
        @Bean
        HttpClient httpClient() {
//            return HttpClient.create() // Spring Boot 2.4.X
//                    //.responseTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS)) // since reactor-netty:0.9.11
//                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIME_MILLIS)
//                    .doOnConnected(c -> {
//                        c.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS))
//                                .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_SECONDS));
//                    });
            return HttpClient.from(tcpClient());
        }

        @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient
        @Bean
        ClientHttpConnector clientConnector(
                @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient final HttpClient httpClient) {
            return new ReactorClientHttpConnector(httpClient);
        }

        @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient
        @Bean
        ExchangeStrategies exchangeStrategies() {
            final int byteCount = 256 * 1000; // 256K by default
            return ExchangeStrategies.builder()
                    .codecs(c -> {
                        //c.defaultCodecs().maxInMemorySize(byteCount);
                    })
                    .build();
        }

        @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient
        @Bean
        WebClient lunPhInfoServiceWebClient(
                @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient final ClientHttpConnector clientConnector,
                @LunPhInfoServiceReactiveClient.LunPhInfoServiceWebClient final ExchangeStrategies exchangeStrategies) {
            return WebClient.builder()
                    .clientConnector(clientConnector)
                    .exchangeStrategies(exchangeStrategies)
                    .baseUrl(AbstractLunPhInfoServiceClient.BASE_URL_PRODUCTION)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
                    .build();
        }

        @Bean
        public MethodValidationPostProcessor bean() {
            return new MethodValidationPostProcessor();
        }
    }

    /**
     * Creates a new instance.
     */
    LunPhInfoServiceReactiveClientIT() {
        super(LunPhInfoServiceReactiveClient.class);
    }
}
