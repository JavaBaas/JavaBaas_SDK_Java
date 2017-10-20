package com.javabaas.javasdk;


import com.javabaas.javasdk.callback.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/8/30.
 */
public class JBUser extends JBObject {

    private static final String SESSIONTOKEN = "sessionToken";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String PHONE = "phone";
    private static final String CODE = "code";
    private static final String AUTH = "auth";

    public String getSessionToken() {
        return (String) get(SESSIONTOKEN);
    }

    public void setSessionToken(String sessionToken) {
        put(SESSIONTOKEN, sessionToken);
    }

    public String getUsername() {
        return (String) get(USERNAME);
    }

    public void setUsername(String username) {
        put(USERNAME, username);
    }

    public String getEmail() {
        return (String) get(EMAIL);
    }

    public void setEmail(String email) {
        put(EMAIL, email);
    }

    public void setPassword(String password) {
        put(PASSWORD, password);
    }

    public String getPhone() {
        return (String) get(PHONE);
    }

    public void setPhone(String phone) {
        put(PHONE, phone);
    }

    public JBUser() {
        super(userClassName());
    }

    public static String userClassName() {
        return "_User";
    }

    public static JBQuery<JBUser> getQuery() {
        return new JBQuery<>(JBUser.userClassName());
    }

    public void signUp() throws JBException {
        signUpFromJavabaas(true, new JBSignUpCallback() {
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

    public void signUpInBackground(JBSignUpCallback callback) {
        signUpFromJavabaas(false, callback);
    }

    private void signUpFromJavabaas(final boolean sync, final JBSignUpCallback callback) {
        String path = JBHttpClient.getUserPath();
        Map<String, Object> body = this.getObjectBody();
        sendRequestForSignUp(path, body, sync, callback);
    }

    public void getSmsCode(String phone) throws JBException {
        getSmsCodeFromJavabaas(phone, true, new JBGetSmsCodeCallback() {
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

    public void getSmsCodeInBackground(String phone, JBGetSmsCodeCallback callback) {
        getSmsCodeFromJavabaas(phone,false, callback);
    }

    private void getSmsCodeFromJavabaas(final String phone, final boolean sync, final JBGetSmsCodeCallback callback) {
        if (JBUtils.isEmpty(phone)) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getUserPath("getSmsCode/" + phone);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    public void signUpWithSns(JBAuth auth, JBSnsType type) throws JBException {
        signUpWithSnsFromJavabaas(auth, type, true, new JBSignUpCallback() {
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

    public void signUpWithSnsInBackground(JBAuth auth, JBSnsType type, JBSignUpCallback callback) {
        signUpWithSnsFromJavabaas(auth, type, false, callback);
    }

    private void signUpWithSnsFromJavabaas(JBAuth auth, JBSnsType type, boolean sync, JBSignUpCallback callback) {
        String path = JBHttpClient.getUserPath("registerWithSns/" + type.getCode());
        Map<String, Object> body = new HashMap<>();
        body.put(AUTH, auth);
        body.put("user", getObjectBody());
        sendRequestForSignUp(path, body, sync, callback);
    }

    private void sendRequestForSignUp(final String path, final Map<String, Object> body, final boolean sync, final JBSignUpCallback callback) {
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (result.getData() != null && result.getData().get("result") != null) {
                    copyFromMap((Map<String, Object>) result.getData().get("result"));
                }
                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    public static JBUser login(String username, String password) throws JBException {
        final JBUser[] users = {null};
        loginFromJavabaas(username, password, true, new JBLoginCallback() {
            @Override
            public void done(boolean success, JBUser user, JBException e) {
                if (success) {
                    users[0] = user;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return users[0];
    }

    public static void loginInBackground(String username, String password, JBLoginCallback callback) {
        loginFromJavabaas(username, password, false, callback);
    }

    private static void loginFromJavabaas(final String username, final String password, final boolean sync, final JBLoginCallback callback) {
        if (JBUtils.isEmpty(username) || JBUtils.isEmpty(password)) {
            callback.done(false, null, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getUserPath("login");
        JBHttpParams params = new JBHttpParams();
        params.put("username", username);
        params.put("password", password);
        final JBUser user = new JBUser();
        user.setUsername(username);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, params, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                processResult(user, result.getData());
                callback.done(true, user, null);
            }

            @Override
            public void onFailure(JBException error) {
                if (callback == null) {
                    callback.done(false, null, error);
                }
            }
        });
    }

    public static JBUser loginWithPhone(String phone, String code) throws JBException {
        final JBUser[] users = {null};
        loginWithPhoneFromJavabaas(phone, code, true, new JBLoginCallback() {
            @Override
            public void done(boolean success, JBUser user, JBException e) {
                if (success) {
                    users[0] = user;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return users[0];
    }

    public static void loginWithPhoneInBackground(String phone, String code, JBLoginCallback callback) {
        loginWithPhoneFromJavabaas(phone, code, false, callback);
    }
    
    private static void loginWithPhoneFromJavabaas(final String phone, final String code, final boolean sync, final JBLoginCallback callback) {
        if (JBUtils.isEmpty(phone) || JBUtils.isEmpty(code)) {
            callback.done(false, null, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getUserPath("loginWithPhone");
        Map<String, Object> body = new HashMap<>();
        body.put(PHONE, phone);
        body.put(CODE, code);
        sendRequestForLogin(path, body, sync, callback);
    }

    public static JBUser loginWithSns(JBAuth auth, JBSnsType type) throws JBException {
        final JBUser[] users = {null};
        loginWithSnsFromJavabaas(auth, type, true, new JBLoginCallback() {
            @Override
            public void done(boolean success, JBUser user, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                } else {
                    users[0] = user;
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return users[0];
    }

    public static void loginWithSnsInBackground(JBAuth auth, JBSnsType type, JBLoginCallback callback) {
        loginWithSnsFromJavabaas(auth, type, false, callback);
    }

    private static void loginWithSnsFromJavabaas(final JBAuth auth, final JBSnsType type, final boolean sync, JBLoginCallback callback) {
        String path = JBHttpClient.getUserPath("loginWithSns/" + type.getCode());
        Map<String, Object> body = new HashMap<>();
        body.put(AUTH, auth);
        sendRequestForLogin(path, body, sync, callback);
    }

    private static void sendRequestForLogin(final String path, final Map<String, Object> body, final boolean sync, final JBLoginCallback callback) {
        final JBUser user = new JBUser();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                processResult(user, result.getData());
                callback.done(true, user, null);
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
                }
            }
        });
    }

    /**
     * 重置用户sessionToken
     * 本方法需要master权限
     *
     * @param userId 用户id
     * @throws JBException 异常信息
     */
    public static void resetSessionToken(String userId) throws JBException {
        String path = JBHttpClient.getUserPath(userId + "/resetSessionToken");
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.PUT, null, null, true, new JBObjectCallback() {
            @Override
            public void onFailure(JBException error) {
                JBExceptionHolder.add(error);
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    public String updatePassword(String oldPassword, String newPassword) throws JBException {
        String sessionToken = null;
        updatePasswordFromJavabaas(oldPassword, newPassword, true, new JBUpdatePasswordCallback() {
            @Override
            public void done(boolean success, String sessionToken, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                } else {
                    sessionToken = sessionToken;
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return sessionToken;
    }

    public void updatePasswordInBackground(String oldPassword, String newPassword, JBUpdatePasswordCallback callback) {
        updatePasswordFromJavabaas(oldPassword, newPassword, false, callback);
    }

    private void updatePasswordFromJavabaas(final String oldPassword, final String newPassword, final boolean sync, final JBUpdatePasswordCallback callback) {
        String userId = this.getObjectId();
        if (JBUtils.isEmpty(userId)) {
            callback.done(false, null, new JBException(JBCode.USER_NOT_LOGIN));
            return;
        }
        String path = JBHttpClient.getUserPath(userId + "/updatePassword");
        Map<String, Object> body = new HashMap<>();
        body.put("oldPassword", oldPassword);
        body.put("newPassword", newPassword);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.PUT, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                if (result.getData().get("sessionToken") == null) {
                    callback.done(false, null, new JBException(JBCode.INTERNAL_ERROR.getCode(), "未能获取sessionToken"));
                } else {
                    String sessionToken = (String) result.getData().get("sessionToken");
                    callback.done(true, sessionToken, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
                }
            }
        });
    }

    public void update() throws JBException {
        updateFromJavabaas(true, new JBUpdateCallback() {
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

    public void updateInBackground(JBUpdateCallback callback) {
        updateFromJavabaas(false, callback);
    }

    private void updateFromJavabaas(final boolean sync, final JBUpdateCallback callback) {
        String userId = getObjectId();
        if (JBUtils.isEmpty(userId)) {
            callback.done(false, new JBException(JBCode.USER_NOT_LOGIN));
            return;
        }
        String path = JBHttpClient.getUserPath(userId);
        Map<String, Object> body = getObjectBody();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.PUT, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    protected static void processResult(JBUser user, Map<String, Object> map) {
        if (map == null || map.get("result") == null) {
            return;
        }
        Object o = map.get("result");
        if (o instanceof Map) {
            JBUtils.copyPropertiesFromMapToJBObject(user, map);
        }
    }
}
