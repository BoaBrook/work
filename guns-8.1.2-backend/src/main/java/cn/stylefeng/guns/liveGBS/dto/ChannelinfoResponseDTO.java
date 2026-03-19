package cn.stylefeng.guns.liveGBS.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelinfoResponseDTO {

    @JsonProperty("Address")
    private String Address;

    @JsonProperty("Altitude")
    private Integer Altitude;

    @JsonProperty("AudioEnable")
    private Boolean AudioEnable;

    @JsonProperty("BatteryLevel")
    private Integer BatteryLevel;

    @JsonProperty("Block")
    private String Block;

    @JsonProperty("Channel")
    private Integer Channel;

    @JsonProperty("CivilCode")
    private String CivilCode;

    @JsonProperty("CloudRecord")
    private Boolean CloudRecord;

    @JsonProperty("CreatedAt")
    private String CreatedAt;

    @JsonProperty("Custom")
    private Boolean Custom;

    @JsonProperty("CustomAddress")
    private String CustomAddress;

    @JsonProperty("CustomBlock")
    private String CustomBlock;

    @JsonProperty("CustomCivilCode")
    private String CustomCivilCode;

    @JsonProperty("CustomFirmware")
    private String CustomFirmware;

    @JsonProperty("CustomID")
    private String CustomID;

    @JsonProperty("CustomIPAddress")
    private String CustomIPAddress;

    @JsonProperty("CustomLatitude")
    private Integer CustomLatitude;

    @JsonProperty("CustomLongitude")
    private Integer CustomLongitude;

    @JsonProperty("CustomManufacturer")
    private String CustomManufacturer;

    @JsonProperty("CustomModel")
    private String CustomModel;

    @JsonProperty("CustomName")
    private String CustomName;

    @JsonProperty("CustomPTZType")
    private Integer CustomPTZType;

    @JsonProperty("CustomParentID")
    private String CustomParentID;

    @JsonProperty("CustomPort")
    private Integer CustomPort;

    @JsonProperty("CustomSerialNumber")
    private String CustomSerialNumber;

    @JsonProperty("CustomStatus")
    private String CustomStatus;

    @JsonProperty("Description")
    private String Description;

    @JsonProperty("DeviceCustomName")
    private String DeviceCustomName;

    @JsonProperty("DeviceID")
    private String DeviceID;

    @JsonProperty("DeviceName")
    private String DeviceName;

    @JsonProperty("DeviceType")
    private String DeviceType;

    @JsonProperty("Direction")
    private Integer Direction;

    @JsonProperty("DownloadSpeed")
    private String DownloadSpeed;

    @JsonProperty("Firmware")
    private String Firmware;

    @JsonProperty("ID")
    private String ID;

    @JsonProperty("IPAddress")
    private String IPAddress;

    @JsonProperty("Latitude")
    private Integer Latitude;

    @JsonProperty("Longitude")
    private Integer Longitude;

    @JsonProperty("Manufacturer")
    private String Manufacturer;

    @JsonProperty("Model")
    private String Model;

    @JsonProperty("Name")
    private String Name;

    @JsonProperty("NumOutputs")
    private Integer NumOutputs;

    @JsonProperty("Ondemand")
    private Boolean Ondemand;

    @JsonProperty("Owner")
    private String Owner;

    @JsonProperty("PTZType")
    private Integer PTZType;

    @JsonProperty("ParentID")
    private String ParentID;

    @JsonProperty("Parental")
    private Integer Parental;

    @JsonProperty("Port")
    private Integer Port;

    @JsonProperty("Quality")
    private String Quality;

    @JsonProperty("RegisterWay")
    private Integer RegisterWay;

    @JsonProperty("Secrecy")
    private Integer Secrecy;

    @JsonProperty("SerialNumber")
    private String SerialNumber;

    @JsonProperty("Shared")
    private Boolean Shared;

    @JsonProperty("SignalLevel")
    private Integer SignalLevel;

    @JsonProperty("SnapURL")
    private String SnapURL;

    @JsonProperty("Speed")
    private Integer Speed;

    @JsonProperty("Status")
    private String Status;

    @JsonProperty("StreamID")
    private String StreamID;

    @JsonProperty("SubCount")
    private Integer SubCount;

    @JsonProperty("UpdatedAt")
    private String UpdatedAt;

}
