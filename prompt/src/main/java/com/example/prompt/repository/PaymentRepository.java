package com.example.prompt.repository;

import com.example.prompt.domain.PaymentEntity;
import com.example.prompt.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository  extends JpaRepository<PaymentEntity, Long> {

    //포트원 결제 고유번호 조회
    Optional<PaymentEntity> findByImpUid(String impUid);

    // 사용자별 결제 내역 조회
    List<PaymentEntity> findByUserOrderByPaidAtDesc(UserEntity user);

    // 중복 결제 확인
    boolean existsByImpUid(String impUid);
}
