package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class PassengerDetailConfig(
    @SerializedName("Address")
    val address: AddressX,
    @SerializedName("Age")
    val age: AgeX,
    @SerializedName("Alternate Email")
    val alternateEmail: AlternateEmailXX,
    @SerializedName("Alternate No")
    val alternateNo: AlternateNoX,
    @SerializedName("Blood Group")
    val bloodGroup: BloodGroupXX,
    @SerializedName("Boarding Stage")
    val boardingStage: BoardingStageX,
    @SerializedName("Boarding Stage Address")
    val boardingStageAddress: BoardingStageAddressXX,
    @SerializedName("Boarding Stage Landmark")
    val boardingStageLandmark: BoardingStageLandmarkXX,
    @SerializedName("Customer Type")
    val customerType: CustomerTypeX,
    @SerializedName("DOB")
    val dOB: DOBX,
    @SerializedName("Destination Address")
    val destinationAddress: DestinationAddressXX,
    @SerializedName("Drop Off Stage")
    val dropOffStage: DropOffStageX,
    @SerializedName("Drop off Stage Address")
    val dropOffStageAddress: DropOffStageAddressXX,
    @SerializedName("Drop off Stage Landmark")
    val dropOffStageLandmark: DropOffStageLandmarkXX,
    @SerializedName("Email")
    val email: EmailX,
    @SerializedName("First Name")
    val firstName: FirstNameXX,
    @SerializedName("ID Number")
    val iDNumber: IDNumberX,
    @SerializedName("ID Type")
    val iDType: IDTypeX,
    @SerializedName("Last Name")
    val lastName: LastNameXX,
    @SerializedName("Name")
    val name: NameX,
    @SerializedName("Origin Address")
    val originAddress: OriginAddressXX,
    @SerializedName("Passenger Nationality")
    val passengerNationality: PassengerNationalityXX,
    @SerializedName("Phone Number")
    val phoneNumber: PhoneNumberX,
    @SerializedName("Primary Passenger Mandatory")
    val primaryPassengerMandatory: PrimaryPassengerMandatoryXX,
    @SerializedName("Remarks")
    val remarks: RemarksX,
    @SerializedName("Title")
    val title: TitleX
)