package nl.smith.account.configuration;

import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(basePackages = "nl.smith.account.persistence")
public class DbConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbConfig.class);

    public static final String SQL_SESSION_FACTORY_NAME_BAMC = "sqlSessionFactoryBamc";

    @Bean()
    public DataSource dataSource(@Value("${spring.datasource.url}") String url, @Value("${spring.datasource.user}") String user,
            @Value("${spring.datasource.password}") String password) {
        DataSource dataSource = new DataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");

        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        dataSource.setInitSQL("set search_path=accounts");
        dataSource.setName("Accounts");

        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("get connection throws Exception");
            e.printStackTrace();
        }

        return dataSource;
    }

    @Primary
    @Bean(name = "transactionManagerBamc")
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
