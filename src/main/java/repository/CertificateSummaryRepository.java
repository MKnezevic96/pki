package repository;

import model.CertificateSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface CertificateSummaryRepository extends JpaRepository<CertificateSummary, Long> {

    CertificateSummary findBySerialNumber(String serialNumber);

    CertificateSummary findByAlias(String alias);

    List<CertificateSummary> findByIsRootTrue();

    List<CertificateSummary> findByIssuerAlias(String alias);

}
