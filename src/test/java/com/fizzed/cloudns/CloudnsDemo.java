package com.fizzed.cloudns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

class CloudnsDemo {
    static private final Logger log = LoggerFactory.getLogger(CloudnsDemo.class);

    static public void main(String[] args) throws Exception {

        CloudnsClient client = new CloudnsClient(123, "test");

        Zones zones = client.getZones();
        zones.forEach(v -> {
            log.info("{}", v.getName());
        });

        Records records = client.getRecords("fizzed.com", null, RecordType.TXT);
        records.forEach((k,v) -> {
            log.info("host={}, type={}, record={}", v.getHost(), v.getType(), v.getRecord());
        });

        String createdRecordId = client.createRecord("fizzed.net", new Record()
            .setHost("mybasictest")
            .setType("TXT")
            .setRecord("safe-to-delete")
            .setTtl(3600));

        log.info("Created record with id {}", createdRecordId);

        Record createdRecord = client.getRecord("fizzed.net", createdRecordId);


        client.updateRecord("fizzed.net", new Record()
            .setId(createdRecordId)
            .setHost("mybasictest")
            .setType("TXT")
            .setRecord("this-was-changed")
            .setTtl(3600));


        boolean deleted = client.deleteRecord("fizzed.net", createdRecordId);
        log.info("Record deleted = {}", deleted);


        boolean deleted2 = client.deleteRecord("fizzed.net", "not-exist");
        log.info("Record deleted2 = {}", deleted2);


        boolean updated = client.isUpdated("fizzed.net");
        log.info("IsUpdated: {}", updated);
    }

}