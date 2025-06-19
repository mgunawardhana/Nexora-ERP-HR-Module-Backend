package com.nexora.backend.domain.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexora.backend.domain.enums.Role;

import lombok.*;

import java.util.Date;


@Builder
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationRequest {
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String nic;
    private String phone_number;

    private String driverProfilePicture;
    private String driverNIC;
    private String phoneNumber;
    private String licenseNumber;
    private Date licenseExpiryDate;
    private String driverAddress;
    private String vehicleAssigned;
    private String driverStatus;
    private String emergencyContact;
    private Date dateOfBirth;
    private Date dateOfJoining;
    private String licenseImageFront;
    private String licenseImageBack;
}
