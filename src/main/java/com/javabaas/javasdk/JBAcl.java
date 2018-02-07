package com.javabaas.javasdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/8/9.
 */
public class JBAcl {
    private final Map<String, Object> permissionById;
    private static String readTag = "read";
    private static String writeTag = "write";
    private static String publicTag = "*";

    public Map<String, Object> getPermissionById() {
        return permissionById;
    }

    public JBAcl() {
        permissionById = new HashMap<>();
    }

    JBAcl(JBAcl right) {
        permissionById = new HashMap<>();
        permissionById.putAll(right.permissionById);
    }

    public JBAcl(JBUser user) {
        permissionById = new HashMap<>();
        setReadAccess(user, true);

    }

    public void setReadAccess(JBUser user, boolean allowed) {
        setReadAccess(user.getObjectId(), allowed);
    }

    public void setReadAccess(String userId, boolean allowed) {
        allowRead(allowed, userId);
    }

    public void setWriteAccess(JBUser user, boolean allowed) {
        setWriteAccess(user.getObjectId(), allowed);
    }

    public void setWriteAccess(String userId, boolean allowed) {
        allowWrite(allowed, userId);
    }

    public boolean getPublicReadAccess() {
        return isReadAllowed(publicTag);
    }

    public boolean getPublicWriteAccess() {
        return isWriteAllowed(publicTag);
    }

    public boolean getReadAccess(JBUser user) {
        return getReadAccess(user.getObjectId());
    }

    public boolean getReadAccess(String userId) {
        return isReadAllowed(userId);
    }

    public boolean getWriteAccess(JBUser user) {
        return getWriteAccess(user.getObjectId());
    }

    public boolean getWriteAccess(String userId) {
        return isWriteAllowed(userId);
    }

    public void setPublicReadAccess(boolean allowed) {
        allowRead(allowed, publicTag);
    }

    public void setPublicWriteAccess(boolean allowed) {
        allowWrite(allowed, publicTag);
    }

    Map<String, Object> getAclMap() {
        Map<String, Object> aclMap = new HashMap<>();
        aclMap.put("acl", getPermissionById());
        return aclMap;
    }

    private void allowRead(boolean allowed, String key) {
        Map<String, Object> map = mapForKey(key, allowed);
        if (allowed) {
            map.put(readTag, true);
        } else if (map != null) {
            map.remove(readTag);
        }
    }

    private void allowWrite(boolean allowed, String key) {
        Map<String, Object> map = mapForKey(key, allowed);
        if (allowed) {
            map.put(writeTag, allowed);
        } else if (map != null) {
            map.remove(writeTag);
        }
    }

    private Map<String, Object> mapForKey(String key, boolean create) {
        Map<String, Object> map = (Map<String, Object>) permissionById.get(key);
        if (map == null && create) {
            map = new HashMap<>();
            this.permissionById.put(key, map);
        }
        return map;
    }

    private boolean isReadAllowed(String key) {
        Map<String, Object> map = mapForKey(key, false);
        return map != null && map.get(readTag) != null && ((Boolean) map.get(readTag)).booleanValue();
    }

    private boolean isWriteAllowed(String key) {
        Map<String, Object> map = mapForKey(key, false);
        return map != null && map.get(writeTag) != null && ((Boolean) map.get(writeTag)).booleanValue();
    }


}
