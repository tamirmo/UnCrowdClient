package tamirmo.uncrowd.communication;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import tamirmo.uncrowd.logic.UncrowdManager;

public class HttpUtilities {
    private static final int SERVER_PORT = 8083;
    private static final int SERVER_CONNECTION_TIMEOUT = 2000;

    public static RestTemplate createRestTemplate(){
        SimpleClientHttpRequestFactory h = new SimpleClientHttpRequestFactory();
        h.setConnectTimeout(SERVER_CONNECTION_TIMEOUT);
        h.setReadTimeout(SERVER_CONNECTION_TIMEOUT);
        RestTemplate restTemplate2 = new RestTemplate(h);
        restTemplate2.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        return restTemplate2;
    }

    public static String getBaseServerUrl(){
        return String.format("http://%s:%d/",
                // Server IP
                UncrowdManager.getInstance().getServerIp(),
                // Server port
                SERVER_PORT);
    }
}
