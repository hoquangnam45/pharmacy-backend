package com.hoquangnam45.pharmacy.pojo;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class FacebookUserInfo extends GenericJson {
    @Key("email")
    private String email;
    @Key("id")
    private String id;
    @Key("name")
    private String name;
}
