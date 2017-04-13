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

if (isset($_GET["id_template"])) { 
    $id_template = $_GET['id_template'];
}

if($action == upload)
{
	$uploads_dir = './';
	
	$ext = array_pop(explode('.',$_FILES['text']['name']));
	$new_name = $id_template.'.'.$ext;
	move_uploaded_file($_FILES['text']['tmp_name'], $uploads_dir.$new_name);
}

?>