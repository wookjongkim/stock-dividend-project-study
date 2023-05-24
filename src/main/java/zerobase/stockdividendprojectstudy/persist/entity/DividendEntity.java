package zerobase.stockdividendprojectstudy.persist.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import zerobase.stockdividendprojectstudy.model.Dividend;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name="DIVIDEND")
@Getter
@ToString
@NoArgsConstructor
@Table(
        // 복합 유니크키 설정
    uniqueConstraints = {
            @UniqueConstraint(
                    columnNames = {"companyId", "date"}
            )
    }
)
public class DividendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private LocalDateTime date;

    private String dividend;

    public DividendEntity(Long companyId, Dividend dividend){
        this.companyId = companyId;
        this.date = dividend.getDate();
        this.dividend = dividend.getDividend();
    }
}
