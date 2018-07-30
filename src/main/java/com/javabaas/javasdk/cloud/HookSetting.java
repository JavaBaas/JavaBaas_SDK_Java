package com.javabaas.javasdk.cloud;

import com.javabaas.javasdk.annotation.HookEvent;

/**
 * Created by Codi on 2018/7/25.
 */
public class HookSetting {
    private boolean beforeInsert;
    private boolean afterInsert;
    private boolean beforeUpdate;
    private boolean afterUpdate;
    private boolean beforeDelete;
    private boolean afterDelete;

    public HookSetting() {
    }

    public HookSetting(boolean enable) {
        this.beforeInsert = enable;
        this.afterInsert = enable;
        this.beforeUpdate = enable;
        this.afterUpdate = enable;
        this.beforeDelete = enable;
        this.afterDelete = enable;
    }

    public HookSetting(boolean beforeInsert, boolean afterInsert, boolean beforeUpdate, boolean afterUpdate, boolean beforeDelete,
                       boolean afterDelete) {
        this.beforeInsert = beforeInsert;
        this.afterInsert = afterInsert;
        this.beforeUpdate = beforeUpdate;
        this.afterUpdate = afterUpdate;
        this.beforeDelete = beforeDelete;
        this.afterDelete = afterDelete;
    }

    public boolean isBeforeInsert() {
        return beforeInsert;
    }

    public void setBeforeInsert(boolean beforeInsert) {
        this.beforeInsert = beforeInsert;
    }

    public boolean isAfterInsert() {
        return afterInsert;
    }

    public void setAfterInsert(boolean afterInsert) {
        this.afterInsert = afterInsert;
    }

    public boolean isBeforeUpdate() {
        return beforeUpdate;
    }

    public void setBeforeUpdate(boolean beforeUpdate) {
        this.beforeUpdate = beforeUpdate;
    }

    public boolean isAfterUpdate() {
        return afterUpdate;
    }

    public void setAfterUpdate(boolean afterUpdate) {
        this.afterUpdate = afterUpdate;
    }

    public boolean isBeforeDelete() {
        return beforeDelete;
    }

    public void setBeforeDelete(boolean beforeDelete) {
        this.beforeDelete = beforeDelete;
    }

    public boolean isAfterDelete() {
        return afterDelete;
    }

    public void setAfterDelete(boolean afterDelete) {
        this.afterDelete = afterDelete;
    }


    /**
     * 获取钩子存储名(类名+事件名)
     *
     * @param name  类名
     * @param event 事件名
     * @return 钩子名
     */
    public static String hookName(String name, HookEvent event) {
        return name + "_" + event.getName();
    }

    public static String hookClazzName(String hookName) {
        if (hookName != null && hookName.contains("_")) {
            String[] values = hookName.split("_");
            if (values.length > 1) {
                return values[0];
            }
        }
        return null;
    }

    public static HookEvent hookEvent(String hookName) {
        if (hookName != null && hookName.contains("_")) {
            String[] values = hookName.split("_");
            if (values.length > 1) {
                String eventName = values[1];
                return HookEvent.getEvent(eventName);
            }
        }
        return null;
    }
}
