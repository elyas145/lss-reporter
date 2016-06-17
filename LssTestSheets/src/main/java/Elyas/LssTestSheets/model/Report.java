package Elyas.LssTestSheets.model;

import java.util.List;
import java.util.Map;

public class Report {
	private Map<String, String> generalNotes;
	private List<ReportDay> days;
	public void removeDay(ReportDay day) {
		for (ReportDay reportDay : days) {
			if(reportDay.equals(day)){
				days.remove(reportDay);
				break;
			}
		}
		
	}
	
}
