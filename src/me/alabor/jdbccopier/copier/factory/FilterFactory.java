package me.alabor.jdbccopier.copier.factory;

import java.util.ArrayList;
import java.util.List;

public class FilterFactory {

	/**
	 * Creates a {@link List} from a comma seperated {@link String}.
	 * 
	 * @param commaSeperatedFilters
	 * @return
	 */
	public List<String> createFilterList(String commaSeperatedFilters) {
		String[] raw = commaSeperatedFilters.split(",");
		List<String> filters = new ArrayList<String>(raw.length);
		
		if(raw.length > 0 && raw[0].length() > 0) {
			for (String filter : raw) {
				filters.add(filter.trim());
			}			
		}
		
		return filters;
	}
	
}
