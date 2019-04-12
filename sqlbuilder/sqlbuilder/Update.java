package com.clickability.cms.dataaccess.sqlbuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.clickability.cms.dataaccess.sqlbuilder.Query;
import com.clickability.cms.dataaccess.sqlbuilder.Resource;

public class Update extends Query {

		private final Resource resource;
		protected final List<UpdateAssignment> assignments;
		protected Expression where;
		
		public Update(Resource resource) {
			this.assignments = new ArrayList<>();
			this.resource = resource;
			this.where = new EmptyExpression();
		}
		
		public Update add(UpdateAssignment... values) {
			for (UpdateAssignment value : values) {
				this.assignments.add(value);
			}
			return this;
		}
		
		/**
		 * Returns a conjunction of the input criteria
		 * For disjunctions use where(new Disjunction(....).or(...))
		 */
		public Update where(Expression ... criteria) {
			if (criteria.length < 1) {
				this.where = new EmptyExpression();
			}
			else if (criteria.length == 1) {
				this.where = criteria[0];
			}
			else {
				Conjunction conjunction = new Conjunction(criteria[0]);
				for (int i=1; i<criteria.length; i++) {
					conjunction.and(criteria[i]);
				}
				this.where = conjunction;
			}
			return this;
		}

		public String get() {
			StringJoiner sj = new StringJoiner(",");
			for (UpdateAssignment set : this.assignments) {
				sj.add(set.get());
			}
			String query = String.format("update %s set %s where %s", this.resource.get(), sj.toString(), this.where.get());
			return query;
		}
				
}
