package com.kritter.entity.user.userid;

import java.util.Map;
import java.util.Set;

public interface DSPUserIdProvider {
    /**
     * Given the exchange user id and a list of DSP ids (not the DSP user ids, but the ad ids corresponding to the DSP
     * in the system), returns the mapping from DSP ids to user id's (or buyer id) corresponding to the exchange user
     * id
     * @param exchangeUserId Id for the user in the exchange. This id is the identifier with which our exchange
     *                       identifies the user
     * @param dspIds Ad inc ids of the DSPs who are eligible for this request
     * @return Mapping from DSP inc id to DSP user id (or buyer id) corresponding to this exchange user id. If the
     * entry for some DSP is not present, it will not be present in the returned map.
     */
    public Map<Integer, String> getDSPUserIdForExchangeId(String exchangeUserId, Set<Integer> dspIds);

    /**
     * Given the exchange user id and a mapping from DSP id (not the DSP user ids, but the ad ids corresponding to the
     * DSP in the system), updates the database with the mapping from exchange user id to the DSP user id (or buyer id).
     * If the mapping is already present, it's overwritten.
     * @param exchangeUserId Id for the user in the exchange. This id is the identifier with which our exchange
     *                       identifies the user
     * @param dspIdToUserIdMap Mapping from DSP inc id to DSP user id (or buyer id) corresponding to this exchange user
     *                         id.
     */
    public void updateDSPUserIdForExchangeId(String exchangeUserId, Map<Integer, String> dspIdToUserIdMap);
}
