package com.example.workflow_service.enums;

public enum Patient_Events {
    Register, //will be sent on the status queue once an appointment is created, but will not trigger the state machine, we are setting the state manually in this case

    TREATMENT_STARTED, //from Registered to Under_Treatment // will be sent on the status queue when the doctor post a scan
    TREATMENT_COMPLETED, // from Under_Treatment to Discharged // will be sent on the status queue when the doctor discharge the patient
    APPOINTMENT_CANCELLED, // from Registered or Under_Treatment to Cancelled // scheduled job
    SKIP_TREATMENT // Direct from Registered to Discharged for special cases // will be sent on the status queue when the doctor discharge the patient
}
