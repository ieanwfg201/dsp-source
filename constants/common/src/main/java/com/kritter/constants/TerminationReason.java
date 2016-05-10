package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;
/**
 * Author: Rishit Shetty (shetty@kritter.in)
 * Date: 20-06-2013
 * Class:
 */
public enum TerminationReason
{
        ALL_OK(0, "No Termination", "Execution was successful. No termination"),
        UA_NOT_RECEIVED(1, "UA Missing", "User agent not found in the request"),
        DEVICE_NOT_DETECTED(2, "Undetected Device", "The device could not be detected"),
        DEVICE_BOT(3, "BOT Detected", "The user agent represents a BOT"),
        NON_WIRELESS_DEVICE(4, "Non-wireless Device", "Not a wireless device (might be desktop)");

        private int code;
        private String label;
        private String description;

        /**
         * A mapping between the integer code and its corresponding TerminationReason to facilitate lookup by code.
         */
        private static Map<Integer, TerminationReason> codeToTerminationReasonMapping;

        private TerminationReason(int code, String label, String description) {
            this.code = code;
            this.label = label;
            this.description = description;
        }

        public static TerminationReason getTerminationReason(int i) {
            if (codeToTerminationReasonMapping == null) {
                initMapping();
            }
            return codeToTerminationReasonMapping.get(i);
        }

        private static void initMapping() {
            codeToTerminationReasonMapping = new HashMap<Integer, TerminationReason>();
            for (TerminationReason s : values()) {
                codeToTerminationReasonMapping.put(s.code, s);
            }
        }

        public int getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder();
            sb.append("TerminationReason");
            sb.append("{code=").append(code);
            sb.append(", label='").append(label).append('\'');
            sb.append(", description='").append(description).append('\'');
            sb.append('}');
            return sb.toString();
        }
}