package cc.itsc.analysis.profile;

import java.util.List;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 00:21
 */
public class ProfileTransfer extends ProfileDefinition {


    private List<String> refProfiles;
    private List<String> refStates;
    private String transMethod;

    public ProfileTransfer(String key, String type, boolean readOnly, String defaultValue, String description, boolean deprecated, List<String> refProfiles, List<String> refStates, String transMethod) {
        super(key, type, readOnly, defaultValue, true, description, deprecated);
        this.refProfiles = refProfiles;
        this.refStates = refStates;
        this.transMethod = transMethod;
    }

    public List<String> getRefProfiles() {
        return refProfiles;
    }

    public List<String> getRefStates() {
        return refStates;
    }

    public String getTransMethod() {
        return transMethod;
    }

}
