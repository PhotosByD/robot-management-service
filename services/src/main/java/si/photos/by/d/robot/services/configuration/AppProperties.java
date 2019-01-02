package si.photos.by.d.robot.services.configuration;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("app-properties")
public class AppProperties {
    @ConfigValue(value = "api-key.rapid", watch = true)
    private String rapidApi;

    public String getRapidApi() {
        return rapidApi;
    }

    public void setRapidApi(String rapidApi) {
        this.rapidApi = rapidApi;
    }
}
