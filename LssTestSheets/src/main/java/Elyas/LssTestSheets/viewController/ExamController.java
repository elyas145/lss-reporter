package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.io.filefilter.IOFileFilter;

import Elyas.LssTestSheets.model.Exam;
import Elyas.LssTestSheets.model.Qualification;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;

public class ExamController extends Controller implements Initializable{
	@FXML
	DatePicker dpkExamDate;
	@FXML
	RadioButton rdbOriginal;
	@FXML
	RadioButton rdbRecert;
	private Qualification qual;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		
	}
	
	public Exam getExam(){
		Exam exam = new Exam();
		exam.setDate(dpkExamDate.getValue());
		exam.setOriginal(rdbOriginal.isSelected());
		return exam;
				
	}

	public void setQual(Qualification qual) {
		this.qual = qual;
		if(qual.getExam() != null){
			Exam exam = qual.getExam();
			if(exam.getDate() != null){
				dpkExamDate.setValue(exam.getDate());
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
		if(dpkExamDate.getValue() != null){
			qual.setExamDate(dpkExamDate.getValue());
			qual.getExam().setOriginal(rdbOriginal.isSelected());
		}		
	}

}
