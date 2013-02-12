<?php
require_once "dataaccess/QueryObject.php";
require_once "dataaccess/ResultSet.php";
require_once "dataaccess/JoinCriteria.php";

class View implements QueryObject {
	
	private $qObject1;
	private $qObject2;
	private $joinCriteria = array();
	private $joinType;
	
	public function __construct($dto1, $dto2, $joinType="join") {
		$this->qObject1 = $dto1;
		$this->qObject2 = $dto2;
		$this->joinType = $joinType;
	}
	
	public function addJoinCriteria($attr1, $attr2, $op="=") {
		$this->joinCriteria[] = new JoinCriteria($attr1, $attr2, $op);
	}
	
	public function getQueryResource() {
		$qOn = "";
		foreach ($this->joinCriteria as $crit) {
			$qOn = $qOn . $crit->toString() . " and ";
		}
		if (!empty($qOn)) {
			$qOn = "on (" . substr($qOn, 0, -5) . ")";
		}
		$select = "(" . $this->qObject1->getQueryResource() . ") " . $this->joinType . " (" . $this->qObject2->getQueryResource() . ") " . $qOn;
        return $select;
	}
	
	public function getResultType() {
		return "ResultSet";
	}
	
	public function morphQueryResults(&$results) {
		return $results;
	}
	
}
?>