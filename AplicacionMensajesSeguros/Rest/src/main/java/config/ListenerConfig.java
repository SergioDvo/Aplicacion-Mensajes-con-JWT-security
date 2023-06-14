package config;

import JDBC.DBConnectionPool;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener()
public class ListenerConfig implements ServletContextListener {
    private final DBConnectionPool pool;

    @Inject
    public ListenerConfig(DBConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context
         (the Web application) is undeployed or
         Application Server shuts down.
      */
        pool.closePool();
    }
}
