package antifraud.repositories;

import antifraud.models.MaxValueTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface MaxValueTransactionRepository extends JpaRepository<MaxValueTransaction,
        MaxValueTransaction.MaxValueTransactionKey> {
    boolean existsByNumber(String number);
    MaxValueTransaction getByNumber(String number);
    @Transactional
    @Modifying
    @Query("update MaxValueTransaction m set m.maxALLOWED = :maxALLOWED, m.maxMANUAL = :maxMANUAL where m.number = :number")
    void update (@Param("number") String number, @Param("maxALLOWED") long maxALLOWED,
                 @Param("maxMANUAL") long maxMANUAL);
}
