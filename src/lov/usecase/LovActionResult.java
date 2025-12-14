package lov.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of executing a LOV action. Contains messages for the View to display.
 * Model/UseCase never prints directly.
 */
public final class LovActionResult {
    private final boolean success;
    private final boolean shouldRenderMap;
    private final List<String> successMessages;
    private final List<String> warningMessages;
    private final List<String> failMessages;

    private LovActionResult(boolean success,
                            boolean shouldRenderMap,
                            List<String> successMessages,
                            List<String> warningMessages,
                            List<String> failMessages) {
        this.success = success;
        this.shouldRenderMap = shouldRenderMap;
        this.successMessages = Collections.unmodifiableList(successMessages);
        this.warningMessages = Collections.unmodifiableList(warningMessages);
        this.failMessages = Collections.unmodifiableList(failMessages);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean shouldRenderMap() {
        return shouldRenderMap;
    }

    public List<String> getSuccessMessages() {
        return successMessages;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }

    public List<String> getFailMessages() {
        return failMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean success = true;
        private boolean shouldRenderMap = false;
        private final List<String> successMessages = new ArrayList<>();
        private final List<String> warningMessages = new ArrayList<>();
        private final List<String> failMessages = new ArrayList<>();

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder shouldRenderMap(boolean shouldRenderMap) {
            this.shouldRenderMap = shouldRenderMap;
            return this;
        }

        public Builder addSuccess(String msg) {
            if (msg != null && !msg.isEmpty()) {
                successMessages.add(msg);
            }
            return this;
        }

        public Builder addWarning(String msg) {
            if (msg != null && !msg.isEmpty()) {
                warningMessages.add(msg);
            }
            return this;
        }

        public Builder addFail(String msg) {
            if (msg != null && !msg.isEmpty()) {
                failMessages.add(msg);
            }
            return this;
        }

        public LovActionResult build() {
            return new LovActionResult(success, shouldRenderMap, successMessages, warningMessages, failMessages);
        }
    }
}


