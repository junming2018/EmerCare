package com.example.emercare.home;

public class RecordChildData {
    String recordDate, recordDateString, recordPeriod, childRecord, recordData, systolicString, diastolicString, pulseString;
    int recordIcon;

    public RecordChildData(String recordDate, String recordDateString, String recordPeriod,
                           int recordIcon, String childRecord, String recordData,
                           String systolicString, String diastolicString, String pulseString) {
        this.recordDate = recordDate;
        this.recordDateString = recordDateString;
        this.recordPeriod = recordPeriod;
        this.recordIcon = recordIcon;
        this.childRecord = childRecord;
        this.recordData = recordData;
        this.systolicString = systolicString;
        this.diastolicString = diastolicString;
        this.pulseString = pulseString;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public String getRecordDateString() {
        return recordDateString;
    }

    public String getRecordPeriod() {
        return recordPeriod;
    }

    public int getRecordIcon() {
        return recordIcon;
    }

    public String getChildRecord() {
        return childRecord;
    }

    public String getRecordData() {
        return recordData;
    }

    public String getSystolicString() {
        return systolicString;
    }

    public String getDiastolicString() {
        return diastolicString;
    }

    public String getPulseString() {
        return pulseString;
    }
}