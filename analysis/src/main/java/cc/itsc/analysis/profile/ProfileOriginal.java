package cc.itsc.analysis.profile;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 00:06
 */
public class ProfileOriginal extends ProfileDefinition {

    private boolean needVerify;
    private boolean needSpread;

    public ProfileOriginal(String key, String type, boolean readOnly, String defaultValue,
                           String description, boolean deprecated, boolean needVerify, boolean needSpread) {
        super(key, type, readOnly, defaultValue, false, description, deprecated);
        this.needVerify = needVerify;
        this.needSpread = needSpread;
    }

    public boolean isNeedVerify() {
        return needVerify;
    }

    public void setNeedVerify(boolean needVerify) {
        this.needVerify = needVerify;
    }

    public boolean isNeedSpread() {
        return needSpread;
    }

    public void setNeedSpread(boolean needSpread) {
        this.needSpread = needSpread;
    }
}
