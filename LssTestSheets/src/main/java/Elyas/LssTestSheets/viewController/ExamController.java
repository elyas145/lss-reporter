package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.controlsfx.control.textfield.CustomTextField;

import Elyas.LssTestSheets.model.Exam;
import Elyas.LssTestSheets.model.Qualification;
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
			date = LocalDate.parse(txtYear.getText()+"-"+txtMonth.getText()+"-"+txtDay.getText());
		}catch(Exception e){
			date = null;
		}
		exam.setDate(date);
		exam.setOriginal(rdbOriginal.isSelected());
		return exam;
				
	}

	public void setQual(Qualification qual) {
		this.qual = qual;
		if(qual.getExam() != null){
			Exam exam = qual.getExam();
			if(exam.getDate() != null){
				LocalDate date = exam.getDate();
				if(!date.equals(LocalDate.MAX)){
					txtYear.setText(date.getYear()+"");
					txtMonth.setText(date.getMonthValue()+"");
					txtDay.setText(date.getDayOfMonth()+"");
				}else{
					txtYear.setText("");
					txtMonth.setText("");
					txtDay.setText("");
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
		qual.setExamDate(exam.getDate());
		qual.getExam().setOriginal(exam.isOriginal());
		
	}

}
