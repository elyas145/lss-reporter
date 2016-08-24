/**
 *  This file is part of Email Forensics Tool.
    Email Forensics Tool. is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Email Forensics Tool. is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Email Forensics Tool.  If not, see <http://www.gnu.org/licenses/>.
 */
package LssReporter.Updater;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kenan El-Gaouny
 * @author Elyas Syoufi
 * @date 21-12-2015
 * @version 1.0 
 */
public enum Language {
	EN_CA, FR_CA;

        @Override
	public String toString() {
		switch (this) {
		case EN_CA:
			return "English";
		case FR_CA:
			return "Francais";
		default:
			return null;
		}
	}
        /**
         * converts a language to its corresponding dictionary filename
         * @return the filename 
         */
	public String toFileName() {
		switch (this) {
		case EN_CA:
			return "/en-ca";
		case FR_CA:
			return "/fr-ca";
		default:
			return null;
		}
	}
        /**
         * 
         * @return supported languages as a list
         */
	public static List<Language> asList() {
		List<Language> l = new ArrayList<Language>();
		l.add(EN_CA);
		l.add(FR_CA);
		return l;
	}
}
