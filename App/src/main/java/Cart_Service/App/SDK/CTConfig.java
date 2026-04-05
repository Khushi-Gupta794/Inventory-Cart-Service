package Cart_Service.App.SDK;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ApiRootBuilderUtil;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CTConfig {
    @Value("${ct.client.id}")
    private String clientId;

    @Value("${ct.client.secret}")
    private String clientSecret;

    @Value("${ct.project.key}")
    private String projectKey;

    @Value("${ct.auth.url}")
    private String authUrl;

    @Value("${ct.api.url}")
    private String apiUrl;

    @Bean
    public ProjectApiRoot projectApiRoot(){
       return ApiRootBuilder.of().defaultClient(ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),authUrl,apiUrl).build(projectKey);
    }
}
