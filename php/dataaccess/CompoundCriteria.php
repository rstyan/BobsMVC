<?php
require_once "QueryCriteria.php";
require_once "Query.php";

/*
 * For combining logic in SelectCriteria or HavingCriteria.
 */
class CompoundCriteria extends QueryCriteria {
	private $operator;
	private $expr2;
	
	public function __construct($expr1, $expr2, $op="and") {
		parent::__construct($expr1, $expr1->criteriaType);
		$this->operator = $op;
		$this->expr2 = $expr2;
	}
	
	public function toString() {
		return "({$this->key->toString()} $this->operator {$this->expr2->toString()})";
	}
}
?>