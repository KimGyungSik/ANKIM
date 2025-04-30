package shoppingmall.ankim.domain.item.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class StockReduceFailedException extends CustomLogicException {

    private final String orderName;

    public StockReduceFailedException(ErrorCode errorCode, String orderName) {
        super(errorCode);
        this.orderName = orderName;
    }

    public String getOrderName() {
        return orderName;
    }
}


