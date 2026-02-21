package dev.nilswitt.webmap.api.dtos;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class MapOverlayDto extends AbstractEntityDto {

    private String name;

    private String baseUrl = "";

    private String basePath = "";

    private String tilePathPattern = "/{z}/{x}/{y}.png";

    private int layerVersion = 0;

    private UUID mapGroupId;

    @JsonGetter("fullTileUrl")
    public String getFullTileUrl() {
        return this.baseUrl + "/" + this.basePath + "/" + this.layerVersion + "/" + this.tilePathPattern;
    }
}
