<?php
require_once "Query.php";

class DeleteQuery extends Query {
	
	private $table;

	public function __construct($qObject, $table=null) {
		parent::__construct($qObject);
		$this->table = $table;
	}
	
	public function getBaseQuery() {
		$table = is_null($this->table) ? "" : $this->table->getQueryResource();
		return "delete {$table} from {$this->qObject->getQueryResource()}";
	}
	
	// Bah... limit makes no sence in this context, but PHP Strict Standards says keep it!
	// So umm... change the function name?   Nah.
	public function executeQuery($limit=null, $printQuery=null) {
		$q = $this->toString();
		dbConnection::getInstance()->executeNRQuery($q, $this->qObject->getResultType());
	}
}
?>