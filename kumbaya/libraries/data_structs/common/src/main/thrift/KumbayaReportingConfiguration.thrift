namespace java com.kritter.kumbaya.libraries.data_structs.common

enum MEMBERTYPE {
    METRIC = 1,
    DIMENSION = 2,
    TIME = 3,
    HIERARCHY = 4,
}

enum TABLE{
    first_level = 1,
    first_level_daily = 2,
    first_level_monthly = 3,
    first_level_yearly = 4,
    first_level_limited = 5,
    first_level_limited_daily = 6,
    first_level_limited_monthly = 7,
    first_level_limited_yearly = 8,    
    ad_position_hourly = 9,
    ad_position_daily = 10,
    ad_position_monthly = 11,
    channel_hourly = 12,
    channel_daily = 13,
    channel_monthly = 14,
    fast_path = 15,
    exchange_hourly = 16,
    exchange_daily = 17,
    tracking_hourly = 18,
    tracking_daily = 19,
    tracking_monthly = 20,
    fraud_hourly = 21,
    fraud_daily = 22,
}  

struct SiteId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'siteId',
    3: string dim_table = 'site', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'SITE NAME',
    8: string return_prefix = 'site'
    9: string dim_guid = 'guid',
}

struct Ext_site {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'ext_site',
    3: string dim_table = 'ext_supply_attr', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'ext_supply_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'External Site',
    8: string return_prefix = 'ext_site'
}


struct Supply_source_type {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'supply_source_type',
    3: string dim_table = 'supply_source_type', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Supply Source Type',
    8: string return_prefix = 'supply_source_type'
}

struct Device_type {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'device_type',
    3: string dim_table = 'device_type', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Device Type',
    8: string return_prefix = 'device_type'
}

struct Reqslot {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'reqslot',
    3: string dim_table = 'creative_slots', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'width',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'reqslot',
    8: string return_prefix = 'reqslot'
    9: string second_dim_column_name = 'height',
    10: string mul_dim_column_delimeter = '*',
}


struct DeviceId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'deviceId',
    3: string dim_table = 'handset_detection_data', 
    4: string dim_column = 'internal_id',
    5: string dim_column_name = 'marketing_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Marketing Name',
    8: string return_prefix = 'device'
}

struct BrowserId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'browserId',
    3: string dim_table = 'handset_browser', 
    4: string dim_column = 'browser_id',
    5: string dim_column_name = 'browser_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Browser',
    8: string return_prefix = 'browser'
}

struct DeviceManufacturerId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'deviceManufacturerId',
    3: string dim_table = 'handset_manufacturer', 
    4: string dim_column = 'manufacturer_id',
    5: string dim_column_name = 'manufacturer_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Manufacturer Name',
    8: string return_prefix = 'deviceManufacturer'
}

struct DeviceModelId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'deviceModelId',
    3: string dim_table = 'handset_model', 
    4: string dim_column = 'model_id',
    5: string dim_column_name = 'model_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Model NAME',
    8: string return_prefix = 'deviceModel'
    
}

struct DeviceOsId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'deviceOsId',
    3: string dim_table = 'handset_os', 
    4: string dim_column = 'os_id',
    5: string dim_column_name = 'os_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'OS NAME',
    8: string return_prefix = 'deviceOs'
}

struct ConnectionType {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'connection_type',
    3: string dim_table = 'connection_type_metadata', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Connection Type',
    8: string return_prefix = 'connection_type'
}

struct CountryId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'countryId',
    3: string dim_table = 'ui_targeting_country', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'country_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'COUNTRY NAME',
    8: string return_prefix = 'country'
}

struct CountryCarrierId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'countryCarrierId',
    3: string dim_table = 'ui_targeting_isp', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'isp_ui_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'ISP NAME',
    8: string return_prefix = 'isp'
}
struct StateId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'stateId',
    3: string dim_table = 'ui_targeting_state', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'state_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'State/Province',
    8: string return_prefix = 'state'
}
struct CityId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'cityId',
    3: string dim_table = 'ui_targeting_city', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'city_name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'City',
    8: string return_prefix = 'city'
}
struct Marketplace {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'marketplace',
    3: string dim_table = 'marketplace', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'pricing',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Marketplace',
    8: string return_prefix = 'marketplace'
}
struct ChannelId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'channelId',
    3: string dim_table = 'channel', 
    4: string dim_column = 'internalid',
    5: string dim_column_name = 'channelname',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Channel',
    8: string return_prefix = 'channel'
}

