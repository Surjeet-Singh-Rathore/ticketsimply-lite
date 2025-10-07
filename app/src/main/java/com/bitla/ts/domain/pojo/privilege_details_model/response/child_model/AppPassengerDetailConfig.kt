package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model

import com.google.gson.annotations.SerializedName

data class AppPassengerDetailConfig(
    @SerializedName("Address")
    val address: Address?,
    @SerializedName("Age")
    val age: Age?,
    @SerializedName("Alternate_Email")
    val alternateEmail: AlternateEmail?,
    @SerializedName("Alternate_No")
    val alternateNo: AlternateNo?,
    @SerializedName("Blood_Group")
    val bloodGroup: BloodGroup?,
    @SerializedName("Boarding_Stage")
    val boardingStage: BoardingStage?,
    @SerializedName("Boarding_Stage_Address")
    val boardingStageAddress: BoardingStageAddress?,
    @SerializedName("Boarding_Stage_Landmark")
    val boardingStageLandmark: BoardingStageLandmark?,
    @SerializedName("Customer_Type")
    val customerType: CustomerType?,
    @SerializedName("DOB")
    val dOB: DOB?,
    @SerializedName("Destination_Address")
    val destinationAddress: DestinationAddress?,
    @SerializedName("Drop_Off_Stage")
    val dropOffStage: DropOffStage?,
    @SerializedName("Drop_Off_Stage_Address")
    val dropOffStageAddress: DropOffStageAddress?,
    @SerializedName("Drop_Off_Stage_Landmark")
    val dropOffStageLandmark: DropOffStageLandmark?,
    @SerializedName("Email")
    val email: Email?,
    @SerializedName("First_Name")
    val firstName: FirstName?,
    @SerializedName("Id_Number")
    val iDNumber: IDNumber?,
    @SerializedName("Id_Type")
    val iDType: IDType?,
    @SerializedName("Last_Name")
    val lastName: LastName?,
    @SerializedName("Name")
    val name: Name?,
    @SerializedName("Origin_Address")
    val originAddress: OriginAddress?,
    @SerializedName("Passenger_Nationality")
    val passengerNationality: PassengerNationality?,
    @SerializedName("Phone_Number")
    val phoneNumber: PhoneNumber?,
    @SerializedName("Primary_Passenger_Mandatory")
    val primaryPassengerMandatory: PrimaryPassengerMandatory?,
    @SerializedName("Remarks")
    val remarks: Remarks?,
    @SerializedName("Title")
    val title: Title?
)