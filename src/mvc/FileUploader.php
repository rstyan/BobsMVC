<?php
require_once "dataaccess/FileUtil.php";
require_once "logging/SimpleLogger.php";

class FileUploader  {
	
	private $fieldName;
	private $prefix;
	private $destFolder;
	private $logger;
	private $mimeType;
	private $maxWidth;
	private $maxHeight;
	private $maxSize;
	
	// Use factory method, not constructor
	protected function __construct($field, $dest, $prefix, $maxSize, $maxWidth, $maxHeight) {
		$this->fieldName = $field;
		$this->prefix = $prefix;
		$this->destFolder = $dest;
		$this->logger = SimpleLogger::getInstance();
		$this->maxSize = $maxSize;
		$this->maxWidth = $maxWidth;
		$this->maxHeight = $maxHeight;
	}
	
	// Something that may need to get overridden for testing purposes.
	protected function relocateFile($src, $dst) {
		return move_uploaded_file($src, $dst);
	}
	
	public function doUpload($allowedTypes) {
		// First check to see if anything got uploaded.  If not,
		// we'll assume its cause the file was too large, which
		// seems to be the most probable cause.
		if (empty($_FILES) || !array_key_exists($this->fieldName, $_FILES)) {
			return "Unable to upload: please make sure your file is under 30M in size.";
		}
		
		// If the file didn't upload, say why.
		switch($_FILES[$this->fieldName]['error']) {
			case UPLOAD_ERR_OK:
				break;
			case UPLOAD_ERR_INI_SIZE:
				return "Upload failed, file too big";
			case UPLOAD_ERR_FORM_SIZE:
				return "Upload failed, file too big";
			case UPLOAD_ERR_PARTIAL:
				return "Upload failed, file only partially uploaded";
			case UPLOAD_ERR_NO_FILE:
				return "Upload failed, no file specified";
			case UPLOAD_ERR_NO_TMP_DIR:
				return "Upload failed, can't access tmp directory";
			case UPLOAD_ERR_CANT_WRITE:
				return "Upload failed, write failed";
			case UPLOAD_ERR_EXTENSION:
				return "Upload failed, invalid file extension";
			default:
				return "unknown error";
		}

		// Make sure it really did get uploaded.
		if ($this->uploadedSize() <= 0) {
			return "Unable to upload file - it may be too large";
		}
		
		// Check the mime type supplied by the browser:
		$mimeType = $this->uploadedType();
		if (!empty($mimeType) && preg_match($allowedTypes, $mimeType)==0) {
			return 'Files of type "'.$mimeType.'" are not permitted.';
		}
		
		// Find actual type of uploaded file - mime type can be fooled.
		$finfo = finfo_open(FILEINFO_MIME_TYPE);
		$this->mimeType = finfo_file($finfo, $this->source());
		finfo_close($finfo);
		if (empty($this->mimeType)) {
			return "Cannot determine the type of the file.  Please only upload image or audio files.";
		}
		if (preg_match($allowedTypes, $this->mimeType)==0) {
			return 'Files of type "'.$this->mimeType.'" are not permitted.';
		}
		
		// Check the file size;
		if (!is_null($this->maxSize)) {
			$size = filesize($this->source());
			if ($size > $this->maxSize) {
				return 'File ('.$this->fieldName.') must be less than '.$this->human_filesize($this->maxSize).' in size';
			}
		}
		if (!is_null($this->maxWidth)) {
			$size = getimagesize($this->source());
			if ($size[0] > $this->maxWidth) {
				return 'Image ('.$this->fieldName.') must be less than '.$this->maxWidth.'px wide';
			}
		}
		
		if (!is_null($this->maxHeight)) {
			$size = getimagesize($this->source());
			if ($size[1] > $this->maxHeight) {
				return 'Image ('.$this->fieldName.') must be less than '.$this->maxHeight.'px high';
			}
		}
		// If valid type move file to user directory.
		if (!$this->relocateFile($this->source(), $this->target())) {
			return 'Unable to move "'.$this->uploadedName().'" from tmp directory';
		}
		return null;
		
	}
	
	private function human_filesize($bytes, $decimals = 2) {
		$sz = 'BKMGTP';
		$factor = floor((strlen($bytes) - 1) / 3);
		return sprintf("%.{$decimals}f", $bytes / pow(1024, $factor)) . @$sz[$factor];
	}
	
	private function getFileProperty($property) {
		if (empty($_FILES) || !array_key_exists($this->fieldName, $_FILES) || !array_key_exists($property, $_FILES[$this->fieldName])) {
			return null;
		}
		return $_FILES[$this->fieldName][$property];
	}
	
	public function source() {
		return $this->getFileProperty('tmp_name');
	}
	
	public function uploadedName() {
		$name = $this->getFileProperty('name');
		return is_null($name)?null:basename($name);
	}
	
	public function uploadedSize() {
		return $this->getFileProperty('size');
	}
	
	public function uploadedType() {
		return $this->getFileProperty('type');
	}
	
	public function adjustedName() {
		$name = $this->uploadedName();
		return is_null($name)?null:$this->prefix.$name;
	}
	
	public function mimeType() {
		return $this->mimeType;
	}
	
	public function target() {
		$name = $this->adjustedName();
		return is_null($name)?null:$this->destFolder.FileUtil::fileSeparator.$name;
	}
	
	// Factory method:
	public static function createUploader($field, $dest, $prefix="", $maxSize=null, $maxWidth=null, $maxHeight=null) {
		return new FileUploader($field, $dest, $prefix, $maxSize, $maxWidth, $maxHeight);
	}
}

?>