<?php
/*
 * Simple list iterator:
 * Extents the array concept to include methods for determining the index
 * for the next and previous elements in the list, wrapping-around when the
 * the ends of the list are reached.
 */
class ListIterator implements Iterator {
	
	private $list;
	private $current=0;
	private $prefix;
	
	public function __construct($list, $prefix) {
		$this->list = $list;
		$this->prefix = $prefix;
	}
	
	public function current() {
		return $this->list[$this->current];
	}
	public function key() {
		return $this->current;
	}
	public function next() {
		$this->current = $this->current+1;
		return $this->current >= $this->count()? null : $this->current;
	}
	public function rewind() {
		$this->current = 0;
	}
	public function valid() {
		return $this->current < $this->count();
	}
	
	public function count() {
		return count($this->list);
	}
	
	public function iNext() {
		return $this->prefix . (($this->current < $this->count()-1?$this->current:-1)+1);
	}
	
	public function iPrev() {
		return $this->prefix . (($this->current <= 0?$this->count():$this->current)-1);
	}
	
	public function iKey() {
		return $this->prefix . $this->current;
	}
}
?>