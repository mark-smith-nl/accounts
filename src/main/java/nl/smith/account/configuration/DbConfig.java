package nl.smith.account.configuration;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(basePackages = { "nl.smith.account.persistence", "nl.smith.account.development.persistence" })
public class DbConfig {

	private final static Logger LOGGER = LoggerFactory.getLogger(DbConfig.class);

	@Bean()
	public static DataSource dataSource(@Value("${spring.datasource.url}") String url, @Value("${spring.datasource.user}") String user,
			@Value("${spring.datasource.password}") String password, @Value("${spring.datasource.driver-class-name}") String driverClassName) {
		DataSource dataSource = new DataSource();
		dataSource.setDriverClassName(driverClassName);

		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);

		try {
			dataSource.getConnection();
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Could not connect to database [%s].\nIs the dbserver active?", url));
		}

		LOGGER.info("Connected to database {}", url);

		return dataSource;
	}

	@Bean()
	public static DataSourceTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

}
