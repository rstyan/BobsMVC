<?php

require_once 'dbConnection.php';
require_once 'FieldDescriptor.php';
require_once 'RelationshipDescriptor.php';
require_once 'logging/SimpleLogger.php';
require_once 'Query.php';
require_once 'SelectCriteria.php';
require_once 'QueryObject.php';
require_once 'QueryResult.php';

abstract class Table implements QueryObject, QueryResult {
	
	// corresponding database attributes & relationships
	protected $table;
	protected $alias;
	protected $attributes;
	protected $relationships=array();

	protected $logger;
	
	// for testing
	private static $isTestMode=false;
	private static $testInstances=array();
	
	public static function setTestMode($mode) {
		self::$isTestMode = $mode;
	}
	
	public static function removeTestInstances() {
  		foreach (self::$testInstances as $dto) {
  			$dto->removeInstance();
  		}
  		self::$testInstances = array();
	}

	public function __construct($table, $alias=null) {
		$this->table = $table;
		$this->alias = $alias;
		$this->logger = SimpleLogger::getInstance();
	}
	
	// Leave it for the subclasses to do something
	// if it is necessary.
	public function initialize() {
	}
	
	public function addAttribute($field, $name, $type, $len=null, $nullable=false, $default=null, $key=false, $auto=false) {
		$this->attributes[$field] = new FieldDescriptor($name, $type, $len, $nullable, $default, $key, $auto);
	}
	
	public function addRelationship($relName, $className, $keyMap, $type=RelationshipDescriptor::one2many, $autoUpdate=true) {
		$this->relationships[$relName] = new RelationshipDescriptor($className, $keyMap, $type, $autoUpdate);
		
		// add the relationship property to the object:
		if (property_exists($this, $relName)) {
			$this->logger->log("property $relName already exists for class " . get_class($this) . ". Setting to null");
		}
		switch ($type) {
			case RelationshipDescriptor::one2many:
				$this->$relName = array();
				break;
			case RelationshipDescriptor::one2one:
				$this->$relName = null;
				break;
		}
	}
	
	public function getQueryResource() {
		return $this->table . (is_null($this->alias)?'':" $this->alias");
	}
	
	public function getResultType() {
		return get_class($this);
	}
	
	public function morphQueryResults(&$result) {
		return $result;
	}
	
	public function getPropertyNames() {
		return array_keys($this->attributes);
	}
	
	public function getPrimaryKey() {
		$result = array();
		$i=0;
		foreach ($this->attributes as $key=>$fd) {
			if ($fd->isKey) {
				$result[$i++] = $key;
			}
		}
		return $result;
	}
	
	private function getNonPrimaryFields() {
		$result = array();
		$i=0;
		foreach ($this->attributes as $key=>$fd) {
			if (!$fd->isKey) {
				$result[$i++] = $key;
			}
		}
		return $result;
	}
	
	private function getAutogenField() {
		$result = null;
		foreach ($this->attributes as $key=>$fd) {
			if ($fd->isAutogen) {
				$result = $key;
				break;
			}
		}
		return $result;
	}
	
	private function isValidType($type, $value) {
		switch ($type) {
			case FieldDescriptor::int:
				return is_int($value);
			case FieldDescriptor::bool:
				return is_bool($value);
			case FieldDescriptor::decimal:
				return is_double($value);
			case FieldDescriptor::string:
				return is_string($value);
		}
		return false;
	}
	
	// Remove existing object's properties in case of re-population.
	private function reset() {
	  	foreach ($this->attributes as $field=>$fd) {
	  		if (property_exists($this, $field)) {
	  			unset($this->$field);
	  		}
	  	}
	}

	// the insert/update methods already to this.
	// so, like, umm... use this for ....
	public function cloneForDb() {
		$copy = clone $this;
		$copy->xformAttributes('addslashes');
		return $copy;
	}
	
	// Get rid of sql safeness
	public function cloneForDisplay() {
		$copy = clone $this;
		$copy->xformAttributes('stripslashes');
		return $copy;
	}


	
	// Transform attributes:
	// Applies a transformation function to each of the objects
	// attributes, including those of its relations.
    // TODO: only applies to string attributs (cause i'm lazy).  FIX THIS!
    // NOTE: the function must accept an attribute and return it transformed.
	public function xformAttributes($func) {
		foreach ($this->attributes as $key=>$fd) {
			if ($fd->type == FieldDescriptor::string && property_exists($this, $key)) {
				$this->$key = $func($this->$key);
			}
		}
		foreach ($this->relationships as $key=>$rd) {
			switch ($rd->type) {
				case RelationshipDescriptor::one2many:
					for ($i=0; $i<sizeof($this->$key); $i++) {
						$tmp = &$this->$key;
						$tmp[$i]->xformAttributes($func);
					}
					break;
				case RelationshipDescriptor::one2one:
					$this->$key->xformAttributes($func);
					break;
			}
		}
	}
		
