package ch.so.arp.planregister;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DokumentService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbschema}")
    private String dbschema;

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    public List<Dokument> findAllDocuments(String stringFilter) {
    //public void findAllDocuments(String stringFilter) {
//        if (stringFilter == null || stringFilter.isEmpty()) { 
//            return contactRepository.findAll();
//        } else {
//            return contactRepository.search(stringFilter);
//        }
        
        List<Dokument> documentList=jdbcTemplate.query(
                "SELECT planungsinstrument, bezeichnung, planungsbehoerde, gemeinde, rechtskraft_ab, "
                + "rechtsstatus, TRIM(dokument_url) AS dokument_url, rrb_datum, rrb_nr, TRIM(rrb_url) AS rrb_url, TRIM(sonderbauvorschrift_url) AS sonderbauvorschrift_url, "
                + "karte_url, zustaendige_amt, aktuelle_ortsplanung, offiziellenr "
                + "FROM " + dbschema + ".dokument ORDER BY rrb_datum DESC NULLS LAST"
                , new RowMapper<Dokument>() {
                    @Override
                    public Dokument mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String planungsinstrument = rs.getString("planungsinstrument");
                        String bezeichnung = rs.getString("bezeichnung");
                        String planungsbehoerde = rs.getString("planungsbehoerde");
                        String gemeinde = rs.getString("gemeinde");
                        LocalDate rechtskraftAb =  null;
                        if (rs.getDate("rechtskraft_ab") != null) {
                            rechtskraftAb = rs.getDate("rechtskraft_ab").toLocalDate();   
                        }
                        String rechtsstatus = rs.getString("rechtsstatus");
                        URI dokumentUrl = URI.create(rs.getString("dokument_url"));
                        LocalDate rrbDatum = null;
                        if (rs.getDate("rrb_datum") != null) {
                            rrbDatum = rs.getDate("rrb_datum").toLocalDate();
                        }
                        String rrbNr = rs.getString("rrb_nr");
                        URI rrbUrl = null;
                        if (rs.getString("rrb_url") != null) {
                            rrbUrl  = URI.create(rs.getString("rrb_url"));
                        }
                        URI sonderbauvorschriftenUrl = null;
                        if (rs.getString("sonderbauvorschrift_url") != null) {
                            sonderbauvorschriftenUrl = URI.create(rs.getString("sonderbauvorschrift_url"));                            
                        }
                        URI karteUrl = null;
                        if (rs.getString("karte_url") != null) {
                            karteUrl = URI.create(rs.getString("karte_url"));                            
                        }
                        String zustaendigesAmt = rs.getString("zustaendige_amt");
                        boolean aktuelleOrtsplanung = rs.getBoolean("aktuelle_ortsplanung");
                        String offizielleNr = rs.getString("offiziellenr");
                        
                        return new Dokument(
                                planungsinstrument, 
                                bezeichnung, 
                                planungsbehoerde, 
                                gemeinde, 
                                rechtskraftAb, 
                                rechtsstatus, 
                                dokumentUrl, 
                                rrbDatum, 
                                rrbNr, 
                                rrbUrl, 
                                sonderbauvorschriftenUrl, 
                                karteUrl, 
                                zustaendigesAmt, 
                                aktuelleOrtsplanung, 
                                offizielleNr
                                );
                    }
                });
        
        return documentList;
   
    }

}
