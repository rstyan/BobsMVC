<?php
require_once "StandardImage.php";

class Base64Image {
	
	const startTag = "<base64Image>";
	const endTag   = "</base64Image>";
	private $image;
	private $image_type;
   
	public function __construct($image, $type) {
		$this->image = $image;
		$this->image_type = $type;
	}
   
	public function __get($name) {
		return $this->$name;
	}
	
	public function toString() {
		$xml =  self::startTag;
		$xml .= 	"<type>".$this->image_type."</type>";
		$xml .=		"<encoding>".$this->image."</encoding>";
		$xml .= self::endTag;
		return $xml;
	}
	
	public function save($filename) {
		file_put_contents($filename, $this->toString());
	}
	
	public static function load($filename) {
		if (!file_exists($filename)) {
			return null;
		}
		$index = array();
		$values = array();
		
		$xml = file_get_contents($filename);

		$p = xml_parser_create();
		xml_parse_into_struct($p, $xml, $values, $index);
		xml_parser_free($p);
		$type = $values[$index['TYPE'][0]]['value'];
		$image = $values[$index['ENCODING'][0]]['value'];
		return new Base64Image($image, $type);
	}
	
	public static function encode($filename) {
		if (!file_exists($filename)) {
			return null;
		}
		$image_info = getimagesize($filename);
		$type = $image_info[2];
		$handle = fopen($filename, "r");
		$imgbinary = fread($handle, filesize($filename));
		$image = base64_encode($imgbinary);
		return new Base64Image($image, $type);
	}
	
	public function decode() {
		$image = imagecreatefromstring(base64_decode($this->image));
		return new StandardImage($image, $this->image_type);
	}
	
}
?>