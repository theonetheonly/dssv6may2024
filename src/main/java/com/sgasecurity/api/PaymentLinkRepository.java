package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentLinkRepository extends JpaRepository<PaymentLink, Long> {
    PaymentLink findBySystemCustomerNo(String systemCustomerNo);
    PaymentLink findByTokenId(String tokenId);
    List<PaymentLink> findByIsUsed(String isUsed);

    @Query("SELECT p FROM PaymentLink p WHERE p.systemCustomerNo = :customerNo AND p.tokenId = :tokenId")
    PaymentLink findPaymentLink(@Param("customerNo") String customerNo, @Param("tokenId") String tokenId);

    @Query("SELECT p FROM PaymentLink p WHERE p.systemCustomerNo = :customerNo AND p.id = (SELECT MAX(p2.id) FROM PaymentLink p2 WHERE p2.systemCustomerNo = :customerNo)")
    PaymentLink findLatestPaymentLink(@Param("customerNo") String customerNo);

    @Query("SELECT p FROM PaymentLink p WHERE p.systemCustomerNo = :customerNo AND p.isUsed = 'NO'")
    List<PaymentLink> findPaymentLinksBySystemCustomerNoIsNo(@Param("customerNo") String customerNo);

    @Query("SELECT p FROM PaymentLink p WHERE p.systemCustomerNo = :customerNo AND p.isUsed = 'YES'")
    List<PaymentLink> findPaymentLinksBySystemCustomerNoIsYes(@Param("customerNo") String customerNo);
}
