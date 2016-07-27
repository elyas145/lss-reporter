package Elyas.LssTestSheets;

import java.time.LocalDate;
import java.util.List;

import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.factory.FacilityFactory;
import Elyas.LssTestSheets.factory.PersonFactory;
import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.Prerequisite;
import Elyas.LssTestSheets.model.Prerequisite.Type;
import Elyas.LssTestSheets.model.Qualification;
import Elyas.LssTestSheets.model.TestSheet;

public class Test {
	public static void main(String args[]) {
		Course course = new Course();
		List<Qualification> qualifications = CourseFactory.getSupportedQualifications();
		TestSheet testSheet = null;
		Qualification qual = null;
		for (Qualification qualification : qualifications) {
			if (qualification.getName().equals("Bronze Medallion")) {
				course.addQualification(qualification);
				qualification.addExaminer(PersonFactory.getKnownEmployees().get(0));
				qualification.addInstructor(PersonFactory.getKnownEmployees().get(1));
				qualification.setExamDate(LocalDate.parse("2007-12-13"));
				qual = qualification;
				qual.setTestSheetPath("/pdf/bronze-med.pdf", "/template/bronze-med.json");
				testSheet = qual.getTestSheet();
			}
		}

		course.setFacility(FacilityFactory.getKnownFacilities().get(0));
		course.setBarcodeOne("barcode1");
		course.setBarcodeTwo("barcode2");
		for (int i = 0; i < 16; i++) {
			Client client = new Client();
			client.setAddress("address " + i);
			client.setApartment("apt " + i);
			client.setCity("city " + i);
			client.setDay("d" + i);
			client.setEmail("email " + i);
			client.setMustSees(qual.getName(), testSheet.getClientMustSees());
			client.setID(i + "");
			client.setMonth("m" + i);
			client.setName("name " + i);
			client.setPhone("phone " + i);
			client.setPostalCode("postal " + i);
			course.addClient(client);

			for (Prerequisite prerequisite : client.getPrerequisites(qual.getName())) {
				if (prerequisite.getType().equals(Type.DATE)) {
					if(i<10){
						prerequisite.setDateEarned("2007-08-0" + (i+1));
					}else{
						prerequisite.setDateEarned("2007-08-" + (i+1));
					}
					prerequisite.setLocation("location " + i);
				}else{
					prerequisite.setMet(true);
				}

			}
			client.setYear("y" + i);
			client.setExaminerItems(qual.getName(), true);
			client.setInstructorItems(qual.getName(), true);

		}
		course.setName("Med");
		CourseFactory.exportInfo(true, false, null, "/Users/elyas/Desktop", null, course);
	}
}
