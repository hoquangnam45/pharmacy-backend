package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Optional;

@Getter
@Setter
public class StoredUploadConfigDetail {
    private String path;

    public File getPath() {
        return Optional.ofNullable(path).map(File::new).orElse(null);
    }
}
