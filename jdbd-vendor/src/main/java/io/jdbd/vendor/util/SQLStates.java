/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.util;

public abstract class SQLStates {

    protected SQLStates() {
        throw new UnsupportedOperationException();
    }

    //Class 00—Successful Completion
    public static final String SUCCESSFUL = "00000";

    //Class 01—Warning
    public static final String WARNING = "01000";
    public static final String WARNING_NULL_VALUE_ELIMINATED_IN_SET_FUNCTION = "01003";
    public static final String WARNING_STRING_DATA_RIGHT_TRUNCATION = "01004";
    public static final String WARNING_PRIVILEGE_NOT_REVOKED = "01006";

    public static final String WARNING_PRI08004VILEGE_NOT_GRANTED = "01007";
    public static final String WARNING_PRIVILEGE_ALREADY_GRANTED = "01008";
    public static final String WARNING_PRIVILEGE_ALREADY_REVOKED = "01009";
    public static final String WARNING_DYNAMIC_RESULT_SETS_RETURNED = "0100C";

    public static final String WARNING_DEPRECATED_FEATURE = "01V01";
    public static final String WARNING_QUERY_RETRIED = "01V02";

    //Class 02—No Data
    public static final String NO_DATA = "02000";
    public static final String NO_ADDITIONAL_DYNAMIC_RESULT_SETS_RETURNED = "02001";

    //Class 03—SQL Statement Not Yet Complete
    public static final String SQL_STATEMENT_NOT_YET_COMPLETE = "03000";

    //Class 08—Client Connection Exception
    public static final String CONNECTION_EXCEPTION = "08000";
    public static final String SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION = "08001";
    public static final String CONNECTION_DOES_NOT_EXIST = "08003";
    public static final String SQLSERVER_REJECTED_ESTABLISHMENT_OF_SQLCONNECTION = "08004";

    public static final String CONNECTION_FAILURE = "08006";
    public static final String TRANSACTION_RESOLUTION_UNKNOWN = "08007";
    public static final String PROTOCOL_VIOLATION = "08V01";

    //Class 09—Triggered Action Exception
    public static final String TRIGGERED_ACTION_EXCEPTION = "09000";

    //Class 0A—Feature Not Supported
    public static final String FEATURE_NOT_SUPPORTED = "0A000";
    public static final String PLAN_TO_SQL_NOT_SUPPORTED = "0A005";

    //Class 0B—Invalid Transaction Initiation
    public static final String INVALID_TRANSACTION_INITIATION = "0B000";

    //Class 0F—Locator Exception
    public static final String LOCATOR_EXCEPTION = "0F000";
    public static final String L_E_INVALID_SPECIFICATION = "0F001";

    //Class 0L—Invalid Grantor
    public static final String INVALID_GRANTOR = "0L000";
    public static final String INVALID_GRANT_OPERATION = "0LV01";

    //Class 0P—Invalid Role Specification
    public static final String INVALID_ROLE_SPECIFICATION = "0P000";

    //Class 21—Cardinality Violation
    public static final String CARDINALITY_VIOLATION = "21000";

    //Class 22—Data Exception
    public static final String DATA_EXCEPTION = "22000";
    public static final String STRING_DATA_RIGHT_TRUNCATION = "22001";
    public static final String NULL_VALUE_NO_INDICATOR_PARAMETER = "22002";
    public static final String NUMERIC_VALUE_OUT_OF_RANGE = "22003";

    public static final String NULL_VALUE_NOT_ALLOWED = "22004";
    public static final String ERROR_IN_ASSIGNMENT = "22005";
    public static final String INVALID_DATETIME_FORMAT = "22007";
    public static final String DATETIME_FIELD_OVERFLOW = "22008";

    public static final String INVALID_TIME_ZONE_DISPLACEMENT_VALUE = "22009";
    public static final String ESCAPE_CHARACTER_CONFLICT = "2200B";
    public static final String INVALID_USE_OF_ESCAPE_CHARACTER = "2200C";
    public static final String INVALID_ESCAPE_OCTET = "2200D";

    public static final String ZERO_LENGTH_CHARACTER_STRING = "2200F";
    public static final String MOST_SPECIFIC_TYPE_MISMATCH = "2200G";
    public static final String INVALID_INDICATOR_PARAMETER_VALUE = "22010";
    public static final String SUBSTRING_ERROR = "22011";

    public static final String DIVISION_BY_ZERO = "22012";
    public static final String INTERVAL_FIELD_OVERFLOW = "22015";
    public static final String INVALID_CHARACTER_VALUE_FOR_CAST = "22018";
    public static final String INVALID_ESCAPE_CHARACTER = "22019";

