package Elyas.LssTestSheets.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDay {
	private Map<String, Boolean> attendance;
	List<ReportQualification> qualifications;
	private int dayNumber;
	
	public ReportDay(int day) {
		this.dayNumber = day;
		this.attendance = new HashMap<>();
		this.qualifications = new ArrayList<>();
	}

	public void setNumer(int dayNumber) {
		this.dayNumber = dayNumber;		
	}

	public Map<String, Boolean> getAttendace() {
		return attendance;
	}
}
