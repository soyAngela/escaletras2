<?php

include 'conexion.php';

$usuario = $_POST["usuario"];
$foto = $_POST["foto"];

$resultado = mysqli_query($con, "UPDATE Usuarios SET foto = '$foto' WHERE user = '$usuario'");

if ($resultado==true) {
    echo '1';
}else{
    echo 'Ha habido un error: ' . mysqli_error($con);
}

?>