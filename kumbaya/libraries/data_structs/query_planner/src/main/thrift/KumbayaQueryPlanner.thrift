namespace java com.kritter.kumbaya.libraries.data_structs.query_planner

enum MEMBERTYPE {
    DB = 1
}
enum ErrorCode{
    NO_ERROR = 0, INTERNAL_EXCEPTION = 1, START_TIME_ABSENT = 2,END_TIME_ABSENT = 3,START_TIME_GT_END_TIME = 4,DATE_FORMAT_INCORRECT =5,
    TABLE_NOT_FOUND = 6,
}


struct KProjection {
    1: optional map<string,string> keyvalue 
}

enum OperationEnum {
    IN = 1,
    EQUAL = 2,
    LT = 3,
    LT_EQ = 4,
    GT = 5,
    GT_EQ = 6,
    NE = 7,
}

enum METRICTYPE {
    SUM = 1,
}

struct KFilterFields {
    1: string left,
    2: string right,
    3: OperationEnum operation,
}

struct KFilter {
    1: optional set<KFilterFields> keyvalue,
}

struct KGroupby {
    1: optional set<string> keyvalue
}

struct KJoin {
    1: optional map<string,string> keyvalue
}

struct KOrderBy {
    1: optional set<string> keyvalue
}

struct KLimit {
    1: i32 startindex = 1,
    2: i32 pagesize = 50
    
}
struct PlanStatus {
    1: ErrorCode errorcode,
    2: string msg,
}

struct TimeRange {
    1: string start_time,
    2: string end_time,
    3: string column_name,
}

enum HeaderType {
    INT = 1,
    STRING = 2,
    LONG = 3,
    DOUBLE = 4,
}

enum ColumnType {
    METRIC = 1,
    DIM = 2,
    DERIVED = 3,
}

struct Header {
    1: HeaderType headerType,
    2: string name,
    3: optional string ui_name,
    4: optional HeaderType id_headerType,
    5: optional ColumnType columnType,
    6: optional bool visible,
    7: optional bool clickable, 
}
struct KumbayaQueryPlanner {
    1: MEMBERTYPE membertype = MEMBERTYPE.DB,
    2: optional   KProjection kprojection,
    3: optional   KFilter kfilter,
    4: optional   KGroupby kgroupby,
    5: optional   KJoin kjoin,
    6: optional   KOrderBy korderby,
    7: optional   KLimit klimit,
    8: PlanStatus planStatus,
    9: TimeRange timeRange,
    10: optional string queryString,
    11: optional list<Header> headerList
}


