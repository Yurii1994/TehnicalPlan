<?php 
$mysql_host = "localhost";
$mysql_user = "technicalplan";
$mysql_password = "11121994";
$mysql_database = "technicalplan";

if (!mysql_connect($mysql_host, $mysql_user, $mysql_password)){
	
echo "<h2>Ѕаза недоступна!</h2>";
exit;
}

if (isset($_GET["action"])) { 
    $action = $_GET['action'];
}
if (isset($_GET["login"])) { 
    $login = $_GET['login'];
}
if (isset($_GET["new_login"])) { 
    $new_login = $_GET['new_login'];
}
if (isset($_GET["position"])) { 
    $position = $_GET['position'];
}
if (isset($_GET["enterprise"])) { 
    $enterprise = $_GET['enterprise'];
}
if (isset($_GET["code"])) { 
    $code = $_GET['code'];
}
if (isset($_GET["name_table"])) { 
    $name_table = $_GET['name_table'];
}
if (isset($_GET["name_position"])) { 
    $name_position = $_GET['name_position'];
}
if (isset($_GET["comment_state"])) { 
    $comment_state = $_GET['comment_state'];
}
if (isset($_GET["delete_table"])) { 
    $delete_table = $_GET['delete_table'];
}
if (isset($_GET["refresh"])) { 
    $refresh = $_GET['refresh'];
}
if (isset($_GET["stitched"])) { 
    $stitched = $_GET['stitched'];
}
if (isset($_GET["date"])) { 
    $date = $_GET['date'];
}
if (isset($_GET["id"])) { 
    $id = $_GET['id'];
}
if (isset($_GET["comment_manager"])) { 
    $comment_manager = $_GET['comment_manager'];
}
if (isset($_GET["comment_performer"])) { 
    $comment_performer = $_GET['comment_performer'];
}

mysql_connect($mysql_host, $mysql_user, $mysql_password);
mysql_select_db($mysql_database);
mysql_set_charset('utf8');
header('Content-type: text/html; charset=utf-8');

if($action == insert_stitched && $name_table != null && $id != null && $date != null && $login != null)
{
	$res = mysql_query("SELECT * FROM `users` WHERE login='$login'");
	for ($c = 0; $c < mysql_num_rows($res); $c++)
	{
		$f = mysql_fetch_array($res);
		$name = $f[name];
		$surname = $f[surname];
		$surname_father = $f[surname_father];
	}	
	mysql_query("UPDATE $name_table SET date='$date', stitched='$stitched', login='$login', name='$name', surname='$surname', surname_father='$surname_father' WHERE id='$id'") or die(print(json_encode(false)));
	print(json_encode(true));
}

if($action == insert_comment && $name_table != null && $id != null)
{
	mysql_query("UPDATE $name_table SET comment_manager='$comment_manager', comment_performer='$comment_performer' WHERE id='$id'") or die(print(json_encode(false)));
	print(json_encode(true));
}

if($action == insert && $position != null && $login != null && $enterprise != null)
{
	if($refresh == 'true' && $code != null && $name_table != null)
	{
		if($delete_table == 'true')
		{
			ClearTable($mysql_host, $mysql_user, $mysql_password, $mysql_database, $name_table);
			$code_table = generate_code(10);
			InsertTable($_FILES['scv']['tmp_name'], $name_table);
			
			mysql_query("UPDATE `position` SET position='".mysql_real_escape_string($position)."', name_table='$name_table' WHERE code='$code' AND login='$login'");
			mysql_query("UPDATE `linking` SET position='".mysql_real_escape_string($position)."', name_table='$name_table' WHERE code='$code' AND where_user='$login'");
			mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."', position='".mysql_real_escape_string($position)."',
			name_table='$name_table' WHERE name_table='$name_table'");
			
			$res = mysql_query("SELECT * FROM `position` WHERE login='$login'");
			$count = 0;
			for ($c = 0; $c < mysql_num_rows($res); $c++)
			{
				$count = $c + 1;
			}
			if($count > 0)
			{
				mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."', position = 'true', name_table = 'true' WHERE login='$login'");
			}
			else
			{
				mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."', position = 'false', name_table = 'false' WHERE login='$login'");
			}
			
		}
		else
		{
			mysql_query("UPDATE `position` SET position='".mysql_real_escape_string($position)."', name_table='$name_table' WHERE code='$code' AND login='$login'");
			mysql_query("UPDATE `linking` SET position='".mysql_real_escape_string($position)."', name_table='$name_table' WHERE code='$code' AND where_user='$login'");
			mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."', position='".mysql_real_escape_string($position)."',
			name_table='$name_table' WHERE name_table='$name_table'");
			
			$res = mysql_query("SELECT * FROM `position` WHERE login='$login'");
			$count = 0;
			for ($c = 0; $c < mysql_num_rows($res); $c++)
			{
				$count = $c + 1;
			}
			if($count > 0)
			{
				mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."', position = 'true', name_table = 'true' WHERE login='$login'");
			}
			else
			{
				mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."', position = 'false', name_table = 'false' WHERE login='$login'");
			}
			
		}
		print(json_encode(true));
	}
	else	
	{
		$code = generate_code(10);
		$code_table = generate_code(10);
		$name_table = CreateTable($mysql_host, $mysql_user, $mysql_password, $mysql_database, $code_table);
		InsertTable($_FILES['scv']['tmp_name'], $name_table);
		
		mysql_query("INSERT INTO `position`(`login`,`position`,`code`,`name_table`) VALUES ('$login','".mysql_real_escape_string($position)."',
		'$code', '$name_table')") or die(print(json_encode(false)));
		mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."', position = 'true', name_table = 'true' WHERE login='$login'");
		print(json_encode(true));
	}
}