    public static final String INVALID_REGULAR_EXPRESSION = "2201B";
    public static final String INVALID_ARGUMENT_FOR_LOG = "2201E";
    public static final String INVALID_ARGUMENT_FOR_POWER_FUNCTION = "2201F";
    public static final String INVALID_ARGUMENT_FOR_WIDTH_BUCKET_FUNCTION = "2201G";

    public static final String INVALID_LIMIT_VALUE = "22020";
    public static final String CHARACTER_NOT_IN_REPERTOIRE = "22021";
    public static final String INDICATOR_OVERFLOW = "22022";
    public static final String INVALID_PARAMETER_VALUE = "22023";

    public static final String UNTERMINATED_C_STRING = "22024";
    public static final String INVALID_ESCAPE_SEQUENCE = "22025";
    public static final String STRING_DATA_LENGTH_MISMATCH = "22026";
    public static final String TRIM_ERROR = "22027";

    public static final String ARRAY_ELEMENT_ERROR = "2202E";
    public static final String NONSTANDARD_USE_OF_ESCAPE_CHARACTER = "22906";
    public static final String FLOATING_POINT_EXCEPTION = "22V01";
    public static final String INVALID_TEXT_REPRESENTATION = "22V02";

    public static final String INVALID_BINARY_REPRESENTATION = "22V03";
    public static final String BAD_COPY_FILE_FORMAT = "22V04";
    public static final String UNTRANSLATABLE_CHARACTER = "22V05";
    public static final String ESCAPE_CHARACTER_ON_NOESCAPE = "22V0B";

    public static final String INVALID_EPOCH = "22V21";
    public static final String PLPGSQL_ERROR = "22V22";
    public static final String RAISE_EXCEPTION = "22V23";
    public static final String COPY_PARSE_ERROR = "22V24";


    //Class 23—Integrity Constraint Violation
    public static final String INTEGRITY_CONSTRAINT_VIOLATION = "23000";
    public static final String RESTRICT_VIOLATION = "23001";
    public static final String NOT_NULL_VIOLATION = "23502";
    public static final String FOREIGN_KEY_VIOLATION = "23503";

    public static final String UNIQUE_VIOLATION = "23505";
    public static final String CHECK_VIOLATION = "23514";

    //Class 24—Invalid Cursor State
    public static final String INVALID_CURSOR_STATE = "24000";

    //Class 25—Invalid Transaction State
    public static final String INVALID_TRANSACTION_STATE = "25000";
    public static final String ACTIVE_SQL_TRANSACTION = "25001";
    public static final String BRANCH_TRANSACTION_ALREADY_ACTIVE = "25002";
    public static final String INAPPROPRIATE_ACCESS_MODE_FOR_BRANCH_TRANSACTION = "25003";

    public static final String INAPPROPRIATE_ISOLATION_LEVEL_FOR_BRANCH_TRANSACTION = "25004";
    public static final String NO_ACTIVE_SQL_TRANSACTION_FOR_BRANCH_TRANSACTION = "25005";
    public static final String READ_ONLY_SQL_TRANSACTION = "25006";
    public static final String SCHEMA_AND_DATA_STATEMENT_MIXING_NOT_SUPPORTED = "25007";

    public static final String HELD_CURSOR_REQUIRES_SAME_ISOLATION_LEVEL = "25008";
    public static final String NO_ACTIVE_SQL_TRANSACTION = "25V01";
    public static final String IN_FAILED_SQL_TRANSACTION = "25V02";

    //Class 26—Invalid SQL Statement Name
    public static final String INVALID_SQL_STATEMENT_NAME = "26000";

    //Class 27—Triggered Data Change Violation
    public static final String TRIGGERED_DATA_CHANGE_VIOLATION = "27000";

    //Class 28—Invalid Authorization Specification
    public static final String INVALID_AUTHORIZATION_SPECIFICATION = "28000";
    public static final String ACCOUNT_LOCKED = "28001";
    public static final String PASSWORD_EXPIRED = "28002";
    public static final String PASSWORD_IN_GRACE_PERIOD = "28003";


    //Class 2B—Dependent Privilege Descriptors Still Exist
    public static final String DEPENDENT_PRIVILEGE_DESCRIPTORS_STILL_EXIST = "2B000";
    public static final String DEPENDENT_OBJECTS_STILL_EXIST = "2BV01";

    //Class 2D—Invalid Transaction Termination
    public static final String INVALID_TRANSACTION_TERMINATION = "2D000";

