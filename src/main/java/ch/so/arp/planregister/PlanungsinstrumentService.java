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
public class PlanungsinstrumentService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbschema}")
    private String dbschema;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Planungsinstrument> findAllPlanningInstruments() {
        List<Planungsinstrument> planningInstrumenList = jdbcTemplate.query(
                "SELECT \n"
                + "    DISTINCT trim(UNNEST(string_to_array(planungsinstrument, ','))) AS planungsinstrument \n"
                + "FROM \n"
                + "    "+dbschema+".dokument AS d\n"
                + "ORDER BY \n"
                + "    planungsinstrument ASC "
                , new RowMapper<Planungsinstrument>() {
                    @Override
                    public Planungsinstrument mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String name = rs.getString("planungsinstrument");
                        
                        return new Planungsinstrument (
                                name
                                );
                    }
                });
        
        return planningInstrumenList;
    }
}
