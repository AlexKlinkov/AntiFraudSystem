package antifraud.repositories;

import antifraud.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByNumberAndCreatedDateIsBetween(String cardNumber,
                                                             LocalDateTime time1, LocalDateTime time2);
    List<Transaction> findAllByNumber(String number);
}