    //Class 2F—SQL Routine Exception
    public static final String SQL_ROUTINE_EXCEPTION = "2F000";
    public static final String S_R_E_MODIFYING_SQL_DATA_NOT_PERMITTED = "2F002";
    public static final String S_R_E_PROHIBITED_SQL_STATEMENT_ATTEMPTED = "2F003";
    public static final String S_R_E_READING_SQL_DATA_NOT_PERMITTED = "2F004";

    public static final String S_R_E_FUNCTION_EXECUTED_NO_RETURN_STATEMENT = "2F005";

    //Class 34—Invalid Cursor Name
    public static final String INVALID_CURSOR_NAME = "34000";

    //Class 38—External Routine Exception
    public static final String EXTERNAL_ROUTINE_EXCEPTION = "38000";
    public static final String E_R_E_CONTAINING_SQL_NOT_PERMITTED = "38001";
    public static final String E_R_E_MODIFYING_SQL_DATA_NOT_PERMITTED = "38002";
    public static final String E_R_E_PROHIBITED_SQL_STATEMENT_ATTEMPTED = "38003";

    public static final String E_R_E_READING_SQL_DATA_NOT_PERMITTED = "38004";

    //Class 39—External Routine Invocation Exception
    public static final String EXTERNAL_ROUTINE_INVOCATION_EXCEPTION = "39000";
    public static final String E_R_I_E_INVALID_SQLSTATE_RETURNED = "39001";
    public static final String E_R_I_E_NULL_VALUE_NOT_ALLOWED = "39004";
    public static final String E_R_I_E_TRIGGER_PROTOCOL_VIOLATED = "39V01";

    public static final String E_R_I_E_SRF_PROTOCOL_VIOLATED = "39V02";

    //Class 3B—Savepoint Exception
    public static final String SAVEPOINT_EXCEPTION = "3B000";
    public static final String S_E_INVALID_SPECIFICATION = "3B001";

    //Class 3D—Invalid Catalog Name
    public static final String INVALID_CATALOG_NAME = "3D000";

    //Class 3F—Invalid Schema Name
    public static final String INVALID_SCHEMA_NAME = "3F000";

    //Class 40—Transaction Rollback
    public static final String TRANSACTION_ROLLBACK = "40000";
    public static final String T_R_SERIALIZATION_FAILURE = "40001";
    public static final String T_R_INTEGRITY_CONSTRAINT_VIOLATION = "40002";
    public static final String T_R_STATEMENT_COMPLETION_UNKNOWN = "40003";

    public static final String T_R_DEADLOCK_DETECTED = "40V01";

    //Class 42—Syntax Error or Access Rule Violation
    public static final String SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION = "42000";
    public static final String INSUFFICIENT_PRIVILEGE = "42501";
    public static final String SYNTAX_ERROR = "42601";
    public static final String INVALID_NAME = "42602";

    public static final String INVALID_COLUMN_DEFINITION = "42611";
    public static final String NAME_TOO_LONG = "42622";
    public static final String DUPLICATE_COLUMN = "42701";
    public static final String AMBIGUOUS_COLUMN = "42702";

    public static final String UNDEFINED_COLUMN = "42703";
    public static final String UNDEFINED_OBJECT = "42704";
    public static final String DUPLICATE_OBJECT = "42710";
    public static final String DUPLICATE_ALIAS = "42712";

    public static final String DUPLICATE_FUNCTION = "42723";
    public static final String AMBIGUOUS_FUNCTION = "42725";
    public static final String GROUPING_ERROR = "42803";
    public static final String DATATYPE_MISMATCH = "42804";

    public static final String WRONG_OBJECT_TYPE = "42809";
    public static final String INVALID_FOREIGN_KEY = "42830";
    public static final String CANNOT_COERCE = "42846";
    public static final String UNDEFINED_FUNCTION = "42883";

    public static final String RESERVED_NAME = "42939";
    public static final String WINDOWING_ERROR = "42P20";
    public static final String UNDEFINED_TABLE = "42V01";
    public static final String UNDEFINED_PARAMETER = "42V02";

    public static final String DUPLICATE_CURSOR = "42V03";
    public static final String DUPLICATE_DATABASE = "42V04";
    public static final String DUPLICATE_PSTATEMENT = "42V05";
    public static final String DUPLICATE_SCHEMA = "42V06";

    public static final String DUPLICATE_TABLE = "42V07";
    public static final String AMBIGUOUS_PARAMETER = "42V08";
    public static final String AMBIGUOUS_ALIAS = "42V09";
    public static final String INVALID_COLUMN_REFERENCE = "42V10";

