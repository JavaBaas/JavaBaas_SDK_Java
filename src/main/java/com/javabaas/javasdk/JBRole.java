package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBBooleanCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zangyilin on 2018/4/23.
 */
public final class JBRole extends JBObject {
    private static final String USERS = "users";
    private static final String ROLES = "roles";

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        this.put("name", name);
    }

    public List<String> getRoles() {
        return getList("roles");
    }

    public List<String> getUsers() {
        return getList("users");
    }

    public JBRole() {
        super(roleClassName());
    }

    public JBRole(String name) {
        super(roleClassName());
        setName(name);
    }

    public JBRole(String name, JBAcl acl) {
        super(roleClassName());
        setName(name);
        setAcl(acl);
    }

    public static String roleClassName() {
        return "_Role";
    }

    public void addUser(JBUser user) {
        List<String> userIds = new ArrayList<>();
        userIds.add(user.getObjectId());
        super.addUniqueArray("users", userIds);
    }

    public void addUsers(List<JBUser> users) {
        List<String> userIds = new ArrayList<>();
        for (JBUser user : users) {
            userIds.add(user.getObjectId());
        }
        super.addUniqueArray("users", userIds);
    }

    public void removeUser(JBUser user) {
        List<String> userIds = new ArrayList<>();
        userIds.add(user.getObjectId());
        super.removeArray("users", userIds);
    }

    public void removeUsers(List<JBUser> users) {
        List<String> userIds = new ArrayList<>();
        for (JBUser user : users) {
            userIds.add(user.getObjectId());
        }
        super.removeArray("users", userIds);
    }

    public void addRole(JBRole role) {
        List<String> roleIds = new ArrayList<>();
        roleIds.add(role.getObjectId());
        super.addUniqueArray("roles", roleIds);
    }

    public void addRoles(List<JBRole> roles) {
        List<String> roleIds = new ArrayList<>();
        for (JBRole role : roles) {
            roleIds.add(role.getObjectId());
        }
        super.addUniqueArray("roles", roles);
    }

    public void removeRole(JBRole role) {
        List<String> roleIds = new ArrayList<>();
        roleIds.add(role.getObjectId());
        super.removeArray("roles", roleIds);
    }

    public void removeRoles(List<JBRole> roles) {
        List<String> roleIds = new ArrayList<>();
        for (JBRole role : roles) {
            roleIds.add(role.getObjectId());
        }
        super.removeArray("roles", roleIds);
    }

    public void save() throws JBException {
        saveRoleToJavaBaas(true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    public void saveInBackground() {
        saveRoleToJavaBaas(false, null);
    }

    public void saveInBackground(JBBooleanCallback callback) {
        this.saveRoleToJavaBaas(false, callback);
    }

    private void saveRoleToJavaBaas(final boolean sync, final JBBooleanCallback callback) {
        String path = JBHttpClient.getRolePath(null);
        Map<String, Object> body = this.getObjectForSaveBody();
        saveToJavaBaas(path, JBHttpMethod.POST, null, body, sync, callback);
    }

}
