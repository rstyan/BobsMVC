<?php
class JoinCriteria {
	private $attribute;
	private $expr;
	private $operator;
	
	// keeps the object immutable cause we don't have to make
	// the attributes public.
	public function __get($name) {
		return $this->$name;
	}
	
	public function __construct($attribute, $expr, $operator) {
		$this->attribute = $attribute;
		$this->expr = $expr;
		$this->operator=$operator;
	}
	
	public function toString() {
		$expr = is_object($this->expr)?("(" . $this->expr->toString() . ")"):$this->expr;
		return $this->attribute ." ".$this->operator ." ".$expr;
	}
}
?>