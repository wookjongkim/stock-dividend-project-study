package zerobase.stockdividendprojectstudy.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.stockdividendprojectstudy.persist.entity.DividendEntity;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
}