	// Make sure we get a clone of the related objects, not
	// just references the originals.
	public function __clone() {
		foreach ($this->relationships as $key=>$rel) {
			switch ($rel->type) {
				case RelationshipDescriptor::one2many:
					for ($i=0; $i<sizeof($this->$key); $i++) {
						$tmp = &$this->$key;
						$tmp[$i] = clone $tmp[$i];
					}
					break;
				case RelationshipDescriptor::one2one:
					$this->$key = clone $this->$key;
					break;
			}
		}
	}
	
	// Converts string values to their proper type.
	public function convert($value, $type) {
		if ($type == FieldDescriptor::string) {
			return $value;
		}
		$result = null;
		switch ($type) {
			case FieldDescriptor::int:
				if (is_numeric($value)) {
					$tmp1 = intval($value);
					$tmp2 = floatval($value);
					// make sure it really is an int, not a double
					if ($tmp1 == $tmp2) {
						$result=$tmp1;
					}
				}
				break;
				
			case FieldDescriptor::decimal:
				if (is_numeric($value)) {
					$result = floatval($value);
				}
				break;
				
			case FieldDescriptor::bool:
				// several string values can be converted;
				     if(strtolower($value)=='on') $result = true;
				else if(strtolower($value)=='off') $result = false;
				else if(strtolower($value)=='yes') $result = true;
				else if(strtolower($value)=='no') $result = false;
				else if(strtolower($value)=='true') $result = true;
				else if(strtolower($value)=='false') $result = false;
				
				// and integer values too!
				else if(strval($value)=="1") $result = true;
				else if(strval($value)=="0") $result = false;
				break;
		}
		return $result;
	}
	
	
	public function populate($resultSet) {
		$this->reset();       // clear out old properties
		$errorMsgs = array(); // and assume the best		
	  	foreach ($resultSet as $key => $value) {
	  		// Ensure this is a valid field for the DTO
	  		if (!array_key_exists($key, $this->attributes)) {
	  			continue;
	  		}
  			$fd = $this->attributes[$key];
  			
	  		// trim any whitespace
	  		if (!is_null($value) && is_string($value)) {
	  			$value = trim($value);
	  		}
	  		
	  		// get rid of empty strings; they are considered null
	  		// and should be treated as such
	  		if (!is_null($value) && is_string($value) && strlen($value) == 0) {
	  			$value = null;
	  		}
	  		
  			// Ensure null values are allowed
  			if (is_null($value) && !$fd->isNullable) {
	  			$errorMsgs[$key] = "$fd->name is required";
  				continue;
  			}
  			
	  		// Convert non-null input, but don't bother converting strings.
  			if (!is_null($value) && $fd->type!=FieldDescriptor::string) {
				$tmp = $this->convert($value, $fd->type);
				if (is_null($tmp)) {
		  			$errorMsgs[$key] = "$value cannot be converted to $fd->type for $fd->name";
		  			continue;
				}
				$value = $tmp;
  			}
			
			// check to ensure the input value is a valid for the field's type
  			if (!is_null($value) && !$this->isValidType($fd->type, $value)) {
	  			$errorMsgs[$key] = "$value is an invalid type for $fd->name";
	  			continue;
  			}
  			
  			// Check string lengths.
  			if (!is_null($value) && $fd->type==FieldDescriptor::string && strlen($value) > $fd->length) {
	  			$errorMsgs[$key] = "$fd->name cannot have more than $fd->length characters";
  				continue;
  			}
  			$this->$key = $value;
	  	}
	  	
	  	// Ensure all values are present: Any fields not explicitly set by the input
	  	// must either have a default value or be allowed to be null
	  	foreach ($this->attributes as $key=>$fd) {
	  		if (!array_key_exists($key, $resultSet)) {
	  			
	  			// If the field value hasn't been specifed and it has a default value, 
	  			// then set it to such
	  			if (!is_null($fd->default)) {
	  				$this->$key = $fd->default;
	  			}
	  			
	  			// If the field is allowed to be null then set it explicitly
	  			// to ensure the object's field exists
	  			else if ($fd->isNullable) {
	  				$this->$key = null;
	  			}
	  			
	  			// Otherwise whine about the field not being present.
	  			else {
	  				$errorMsgs[$key] = "$fd->name is a required parameter";
	  				continue;
	  			}
	  		}
	  	}
	  	return $errorMsgs;
	}
	
