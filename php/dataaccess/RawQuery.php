<?php
require_once 'ResultSet.php';

class RawQuery {
	
	private $queryString;
	
	public function __construct($query) {
		$this->queryString = $query;
	}

	public function executeQuery() {
		return dbConnection::getInstance()->executeQuery($this->queryString, 'ResultSet');
	}

	public function findCount($name="count") {
		$results = dbConnection::getInstance()->executeQuery($this->queryString, "ResultSet");
		return $results[0]->$name;
	}
}
?>