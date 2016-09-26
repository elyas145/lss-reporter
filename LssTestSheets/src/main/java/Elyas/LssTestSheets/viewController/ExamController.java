package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.controlsfx.control.textfield.CustomTextField;

import Elyas.LssTestSheets.model.Exam;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.Qualification;
import Elyas.LssTestSheets.util.DateUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;

public class ExamController extends Controller implements Initializable{
	@FXML
	CustomTextField txtYear;
	@FXML
	CustomTextField txtMonth;
	@FXML
	CustomTextField txtDay;
	@FXML
	RadioButton rdbOriginal;
	@FXML
	RadioButton rdbRecert;
	@FXML
	CustomTextField txtExamLocation;
	
	private Qualification qual;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		
	}
	
	public Exam getExam(){
		Exam exam = new Exam();
		LocalDate date;
		try{
			date = LocalDate.parse(DateUtil.prettyYear(txtYear.getText())+"-"+txtMonth.getText()+"-"+txtDay.getText());
		}catch(Exception e){
			date = null;
		}
		exam.setDate(date);
		exam.setOriginal(rdbOriginal.isSelected());
		exam.setLocation(txtExamLocation.getText());
		return exam;
				
	}

	public void setQual(Qualification qual) {
		this.qual = qual;
		if(qual.getExam() != null){
			Exam exam = qual.getExam();
			if(exam.getDate() != null){
				LocalDate date = exam.getDate();
				if(!date.equals(LocalDate.MAX)){
					txtYear.setText(DateUtil.prettyYear(date));
					txtMonth.setText(DateUtil.prettyMonthNumber(date));
					txtDay.setText(DateUtil.prettyDayNumber(date));
				}else{
					txtYear.setText("");
					txtMonth.setText("");
					txtDay.setText("");
				}
				
			}
			if(exam.getLocation() != null){
				this.txtExamLocation.setText(exam.getLocation());
			}else{
				String facility = Model.getInstance().getCourse().getFacility().getName();
				if(facility != null){
					this.txtExamLocation.setText(facility);
				}
			}
			if (exam.isOriginal()) {
				rdbOriginal.setSelected(true);
			}else{
				rdbRecert.setSelected(true);
			}
		}
	}
	@Override
	public void finalize(){
		Exam exam = getExam();
		qual.setExam(exam);	
	}

	public String getExamLocation() {
		return this.txtExamLocation.getText();
	}

	public void setExamLocation(String name) {
		this.txtExamLocation.setText(name);
		
	}

}
