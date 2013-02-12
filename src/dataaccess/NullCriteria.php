<?php
require_once 'QueryCriteria.php';

class NullCriteria extends QueryCriteria {
	
	private $notNull;
	
	public function __construct($key, $not=false) {
		parent::__construct($key, QueryCriteria::selector);
		$this->notNull = $not;
	}
	
	public function toString() {
		$op = $this->notNull ? " is not null" : " is null";
		return $this->key . $op;
	}
}


?>