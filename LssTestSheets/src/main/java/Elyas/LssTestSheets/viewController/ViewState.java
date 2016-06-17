package Elyas.LssTestSheets.viewController;

public enum ViewState {
	START, INSTRUCTOR, EXAMINER, STUDENTS, STUDENT, COURSE, COURSE_TYPE, MUST_SEES, REPORT, TEST_SHEETS;

	public static boolean isMenuState(ViewState state) {
		switch (state){
		case COURSE:
			return true;
		case COURSE_TYPE:
			break;
		case EXAMINER:
			return true;
		case INSTRUCTOR:
			return true;
		case START:
			break;
		case STUDENT:
			return true;
		case STUDENTS:
			return true;
		default:
			break;
		
		}
		return false;
	}

	public static SideMenuAction getSideMenuAction(ViewState state) {
		switch(state){
		case COURSE:
			return SideMenuAction.COURSE;
		case COURSE_TYPE:
			break;
		case EXAMINER:
			return SideMenuAction.EXAMINERS;
		case INSTRUCTOR:
			return SideMenuAction.INSTRUCTORS;
		case START:
			break;
		case STUDENT:
			return SideMenuAction.CLIENTS;
		case STUDENTS:
			return SideMenuAction.CLIENTS;
		default:
			break;
		
		}
		return null;
	}
}