if($action == download && $name_table != null && $name_position != null && $comment_state  != null)
{
	DownloadTableInCsv($name_table, $name_position, $comment_state);
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

function TranslitUrlCode($str)
{
    return trim(preg_replace('~[^-a-z0-9_]+~u', '-', strtolower(Translit($str))), "-");
}

function DownloadTableInCsv($name_table, $name_position, $comment_state)
{
	$name_position = iconv("UTF-8", "WINDOWS-1251", $name_position);
    $fichier = TranslitUrlCode($name_position);
	$fichier = $fichier.''.'.csv';
	if($fichier == '.csv')
	{
		$fichier = 'map'.''.'.csv';
	}
	header( "Content-Type: text/csv;charset=utf-8" );
	header( "Content-Disposition: attachment;filename=\"$fichier\"" );
	header("Pragma: no-cache");
	header("Expires: 0");

	$f= fopen('php://output', 'w');
	$sql = mysql_query("SELECT code, general, relative, description, normal, lightweight, light, date, comment_manager, comment_performer FROM $name_table"); 
	if($sql) 
	{  
		$count = 0;
		while($data = mysql_fetch_row($sql)) 
		{ 
			$code = iconv("UTF-8", "WINDOWS-1251", $data[0]);
			$general = iconv("UTF-8", "WINDOWS-1251", $data[1]);
			$relative= iconv("UTF-8", "WINDOWS-1251", $data[2]);
			$description = iconv("UTF-8", "WINDOWS-1251", $data[3]);
			$normal = iconv("UTF-8", "WINDOWS-1251", $data[4]);
			$lightweight = iconv("UTF-8", "WINDOWS-1251", $data[5]);
			$light = iconv("UTF-8", "WINDOWS-1251", $data[6]);
			$date = iconv("UTF-8", "WINDOWS-1251", $data[7]);
			$comment_manager = iconv("UTF-8", "WINDOWS-1251", $data[8]);
			$comment_performer = iconv("UTF-8", "WINDOWS-1251", $data[9]);
			if($comment_state == 'true')
			{
				fputcsv($f, array($code,$general,$relative, $description,$normal,$lightweight,$light,$date, $comment_manager, $comment_performer), ';'); 
			}
			else
			{
				if($count == 0)
				{
					fputcsv($f, array($code,$general,$relative, $description,$normal,$lightweight,$light,$date, $comment_manager, $comment_performer), ';'); 
				}
				else
				{
					fputcsv($f, array($code,$general,$relative, $description,$normal,$lightweight,$light,$date, '', ''), ';');
				}
			}
			$count++;
		} 
	} 
	fclose($f); 
}

function InsertTable($file, $name_table)
{
    $f = fopen($file, 'r'); 
	$data = fgetcsv($f, 3000, ';');
	if (($handle = fopen($file, "r")) !== FALSE)
	{
		while (($data = fgetcsv($handle, 3000, ";")) !== FALSE)
		{
			$code = iconv("WINDOWS-1251", "UTF-8", $data[0]);
			$general = iconv("WINDOWS-1251", "UTF-8", $data[1]);
			$relative= iconv("WINDOWS-1251", "UTF-8", $data[2]);
			$description = iconv("WINDOWS-1251", "UTF-8", $data[3]);
			$normal = iconv("WINDOWS-1251", "UTF-8", $data[4]);
			$lightweight = iconv("WINDOWS-1251", "UTF-8", $data[5]);
			$light = iconv("WINDOWS-1251", "UTF-8", $data[6]);
			$date = iconv("WINDOWS-1251", "UTF-8", $data[7]);
			$comment_manager = iconv("WINDOWS-1251", "UTF-8", $data[8]);
			$comment_performer = iconv("WINDOWS-1251", "UTF-8", $data[9]);
			mysql_query("INSERT INTO $name_table (`code`,`general`,`relative`,`description`,`normal`,`lightweight`,`light`,`date`,`comment_manager`,`comment_performer`)
			VALUES ('".mysql_real_escape_string($code)."','".mysql_real_escape_string($general)."', '".mysql_real_escape_string($relative)."',
			'".mysql_real_escape_string($description)."', '".mysql_real_escape_string($normal)."', '".mysql_real_escape_string($lightweight)."',
			'".mysql_real_escape_string($light)."', '".mysql_real_escape_string($date)."', '".mysql_real_escape_string($comment_manager)."',
			'".mysql_real_escape_string($comment_performer)."')");
		}
		fclose($handle);
	}
}

function CreateTable($mysql_host, $mysql_user, $mysql_password, $mysql_database, $name_table)
{
    $connection = mysqli_connect($mysql_host, $mysql_user, $mysql_password);
	mysqli_select_db($connection, $mysql_database);
	$sql = "CREATE TABLE $name_table
	( `id` INT NOT NULL AUTO_INCREMENT ,
	`code` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
	`general` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
	`relative` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
	`description` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
	`normal` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
	`lightweight` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
	`light` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
	`date` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
	`comment_manager` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`comment_performer` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`stitched` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`login` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`name` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`surname` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`surname_father` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,	PRIMARY KEY (`id`)) ENGINE = InnoDB"; 
	mysqli_query($connection,$sql);
	return $name_table;
}

function DeleteTable($mysql_host, $mysql_user, $mysql_password, $mysql_database, $name_table)
{
    $connection = mysqli_connect($mysql_host, $mysql_user, $mysql_password);
	mysqli_select_db($connection, $mysql_database);
	$sql ="DROP TABLE $name_table";
	mysqli_query($connection, $sql); 
}

function ClearTable($mysql_host, $mysql_user, $mysql_password, $mysql_database, $name_table)
{
    $connection = mysqli_connect($mysql_host, $mysql_user, $mysql_password);
	mysqli_select_db($connection, $mysql_database);
	$sql ="TRUNCATE TABLE $name_table";
	mysqli_query($connection, $sql); 
}

function generate_code($number)
{
    $arr = array('a','b','c','d','e','f',
                 'g','h','i','j','k','l',
                 'm','n','o','p','r','s',
                 't','u','v','x','y','z',
                 'A','B','C','D','E','F',
                 'G','H','I','J','K','L',
                 'M','N','O','P','R','S',
                 'T','U','V','X','Y','Z',
                 '1','2','3','4','5','6',
                 '7','8','9','0');
    $pass = "";
    for($i = 0; $i < $number; $i++)
    {    
      $index = rand(0, count($arr) - 1);
      $pass .= $arr[$index];
    }
    return $pass;
}

if($action == remove)
{
	if($login == null)
	{
		mysql_query("TRUNCATE TABLE `position`");
	}
	else
	{
		if($code != null && $name_table != null)
		{
			mysql_query("DELETE FROM `position` WHERE login='$login' AND code='$code'") or die(print(json_encode(false)));
			DeleteTable($mysql_host, $mysql_user, $mysql_password, $mysql_database, $name_table);
			$res = mysql_query("SELECT * FROM `position` WHERE login='$login'");
			$count = 0;
			for ($c = 0; $c < mysql_num_rows($res); $c++)
			{
				$count = $c + 1;
			}
			if($count > 0)
			{
				mysql_query("UPDATE `users` SET position = 'true', name_table = 'true' WHERE login='$login'");
			}
			else
			{
				mysql_query("UPDATE `users` SET position = 'false', name_table = 'false' WHERE login='$login'");
			}
			mysql_query("UPDATE `users` SET enterprise = 'false', position = 'false', name_table = 'false' WHERE name_table='$name_table'");
			mysql_query("DELETE FROM `linking` WHERE where_user='$login'");
			mysql_query("DELETE FROM `linking` WHERE from_user='$login'");
			print(json_encode(true));
		}
		else
		{
			print(json_encode(false));
		}
	}
}

if($action == select)
{
	if($login == null)
	{
		if($name_table != null)
		{
			$q=mysql_query("SELECT * FROM `$name_table`");
		}
		else
		{
			$q=mysql_query("SELECT * FROM `position`");
		}
	}
	else
	{
		$q=mysql_query("SELECT * FROM `position` WHERE login='$login'");		
	}

	while($e=mysql_fetch_assoc($q))
    $output[]=$e;
	if(count($output) > 0)
	{
		print(json_encode(true));
		print(json_encode($output, JSON_UNESCAPED_UNICODE));
	}
	else
	{
		print(json_encode(false));
	}
}

mysql_close();
?>