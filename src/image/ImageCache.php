<?php
require_once 'StandardImage.php';
require_once 'Base64Image.php';
require_once 'dataaccess/FileUtil.php';

// Managing a thumb nail image cache on the file system.  A cache on the file system you say?  Seems
// a bit odd.  But not when you realize it takes almost 3 seconds to reduce an image
// to thumbnail size.  Oh yes, yes it does!
//
// Most functions silently ignore stupid requests.
class ImageCache {
	
	// Directory containing the cached images.
	private $dstDir;
	private $defaultImageWidth;
	
	public function __construct($dstDir, $defaultImageWidth=64) {
		$this->dstDir = FileUtil::getDocumentRoot() . DIRECTORY_SEPARATOR . $dstDir;
		$this->defaultImageWidth = $defaultImageWidth;
	}
	
	private function getFullName($name) {
		return $this->dstDir . DIRECTORY_SEPARATOR . $name . ".xml";
	}
	
	public function get($name) {
		$file = $this->getFullName($name);
		if (!file_exists($file)) {
			return null;
		}
		return file_get_contents($file, 'r');
	}
	
	public function isCached($name) {
		$file = $this->getFullName($name);
		return file_exists($file);
	}
	
	/*
	 * The source directory, containing the original images,
	 * must be relative to the document root.
	 */
	public function add($name, $srcDir) {
		$file = $this->getFullName($name);
		if (file_exists($file)) {
			return null;
		}
		$srcFile = FileUtil::getDocumentRoot() . DIRECTORY_SEPARATOR . $srcDir . DIRECTORY_SEPARATOR . $name;
		if (!file_exists($srcFile)) {
			return null;
		}
		$srcImage = StandardImage::load($srcFile);
		$srcImage = $srcImage->resizeToWidth($this->defaultImageWidth);
		$finalImage = $srcImage->encode();
		$finalImage->save($file);
		return $finalImage->toString();
	}
	
	public function remove($name) {
		$file = $this->getFullName($name);
		if (!file_exists($file)) {
			return;
		}
		unlink($file);
	}
}
?>