package antifraud.repositories;

import antifraud.models.SuspiciousIPAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuspiciousIPAddressRepository extends JpaRepository<SuspiciousIPAddress, Long> {
    boolean existsByIp(String ip);
    SuspiciousIPAddress getIPAddressByIp(String ip);
    void deleteIPAddressByIp(String ip);
}
