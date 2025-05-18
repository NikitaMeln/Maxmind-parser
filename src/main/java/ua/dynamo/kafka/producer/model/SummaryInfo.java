package ua.dynamo.kafka.producer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class SummaryInfo {
    private String network;
    private String geonameId;
    private String isAnycast;
    private String isAnonymousProxy;
    private String registeredCountryGeonameId;
    private String representedCountryGeonameId;
    private String latitude;
    private String isSatelliteProvider;
    private String accuracyRadius;
    private String postalCode;
    private String longitude;
    private String continentName;
    private String subdivision2Name;
    private String continentCode;
    private String subdivision1IsoCode;
    private String timeZone;
    private String subdivision2IsoCode;
    private String localeCode;
    private String cityName;
    private String countryIsoCode;
    private String metroCode;
    private String countryName;
    private String isInEuropeanUnion;
    private String subdivision1Name;
}
