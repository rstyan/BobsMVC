<?php
class Selector {
	private $expr;
	private $alias;
	
	public function __construct($expr, $alias=null) {
		$this->expr = $expr;
		$this->alias = $alias;
	}
	
	public function toString() {
		$value = $this->expr;
		if (!is_null($this->alias)) {
			$value = $value . " as $this->alias";
		}
		return $value;
	}
	
}
?>