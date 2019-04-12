<?php
/*
 * Essentially a Map; key/value pairs.  Except it is not at all strongly typed.
 * Or meant to be.  Its a loose structure to deliver a collection of query results
 * to the web pages.
 * OO v.s arrays. Bonk->morf v.s. Bonk['morf'].  Honestly, its 2 less characters to type.
 */
class Map {
	private $items;
	
	public function __construct() {
		$this->items = array();
	}	
	public function __get($name) {
		return array_key_exists($name, $this->items)?$this->items[$name]:null;
	}	
	public function addItem($name, $item) {
		$this->items[$name] = $item;
	}
	public function property_exists($prop) {
		return array_key_exists($prop, $this->items);
	}
}
?>