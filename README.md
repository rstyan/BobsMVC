An object oriented approach to writing SQL

Examples:
The easiest thing you can do is write a simple select statement, like this:
	
	public List<Order> findMaximumQuantyOnOrders() throws SQLException {
		Query query = new Query()
			.select(new Selection("customerId", "productId", "max(quantity)"))
			.from(getResource())
			.where(new Equal("status", "shipped"))
			.groupBy(new GroupBy("customerId").add("productId"));
		return fetchAll(query, new ArrayList<>(), this::getItem);
	}

More complex queries includng joins and subqueries, can be easily implemented too:

	public Order findBiggestOrder(int productId, int customerId) throws Exception {
		Resource products = getResource("p");
		Resource orders = getResource(Order.class, "o");
		Resource customers = getResource(Customer.class, "c");

		Join resources = new Join(products)
			.join(new JoinExpression(orders)
			.on(new Conjunction(new Equal(new Column(p, "productId"), new Column(o, "productId")))),
			.join(new JoinExpression(customers)
			.on(new Equal("o.customerId", "c.customerId");

		Query maxQuantity = new Query()
			.select(new Selection().add("max(quantity)"))
			.from(o)
			.where(new Equal("o.productId").rhs(new Column(p, "productId")));

		Query q = new Query()
			.from(resources)
			.where(new Equal("p.productId"), new Equal("c.customerId"), new Equal("o.quantity").rhs(maxQuantity));

		Binding[] bindings= {new Binding(productId), new Binding(customerId)};
		return fetch(q, bindings);
	}
