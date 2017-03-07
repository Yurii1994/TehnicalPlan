<?php 
$mysql_host = "localhost";
$mysql_user = "technicalplan";
$mysql_password = "11121994";
$mysql_database = "technicalplan";

mysql_connect($mysql_host, $mysql_user, $mysql_password);
mysql_select_db($mysql_database);
mysql_set_charset('utf8');
header('Content-type: text/html; charset=utf-8');


if (isset($_GET["action"])) { 
    $action = $_GET['action'];
}

if (isset($_GET["login"])) { 
    $login = $_GET['login'];
}
//зегрузка на сервер
if($action == insert && $login != null)
{
	$uploads_dir = './images_account/';
	$ext = array_pop(explode('.',$_FILES['image']['name']));
	$new_name = time().'.'.$ext;
	if(move_uploaded_file($_FILES['image']['tmp_name'], $uploads_dir.$new_name))
	{
		$res = mysql_query("SELECT * FROM `users` WHERE login='$login'");
		for ($c = 0; $c < mysql_num_rows($res); $c++)
		{
			$f = mysql_fetch_array($res);
			$name = $f[images];
			unlink("images_account/$name");
		}	
		mysql_query("UPDATE `users` SET images='$new_name' WHERE login='$login'");
	}
}

if($action == remove)
{
	if($login != null)
	{
		$res = mysql_query("SELECT * FROM `users` WHERE login='$login'");
		while($e=mysql_fetch_assoc($res))
		$output[]=$e;
		if(count($output) > 0)
		{
			$res = mysql_query("SELECT * FROM `users` WHERE login='$login'");
			for ($c = 0; $c < mysql_num_rows($res); $c++)
			{
				$f = mysql_fetch_array($res);
				$name = $f[images];
				unlink("images_account/$name");
			}	
			$new_name = '';
			mysql_query("UPDATE `users` SET images='$new_name' WHERE login='$login'");
		}
	}
}

mysql_close();
?>