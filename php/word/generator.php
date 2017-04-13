<?php
	require 'vendor/autoload.php';
	
	header('Content-type: text/html; charset=utf-8');
	
	$action = $_POST['action'];
	$text_report = $_POST['text_report'];
	$text_description = $_POST['text_description'];
	$enterprise = $_POST['enterprise'];
	$head = $_POST['head'];
	$manager = $_POST['manager'];
	$position = $_POST['position'];
	$name_file = $_POST['name_file'];
	$path = $_POST['path'];
	
	if($name_file == null)
	{
		$name_file = $_GET['name_file'];
	}
	if($action == null)
	{
		$action = $_GET['action'];
	}
	
	if($action == delete && $name_file != null)
	{
		unlink("$name_file") or die(print(json_encode(false)));
		print(json_encode(true));
	}
	
	if($action == create && $text_report != null && $enterprise != null && $head != null && $manager != null && $position != null)
	{
		if($name_file == null)
		{
			$name_file = 'report';
		}
		$name_file = Translit($name_file);
		$name_file = $name_file."_".date("d.m.Y_H.i.s");
		$phpWord = new \PhpOffice\PhpWord\PhpWord();
		
		if($path != null)
		{
			$file_path = $path;
		}
		else
		{
			$file_path = 'word_template/sample_report_template.docx';
		}
		
		$document = $phpWord->loadTemplate($file_path);
		
		$document->setValue('date', date("d.m.Y"));
		$document->setValue('enterprise', $enterprise);
		$document->setValue('head', $head);
		$document->setValue('manager', $manager);
		$document->setValue('position', $position);
		
		$text_report = str_replace("\n", ' ', $text_report);
		$text_description = str_replace("\n", ' ', $text_description);
		
		$document->setValue('report', $text_report);
		$document->setValue('description', $text_description);
		
		$temp_file = $name_file.'.docx';
		$document->saveAs($temp_file);
		print(json_encode($temp_file));
		if($path != null)
		{
			unlink("$path");
		}
	}
	
	function Translit($string) {
	$replace=array(
		"'"=>"",
		"`"=>"",
		"а"=>"a","А"=>"a",
		"б"=>"b","Б"=>"b",
		"в"=>"v","В"=>"v",
		"г"=>"g","Г"=>"g",
		"д"=>"d","Д"=>"d",
		"е"=>"e","Е"=>"e",
		"ж"=>"zh","Ж"=>"zh",
		"з"=>"z","З"=>"z",
		"и"=>"i","И"=>"i",
		"й"=>"y","Й"=>"y",
		"к"=>"k","К"=>"k",
		"л"=>"l","Л"=>"l",
		"м"=>"m","М"=>"m",
		"н"=>"n","Н"=>"n",
		"о"=>"o","О"=>"o",
		"п"=>"p","П"=>"p",
		"р"=>"r","Р"=>"r",
		"с"=>"s","С"=>"s",
		"т"=>"t","Т"=>"t",
		"у"=>"u","У"=>"u",
		"ф"=>"f","Ф"=>"f",
		"х"=>"h","Х"=>"h",
		"ц"=>"c","Ц"=>"c",
		"ч"=>"ch","Ч"=>"ch",
		"ш"=>"sh","Ш"=>"sh",
		"щ"=>"sch","Щ"=>"sch",
		"ъ"=>"","Ъ"=>"",
		"ы"=>"y","Ы"=>"y",
		"ь"=>"","Ь"=>"",
		"э"=>"e","Э"=>"e",
		"ю"=>"yu","Ю"=>"yu",
		"я"=>"ya","Я"=>"ya",
		"і"=>"i","І"=>"i",
		"ї"=>"yi","Ї"=>"yi",
		"є"=>"e","Є"=>"e"
	);
	return $str=iconv("UTF-8","UTF-8//IGNORE",strtr($string,$replace));
}
	
?>