package com.example.prompt.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    // 사용자 fk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private UserEntity user;

    //플랜 fk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanEntity plan;

    // 포트원 결제 고유 번호
    @Column(name = "imp_uid", nullable = false, unique = true)
    private String impUid;

    // 결제 금액
    @Column(name = "amount", nullable = false)
    private int amount;

    // 결제 완료 시간
    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;


}
