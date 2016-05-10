package com.kritter.billing.budget_reset.common;

import java.sql.Connection;

public interface IReset {
    int reset(Connection con);
    int resetDailyPayout(Connection con);
}
