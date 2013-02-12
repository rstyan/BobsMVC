<?php
require_once 'QueryCriteria.php';

class Limitor extends QueryCriteria {
	private $limit;
	
	public function __get($name) {
		return $this->$name;
	}
	
	public function __construct($limit) {
		parent::__construct(null, QueryCriteria::limit);
		$this->limit = $limit;
	}
	
	public function toString() {
		return $this->limit;
	}
}
?>