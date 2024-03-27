package com.example.unitech.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    private BigDecimal balance;

    private boolean isActive;

}
