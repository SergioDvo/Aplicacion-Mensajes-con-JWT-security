module Rest {
    requires Utils;
    requires jakarta.jakartaee.web.api;
    requires lombok;
    requires com.zaxxer.hikari;
    requires java.sql;
    requires spring.tx;
    requires spring.jdbc;
    requires jakarta.mail;
    requires thymeleaf;
    requires jjwt.api;
    requires seguridad;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.pkix;
}