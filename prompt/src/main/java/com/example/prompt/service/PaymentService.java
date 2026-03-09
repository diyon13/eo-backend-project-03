package com.example.prompt.service;

import com.example.prompt.domain.PaymentEntity;
import com.example.prompt.domain.PlanEntity;
import com.example.prompt.domain.UserEntity;
import com.example.prompt.dto.payment.PaymentDto;
import com.example.prompt.repository.PaymentRepository;
import com.example.prompt.repository.PlanRepository;
import com.example.prompt.repository.UserRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    /**
     *  결제 검증 및 저장
     */
    @Transactional
    public PaymentDto verifyAndSave(Long userId, PaymentDto dto){

        log.info("결제 검증 시작 - impUid: {}, planName: {}", dto.getImpUid(), dto.getPlanName());

        // 중복 결제 확인
        if (paymentRepository.existsByImpUid(dto.getImpUid())) {
            throw new IllegalArgumentException("이미 처리된 결제입니다.");
        }


        // 포트원 서버에서 결제 정보 조회
        IamportResponse<Payment> iamportResponse;
        try {
            iamportResponse = iamportClient.paymentByImpUid(dto.getImpUid());
        } catch (IamportResponseException | IOException e) {
            log.error("포트원 결제 조회 실패: {}", e.getMessage());
            throw new RuntimeException("결제 정보 조회에 실패했습니다.");
        }

        Payment payment = iamportResponse.getResponse();

        // 결제 상태 확인
        if (!"paid".equals(payment.getStatus())) {
            throw new IllegalArgumentException("결제가 완료되지 않았습니다. 상태: " + payment.getStatus());
        }

        //  결제 금액 검증
        PlanEntity plan = planRepository.findByPlanName(dto.getPlanName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플랜입니다: " + dto.getPlanName()));

        BigDecimal paidAmount = payment.getAmount();
        if (paidAmount.intValue() != plan.getPrice()) {
            log.error("결제 금액 불일치 - 실제: {}, 플랜가격: {}", paidAmount.intValue(), plan.getPrice());
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        //사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 결제 내역 저장
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .user(user)
                .plan(plan)
                .impUid(dto.getImpUid())
                .amount(paidAmount.intValue())
                .paidAt(LocalDateTime.now())
                .build();
        paymentRepository.save(paymentEntity);
        log.info("결제 내역 저장 완료 - paymentId: {}", paymentEntity.getPaymentId());

        //사용자 플랜 업그레이드
        user.setPlan(plan);
        userRepository.save(user);
        log.info("플랜 업그레이드 완료 - userId: {}, planName: {}", userId, plan.getPlanName());

        return PaymentDto.builder()
                .impUid(dto.getImpUid())
                .planName(plan.getPlanName())
                .amount(paidAmount.intValue())
                .paidAt(LocalDateTime.now())
                .success(true)
                .message(plan.getPlanName() + " 플랜으로 업그레이드 되었습니다.")
                .build();


    }

    /**
     * 내 결제 내역 조회
     */
    @Transactional(readOnly = true)
    public List<PaymentDto> getMyPayments(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return paymentRepository.findByUserOrderByPaidAtDesc(user).stream()
                .map(p -> PaymentDto.builder()
                        .impUid(p.getImpUid())
                        .planName(p.getPlan().getPlanName())
                        .amount(p.getAmount())
                        .paidAt(p.getPaidAt())
                        .success(true)
                        .build())
                .toList();
    }
}
