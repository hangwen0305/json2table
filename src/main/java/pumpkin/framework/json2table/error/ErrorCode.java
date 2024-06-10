package pumpkin.framework.json2table.error;

/**
 * @author hangwen
 * @date 2021/3/17
 */
public enum ErrorCode {
    INVALID_JSON,
    INVALID_SELECTOR_PATH,
    UNIQUE_KEY_IS_REQUIRED,
    UNIQUE_VALUES_IS_DUPLICATED;

    public String getCode() {
        return name().replace('_', '.');
    }
}
