package org.opensrp.scheduler.repository;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.scheduler.ScheduleRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;


@Repository
public class ScheduleRuleRepository extends MotechBaseRepository<ScheduleRules>{
	
	@Autowired
    protected ScheduleRuleRepository(@Qualifier(AllConstants.OPENSRP_SCHEDULE_DATABASE_CONNECTOR) CouchDbConnector db) {
        super(ScheduleRules.class, db);
    }
	public String submit(ScheduleRules scheduleRules){		
		try{			
			add(scheduleRules);
			return "1";
		}catch(Exception e){
			e.printStackTrace();
			return "0";
		}
	}
	@View(name = "all_rule", map = "function(doc) { if (doc.type === 'ScheduleRules') { emit(doc); } }")
    public List<ScheduleRules> allRule(){
    	return db.queryView(
				createQuery("all_rule")
						.includeDocs(true), ScheduleRules.class);
    }
	
	private static final String FUNCTION_DOC_EMIT_DOC_NAME = "function(doc) { if(doc.type === 'ScheduleRules') emit([doc.name], doc._id);}";
    @View(name = "by_Name", map = FUNCTION_DOC_EMIT_DOC_NAME)
    public ScheduleRules findByName(String name) {
       return  queryView("by_Name", ComplexKey.of(name)).get(0);
        
    } 
}