struct AdpositionId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'adpositionId',
    3: string dim_table = 'ad_position', 
    4: string dim_column = 'internalid',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Adposition',
    8: string return_prefix = 'adposition'
}

struct ExchangeId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'exchangeId',
    3: string dim_table = 'account', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'EXCHANGE NAME',
    8: string return_prefix = 'exchange'    
}
struct CreativeId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'creativeId',
    3: string dim_table = 'creative_container', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'label',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'CREATIVE NAME',
    8: string return_prefix = 'creative'
}
struct AdId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'adId',
    3: string dim_table = 'ad', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'AD NAME',
    8: string return_prefix = 'ad'
}

struct CampaignId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'campaignId',
    3: string dim_table = 'campaign', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'CAMPAIGN NAME',
    8: string return_prefix = 'campaign'
}

struct NofillReason {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'nofillReason',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'NOFILL REASON',
    8: string return_prefix = 'nofillreason'
}
struct Total_request {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_request',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL REQUEST',
    8: string return_prefix = 'total_request'
}
struct Total_impression {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_impression',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL IMPRESSION',
    8: string return_prefix = 'total_impression'
}

struct Total_bidValue {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_bidValue',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL BID VALUE',
    8: string return_prefix = 'total_bidValue'
}

struct Total_click {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_click',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL CLICK',
    8: string return_prefix = 'total_click'
}

struct Billedclicks {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'billedclicks',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Billed Clicks',
    8: string return_prefix = 'billedclicks'
}

struct Billedcsc {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'billedcsc',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Billed CSC',
    8: string return_prefix = 'billedcsc'
}

struct Total_win {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_win',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL WIN',
    8: string return_prefix = 'total_win'
}
struct Total_win_bidValue {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_win_bidValue',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL WIN BID VALUE',
    8: string return_prefix = 'total_win_bidValue'
}
struct Total_csc {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_csc',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL CLIENT RENDER',
    8: string return_prefix = 'total_csc'
}
struct Total_event_type {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_event_type',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL EVENT TYPE',
    8: string return_prefix = 'total_event_type'
}
struct DemandCharges {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'demandCharges',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Revenue',
    8: string return_prefix = 'demandCharges'
}
struct SupplyCost {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'supplyCost',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'PubIncome',
    8: string return_prefix = 'supplyCost'
}
struct Earning {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'earning',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL EARNING',
    8: string return_prefix = 'earning'
}
struct Conversion {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'conversion',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Conversions',
    8: string return_prefix = 'conversion'
}

struct CPAGoal {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'cpa_goal',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'CPA Revenue',
    8: string return_prefix = 'cpagoal'
}

struct ExchangePayout {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'exchangepayout',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Exchange Income',
    8: string return_prefix = 'exchangepayout'
}

struct ExchangeRevenue {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'exchangerevenue',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Exchange Revenue',
    8: string return_prefix = 'exchangerevenue'
}

struct NetworkPayout {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'networkpayout',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Network Income',
    8: string return_prefix = 'networkpayout'
}

struct NetworkRevenue {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'networkrevenue',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Network Revenue',
    8: string return_prefix = 'networkrevenue'
}

struct Start_time {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'impression_time',
    6: MEMBERTYPE member_type = MEMBERTYPE.TIME
    7: string uiname = 'TIME',
    8: string return_prefix = 'time'
}

struct End_time {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'impression_time',
    6: MEMBERTYPE member_type = MEMBERTYPE.TIME
}

struct PublisherId {
    1: MEMBERTYPE member_type = MEMBERTYPE.HIERARCHY,
    2: string first_table = 'account',
    3: string first_column = 'id',
    4: string first_column_name = 'name',
    5: string second_table = 'site',
    6: string second_column_name = 'pub_id',
    7: string uiname = 'Publisher',
    8: string return_prefix = 'pub',
    9: string fact_column = 'siteId',
    10: string second_fact_column = 'id'
    11: TABLE fact_table = 1,
}

