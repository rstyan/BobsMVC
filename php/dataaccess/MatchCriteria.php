<?php
require_once 'QueryCriteria.php';

class MatchCriteria extends QueryCriteria {
	
	private $keywords;
	
	public function __construct($key, $keywords) {
		parent::__construct($key, QueryCriteria::selector);
		$this->keywords = $keywords;
	}
	
	public function toString() {
		return "MATCH(".$this->key.") AGAINST ('".$this->keywords."')";
	}
}


?>