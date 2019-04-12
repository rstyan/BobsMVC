<?php
require_once "QueryCriteria.php";
require_once "Query.php";

class SelectCriteria extends QueryCriteria {
	private $expr;
	private $operator;
	private $valueType;
	
    // TODO We wouldn't have to pass $type except for field names
	// like "Lot.lotID", which can't be quoted.  So,
	// 1) default to int -> then only if string do we do anything other than pass the value along.
	//    but this sucks cause field names aren't int.
	// 2) pass in a "FieldName" object with a simple toString method: sucks cause then all field
	//    names would have to be passed as new FieldName('lot.lotID');
	// 3) pass in a "DbSafeString" which has the appropriate toString method: sucks cause all
	//    string literals would be passed in as new DbSafeString("bla bla bla");  But we don't do that as often as 2)
	// 4) Somehow know the context of the string ie. it is a field name; cause table already knows, sort of.
	public function __construct($key, $expr, $valueType=FieldDescriptor::string, $operator="=", $type=QueryCriteria::selector) {
		parent::__construct($key, $type);
		$this->expr = $expr;
		$this->operator = $operator;
		$this->valueType = $valueType;
	}
	
	public function toString() {
		$val = is_object($this->expr)?("(" . $this->expr->toString() . ")"):Query::toSafeString($this->expr, $this->valueType);
		return $this->key . " " . $this->operator . " $val";
	}
}
?>