struct AdvertiserId {
    1: MEMBERTYPE member_type = MEMBERTYPE.HIERARCHY,
    2: string first_table = ' account',  /*NOTE a SPACE before account has been specifically added*/
    3: string first_column_filter = 'guid',
    4: string first_column = 'guid',
    5: string first_column_name = 'name',
    6: string second_table = 'campaign',
    7: string second_column_name = 'account_guid',
    8: string uiname = 'Advertiser',
    9: string return_prefix = 'adv',
    10: string fact_column = 'campaignId',
    11: string second_fact_column = 'id'
    12: TABLE fact_table = 1,
}


struct Site_hygiene {
    1: MEMBERTYPE member_type = MEMBERTYPE.HIERARCHY,
    2: string first_table = 'reporting_hygiene_categories', 
    3: string first_column_filter = 'id',
    4: string first_column = 'id',
    5: string first_column_name = 'name',
    6: string second_table = ' site',
    7: string second_column_name = 'hygiene_list',
    8: string uiname = 'Site Hygiene',
    9: string return_prefix = 'site_hygiene',
    10: string fact_column = 'siteId',
    11: string second_fact_column = 'id'
    12: TABLE fact_table = 1,
}


struct Query_Destination {
    1: string destination = 'DB',
    2: string destination_type = 'MYSQL',
    3: string tz = 'UTC'
}

struct PubIncId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'pubId',
    3: string dim_table = 'account', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Pub NAME',
    8: string return_prefix = 'pubId'
}

struct AdvIncId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'advId',
    3: string dim_table = ' account', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'Adv NAME',
    8: string return_prefix = 'advId'
}

struct CreativeFormatId {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'formatId',
    3: string dim_table = ' creative_formats', 
    4: string dim_column = 'id',
    5: string dim_column_name = 'name',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'CreativeFormat',
    8: string return_prefix = 'formatId'
}

struct Total_floor {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_floor',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL FLOOR',
    8: string return_prefix = 'total_floor'
}

struct ReqState {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'reqState',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'ReqState',
    8: string return_prefix = 'reqState'
}

struct DspNoFill {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'dspNoFill',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'DspNoFill',
    8: string return_prefix = 'dspNoFill'
}

struct TerminationReason {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'terminationReason',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'TerminationReason',
    8: string return_prefix = 'terminationReason'
}

struct Tevent {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'tevent',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'TEvent',
    8: string return_prefix = 'tevent'
}

struct Teventtype {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'teventtype',
    6: MEMBERTYPE member_type = MEMBERTYPE.DIMENSION,
    7: string uiname = 'TEventtype',
    8: string return_prefix = 'teventtype'
}

struct Total_event {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_event',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL Event',
    8: string return_prefix = 'total_event'
}

struct PostimpEvent {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'event',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'PostimpEvent',
    8: string return_prefix = 'postimpevent'
}

struct Total_Request_To_Dsp {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'total_impression',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'Total Request To Dsp',
    8: string return_prefix = 'total_request_to_dsp'
}

struct Total_count {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'count',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL Count',
    8: string return_prefix = 'count'
}

struct Total_bidfloor {
    1: TABLE fact_table = 1, 
    2: string fact_column = 'bidFloor',
    6: MEMBERTYPE member_type = MEMBERTYPE.METRIC,
    7: string uiname = 'TOTAL BidFloor',
    8: string return_prefix = 'total_bidfloor'
}


