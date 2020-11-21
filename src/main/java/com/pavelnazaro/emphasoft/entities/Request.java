package com.pavelnazaro.emphasoft.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column
    private Double money;

    @Column(name = "currency_base")
    private String currencyBase;

    @Column(name = "currency_need")
    private String currencyNeed;

    @Column
    private Double course;

    @Column
    private Double sum;

    public Request(Long userId, Double money, String currencyBase, String currencyNeed, Double course, Double sum) {
        this.userId = userId;
        this.money = money;
        this.currencyBase = currencyBase;
        this.currencyNeed = currencyNeed;
        this.course = course;
        this.sum = sum;
    }
}
