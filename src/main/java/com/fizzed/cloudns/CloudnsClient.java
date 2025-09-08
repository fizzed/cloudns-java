package com.fizzed.cloudns;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizzed.crux.okhttp.OkHttpOptions;
import com.fizzed.crux.okhttp.OkHttpUtils;
import com.fizzed.crux.uri.MutableUri;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

import static java.util.Optional.ofNullable;

public class CloudnsClient {
    static private final Logger log = LoggerFactory.getLogger(CloudnsClient.class);

    private final Integer authId;
    private final String authPassword;
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CloudnsClient(Integer authId, String authPassword) {
        this.authId = authId;
        this.authPassword = authPassword;
        this.baseUrl = "https://api.cloudns.net";
        OkHttpOptions<?> options = new OkHttpOptions<>(this.baseUrl + "?logging_level=none");
        this.httpClient = OkHttpUtils.buildClient(options);
        this.objectMapper = new ObjectMapper();
    }

    private MutableUri newUrl(String path) {
        return new MutableUri(this.baseUrl)
            .path(path)
            .query("auth-id", this.authId)
            .query("auth-password", this.authPassword);
    }

    private <T> T execute(Request.Builder requestBuilder, Class<T> type) throws IOException {
        try (final okhttp3.Response response = this.httpClient.newCall(requestBuilder.build()).execute()) {
            final String responseBody = response.body().string();

            // try to parse an exception
            Response status = null;
            try {
                status = this.objectMapper.readValue(responseBody, Response.class);
                if (status != null) {
                    // is the status filled in?
                    if (status.isFailed() && status.getStatusDescription() != null) {
                        throw new CloudnsException(status.getStatusDescription());
                    }
                }
            } catch (JsonMappingException | JsonParseException e) {
                // do nothing
            }

            return this.objectMapper.readValue(responseBody, type);
        }
    }

    public Zones getZones() throws IOException {
        final String url = this.newUrl("dns/list-zones.json")
            .query("page", 1)
            .query("rows-per-page", 100)
            .toString();

        final Request.Builder requestBuilder = new Request.Builder()
            .url(url);

        return this.execute(requestBuilder, Zones.class);
    }

    public Records getRecords(String zoneOrDomain, String host, String type) throws IOException {
        final String url = this.newUrl("dns/records.json")
            .query("page", 1)
            .query("rows-per-page", 100)
            .query("domain-name", zoneOrDomain)
            .queryIfPresent("host", ofNullable(host))
            .queryIfPresent("type", ofNullable(type))
            .toString();

        final Request.Builder requestBuilder = new Request.Builder()
            .url(url);

        return this.execute(requestBuilder, Records.class);
    }

    public Record getRecord(String zoneOrDomain, String recordId) throws IOException {
        final String url = this.newUrl("dns/get-record.json")
            .query("domain-name", zoneOrDomain)
            .query("record-id", recordId)
            .toString();

        final Request.Builder requestBuilder = new Request.Builder()
            .url(url);

        return this.execute(requestBuilder, Record.class);
    }

    public String createOrUpdateRecord(String zoneOrDomain, Record record) throws IOException {
        final Records records = this.getRecords(zoneOrDomain, record.getHost(), record.getType());
        if (!records.isEmpty()) {
            Record r = records.values().iterator().next();
            record.setId(r.getId());
            // only update the record if something changed
            this.updateRecord(zoneOrDomain, record, false);
            return r.getId();
        } else {
            return this.createRecord(zoneOrDomain, record);
        }
    }

    public String createRecord(String zoneOrDomain, Record record) throws IOException {
        final String url = this.newUrl("dns/add-record.json")
            .query("domain-name", zoneOrDomain)
            .query("record-type", record.getType())
            .query("host", record.getHost())
            .query("record", record.getRecord())
            .query("ttl", ofNullable(record.getTtl()).orElse(3600))
            .toString();

        final Request.Builder requestBuilder = new Request.Builder()
            .url(url);

        Response r = this.execute(requestBuilder, Response.class);
        return r.getData().getId();
    }

    public boolean updateRecord(String zoneOrDomain, Record record) throws IOException {
        return this.updateRecord(zoneOrDomain, record, true);
    }

    public boolean updateRecord(String zoneOrDomain, Record record, boolean force) throws IOException {
        Objects.requireNonNull(record, "record was null");

        if (record.getId() == null) {
            throw new IllegalArgumentException("Record MUST have an id");
        }

        final Record existingRecord = this.getRecord(zoneOrDomain, record.getId());

        if (existingRecord == null) {
            throw new CloudnsException("Record not found");
        }

        final boolean changed = existingRecord.update(record);
        if (!force && !changed) {
            return false;       // no update needed
        }

        final String url = this.newUrl("dns/mod-record.json")
            .query("domain-name", zoneOrDomain)
            .query("record-id", existingRecord.getId())
            .query("host", existingRecord.getHost())
            .query("record", existingRecord.getRecord())
            .query("ttl", existingRecord.getTtl())
            .toString();

        final Request.Builder requestBuilder = new Request.Builder()
            .url(url);

        Response r = this.execute(requestBuilder, Response.class);

        return r.isSuccess();
    }

    public boolean deleteRecord(String zoneOrDomain, String recordId) throws IOException {
        final String url = this.newUrl("dns/delete-record.json")
            .query("domain-name", zoneOrDomain)
            .query("record-id", recordId)
            .toString();

        final Request.Builder requestBuilder = new Request.Builder()
            .url(url);

        try {
            Response r = this.execute(requestBuilder, Response.class);
            return r.isSuccess();
        } catch (CloudnsException e) {
            if (e.getMessage().toLowerCase().contains("missing")) {
                return false;
            }
            throw e;
        }
    }

    public boolean isUpdated(String zoneOrDomain) throws IOException {
        final String url = this.newUrl("dns/is-updated.json")
            .query("domain-name", zoneOrDomain)
            .toString();

        final Request.Builder requestBuilder = new Request.Builder()
            .url(url);

        final Boolean v = this.execute(requestBuilder, Boolean.class);

        return v != null && v;
    }

    /*public boolean isUpdated(String hostname) throws IOException {
        // parse hostname, so we can check its domain
        HostName hn = HostName.parse(hostname);
        return this.isZoneUpdated(hn.getDomain());
    }

    *//**
     * Checks if the zone OR cloud domain is fully updated (e.g. any change has been propagated everywhere)
     *//*
    public boolean isZoneUpdated(String zoneOrDomain) throws IOException {
        final String url = new MutableUri("https://api.cloudns.net/dns/is-updated.json")
            .query("auth-id", this.authId)
            .query("auth-password", this.authPassword)
            .query("domain-name", zoneOrDomain)
            .toString();

        final Request.Builder requestBuilder = new Request.Builder()
            .url(url);

        final Response response = OkEdge.create()
            .get(url)
            .execute();

        final String responseBody = response.body().string().toLowerCase();

        //System.out.println(responseBody);

        // if this a missing domain name?
        if (responseBody.contains("missing domain-name")) {
            throw new IllegalArgumentException("Zone or domain " + zoneOrDomain + " does not exist (perhaps you did not check the TLD zone?)");
        }

        return responseBody.equalsIgnoreCase("true");
    }
    */

}