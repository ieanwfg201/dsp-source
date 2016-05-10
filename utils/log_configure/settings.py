LOG_SETTINGS = {
    "version":1,
    "root":{
        "level":"INFO",
        "handlers":["simpleFileHandler"]
    },
    "handlers":{
        "simpleFileHandler":{
            "class":"utils.log_configure.file_handler.file_handler",
            "level":"INFO",
            "formatter":"simpleFormatter",
            "pattern":".%Y-%m-%d-%H-%M",
            "mode":"a"
        }
    },
    "formatters":{
        "simpleFormatter":{
            "format":"%(asctime)s %(name)-12s %(levelname)-8s %(message)s",
            "datefmt":"%Y-%m-%d %H:%M"
        }
    }
}
