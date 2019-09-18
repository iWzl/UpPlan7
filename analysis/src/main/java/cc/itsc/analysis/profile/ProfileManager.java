package cc.itsc.analysis.profile;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/17 21:42
 */
public class ProfileManager {
    /**
     * 定义字段映射的Key常量
     */
    private static final String PROFILE_ELEMENT_ORIGINAL = "original";
    private static final String PROFILE_ELEMENT_TRANSFER = "transfer";
    private static final String PROFILE_ATTRIBUTE_CATEGORY = "category";
    private static final String PROFILE_ATTRIBUTE_KEY = "key";
    private static final String PROFILE_ATTRIBUTE_TYPE = "type";
    private static final String PROFILE_ATTRIBUTE_READONLY = "readonly";
    private static final String PROFILE_ATTRIBUTE_VERIFY = "verify";
    private static final String PROFILE_ATTRIBUTE_DEFAULT = "default";
    private static final String PROFILE_ATTRIBUTE_DESCRIPTION = "description";
    private static final String PROFILE_ATTRIBUTE_DEPRECATED = "deprecated";
    private static final String PROFILE_ATTRIBUTE_SPREAD = "deprecated";
    private static final String PROFILE_ATTRIBUTE_REF_PROFILE = "ref-profile";
    private static final String PROFILE_ATTRIBUTE_REF_STATE = "ref-state";
    private static final String PROFILE_ATTRIBUTE_TRANS_METHOD = "transMethod";


    /**
     * XML文件的地址
     */
    private static String xmlPath;
    /**
     * 映射到库的Key前缀
     */
    private static String keyPrefix;
    /**
     * Profile Key 及其对应的属性
     */
    private static Map<String, ProfileDefinition> key2Profile = new HashMap<>();
    /**
     * Profile 分组profile对应的Key
     */
    private static Map<String, Set<String>> profileCategory = new HashMap<>();
    /**
     * Profile 分组profile对应的Key
     */
    private static Map<String, String> name2Column = new HashMap<>();
    /**
     * 数组到数据路字段名称的映射
     */
    private static Set<String> needVerifyKeys = new HashSet<>();
    /**
     * 需要Spread的Key
     */
    private static Set<String> needSpreadKeys = new HashSet<>();
    /**
     * 只读Key标识
     */
    private static Set<String> readOnlyKeys = new HashSet<>();
    /**
     * 已经废弃Key标识
     */
    private static Set<String> deprecatedKeys = new HashSet<>();


    public void loadProfileDefinition() {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File(xmlPath));
            Element profiles = document.getRootElement();
            Iterator itp = profiles.elementIterator();
            while (itp.hasNext()) {
                Element item = (Element) itp.next();
                String itemName = item.getName();
                if (PROFILE_ELEMENT_ORIGINAL.equals(itemName) || PROFILE_ELEMENT_TRANSFER.equals(itemName)) {
                    Iterator profileIter = item.elementIterator();
                    while (profileIter.hasNext()) {
                        loadCommonProfileDefinition((Element) profileIter.next(), itemName);
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void loadCommonProfileDefinition(Element profile, String itemName) {
        Attribute attributeName = profile.attribute(PROFILE_ATTRIBUTE_KEY);
        Attribute attributeType = profile.attribute(PROFILE_ATTRIBUTE_TYPE);
        Attribute attributeCategory = profile.attribute(PROFILE_ATTRIBUTE_CATEGORY);
        Attribute attributeVerify = profile.attribute(PROFILE_ATTRIBUTE_VERIFY);
        Attribute attributeSpread = profile.attribute(PROFILE_ATTRIBUTE_SPREAD);
        Attribute attributeReadonly = profile.attribute(PROFILE_ATTRIBUTE_READONLY);
        Attribute attributeDefault = profile.attribute(PROFILE_ATTRIBUTE_DEFAULT);
        Attribute attributeDescription = profile.attribute(PROFILE_ATTRIBUTE_DESCRIPTION);
        Attribute attributeDeprecated = profile.attribute(PROFILE_ATTRIBUTE_DEPRECATED);

        String profileName = attributeName.getValue();
        String profileType = attributeType.getValue();
        String description = "";
        if (null != attributeDescription) {
            description = attributeDescription.getValue();
        }
        if (!PROFILE_ELEMENT_TRANSFER.equals(itemName)) {
            name2Column.put(profileName, String.format("%s%s", keyPrefix, profileName));
        }
        boolean needVerify = attribute2Boolean(attributeVerify);
        boolean needSpread = attribute2Boolean(attributeSpread);
        boolean readOnly = attribute2Boolean(attributeReadonly);
        boolean deprecated = attribute2Boolean(attributeDeprecated);
        if (needVerify) {
            needVerifyKeys.add(profileName);
        }
        if (needSpread) {
            needSpreadKeys.add(profileName);
        }
        if (readOnly) {
            readOnlyKeys.add(profileName);
        }
        if (deprecated) {
            deprecatedKeys.add(profileName);
        }
        String defaultVal = null;
        if (null != attributeDefault) {
            defaultVal = attributeDefault.getValue();
        }
        if (key2Profile.containsKey(profileName)) {
            throw new RuntimeException(String.format("duplicated profile [%s]", profileName));
        }
        if (!PROFILE_ELEMENT_TRANSFER.equals(itemName)) {
            key2Profile.put(profileName, new ProfileOriginal(
                    profileName, profileType, readOnly, defaultVal, description, deprecated, needVerify, needSpread)
            );
        } else {
            Attribute attributeTransMethod = profile.attribute(PROFILE_ATTRIBUTE_TRANS_METHOD);
            Attribute attributeRefProfile = profile.attribute(PROFILE_ATTRIBUTE_REF_PROFILE);
            Attribute attributeRefState = profile.attribute(PROFILE_ATTRIBUTE_REF_STATE);
            String transMethod = "";
            if (null != attributeTransMethod) {
                transMethod = attributeTransMethod.getValue();
            }
            List<String> refProfiles = new LinkedList<>();
            if (null != attributeRefProfile) {
                refProfiles.add(attributeRefProfile.getValue());
            }
            List<String> refStates = new ArrayList<>();
            if (null != attributeRefState) {
                refStates.add(attributeRefState.getValue());
            }
            Iterator refProfileIter = profile.elementIterator();
            while (refProfileIter.hasNext()) {
                Element refProfile = (Element) refProfileIter.next();
                String refProfileName = refProfile.attribute("name").getValue();
                refProfiles.add(refProfileName);
            }
            key2Profile.put(profileName, new ProfileTransfer(
                    profileName, profileType, readOnly, defaultVal, description, deprecated, refProfiles, refStates, transMethod)
            );
        }
    }


    private void loadDefaultTransferDefinition(){}


    private boolean attribute2Boolean(Attribute attribute) {
        boolean value = false;
        if (null != attribute) {
            value = Boolean.parseBoolean(attribute.getValue());
        }
        return value;
    }

}

