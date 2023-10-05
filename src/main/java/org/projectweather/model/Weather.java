package org.projectweather.model;

import lombok.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Weather {

    private long regionId;
    private String regionName;
    private int temperature;
    private ZonedDateTime zonedDateTime;

}

