package com.example.diplom.model.dto.response;

import com.example.diplom.model.dto.request.PassengerInfoRequest;
import com.example.diplom.model.enums.PassengerStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// Does not send NULL data
// Needed for a test to avoid crush
@JsonIgnoreProperties(ignoreUnknown = true)
public class PassengerInfoResponse extends PassengerInfoRequest {
    Long id;
    PassengerStatus status;
}
