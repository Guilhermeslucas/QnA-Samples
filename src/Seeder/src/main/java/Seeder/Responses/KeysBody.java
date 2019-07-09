package Seeder.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeysBody {
        public String primaryEndpointKey;
        public String secondaryEndpointKey;
        public String installedVersion;
        public String lastStableVersion;
}
