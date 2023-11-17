package com.example.demowithtests.dto;

import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.util.annotations.dto.BlockedEmailDomains;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public record UpdateDto(

        @NotNull
        @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters long")
        @Schema(description = "Name of an employee.", example = "Billy", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Name of the country.", example = "England", requiredMode = Schema.RequiredMode.REQUIRED)
        String country,

        @Email
        @NotNull
        @BlockedEmailDomains(contains = {".com1", ".ru", ".su"})
        @Schema(description = "Email address of an employee.", example = "billys@mail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        Gender gender,
        @Valid
        Set<AddressDto> addresses) {

    public UpdateDto(String name,
                     String country,
                     String email,
                     Gender gender,
                     Set<AddressDto> addresses) {
        this.name = name;
        this.country = country;
        this.email = email;
        this.gender = gender;
        this.addresses = addresses != null ? addresses : new HashSet<>();

    }
}
