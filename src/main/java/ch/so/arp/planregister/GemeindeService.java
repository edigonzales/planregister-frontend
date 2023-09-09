package ch.so.arp.planregister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GemeindeService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbschema}")
    private String dbschema;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Gemeinde> findAllMunicipalities() {
        List<Gemeinde> municipalityList = jdbcTemplate.query(
                "SELECT DISTINCT gemeinde "
                + "FROM " + dbschema + ".dokument ORDER BY gemeinde ASC"
                , new RowMapper<Gemeinde>() {
                    @Override
                    public Gemeinde mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String name = rs.getString("gemeinde");
                        
                        return new Gemeinde (
                                name
                                );
                    }
                });
        
        return municipalityList;
   
    }

}
