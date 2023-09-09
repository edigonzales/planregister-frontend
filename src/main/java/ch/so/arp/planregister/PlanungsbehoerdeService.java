package ch.so.arp.planregister;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlanungsbehoerdeService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<Planungsbehoerde> findAllPlanningAuthorities() {
        ArrayList<Planungsbehoerde> planningAuthorityList = new ArrayList<>();
        planningAuthorityList.add(new Planungsbehoerde("Gemeinde"));
        planningAuthorityList.add(new Planungsbehoerde("Kanton"));
        
        return planningAuthorityList;
    }
}
