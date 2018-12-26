package com.mks.sendstatlib;

/***************************************
 *    Класс содержащий константы
 ***************************************/
public final class C {
    public static String CODE_VERSION = "1.0.0";  //Версия кода
    public static boolean NEED_LOG    = false;     //Надо ли вести логирование

    public static String BASE_URL = "https://intlib.mks.group/logs-__DATA__/log/?pipeline=timestamper";  //Ссылка по которой будет отправляться статистика
    public static String PROXY_BASE_URL = "https://intlib.mks.group"; //По какой ссылке проверяем соединение для установки прокси //"https://fakegaid.appclick.org/gaid"
    public static final String EXTERNAL_PACKAGE_NAME = "com.mks.externalintlib"; //Имя пакета во внешней библиотеке

    public static String URL_TO_CONFIG_FILE      = "http://vertex-digital.ru/json/configsClub/config_test.json";//"https://drive.google.com/a/adviator.com/uc?authuser=0&id=1430eIWzu5gAYPU2nqR2GxDBxm9mfg2q8&export=download"; //"config_1.zip";//;"https://drive.google.com/a/adviator.com/uc?authuser=0&id=134aH-Y1FQZcKC_Pwt06ygTyXHyafaARp&export=download";

    //*********** Сохраняемые данные для глобальных параметров передаваемых в логи *****************
    public static final String SPF_SESSION_GLOBAL_PUBLISHERID = "session_publisherId";
    public static final String SPF_KEY_GLOBAL_PUBLISHERID     = "publisherId";

    public static final String SPF_SESSION_GLOBAL_EXTERNAL_GAID = "session_externalGaid"; //GAID который прислали нам извне
    public static final String SPF_KEY_GLOBAL_EXTERNAL_GAID     = "externalGaid";

    public static final String SPF_SESSION_GLOBAL_APPID = "session_appid";  //Appid встроенного SDK
    public static final String SPF_KEY_GLOBAL_APPID     = "appid";

    public static final String SPF_SESSION_GLOBAL_SDKVERSION = "session_sdkversion"; //Версия встроенного в приложение SDK
    public static final String SPF_KEY_GLOBAL_SDKVERSION     = "sdkversion";

    public static final String SPF_SESSION_GLOBAL_PACKAGENAMES = "session_packagenames"; //имена пакетов, которые надо проверять
    public static final String SPF_KEY_GLOBAL_PACKAGENAMES     = "packagenames";

    public static final String SPF_SESSION_GLOBAL_FCM_TOKEN = "session_fcm_token"; //FCM токен, если подключен файербейс
    public static final String SPF_KEY_GLOBAL_FCM_TOKEN     = "fcm_token";


    public static final String SPF_SESSION_GLOBAL_PREF_APPNAME = "session_pref_appname_"; //префиксы имён пакетов в которые мы будем сохранять инфу о устнановке/удалении прилоежния
    public static final String SPF_KEY_GLOBAL_PREF_APPNAME     = "pref_appname_";
    //**********************************************************************************************
    public static final String APP_IS_NOTINSTALL = "not_be_install"; //Приложение ни разу не было установлено
    public static final String APP_IS_INSTALL    = "install";        //Приложение сейчас установлено
    public static final String APP_IS_DELETE     = "delete";         //Приложение было установлено но сейчас удалено

    //Префиксы для счетчиков сообщений
    public static final String SPF_SESSION_COUNTER = "session_counter_";
    public static final String SPF_KEY_COUNTER     = "counter_";
    //**************************************************************************************************
    //Очередь задач
    public static final String SPF_SESSION_LOGS_QUEUE = "session_logs_queue";
    public static final String SPF_KEY_LOGS_QUEUE     = "logs_queue";
    //**************************************************************************************************

}

