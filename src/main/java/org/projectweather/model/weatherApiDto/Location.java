package org.projectweather.model.weatherApiDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.time.ZoneId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@EqualsAndHashCode
public class Location {
    private String name;
    private String region;
    private String country;
    private ZoneId tz_id;
    @JsonProperty("localtime_epoch")
    private Instant unixTime;
}
