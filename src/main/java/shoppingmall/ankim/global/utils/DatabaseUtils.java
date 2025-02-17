package shoppingmall.ankim.global.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseUtils {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getDatabaseType() {
        return jdbcTemplate.execute((ConnectionCallback<String>) (connection) -> connection.getMetaData().getDatabaseProductName());
    }
}