package hr.tvz.vibecheck.exception.framework;

public enum ErrorKey {
    BAD_REQUEST("bad-request"),
    NOT_FOUND("not-found"),
    CONFLICT("conflict"),
    INTERNAL_SERVER_ERROR("internal-server-error"),
    UNAUTHORIZED("unauthorized"),
    FORBIDDEN("forbidden");

    private final String propertyKey;

    ErrorKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String propertyKey() {
        return propertyKey;
    }
}