	public function findInstance($limit=null, $op="=") {
		$attributes = $this->getPropertyNames();
		$query = new Query($this);
		foreach ($attributes as $property) {
			if (property_exists($this, $property)) {
				$query->addCriteria(new SelectCriteria($property, $this->$property, $this->attributes[$property]->type, $op));
			}
		}
		$result = $query->executeQuery($limit);		
		// populate any related dto fields:
		foreach ($result as $dto) {
			$dto->findRelations();
		}
		return $result;
	}
	
	private function findRelations() {
		foreach ($this->relationships as $relName=>$descriptor) {
			if ($descriptor->autoUpdate) {
				$template = new $descriptor->className();
				foreach ($descriptor->keyMap as $pk=>$fk) {
					$template->$fk = $this->$pk;
				}
				$foreigners = $template->findInstance();
				if ($descriptor->type==RelationshipDescriptor::one2many && !empty($foreigners)) {
					$this->$relName = $foreigners;
				}
				else if ($descriptor->type==RelationshipDescriptor::one2one && !empty($foreigners)) {
					$this->$relName = $foreigners[0];
					if (sizeof($foreigners)>1) {
						$this->logger->log("Too many instances of $relName found");
					}
				}
			}
		}
	}
	
	private function doUntoMe($key, $op) {
		switch ($this->relationships[$key]->type) {
			case RelationshipDescriptor::one2many:
				foreach ($this->$key as $obj) {
					$obj->$op();
				}
				break;
			case RelationshipDescriptor::one2one:
				$this->$key->$op();
				break;
		}
	}
	
	private function doUntoOthers($op) {
		foreach ($this->relationships as $key=>$rel) {
			if ($rel->autoUpdate) {
				$this->doUntoMe($key, $op);
			}
		}
	}
	
	// Delete the instance of the object from the database
	public function removeInstance() {
		// First remove related objects with autoUpdate status
		$this->doUntoOthers('removeInstance');
		
		$uniqueSelector = "";
		foreach ($this->getPrimaryKey() as $key) {
			$uniqueSelector = $uniqueSelector . "$key=" . Query::toSafeString($this->$key, $this->attributes[$key]->type) . " and ";
		}
		$uniqueSelector = substr($uniqueSelector, 0, -5);
		$query = "delete from " . $this->table . " where $uniqueSelector";
		return dbConnection::getInstance()->executeNRQuery($query);
	}
	
	// Update the instance of the object in the database
	// at the moment it does not update dependent objects.
	public function updateInstance() {
		$query = $this->generateUpdateQuery();
		$result = dbConnection::getInstance()->executeNRQuery($query);
		$this->doUntoOthers('updateInstance');
		return $result;
	}
	
	public function generateUpdateQuery() {
		$setter="";
		foreach ($this->getNonPrimaryFields() as $key) {
			if (property_exists($this, $key)) {
				$setter = $setter . "$key=" . Query::toSafeString($this->$key, $this->attributes[$key]->type) . ", ";
			}
		}
		$setter = substr($setter, 0, -2);
		$selector="";
		foreach ($this->getPrimaryKey() as $key) {
			$selector = $selector . "$key=" . Query::toSafeString($this->$key, $this->attributes[$key]->type) . " and ";
		}
		$selector = substr($selector, 0, -5);
		$query = "update $this->table set $setter where $selector";
		return $query;
	}
	
	public function insertOrUpdateInstance($echoQuery=false) {
		$setter="";
		foreach ($this->getNonPrimaryFields() as $key) {
			if (isset($this->$key)) {
				$setter = $setter . "$key=" . Query::toSafeString($this->$key, $this->attributes[$key]->type) . ", ";
			}
		}
		$query = (strlen($setter) > 0) ?
			($this->generateInsertQuery(). " on duplicate key update " . substr($setter, 0, -2)):
			($this->generateInsertQuery(true));
		$db = dbConnection::getInstance();
		if ($echoQuery) {
			echo $query;
		}
		$result = $db->executeNRQuery($query);
		$this->updateForeignKeys($db);
		$this->doUntoOthers('insertOrUpdateInstance');
		if (self::$isTestMode) {
  			self::$testInstances[] = $this;
		}
		return $result;
	}
	
