<?php

include 'conexion.php';

$usuario = $_POST["usuario"];
$contrasena = $_POST["contrasena"];

$resultado = mysqli_query($con, "INSERT INTO Usuarios (user, pass, foto) VALUES ('$usuario','$contrasena', 'null')");

if ($resultado==true) {
    echo '1';
}else{
    echo 'Ha habido un error: ' . mysqli_error($con);
}

?>