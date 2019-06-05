//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gamamobi.onestore.api;

public class IapEnum {
    public IapEnum() {
    }

    public static enum RecurringAction {
        CANCEL("cancel"),
        REACTIVATE("reactivate");

        private final String type;

        private RecurringAction(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }

    public static enum RecurringState {
        NON_AUTO_PRODUCT(-1),
        AUTO_PAYMENT(0),
        CANCEL_RESERVATION(1);

        private final int type;

        private RecurringState(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }
    }

    public static enum ProductType {
        IN_APP("inapp"),
        AUTO("auto"),
        ALL("all");

        private final String type;

        private ProductType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }
}
