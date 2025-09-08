package com.fizzed.cloudns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {"name":"coming-soon.com","type":"master","group":"None","hasBulk":true,"zone":"domain","status":"1","serial":"2021021803","isUpdated":1}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Zone {

    private String name;
    private String type;
    private String zone;
    private String status;
    private String serial;
    private Integer isUpdated;

    public String getName() {
        return name;
    }

    public Zone setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public Zone setType(String type) {
        this.type = type;
        return this;
    }

    public String getZone() {
        return zone;
    }

    public Zone setZone(String zone) {
        this.zone = zone;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Zone setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getSerial() {
        return serial;
    }

    public Zone setSerial(String serial) {
        this.serial = serial;
        return this;
    }

    public Integer getIsUpdated() {
        return isUpdated;
    }

    public Zone setIsUpdated(Integer isUpdated) {
        this.isUpdated = isUpdated;
        return this;
    }
}