    public static final String INVALID_CURSOR_DEFINITION = "42V11";
    public static final String INVALID_DATABASE_DEFINITION = "42V12";
    public static final String INVALID_FUNCTION_DEFINITION = "42V13";
    public static final String INVALID_PSTATEMENT_DEFINITION = "42V14";

    public static final String INVALID_SCHEMA_DEFINITION = "42V15";
    public static final String INVALID_TABLE_DEFINITION = "42V16";
    public static final String INVALID_OBJECT_DEFINITION = "42V17";
    public static final String INDETERMINATE_DATATYPE = "42V18";

    public static final String UNDEFINED_PROJECTION = "42V21";
    public static final String UNDEFINED_NODE = "42V22";
    public static final String UNDEFINED_PERMUTATION = "42V23";
    public static final String UNDEFINED_USER = "42V24";

    public static final String PATTERN_MATCH_ERROR = "42V25";
    public static final String DUPLICATE_NODE = "42V26";

    //Class 44—WITH CHECK OPTION Violation
    public static final String WITH_CHECK_OPTION_VIOLATION = "44000";

    //Class 53—Insufficient Resources
    public static final String INSUFFICIENT_RESOURCES = "53000";
    public static final String DISK_FULL = "53100";
    public static final String OUT_OF_MEMORY = "53200";
    public static final String TOO_MANY_CONNECTIONS = "53300";


    //Class 54—Program Limit Exceeded
    public static final String PROGRAM_LIMIT_EXCEEDED = "54000";
    public static final String STATEMENT_TOO_COMPLEX = "54001";
    public static final String TOO_MANY_COLUMNS = "54011";
    public static final String TOO_MANY_ARGUMENTS = "54023";


    //Class 55—Object Not In Prerequisite State
    public static final String OBJECT_NOT_IN_PREREQUISITE_STATE = "55000";
    public static final String OBJECT_IN_USE = "55006";
    public static final String CANT_CHANGE_RUNTIME_PARAM = "55V02";
    public static final String LOCK_NOT_AVAILABLE = "55V03";

    public static final String TM_MARKER_NOT_AVAILABLE = "55V04";

    //Class 57—Operator Intervention
    public static final String OPERATOR_INTERVENTION = "57000";
    public static final String QUERY_CANCELED = "57014";
    public static final String SLOW_DELETE = "57015";
    public static final String ADMIN_SHUTDOWN = "57V01";

    public static final String CRASH_SHUTDOWN = "57V02";
    public static final String CANNOT_CONNECT_NOW = "57V03";
    public static final String DML_COMMIT_DURING_SHUTDOWN = "57V04";

    //Class 58—System Error
    public static final String IO_ERROR = "58030";
    public static final String UNDEFINED_FILE = "58V01";
    public static final String DUPLICATE_FILE = "58V02";

    //Class V1—Vertica-specific multi-node errors class
    public static final String LOST_CONNECTIVITY = "V1001";
    public static final String K_SAFETY_VIOLATION = "V1002";
    public static final String CLUSTER_CHANGE = "V1003";

    //Class V2—Vertica-specific miscellaneous errors class
    public static final String AUTH_FAILED = "V2000";
    public static final String LICENSE_ISSUE = "V2001";
    public static final String MOVEOUT_ABORTED = "V2002";

    //Class VC—Configuration File Error
    public static final String CONFIG_FILE_ERROR = "VC001";
    public static final String LOCK_FILE_EXISTS = "VC002";

    //Class VD—DB Designer errors
    public static final String DESIGNER_FUNCTION_ERROR = "VD001";

    //Class VP—User procedure errors
    public static final String USER_PROC_ERROR = "VP000";
    public static final String USER_PROC_EXEC_ERROR = "VP001";

    //Class VX—Internal Error
    public static final String INTERNAL_ERROR = "VX001";
    public static final String DATA_CORRUPTED = "VX002";
    public static final String INDEX_CORRUPTED = "VX003";
    public static final String PLAN_TO_SQL_INTERNAL_EROR = "VX004";

    //Xa
    public static final String ER_XAER_NOTA = "XAE04";
    public static final String ER_XAER_INVAL = "XAE05";
    public static final String ER_XAER_RMFAIL = "XAE07";
    public static final String ER_XAER_OUTSIDE = "XAE09";

    public static final String ER_XA_RMERR = "XAE03";
    public static final String ER_XA_RBROLLBACK = "XA100";
    public static final String ER_XAER_DUPID = "XAE08";
    public static final String XA_RBTIMEOUT = "XA106";

    public static final String ER_XA_RBDEADLOCK = "XA102";


}
