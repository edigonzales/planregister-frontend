package ch.so.arp.planregister;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RechtsstatusService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    public List<Rechtsstatus> findAllLegalStatus() {
        ArrayList<Rechtsstatus> legalStatusList = new ArrayList<>();
        legalStatusList.add(new Rechtsstatus("in Kraft"));
        legalStatusList.add(new Rechtsstatus("aufgehoben"));
        
        return legalStatusList;
    }
}
