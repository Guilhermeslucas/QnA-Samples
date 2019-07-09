package Seeder.Responses;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoBody {
    public String id;
    public String hostName;
    public String lastAccessedTimestamp;
    public String lastChangedTimestamp;
    public String name;
    public String userId;
    ArrayList<Object> urls;
    ArrayList <Object> sources;
    public String language;
    public boolean enableHierarchicalExtraction;
    public String createdTimestamp;
}
