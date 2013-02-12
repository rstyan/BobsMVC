<?php
require_once "QueryCriteria.php";

class Conjunction extends QueryCriteria {
	
	private $expr2;
	
	public function __construct($expr1, $expr2) {
		parent::__construct($expr1, QueryCriteria::selector);
		$this->expr2 = $expr2;
	}
	
	public function toString() {
		return "(" . $this->key->toString() . " and " . $this->expr2->toString() . ")";
	}
}

?>