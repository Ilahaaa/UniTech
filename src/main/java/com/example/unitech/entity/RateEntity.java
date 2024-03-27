package com.example.unitech.entity;

import com.example.unitech.entity.enums.EnumCurrency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Map;



@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "RateEntity")
@Entity
public class RateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "base", columnDefinition = "VARCHAR(255)")
    private EnumCurrency base;

    @ElementCollection
    @CollectionTable(name = "rate_mapping",
            joinColumns = {@JoinColumn(name = "rate_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "currency")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "rates")
    private Map<EnumCurrency, Double> rates;

}