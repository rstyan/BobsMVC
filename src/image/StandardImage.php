<?php
require_once 'Base64Image.php';

class StandardImage {
   
	private $image;
	private $image_type;
   
	public function __construct($image, $type) {
		$this->image = $image;
		$this->image_type = $type;
	}
   
	public function __get($name) {
   		return $this->$name;
	}
 
	public function save($filename, $newType=null, $compression=75, $permissions=null) {
		$type = $newType==null?$this->image_type:$newType;
		if( $type == IMAGETYPE_JPEG ) {
			imagejpeg($this->image,$filename,$compression);
		} 
		elseif( $type == IMAGETYPE_GIF ) {
			imagegif($this->image,$filename);         
		} 
		elseif( $type == IMAGETYPE_PNG ) {
			imagepng($this->image,$filename);
		}   
		if( $permissions != null) {
			chmod($filename,$permissions);
		}
   }
   
	// Output image stream directly.
	// e.g. use: header('Content-Type: image/jpeg');
	public function stream($newType=null) {
		$type = $newType==null?$this->image_type:$newType;
		if( $type == IMAGETYPE_JPEG ) {
			imagejpeg($this->image);
		} 
		elseif( $type == IMAGETYPE_GIF ) {
			imagegif($this->image);         
		} 
		elseif( $type == IMAGETYPE_PNG ) {
			imagepng($this->image);
		}
	}
	
	public function destroy() {
		imagedestroy($this->image);
		$this->image=null;
	}
   
	public function getWidth() {
		return imagesx($this->image);
	}
   
	public function getHeight() {
		return imagesy($this->image);
	}
   
	public function resizeToHeight($height) {
		$ratio = $height / $this->getHeight();
		$width = $this->getWidth() * $ratio;
		return $this->resize($width,$height);
	}
   
	public function resizeToWidth($width) {
		$ratio = $width / $this->getWidth();
		$height = $this->getheight() * $ratio;
		return $this->resize($width,$height);
	}
   
	public function scale($scale) {
		$width = $this->getWidth() * $scale/100;
		$height = $this->getheight() * $scale/100; 
		return $this->resize($width,$height);
	}
   
	private function resize($width,$height) {
		$new_image = imagecreatetruecolor($width, $height);
		imagecopyresampled($new_image, $this->image, 0, 0, 0, 0, $width, $height, $this->getWidth(), $this->getHeight());
		return new StandardImage($new_image, $this->image_type);   
	}      

	public static function load($filename) {
		if (!file_exists($filename)) {
			return null;
		}
		$image_info = getimagesize($filename);
		$type = $image_info[2];
		if( $type == IMAGETYPE_JPEG ) {
			$image = imagecreatefromjpeg($filename);
		} 
		elseif( $type == IMAGETYPE_GIF ) {
			$image = imagecreatefromgif($filename);
		} 
		elseif( $type == IMAGETYPE_PNG ) {
			$image = imagecreatefrompng($filename);
		}
		else {
			return null;
		}
		return new StandardImage($image, $type);
	}
	
	public function encode() {
		ob_start();
		$this->stream();
		$binary = ob_get_clean();
		return new Base64Image(base64_encode($binary), $this->image_type);
	}
	
}
?>