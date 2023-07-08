package antifraud.repositories;

import antifraud.models.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {
    boolean existsByNumber(String number);
    StolenCard getStolenCardByNumber(String number);
    void deleteStolenCardByNumber(String number);
}
