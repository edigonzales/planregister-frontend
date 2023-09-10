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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DokumentService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbschema}")
    private String dbschema;

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    public List<Dokument> findDocuments(List<Object> predicateList) {
    //public void findAllDocuments(String stringFilter) {
//        if (stringFilter == null || stringFilter.isEmpty()) { 
//            return contactRepository.findAll();
//        } else {
//            return contactRepository.search(stringFilter);
//        }
        
        String stmt = "SELECT planungsinstrument, bezeichnung, planungsbehoerde, gemeinde, rechtskraft_ab, "
                + "rechtsstatus, TRIM(dokument_url) AS dokument_url, rrb_datum, rrb_nr, TRIM(rrb_url) AS rrb_url, TRIM(sonderbauvorschrift_url) AS sonderbauvorschrift_url, "
                + "karte_url, zustaendige_amt, aktuelle_ortsplanung, offiziellenr "
                + "FROM " + dbschema + ".dokument";
        
        String whereClause = "";
        List<Object> argsList = new ArrayList<>();
        List<Integer> argTypesList = new ArrayList<>();
        Object[] args = null;
        int[] argTypes = null;
        if (predicateList.size() > 0) {
            for (int i=0; i<predicateList.size(); i++) {
                if (i==0) {
                    whereClause += " WHERE";
                } else {
                    whereClause += " AND";
                }
                if (predicateList.get(i) instanceof Predicate p) {
                    whereClause += " "+p.databaseColumn() + " " + p.operator() + " ?";
                    argsList.add(p.value());
                    argTypesList.add(p.argType());                    
                } else if (predicateList.get(i) instanceof HashMap) {
                    Map<String,List<Predicate>> map = (Map<String, List<Predicate>>) predicateList.get(i);
                    
                    int k=0;
                    for (Map.Entry<String,List<Predicate>> entry : map.entrySet()) {                        
                        List<Predicate> pList = entry.getValue();
                        whereClause += " (";
                        for (int j = 0; j < pList.size(); j++) {
                            Predicate p = pList.get(j);
                            whereClause += " " + p.databaseColumn() + " " + p.operator() + " ?";
                            argsList.add(p.value());
                            argTypesList.add(p.argType());
                            if (j != pList.size() - 1) {
                                whereClause += " OR";
                            }
                        }
                        whereClause += ")";
                       
                        if (k!=map.size()-1) {
                            whereClause += " AND";
                        }
                        
                        k++;
                    }                    
                }
            }

            args = argsList.toArray();
            argTypes = argTypesList.stream().mapToInt(Integer::intValue).toArray();

            stmt += whereClause;
        }
        
        stmt += " ORDER BY rrb_datum DESC NULLS LAST";
        
        log.debug(stmt);
        
        List<Dokument> documentList=jdbcTemplate.query(stmt, args, argTypes,
                new RowMapper<Dokument>() {
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
        
        log.debug("query result size: " + documentList.size());
        return documentList;
   
    }

}
