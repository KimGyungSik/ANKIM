package shoppingmall.ankim.domain.email.service;

public enum Count {
    FAIL, SUCCESS, RETRY;

    @Override
    public String toString() {
        return this.name();
    }
}
