package org.projectweather;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class Weather {

    private int regionID;
    private String regionName;
    private int temperature;
    private ZonedDateTime dateTime;

    public Weather(int regionID, String regionName, int temperature, ZonedDateTime dateTime) {
        this.regionID = regionID;
        this.regionName = regionName;
        this.temperature = temperature;
        this.dateTime = dateTime;
    }

}

