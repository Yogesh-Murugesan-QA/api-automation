package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUserResponse {
    private UserData data;
    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }
}