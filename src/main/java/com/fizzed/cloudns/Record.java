package com.fizzed.cloudns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * {"id":"12553501","type":"A","host":"","record":"107.170.232.117","dynamicurl_status":0,"failover":"0","ttl":"3600","status":1}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {

    private String id;
    private String type;
    private String host;
    private Integer ttl;
    private Integer status;
    private String record;

    public String getId() {
        return id;
    }

    public Record setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public Record setType(String type) {
        this.type = type;
        return this;
    }

    public String getHost() {
        return host;
    }

    public Record setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getTtl() {
        return ttl;
    }

    public Record setTtl(Integer ttl) {
        this.ttl = ttl;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Record setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getRecord() {
        return record;
    }

    public Record setRecord(String record) {
        this.record = record;
        return this;
    }

    public boolean update(Record updateRecord) {
        boolean changed = false;

        if (updateRecord.host != null && !Objects.equals(this.host, updateRecord.host)) {
            this.host = updateRecord.host;
            changed = true;
        }

        if (updateRecord.type != null && !Objects.equals(this.type, updateRecord.type)) {
            this.type = updateRecord.type;
            changed = true;
        }

        if (updateRecord.record != null && !Objects.equals(this.record, updateRecord.record)) {
            this.record = updateRecord.record;
            changed = true;
        }

        if (updateRecord.ttl != null && !Objects.equals(this.ttl, updateRecord.ttl)) {
            this.ttl = updateRecord.ttl;
            changed = true;
        }

        return changed;
    }

}