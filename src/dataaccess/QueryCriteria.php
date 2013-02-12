<?php
abstract class QueryCriteria {
	const selector = 1;
	const orderer = 2;
	const grouper = 3;
	const joiner = 4;
	const limit = 5;
	const postSelector = 6;
	
	protected $key;
	private $criteriaType;
	
	public function __get($name) {
		return $this->$name;
	}
	
	public function __construct($key, $type) {
		$this->key = $key;
		$this->criteriaType = $type;
	}
	
	public abstract function toString();
	
	public function canSelect() {
		return $this->criteriaType == self::selector;
	}
	
	public function canOrder() {
		return $this->criteriaType == self::orderer;
	}
	
	public function canGroup() {
		return $this->criteriaType == self::grouper;
	}
	
	public function canJoin() {
		return $this->criteriaType == self::joiner;
	}
	
	public function isLimit() {
		return $this->criteriaType == self::limit;
	}
	
	public function canPostSelect() {
		return $this->criteriaType == self::postSelector;
	}
}
?>