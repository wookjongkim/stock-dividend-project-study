package zerobase.stockdividendprojectstudy.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.stockdividendprojectstudy.persist.entity.CompanyEntity;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);
}
