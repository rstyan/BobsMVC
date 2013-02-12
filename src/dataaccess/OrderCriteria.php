<?php
require_once 'QueryCriteria.php';

class OrderCriteria extends QueryCriteria {
	
	public function __construct($key) {
		parent::__construct($key, QueryCriteria::orderer);
	}
	
	public function toString() {
		return $this->key;
	}
	
}
?>