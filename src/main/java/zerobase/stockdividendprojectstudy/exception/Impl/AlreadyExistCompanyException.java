package zerobase.stockdividendprojectstudy.exception.Impl;

import org.springframework.http.HttpStatus;
import zerobase.stockdividendprojectstudy.exception.AbstractException;

public class AlreadyExistCompanyException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 DB에 존재하는 회사입니다.";
    }
}
