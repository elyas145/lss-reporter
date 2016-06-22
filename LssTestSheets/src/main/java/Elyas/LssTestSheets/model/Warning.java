package Elyas.LssTestSheets.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * represents the warnings found in a course. the warnings are represented as a
 * map of a title, to description. typical warnings include empty data fields and invalid information.
 * 
 * @author Elyas
 *
 */
public class Warning {
	Map<String, Warning> categories;
	List<String> warnings;
	

	public Warning(){
		categories = new HashMap<>();
		warnings = new ArrayList<>();
	}
	public Warning newCategory(String categoryName) {
		categories.put(categoryName, new Warning());
		return categories.get(categoryName);
	}
	public void add(String string) {
		warnings.add(string);
	}
	public boolean containsWarnings() {
		boolean hasWarnings = false;
		if(warnings.isEmpty()){
			for (String string : categories.keySet()) {
				if(categories.get(string)!= null && categories.get(string).containsWarnings()){
					hasWarnings = true;
				}
			}
		}else{
			return true;
		}
		return hasWarnings;
	}
	public List<String> getWarnings() {
		return warnings;
	}
	public Collection<String> getCategories() {
		return categories.keySet();
	}
	public Collection<String> getWarnings(String category) {
		
		return categories.get(category).getWarnings();
	}

}
