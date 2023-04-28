<?php

include 'conexion.php';

$usuario = $_POST["usuario"];

$resultado = mysqli_query($con, "SELECT foto FROM Usuarios WHERE user='$usuario'");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

$todo = "";

while ($fila = mysqli_fetch_assoc($resultado)) {
    $foto = $fila['foto'];
    $todo .= $foto;
}
echo $todo;

?>