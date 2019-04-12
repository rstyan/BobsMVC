package com.clickability.cms.dataaccess.sqlbuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.clickability.cms.dataaccess.sqlbuilder.Query;
import com.clickability.cms.dataaccess.sqlbuilder.Resource;

public class Insert extends Query {

		private final Resource resource;
		public final List<UpdateAssignment> data;
		
		public Insert(Resource resource) {
			this.data = new ArrayList<UpdateAssignment>();
			this.resource = resource;
		}
		
		public Insert add(UpdateAssignment value) {
			this.data.add(value);
			return this;
		}
		
		public String get() {
			StringJoiner columns = new StringJoiner(",");
			StringJoiner values = new StringJoiner(",");
			for (UpdateAssignment assignment : this.data) {
				columns.add(assignment.column);
				values.add("?");
			}
			String query = String.format("insert into %s (%s) values (%s)", this.resource.get(), columns.toString(), values.toString());
			return query;
		}
				
}