struct KumbayaReportingConfiguration{
    1: SiteId siteId = {'fact_table':TABLE.first_level, 'fact_column':'siteId', 'dim_table':'site', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'SITE NAME','return_prefix':'site','dim_guid':'guid'},
    2: DeviceId deviceId = {'fact_table':TABLE.first_level, 'fact_column':'deviceId', 'dim_table':'handset_detection_data', 'dim_column':'internal_id', 'dim_column_name':'marketing_name','member_type':MEMBERTYPE.DIMENSION, 'uiname':'Marketing Name','return_prefix':'device'},
    3: DeviceManufacturerId deviceManufacturerId = {'fact_table':TABLE.first_level, 'fact_column':'deviceManufacturerId', 'dim_table':'handset_manufacturer', 'dim_column':'manufacturer_id', 'dim_column_name':'manufacturer_name','member_type':MEMBERTYPE.DIMENSION, 'uiname':'Manufacturer Name','return_prefix':'deviceManufacturer'},
    4: DeviceModelId deviceModelId = {'fact_table':TABLE.first_level, 'fact_column':'deviceModelId', 'dim_table':'handset_model', 'dim_column':'model_id', 'dim_column_name':'model_name','member_type':MEMBERTYPE.DIMENSION, 'uiname':'Model NAME','return_prefix':'deviceModel'},
    5: DeviceOsId deviceOsId = {'fact_table':TABLE.first_level, 'fact_column':'deviceOsId', 'dim_table':'handset_os', 'dim_column':'os_id', 'dim_column_name':'os_name','member_type':MEMBERTYPE.DIMENSION,'uiname':'OS NAME','return_prefix':'deviceOs'},
    6: CountryId countryId = {'fact_table':TABLE.first_level, 'fact_column':'countryId', 'dim_table':'ui_targeting_country', 'dim_column':'id', 'dim_column_name':'country_name','member_type':MEMBERTYPE.DIMENSION,'uiname':'COUNTRY NAME','return_prefix':'country'},
    7: CountryCarrierId countryCarrierId = {'fact_table':TABLE.first_level, 'fact_column':'countryCarrierId', 'dim_table':'ui_targeting_isp', 'dim_column':'id', 'dim_column_name':'isp_ui_name','member_type':MEMBERTYPE.DIMENSION,'uiname':'ISP NAME','return_prefix':'isp'},
    8: ExchangeId exchangeId = {'fact_table':TABLE.first_level, 'fact_column':'exchangeId', 'dim_table':'account', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'EXCHANGE NAME','return_prefix':'exchange'},
    9: CreativeId creativeId = {'fact_table':TABLE.first_level, 'fact_column':'creativeId', 'dim_table':'creative_container', 'dim_column':'id', 'dim_column_name':'label','member_type':MEMBERTYPE.DIMENSION, 'uiname':'CREATIVE NAME','return_prefix':'creative'},
    10: AdId adId = {'fact_table':TABLE.first_level, 'fact_column':'adId', 'dim_table':'ad', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION, 'uiname':'AD NAME','return_prefix':'ad'},
    11: CampaignId campaignId = {'fact_table':TABLE.first_level, 'fact_column':'campaignId', 'dim_table':'campaign', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION, 'uiname':'CAMPAIGN NAME','return_prefix':'campaign'},

