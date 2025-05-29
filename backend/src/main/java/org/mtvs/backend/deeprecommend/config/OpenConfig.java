package org.mtvs.backend.deeprecommend.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "gemini.api")
public class OpenConfig {

    private String key;
    private String url;


}
