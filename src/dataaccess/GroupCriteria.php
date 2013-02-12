<?php
require_once 'QueryCriteria.php';

class GroupCriteria extends QueryCriteria {
	
	public function __construct($key) {
		parent::__construct($key, QueryCriteria::grouper);
	}
	
	public function toString() {
		return $this->key;
	}
}

?>