package ua.dynamo.kafka.producer.mapper;

import ua.dynamo.kafka.producer.model.ExportPayload;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExportDataMapper {
    public Map<String, Object> serializeToExport(ExportPayload payload) {
        Map<String, Object> record = new HashMap<>();

        record.put("ETL_TSTAMP", null);

        record.put("GEO_ZIPCODE", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getPostalCode() : null);

        record.put("GEO_CITY", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getCityName() : null);

        record.put("COLLECTOR_TSTAMP", LocalDateTime.now());

        record.put("GEO_LONGITUDE", payload.getSummaryInfo() != null
                ? Double.valueOf(payload.getSummaryInfo().getLongitude())
                : null);

        record.put("GEO_REGION_NAME", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getSubdivision1Name() : null);

        record.put("GEO_REGION", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getCountryIsoCode() : null);

        record.put("GEO_TIMEZONE", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getTimeZone() : null);

        record.put("LOCATION_ID", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getGeonameId() : null);

        record.put("GEO_METROCODE", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getMetroCode() : null);

        record.put("USER_IPADDRESS", payload.getIpAddress() != null ? payload.getIpAddress() : null);

        record.put("IS_SATELLITE_PROVIDER", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getIsSatelliteProvider() : null);

        record.put("GEO_COUNTRY", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getCountryName() : null);

        record.put("IP_RANGE", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getNetwork() : null);

        record.put("ACCURACY_RADIUS", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getAccuracyRadius() : null);

        record.put("GEO_LATITUDE", payload.getSummaryInfo() != null
                ? Double.valueOf(payload.getSummaryInfo().getLatitude())
                : null);

        record.put("IS_ANYCAST", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getIsAnycast() : null);

        record.put("EVENT_VERSION", "0.2");

        record.put("IS_ANONYMOUS_PROXY", payload.getSummaryInfo() != null ? payload.getSummaryInfo().getIsAnonymousProxy() : null);

        return record;
    }
}
