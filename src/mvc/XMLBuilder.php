<?php
/*
 * Abstract (implicit) class to handle building an XML responce to a URL (post) request.
 * Function "takeAction" is intended to handle DB calls etc. to retrieve the data that will
 * then be called by "buildXML" to generate the output.
 * 
 * Override: "takeAction" as needed.
 * Override: "buildXmlContent" to populate the response as necessary.
 * Override: "hasErrors" to indicate whether "takeAction" succeeded or "failed".
 */
class XMLBuilder {
	
	protected $requestType;
	
	public function __construct($type) {
		$this->requestType = $type;
	}
	
	/*
	 * Override as needed: indicate whether or not the request
	 * is valid.
	 * See class RequestValidator for useful validation methods.
	 */	
	protected function validRequest($post, $get) {
		return true;
	}
	
	/*
	 * Override as needed: execute the request.
	 */
	protected function takeAction($post, $get) {
	}
	
	/*
	 * Override as needed: describe the result of the request
	 * via an XML document.
	 */
	protected function buildXmlContent($writer, $post, $get) {
	}
	
	
	/*
	 * Override as needed: indicate whether or not the request
	 * has errors.
	 */
	protected function hasErrors() {
		return false;
	}
	
	private function buildXML($post, $get) {
		$writer = new XMLWriter();
		$writer->openMemory();
		$writer->startDocument("1.0", "UTF-8");
		$writer->startElement($this->requestType);
		$writer->startElement("result");
		$writer->text($this->hasErrors() ? "fail" : "success");
		$writer->endElement();
		$this->buildXmlContent($writer, $post, $get);
		$writer->endElement();
		$writer->endDocument();
		return 	$writer->outputMemory(true);
	}
	
	public function performRequest($post, $get) {
		if ($this->validRequest($post, $get)) {
			$this->takeAction($post, $get);
		}
		return $this->buildXML($post, $get);		
	}
}
?>