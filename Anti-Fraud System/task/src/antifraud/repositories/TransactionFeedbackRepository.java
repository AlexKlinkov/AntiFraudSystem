package antifraud.repositories;


import antifraud.models.TransactionFeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionFeedbackRepository extends JpaRepository<TransactionFeedBack,
        TransactionFeedBack.TransactionFeedbackId> {
    boolean existsByTransactionId(Long transactionId);
    TransactionFeedBack getByTransactionId(long id);
}