	// Update an object's foriegn keys with the
	// new autogen, if present.
	private function updateForeignKeys($db) {
		$autoField = $this->getAutogenField();
		if (!is_null($autoField)) {
			$id = $db->getLastIdGenerated();
			if (is_null($id) || $id <= 0) {
				return false;
			}
			$this->$autoField = $id;
			$this->updateForeigners($autoField);
		}
	}
	
	// Save the dto instance to the DB
	// return true if operation is successful.
	public function addInstance() {
		$db = dbConnection::getInstance();
		$query = $this->generateInsertQuery();
		$result = $db->executeNRQuery($query);
		if (!$result) {
			echo "failed to execute: $query";
			return false;
		}
		$this->updateForeignKeys($db);
		// Save relatives
		$this->doUntoOthers('addInstance');
		// Used for testing
		if (self::$isTestMode) {
  			self::$testInstances[] = $this;
		}
		return true;
	}
	
	// Update all foreign objects contained in this dto
	// with the new primary key value.  This is needed when a dto
	// is saved with an autoincrement key.
	private function updateForeigners($autoAttr) {
	    foreach ($this->relationships as $relName=>$descriptor) {
	    	if (array_key_exists($autoAttr, $descriptor->keyMap)) {
				$fk = $descriptor->keyMap[$autoAttr];
	    		switch ($this->relationships[$relName]->type) {
					case RelationshipDescriptor::one2many:
						foreach ($this->$relName as $dto) {
							$dto->$fk = $this->$autoAttr;
						}
						break;
					case RelationshipDescriptor::one2one:
						$this->$relName->$fk = $this->$autoAttr;
						break;
				}
	    	}
	    }
	}
	
	// set the foreign keys to a newly added object
	// if they are know at this point.
	// NOTE: autogen keys may not be known before the object is saved.
	private function setFK($relName, $relative) {
		$keyMap = $this->relationships[$relName]->keyMap;
		foreach ($keyMap as $myKey=>$theirKey) {
			if (property_exists($this, $myKey) && !is_null($this->$myKey)) {
				$relative->$theirKey = $this->$myKey;
			}
		}
	}

	private function addRelation($relName, $dto) {
		switch ($this->relationships[$relName]->type) {
			case RelationshipDescriptor::one2many:
				$attr = &$this->$relName;
				$attr[] = $dto;
				break;
			case RelationshipDescriptor::one2one:
				$this->$relName = $dto;
				break;
		}
		$this->setFK($relName, $dto);
	}
	
	// Handle add_$name cases for dependent objects
	public function __call($name, $arguements) {
		if (substr($name, 0, 4) == "add_" && sizeof($arguements)==1) {
			$attribute = substr($name, 4);
			$this->addRelation($attribute, $arguements[0]);
		}
		else {
			$position = debug_backtrace();
			die("$name: no such method, in " . $position[1]['file'] . ", line " . $position[1]['line'] . "\n");
		}
	}
	
	// Generate an insert Query from a DTO object;
	// do not add any fields on the ignore list;
	// return a string of the form: insert into <table> (field_list) values (value_list)
	public function generateInsertQuery($ignore=false) {
        $tabledata = $this->getPropertyNames();
        $fields = "";
        $values = "";
        foreach ($tabledata as $key) {
        	if (property_exists($this, $key)) {
	        	$value = Query::toSafeString($this->$key, $this->attributes[$key]->type);
	            $fields = $fields . $key . ', ';
	            $values = $values . $value . ', ';
        	}
        }
        $fields = substr($fields, 0, -2);
        $values = substr($values, 0, -2);
		$query = "insert".($ignore?" ignore":"")." into $this->table ($fields) values ($values)";
		return $query;
	}
	
	// Test to see if this object is already saved in the DB.
	public function isSaved() {
		$keys = $this->getPrimaryKey();
		$class = get_class($this);
		$copy = new $class();
		foreach ($keys as $key) {
			$copy->$key = $this->$key;
		}
		$instance = $copy->findInstance();
		return !is_null($instance) && !empty($instance) && sizeof($instance)==1;
	}
	
}

?>