<?php
require_once "logging/SimpleLogger.php";
require_once "dbConnection.php";
require_once "ResultSet.php";

class Query {
	protected $qObject;
	private $criteria=array();
	private $selectors=array();
	private $logger;
	
	public function __construct($qObject) {
		$this->qObject = $qObject;
		$this->logger = SimpleLogger::getInstance();
	}
	
	public function addCriteria($criteria) {
		$this->criteria[] = $criteria;
	}
	
	public function addSelector($selector) {
		$this->selectors[] = $selector;
	}
	
	public function getBaseQuery() {
		$selector = "*";
		if (!empty($this->selectors)) {
			$selector = "";
			foreach($this->selectors as $sel) {
				$selector = $selector . $sel->toString() . ", ";
			}
			$selector = substr($selector, 0, -2);
		}
		return "select ". $selector . " from ". $this->qObject->getQueryResource();
	}
	
	// Make sure the input value is DB safe - so
	// quote it and convert it etc.
	public static function toSafeString($value, $type) {
        if (is_null($value)) {
            return "null";
        }
		else if ($type == FieldDescriptor::string) {
			return "'" . (get_magic_quotes_gpc() ? $value : addslashes($value)) . "'";
		}
        else if (is_numeric($value)) {
        	return $value;
        }
        else if (is_bool($value)) {
            return $value?1:0;
        }
        else if (is_string($value)) {
        	return trim($value);
        }
        
		return $value;
	}
	
	// Generate the DTO query based on the criteria set.
	public function toString($limit=null, $printQuery=null) {
		
		// Initial select query.
		$qSelect = $this->getBaseQuery();
		$qWhere = "";
		$qOrder = "";
		$qGroup = "";
		$qHaving = "";
		$qLimit = is_null($limit)?"":" limit $limit";
		
		// Populate the criteria into the query
		if (!empty($this->criteria)) {
			foreach ($this->criteria as $criteria) {
				$subQ = $criteria->toString();
				if ($criteria->canSelect()) {
					$qWhere .= $subQ . " and ";
				}
				if ($criteria->canOrder()) {
					$qOrder .= $subQ . ", ";
				}
				if ($criteria->canGroup()) {
					$qGroup .= $subQ . ", ";
				}
				// Override the input limit if necessary, cause we really can't have two:
				if ($criteria->isLimit()) {
					$qLimit = " limit $criteria->limit";
				}
				if ($criteria->canPostSelect()) {
					$qHaving .= $subQ . " and ";
				}
			}
			if (!empty($qWhere)) {
				$qWhere = " where " . substr($qWhere, 0, -5);
			}
			if (!empty($qHaving)) {
				$qHaving = " having " . substr($qHaving, 0, -5);
			}
			if (!empty($qOrder)) {
				$qOrder = " order by " . substr($qOrder, 0, -2);
			}
			if (!empty($qGroup)) {
				$qGroup = " group by " . substr($qGroup, 0, -2);
			}
		}
	    $query = $qSelect . $qWhere . $qGroup . $qHaving . $qOrder . $qLimit;
	    if ($printQuery) {
			$this->logger->log($query);
			echo "$query\n";
	    }
	    return trim($query);
	}
	
	public function executeQuery($limit=null, $printQuery=null) {
		$q = $this->toString($limit, $printQuery);
		$results = dbConnection::getInstance()->executeQuery($q, $this->qObject->getResultType());
		return $this->qObject->morphQueryResults($results);
	}
	
	public function findCount($printQuery=null) {
		$this->addSelector(new Selector("count(*)", "count"));
		$q = $this->toString(null, $printQuery);
		$results = dbConnection::getInstance()->executeQuery($q, "ResultSet");
		return $results[0]->count;
	}
}
?>