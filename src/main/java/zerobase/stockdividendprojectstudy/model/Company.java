package zerobase.stockdividendprojectstudy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 생각을 하고 붙이자, 에를들어 setter가 구현되므로 외부에서 바뀔수가 있다.
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    // entity는 DB에 직접 맵핑 되기 위한 클래스이기에
    // 데이터를 주고 받기 위한 용도로 사용하는 것은 바람직 하지 않음(역할 범위 벗어남)
    private String ticker;
    private String name;
}
