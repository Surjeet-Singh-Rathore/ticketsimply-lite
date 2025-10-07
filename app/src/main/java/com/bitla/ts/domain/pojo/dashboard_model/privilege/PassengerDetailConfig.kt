package com.bitla.ts.domain.pojo.dashboard_model.privilege

data class PassengerDetailConfig(

    val Title: OptionIndividual,
    val Name: OptionIndividual,
    val Age: OptionIndividual,
    val PhoneNumber: OptionIndividual,
    val AlternateNo: OptionIndividual,
    val Email: OptionIndividual,
    val Address: OptionIndividual,
    val IDType: OptionIndividual,
    val IDNumber: OptionIndividual,
    val Remarks: OptionIndividual,
    val BoardingStage: OptionIndividual,
    val DropOffStage: OptionIndividual,
    val PrimaryPassengerMandatory: Option,
    val FirstName: OptionIndividual,
    val LastName: OptionIndividual,
    val CustomerType: OptionIndividual,
    val PassengerNationality: OptionIndividual,
    val DOB: OptionIndividual,
    val AlternateEmail: OptionIndividual,
    val BloodGroup: OptionIndividual,
    val BoardingStageAddress: OptionIndividual,
    val BoardingStageLandmark: OptionIndividual,
    val DropoffStageAddress: OptionIndividual,
    val DropoffStageLandmark: OptionIndividual,
    val OriginAddress: OptionIndividual,
    val DestinationAddress: OptionIndividual
)