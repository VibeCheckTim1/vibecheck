package hr.tvz.vibecheck.exception.framework;

public enum ErrorKey {
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
