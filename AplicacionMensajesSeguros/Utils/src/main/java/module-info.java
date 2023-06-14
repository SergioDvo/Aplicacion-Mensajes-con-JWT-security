module Utils {

    requires lombok;

    exports modelo.utils;
    exports errores;

    opens modelo.utils;
    opens errores;
}