    13: NofillReason nofillReason = {'fact_table':TABLE.first_level, 'fact_column':'nofillReason','member_type':MEMBERTYPE.DIMENSION, 'uiname':'NOFILL REASON','return_prefix':'nofillreason'},
    14: Total_request total_request = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_request', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL REQUEST','return_prefix':'total_request'},
    15: Total_impression total_impression = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_impression', 'member_type' : MEMBERTYPE.METRIC},
    16: Total_bidValue total_bidValue = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_bidValue', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL BID VALUE','return_prefix':'total_bidValue'},
    17: Total_click total_click = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_click', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL CLICK','return_prefix':'total_click'},
    18: Total_win total_win = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_win', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL WIN','return_prefix':'total_win'},
    19: Total_win_bidValue total_win_bidValue = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_win_bidValue', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL WIN BID VALUE','return_prefix':'total_win_bidValue'},
    20: Total_csc total_csc = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_csc', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL CLIENT RENDER','return_prefix':'total_csc'},
    21: Total_event_type total_event_type = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_event_type', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL EVENT TYPE','return_prefix':'total_event_type'},
    22: DemandCharges demandCharges = {'fact_table' :TABLE.first_level, 'fact_column' : 'demandCharges', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Revenue','return_prefix':'demandCharges'},
    23: SupplyCost supplyCost = {'fact_table' :TABLE.first_level, 'fact_column' : 'supplyCost', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'PubIncome','return_prefix':'supplyCost'},
    24: Earning earning = {'fact_table' :TABLE.first_level, 'fact_column' : 'earning', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL EARNING','return_prefix':'earning'},
    25: Start_time start_time = {'fact_table' :TABLE.first_level, 'fact_column' : 'impression_time', 'member_type' : MEMBERTYPE.TIME,'uiname':'TIME','return_prefix':'time'},
    26: End_time end_time = {'fact_table' :TABLE.first_level, 'fact_column' : 'impression_time', 'member_type' : MEMBERTYPE.TIME},
    27: Query_Destination query_Destination = { 'destination':'DB', 'destination_type':'MYSQL', 'tz':'UTC'},
    28: PublisherId publisherId={'member_type':MEMBERTYPE.HIERARCHY,'first_table':'account','first_column':'id','first_column_name':'name','second_table':'site','second_column_name':'pub_id','uiname':'Publisher','return_prefix':'pub','fact_column':'siteId','second_fact_column':'id' ,'fact_table':TABLE.first_level}
    29: AdvertiserId advertiserId={'member_type':MEMBERTYPE.HIERARCHY,'first_table':' account','first_column_filter':'guid','first_column':'guid','first_column_name':'name','second_table':'campaign','second_column_name':'account_guid','uiname':'Advertiser','return_prefix':'adv','fact_column':'campaignId','second_fact_column':'id','fact_table':TABLE.first_level}
    30: Conversion conversion = {'fact_table' :TABLE.first_level, 'fact_column' : 'conversion', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Conversions','return_prefix':'conversion'},
    31: BrowserId browserId = {'fact_table':TABLE.first_level, 'fact_column':'browserId', 'dim_table':'handset_browser', 'dim_column':'browser_id', 'dim_column_name':'browser_name','member_type':MEMBERTYPE.DIMENSION, 'uiname':'Browser','return_prefix':'browser'},
    32: CPAGoal cpagoal = {'fact_table' :TABLE.first_level, 'fact_column' : 'cpa_goal', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'CPA Revenue','return_prefix':'cpagoal'},
    33: ExchangePayout exchangepayout = {'fact_table' :TABLE.first_level, 'fact_column' : 'exchangepayout', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Exchange Income','return_prefix':'exchangepayout'},
    34: ExchangeRevenue exchangerevenue = {'fact_table' :TABLE.first_level, 'fact_column' : 'exchangerevenue', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Exchange Revenue','return_prefix':'exchangerevenue'},
    35: NetworkPayout networkpayout = {'fact_table' :TABLE.first_level, 'fact_column' : 'networkpayout', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Network Income','return_prefix':'networkpayout'},
    36: NetworkRevenue networkrevenue = {'fact_table' :TABLE.first_level, 'fact_column' : 'networkrevenue', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Network Revenue','return_prefix':'networkrevenue'},
    37: Billedclicks billedclicks = {'fact_table' :TABLE.first_level, 'fact_column' : 'billedclicks', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Billed Clicks','return_prefix':'billedclicks'},
    38: Billedcsc billedcsc = {'fact_table' :TABLE.first_level, 'fact_column' : 'billedcsc', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Billed CSC','return_prefix':'billedcsc'},
    39: Supply_source_type supply_source_type = {'fact_table':TABLE.first_level, 'fact_column':'supply_source_type', 'dim_table':'supply_source_type', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'Supply Source Type','return_prefix':'supply_source_type'},
    40: Site_hygiene site_hygiene={'member_type':MEMBERTYPE.HIERARCHY,'first_table':'reporting_hygiene_categories','first_column_filter':'id','first_column':'id','first_column_name':'name','second_table':'site','second_column_name':'hygiene_list','uiname':'Site Hygiene','return_prefix':'site_hygiene','fact_column':'siteId','second_fact_column':'id','fact_table':TABLE.first_level},
    41: Ext_site ext_site = {'fact_table':TABLE.first_level, 'fact_column':'ext_site', 'dim_table':'ext_supply_attr', 'dim_column':'id', 'dim_column_name':'ext_supply_name','member_type':MEMBERTYPE.DIMENSION,'uiname':'External Site','return_prefix':'ext_site'},
    42: ConnectionType connectionType = {'fact_table':TABLE.first_level, 'fact_column':'connection_type', 'dim_table':'connection_type_metadata', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'Connection Type','return_prefix':'connection_type'},
    43: PubIncId pubincId = {'fact_table':TABLE.first_level, 'fact_column':'pubId', 'dim_table':'account', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'Pub Name','return_prefix':'pubId'},
    44: AdvIncId advincId = {'fact_table':TABLE.first_level, 'fact_column':'advId', 'dim_table':' account', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'Adv Name','return_prefix':'advId'},
    45: Device_type device_type = {'fact_table':TABLE.first_level, 'fact_column':'device_type', 'dim_table':'device_type', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'Device Type','return_prefix':'device_type'},
    46: StateId stateId = {'fact_table':TABLE.first_level, 'fact_column':'stateId', 'dim_table':'ui_targeting_state', 'dim_column':'id', 'dim_column_name':'state_name','member_type':MEMBERTYPE.DIMENSION,'uiname':'State/Province','return_prefix':'state'},
    47: CityId cityId = {'fact_table':TABLE.first_level, 'fact_column':'cityId', 'dim_table':'ui_targeting_city', 'dim_column':'id', 'dim_column_name':'city_name','member_type':MEMBERTYPE.DIMENSION,'uiname':'City','return_prefix':'city'},
    48: ChannelId channelId = {'fact_table':TABLE.first_level, 'fact_column':'channelId', 'dim_table':'channel', 'dim_column':'internalid', 'dim_column_name':'channelname','member_type':MEMBERTYPE.DIMENSION,'uiname':'Channel','return_prefix':'channel'},
    49: AdpositionId adpositionId = {'fact_table':TABLE.first_level, 'fact_column':'adpositionId', 'dim_table':'ad_position', 'dim_column':'internalid', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'Adposition','return_prefix':'adposition'},
    50: Marketplace marketplace = {'fact_table':TABLE.first_level, 'fact_column':'marketplace', 'dim_table':'marketplace', 'dim_column':'id', 'dim_column_name':'pricing','member_type':MEMBERTYPE.DIMENSION,'uiname':'Marketplace','return_prefix':'marketplace'},
    51: CreativeFormatId creativeFormatId = {'fact_table':TABLE.first_level, 'fact_column':'formatId', 'dim_table':'creative_formats', 'dim_column':'id', 'dim_column_name':'name','member_type':MEMBERTYPE.DIMENSION,'uiname':'CreativeFormat','return_prefix':'formatId'},
	52: Total_floor total_floor = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_floor', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL FLOOR','return_prefix':'total_floor'},
	53: ReqState reqState = {'fact_table':TABLE.first_level, 'fact_column':'reqState','member_type':MEMBERTYPE.DIMENSION, 'uiname':'ReqState','return_prefix':'reqState'},
	54: DspNoFill dspNoFill = {'fact_table':TABLE.first_level, 'fact_column':'dspNoFill','member_type':MEMBERTYPE.DIMENSION, 'uiname':'DspNoFill','return_prefix':'dspNoFill'},
	55: TerminationReason terminationReason = {'fact_table':TABLE.first_level, 'fact_column':'terminationReason','member_type':MEMBERTYPE.DIMENSION, 'uiname':'TerminationReason','return_prefix':'terminationReason'},
	56: Tevent tevent = {'fact_table':TABLE.first_level, 'fact_column':'tevent','member_type':MEMBERTYPE.DIMENSION, 'uiname':'TEvent','return_prefix':'tevent'},
	57: Teventtype teventtype = {'fact_table':TABLE.first_level, 'fact_column':'teventtype','member_type':MEMBERTYPE.DIMENSION, 'uiname':'TEventtype','return_prefix':'teventtype'},
    58: Total_event total_event = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_event', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL Event','return_prefix':'total_event'},
    59: Total_Request_To_Dsp total_request_to_dsp = {'fact_table' :TABLE.first_level, 'fact_column' : 'total_impression', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'Total Request To Dsp','return_prefix':'total_request_to_dsp'},
    60: PostimpEvent postimpevent = {'fact_table' :TABLE.first_level, 'fact_column' : 'event', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'PostimpEvent','return_prefix':'postimpevent'},
    61: Total_count total_count = {'fact_table' :TABLE.first_level, 'fact_column' : 'count', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL Count','return_prefix':'count'},
    62: Total_bidfloor total_bidfloor = {'fact_table' :TABLE.first_level, 'fact_column' : 'bidFloor', 'member_type' : MEMBERTYPE.METRIC, 'uiname':'TOTAL BidFloor','return_prefix':'total_bidfloor'},
    63: Reqslot reqslot = {'fact_table':TABLE.first_level, 'fact_column':'reqslot', 'dim_table':'creative_slots', 'dim_column':'id', 'dim_column_name':'width','member_type':MEMBERTYPE.DIMENSION,'uiname':'Reqslot','return_prefix':'reqslot','second_dim_column_name':'height','mul_dim_column_delimeter':'*'}
}




