package ua.dynamo.kafka.producer.mapper;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import ua.dynamo.kafka.producer.model.SummaryInfo;
import java.util.Map;

public class TableDataMapper {

    private static final String DEFAULT_STRING = "n/a";
    private static final String DEFAULT_NUMBER = "0";
    private static final String DEFAULT_DECIMAL = "0.0";

    public SummaryInfo parseSummaryInfo(Map<String, AttributeValue> item) {
        return SummaryInfo.builder()
                .isAnycast(getString(item, "is_anycast"))
                .isAnonymousProxy(getNumber(item, "is_anonymous_proxy"))
                .registeredCountryGeonameId(getString(item, "registered_country_geoname_id"))
                .representedCountryGeonameId(getString(item, "represented_country_geoname_id"))
                .latitude(getDecimal(item, "latitude"))
                .isSatelliteProvider(getNumber(item, "is_satellite_provider"))
                .accuracyRadius(getNumber(item, "accuracy_radius"))
                .postalCode(getString(item, "postal_code"))
                .network(getString(item, "network"))
                .geonameId(getNumber(item, "geoname_id"))
                .longitude(getDecimal(item, "longitude"))
                .continentName(getString(item, "continent_name"))
                .subdivision2Name(getString(item, "subdivision_2_name"))
                .continentCode(getString(item, "continent_code"))
                .subdivision1IsoCode(getString(item, "subdivision_1_iso_code"))
                .timeZone(getString(item, "time_zone"))
                .subdivision2IsoCode(getString(item, "subdivision_2_iso_code"))
                .localeCode(getString(item, "locale_code"))
                .cityName(getString(item, "city_name"))
                .countryIsoCode(getString(item, "country_iso_code"))
                .metroCode(getString(item, "metro_code"))
                .countryName(getString(item, "country_name"))
                .isInEuropeanUnion(getNumber(item, "is_in_european_union"))
                .subdivision1Name(getString(item, "subdivision_1_name"))
                .build();
    }

    private String getString(Map<String, AttributeValue> item, String key) {
        return getValue(item, key, DEFAULT_STRING);
    }

    private String getNumber(Map<String, AttributeValue> item, String key) {
        return getValue(item, key, DEFAULT_NUMBER);
    }

    private String getDecimal(Map<String, AttributeValue> item, String key) {
        return getValue(item, key, DEFAULT_DECIMAL);
    }

    private String getValue(Map<String, AttributeValue> item, String key, String defaultValue) {
        return item.getOrDefault(key, AttributeValue.builder().s(defaultValue).build()).s();
    }
}
