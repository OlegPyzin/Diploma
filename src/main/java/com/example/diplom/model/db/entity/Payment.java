package com.example.diplom.model.db.entity;

import com.example.diplom.model.enums.PaymentMethod;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "payments", schema = "financial")
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Column(name = "passenger_id")
    Long passengerId;

    @NotNull
    @Column(name = "bus_id")
    Long busId;

    @NotNull
    @Column(name = "way_id")
    Long wayId;

    @Column(name = "date_payment")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime datePayment;

    @NotNull
    @Column(name = "amount")
    BigDecimal amount;

    @NotNull
    @Column(name = "amount_info")
    String amountInfo;

    @Column(name = "payment_method", length = 30)
    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;
}
