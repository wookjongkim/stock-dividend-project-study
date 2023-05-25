package zerobase.stockdividendprojectstudy.exception.Impl;

import org.springframework.http.HttpStatus;
import zerobase.stockdividendprojectstudy.exception.AbstractException;

public class NoSuchTickerException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "해당 티커에 매치되는 회사가 없습니다";
    }
}
