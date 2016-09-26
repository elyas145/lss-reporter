package Elyas.LssTestSheets.util;

import java.time.LocalDate;

public class DateUtil {

	/**
	 * returns a pretty representation of the date in numbers
	 * @param date
	 * @return
	 */
	public String prettyNumbers(LocalDate date){
		return null;
	}
	
	/**
	 * returns a two digit number representation of a month.
	 * @param date
	 * @return
	 */
	public static String prettyMonthNumber(LocalDate date){
		return prettyMonthNumber(date.getMonthValue());
	}

	/** returns a two digit number representation of a month.
	 * 
	 * @param monthValue
	 * @return
	 */
	public static String prettyMonthNumber(int monthValue) {
		if(monthValue < 1 || monthValue > 12)
			return null;
		return prettyNumber(monthValue);
	}
	
	public static String prettyDayNumber(LocalDate date){
		return prettyDayNumber(date.getDayOfMonth());
	}
	
	public static String prettyDayNumber(int day){
		if(day < 1 || day > 31)
			return null;
		return prettyNumber(day);
	}
	private static String prettyNumber(int number){
		if(number < 10){
			return "0"+number;
		}
		return number+"";
	}
	
	/**
	 * returns a 2 digit year.
	 * @param date
	 * @return
	 */
	public static String prettyYear(LocalDate date){
		return (date.getYear()+"").substring(2);
	}
	
	/**
	 * returns a four digit year, given a two digit year.
	 * @param year
	 * @return
	 */
	public static String prettyYear(int year){
		if(year > 99){
			return null;
		}
		return "20"+year;
	}
	public static String prettyYear(String year){
		
		return "20"+year;
	}
}
